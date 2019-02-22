/*
 *  Copyright (c) 2018 Isa Hekmatizadeh.
 *
 *  This file is part of Geev.
 *
 *  Geev is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Geev is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Geev.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.piranframework.geev;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Isa Hekmatizadeh
 */
@Configuration
@ConditionalOnBean(GeevMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(GeevProperties.class)
public class GeevAutoConfiguration {
  private static final Logger log = Logger.getLogger(GeevAutoConfiguration.class.getName());

  @Bean
  @ConditionalOnMissingBean
  public Geev geev(ApplicationContext applicationContext, GeevProperties geevProperties) {
    Map<String, Object> hookMap =
        applicationContext.getBeansWithAnnotation(GeevHook.class);
    List<Consumer<Node>> joinList = new ArrayList<>();
    List<Consumer<Node>> leftList = new ArrayList<>();
    hookMap.values().forEach(h -> {
      joinList.addAll(Stream.of(h.getClass().getMethods())
          .filter(m -> m.isAnnotationPresent(NodeJoined.class))
          .map(m -> this.methodToConsumer(m, h)).collect(Collectors.toList()));
      leftList.addAll(Stream.of(h.getClass().getMethods())
          .filter(m -> m.isAnnotationPresent(NodeLeft.class))
          .map(m -> this.methodToConsumer(m, h)).collect(Collectors.toList()));
    });
    Geev geev;
    try {
      geev = Geev.run(new GeevConfig.Builder()
          .setMySelf(new Node(geevProperties.getMyselfRole(),
              geevProperties.getMyselfIp(),
              geevProperties.getMyselfPort()))
          .multicastAddress(geevProperties.getMulticastAddress())
          .useBroadcast(geevProperties.isBroadcast())
          .discoveryPort(geevProperties.getDiscoveryPort())
          .onJoin(node -> joinList.forEach(c -> c.accept(node)))
          .onLeave(node -> leftList.forEach(c -> c.accept(node)))
          .build()
      );
    } catch (IOException e) {
      log.warning(e.getMessage());
      return null;
    }
    Runtime.getRuntime().addShutdownHook(new Thread(geev::destroy));
    log.info("geev bootstrap finished");
    return geev;
  }

  private Consumer<Node> methodToConsumer(Method m, Object h) {
    return node -> {
      try {
        m.invoke(h, node);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    };
  }
}
