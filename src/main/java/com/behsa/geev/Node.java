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

import java.net.Inet4Address;

/**
 * @author Isa Hekmatizadeh
 */
public class Node {
  private final String role;
  private final Inet4Address ip;
  private final int port;

  public Node(String role, Inet4Address ip, int port) {
    this.role = role;
    this.ip = ip;
    this.port = port;
  }

  public String getRole() {
    return role;
  }

  public Inet4Address getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return role + "[" + ip + ":" + port + "]";
  }
}
