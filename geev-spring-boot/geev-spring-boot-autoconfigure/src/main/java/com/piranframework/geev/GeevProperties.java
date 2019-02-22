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

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author Isa Hekmatizadeh
 */
@ConfigurationProperties("geev")
public class GeevProperties {
  private boolean broadcast = true;
  private String multicastAddress;
  private int discoveryPort = Geev.DEFAULT_DISCOVERY_PORT;
  private String myselfRole;
  private String myselfIp;
  private int myselfPort;

  public boolean isBroadcast() {
    return broadcast;
  }

  public GeevProperties setBroadcast(boolean broadcast) {
    this.broadcast = broadcast;
    return this;
  }

  public String getMulticastAddress() {
    return multicastAddress;
  }

  public GeevProperties setMulticastAddress(String multicastAddress) {
    this.multicastAddress = multicastAddress;
    return this;
  }

  public int getDiscoveryPort() {
    return discoveryPort;
  }

  public GeevProperties setDiscoveryPort(int discoveryPort) {
    this.discoveryPort = discoveryPort;
    return this;
  }

  public String getMyselfRole() {
    return myselfRole;
  }

  public GeevProperties setMyselfRole(String myselfRole) {
    this.myselfRole = myselfRole;
    return this;
  }

  public String getMyselfIp() {
    return myselfIp;
  }

  public GeevProperties setMyselfIp(String myselfIp) {
    this.myselfIp = myselfIp;
    return this;
  }

  public int getMyselfPort() {
    return myselfPort;
  }

  public GeevProperties setMyselfPort(int myselfPort) {
    this.myselfPort = myselfPort;
    return this;
  }
}
