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
import java.util.function.Consumer;

/**
 * @author Isa Hekmatizadeh
 */
public class GeevConfig {
  private final Consumer<Node> join;
  private final Consumer<Node> leave;
  private final Node mySelf;
  private final boolean broadcast;
  private final Inet4Address multicastAddress;
  private final int discoveryPort;

  private GeevConfig(Builder builder) {
    this.join = builder.onJoin;
    this.leave = builder.onLeave;
    this.mySelf = builder.mySelf;
    this.broadcast = builder.broadcast;
    this.multicastAddress = builder.multicastAddress;
    this.discoveryPort = builder.discoveryPort;
  }

  public Consumer<Node> getJoin() {
    return join;
  }

  public Consumer<Node> getLeave() {
    return leave;
  }

  public Node getMySelf() {
    return mySelf;
  }

  public boolean isBroadcast() {
    return broadcast;
  }

  public Inet4Address getMulticastAddress() {
    return multicastAddress;
  }

  public int getDiscoveryPort() {
    return discoveryPort;
  }

  public static class Builder {
    private Consumer<Node> onJoin;
    private Consumer<Node> onLeave;
    private Node mySelf;
    private boolean broadcast = true;
    private Inet4Address multicastAddress;
    private int discoveryPort = Geev.DEFAULT_DISCOVERY_PORT;


    public Builder onJoin(Consumer<Node> onJoin) {
      this.onJoin = onJoin;
      return this;
    }

    public Builder onLeave(Consumer<Node> onLeave) {
      this.onLeave = onLeave;
      return this;
    }

    public Builder setMySelf(Node mySelf) {
      this.mySelf = mySelf;
      return this;
    }

    public Builder useBroadcast() {
      this.broadcast = true;
      this.multicastAddress = null;
      return this;
    }

    public Builder multicastAddress(Inet4Address multicastAddress) {
      this.multicastAddress = multicastAddress;
      this.broadcast = false;
      return this;
    }

    public Builder discoveryPort(int discoveryPort) {
      this.discoveryPort = discoveryPort;
      return this;
    }

    public GeevConfig build() {
      return new GeevConfig(this);
    }
  }
}
