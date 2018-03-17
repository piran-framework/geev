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

package com.behsa.geev;

import java.net.InetAddress;
import java.util.Objects;

/**
 * Immutable class represents nodes. Each node has a role name, an IP address and a port number.
 * Equality of the nodes determined just by the IP address and port number.
 *
 * @author Isa Hekmatizadeh
 */
public class Node {
  private final String role;
  private final InetAddress ip;
  private final int port;

  /**
   * Constructor of Node class
   *
   * @param role the role name of the node - case sensitive
   * @param ip   IP address of the node
   * @param port port number of the node
   */
  public Node(String role, InetAddress ip, int port) {
    this.role = role;
    this.ip = ip;
    this.port = port;
  }

  public String getRole() {
    return role;
  }

  public InetAddress getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Node node = (Node) o;
    return port == node.port &&
            Objects.equals(ip, node.ip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ip, port);
  }

  @Override
  public String toString() {
    return role + "[" + ip + ":" + port + "]";
  }
}
