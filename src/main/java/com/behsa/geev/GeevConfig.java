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

import java.net.*;
import java.util.function.Consumer;

/**
 * Geev configuration class which is immutable class needed in {@link Geev} constructor.
 * Instances of this class should be created by {@link GeevConfig.Builder} builder class.
 *
 * @author Isa Hekmatizadeh
 */
public class GeevConfig {
  private final Consumer<Node> join;
  private final Consumer<Node> leave;
  private final Node mySelf;
  private final boolean broadcast;
  private final InetAddress multicastAddress;
  private final int discoveryPort;

  private GeevConfig(Builder builder) {
    this.join = builder.onJoin;
    this.leave = builder.onLeave;
    this.mySelf = builder.mySelf;
    this.broadcast = builder.broadcast;
    this.multicastAddress = builder.multicastAddress;
    this.discoveryPort = builder.discoveryPort;
  }

  Consumer<Node> getJoin() {
    return join;
  }

  Consumer<Node> getLeave() {
    return leave;
  }

  Node getMySelf() {
    return mySelf;
  }

  boolean isBroadcast() {
    return broadcast;
  }

  InetAddress getMulticastAddress() {
    return multicastAddress;
  }

  int getDiscoveryPort() {
    return discoveryPort;
  }

  /**
   * Builder class to instantiate {@link GeevConfig} instances. Clients should create an object
   * of this class and after chain calling to setter method call build() method to access
   * {@link GeevConfig} instance created.
   */
  public static class Builder {
    private Consumer<Node> onJoin;
    private Consumer<Node> onLeave;
    private Node mySelf;
    private boolean broadcast = true;
    private InetAddress multicastAddress;
    private int discoveryPort = Geev.DEFAULT_DISCOVERY_PORT;

    /**
     * set a callback to call when a new node discovered
     *
     * @param onJoin the callback
     * @return Builder object
     */
    public Builder onJoin(Consumer<Node> onJoin) {
      this.onJoin = onJoin;
      return this;
    }

    /**
     * set a callback to call when a node announce it leave
     *
     * @param onLeave the callback
     * @return Builder object
     */
    public Builder onLeave(Consumer<Node> onLeave) {
      this.onLeave = onLeave;
      return this;
    }

    /**
     * set the node object of the client
     *
     * @param mySelf the node which represent the client
     * @return Builder object
     */
    public Builder setMySelf(Node mySelf) {
      this.mySelf = mySelf;
      return this;
    }

    /**
     * Indicate that geev should use broadcast strategy, the default strategy is broadcast
     *
     * @return Builder object
     */
    public Builder useBroadcast() {
      this.broadcast = true;
      this.multicastAddress = null;
      return this;
    }

    /**
     * Indicate that geev should use multicast strategy and use the multicast address provided
     *
     * @param multicastAddress multicast address to use
     * @return Builder object
     */
    public Builder multicastAddress(InetAddress multicastAddress) {
      this.multicastAddress = multicastAddress;
      this.broadcast = false;
      return this;
    }

    /**
     * Use discovery port other than the default 5172.
     *
     * @param discoveryPort port to use in UDP discovery
     * @return Builder object
     */
    public Builder discoveryPort(int discoveryPort) {
      this.discoveryPort = discoveryPort;
      return this;
    }

    /**
     * Build the actual {@link GeevConfig} object and return it
     *
     * @return {@link GeevConfig} object created
     */
    public GeevConfig build() {
      return new GeevConfig(this);
    }
  }
}
