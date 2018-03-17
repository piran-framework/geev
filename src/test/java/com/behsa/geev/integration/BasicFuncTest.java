/*
 *  Copyright (c) 2018 Behsa Corporation.
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

package com.behsa.geev.integration;

import com.behsa.geev.Geev;
import com.behsa.geev.GeevConfig;
import com.behsa.geev.Node;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * @author Isa Hekmatizadeh
 */
public class BasicFuncTest {
  @Test
  public void basicFunctionTest() throws IOException, InterruptedException {
    Node node1 = new Node("WORKER", Inet4Address.getByName("127.0.0.1"), 1000);
    Geev geev1 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev1: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev1: %s leaved\n", node))
            .setMySelf(node1)
            .build()
    );
    Node node2 = new Node("WORKER", Inet4Address.getByName("192.168.13.70"), 1001);
    Geev geev2 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev2: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev2: %s leaved\n", node))
            .setMySelf(node2)
            .build()
    );

    Node node3 = new Node("LOG-AGGREGATOR", Inet4Address.getByName("192.168.13.70"), 1002);
    Geev geev3 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev3: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev3: %s leaved\n", node))
            .setMySelf(node3)
            .build()
    );
    Thread.sleep(1000);
    Assert.assertEquals(3, geev1.allNodes().size());
    Assert.assertEquals(3, geev2.allNodes().size());
    Assert.assertEquals(3, geev3.allNodes().size());
    geev1.destroy();
    Thread.sleep(1000);
    Assert.assertEquals(2, geev2.allNodes().size());
    Assert.assertEquals(2, geev3.allNodes().size());
    geev2.nodeDisconnected(node3);
    Assert.assertEquals(1,geev2.allNodes().size());
  }
}
