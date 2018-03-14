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

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZThread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Isa Hekmatizadeh
 */
public class Geev {
  public static final int DEFAULT_DISCOVERY_PORT = 5172;
  private final ZContext ctx;
  private final ZMQ.Socket pipe;
  private GeevConfig config;

  public Geev(GeevConfig config) {
    this.config = config;
    ctx = new ZContext();
    pipe = ZThread.fork(ctx,new GeevInternal(),config);
  }

  public List<Node> allNodes() {
    return null;
  }

  public void nodeDisconnected() {
  }

  public void destroy() {
  }

  private static class GeevInternal implements ZThread.IAttachedRunnable {
    private Map<String, List<Node>> nodes = new ConcurrentHashMap<>();

    @Override
    public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {

    }
  }

}
