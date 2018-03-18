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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author Isa Hekmatizadeh
 */
public class BroadcastTest {
  @Test
  public void simpleBroadcastTest() throws IOException, InterruptedException {
    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    NetworkInterface nInt = null;
    while (networkInterfaces.hasMoreElements()) {
      nInt = networkInterfaces.nextElement();
      if (!nInt.isLoopback())
        break;
    }
    Assert.assertNotNull(nInt);
    InetAddress defaultInetAddress = null;
    Enumeration<InetAddress> addresses = nInt.getInetAddresses();
    while (addresses.hasMoreElements()) {
      defaultInetAddress = addresses.nextElement();
      if (defaultInetAddress instanceof Inet4Address)
        break;
    }
    Assert.assertNotNull(defaultInetAddress);
    System.out.println(defaultInetAddress);
    Node node1 = new Node("WORKER", defaultInetAddress, 1000);
    Geev geev1 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev1: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev1: %s leaved\n", node))
            .setMySelf(node1)
            .build()
    );
    System.out.println("node1 started");
    Node node2 = new Node("WORKER", defaultInetAddress, 1001);
    Geev geev2 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev2: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev2: %s leaved\n", node))
            .setMySelf(node2)
            .build()
    );
    System.out.println("node2 started");

    Node node3 = new Node("LOG-AGGREGATOR", defaultInetAddress, 1002);
    Geev geev3 = new Geev(new GeevConfig.Builder()
            .onJoin((node) -> System.out.printf("geev3: %s joined\n", node))
            .onLeave((node) -> System.out.printf("geev3: %s leaved\n", node))
            .setMySelf(node3)
            .build()
    );
    System.out.println("node3 started");
    Thread.sleep(1000);
    Assert.assertEquals(2, geev1.allNodes().size());
    Assert.assertEquals(1, geev1.allNodes("WORKER").size());
    Assert.assertEquals(1, geev1.allNodes("LOG-AGGREGATOR").size());
    Assert.assertEquals(2, geev2.allNodes().size());
    Assert.assertEquals(2, geev3.allNodes().size());
    geev1.destroy();
    Thread.sleep(1000);
    Assert.assertEquals(1, geev2.allNodes().size());
    Assert.assertEquals(1, geev3.allNodes().size());
    Assert.assertEquals(1, geev3.allNodes("WORKER").size());
    geev2.nodeDisconnected(node3);
    Assert.assertEquals(0, geev2.allNodes().size());
  }
}
