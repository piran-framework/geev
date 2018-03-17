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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Isa Hekmatizadeh
 */
public class Geev {
  static final int DEFAULT_DISCOVERY_PORT = 5172;
  private static final Charset ASCII = Charset.forName("US-ASCII");
  private static final byte[] PROTOCOL_NAME = "RBND".getBytes(ASCII);
  private static final byte PROTOCOL_VERSION = 0x01;
  private static final byte JOIN = 0x01;
  private static final byte JOIN_RESPONSE = 0x02;
  private static final byte LEAVE = 0x03;

  private Thread internalThread;
  private GeevInternal internalInstance;

  public Geev(GeevConfig config) throws IOException {
    internalInstance = new GeevInternal(config);
    internalThread = new Thread(internalInstance);
    internalThread.start();
  }

  public List<Node> allNodes() {
    List<Node> nodes = new LinkedList<>();
    internalInstance.nodes.values().forEach(nodes::addAll);
    return nodes;
  }

  public void nodeDisconnected(Node node) {
    internalInstance.disconnect(node);
  }

  public void destroy() {
    try {
      internalInstance.send(LEAVE);
    } catch (IOException e) {
      e.printStackTrace();
    }
    internalThread.interrupt();
    try {
      internalThread.join(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private class GeevInternal implements Runnable {
    private final DatagramChannel channel;
    private final GeevConfig config;
    private Map<String, List<Node>> nodes = new ConcurrentHashMap<>();

    private GeevInternal(GeevConfig config) throws IOException {
      this.config = config;
      channel = DatagramChannel.open();

      //broadcast first
      channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
      channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      channel.configureBlocking(true);
      channel.bind(new InetSocketAddress(config.getDiscoveryPort()));
    }

    private void disconnect(Node node) {
      Optional<List<Node>> containingList = nodes.values().stream().filter((l) -> l.contains(node)).findFirst();
      containingList.ifPresent(nodes -> nodes.remove(node));
    }

    @Override
    public void run() {
      int retryCountLeft = 3;
      while (retryCountLeft > 0) {
        try {
          send(JOIN);
          break;
        } catch (IOException e) {
          e.printStackTrace();
          retryCountLeft--;
          try {
            Thread.sleep(10);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
        }
      }
      while (!Thread.currentThread().isInterrupted()) {
        try {
          handleReceive();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    private void send(byte messageType) throws IOException {
      String myRole = config.getMySelf().getRole();
      int bufferSize = 8 + myRole.length();
//      if (messageType != LEAVE)
//        bufferSize += ;
      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      buffer.put(PROTOCOL_NAME); //protocol name 4 byte
      buffer.put(PROTOCOL_VERSION); //protocol version 1 byte
      buffer.put(messageType); //message type 1 byte
      buffer.putChar((char) config.getMySelf().getPort()); //port 2 byte
      buffer.put(myRole.getBytes(ASCII));
      buffer.flip();
      channel.send(buffer, config.getTarget());
    }

    private void handleReceive() throws IOException {
      ByteBuffer buffer = ByteBuffer.allocate(128);
      SocketAddress sender = channel.receive(buffer);

      byte[] protocolName = new byte[4];
      buffer.flip();
      buffer.get(protocolName);
      if (!Arrays.equals(PROTOCOL_NAME, protocolName) ||
              buffer.get() != PROTOCOL_VERSION)
        return; //ignore the unknown messages
      byte messageType = buffer.get();
      Node node = createNodeFromMsg(sender, buffer);
      switch (messageType) {
        case JOIN:
          handleJoin(node);
          break;
        case JOIN_RESPONSE:
          handleJoinResponse(node);
          break;
        case LEAVE:
          handleLeave(node);
          break;
      }
    }

    private Node createNodeFromMsg(SocketAddress sender, ByteBuffer buffer) {
      int port = buffer.getChar();
      int length = buffer.limit() - buffer.position();
      byte[] roleBytes = new byte[length];
      buffer.get(roleBytes, 0, length);
      String role = new String(roleBytes, ASCII);
      InetAddress ip = ((InetSocketAddress) sender).getAddress();
      return new Node(role, ip, port);
    }

    private void handleLeave(Node node) {
      List<Node> nodeWithSameRole = this.nodes.get(node.getRole());
      if (nodeWithSameRole != null && nodeWithSameRole.contains(node)) {
        nodeWithSameRole.remove(node);
        config.getLeave().accept(node);
      }
    }

    private void handleJoinResponse(Node node) {
      List<Node> nodesWithSameRole = this.nodes.get(node.getRole());
      if (nodesWithSameRole == null) {
        nodesWithSameRole = new ArrayList<>();
        nodesWithSameRole.add(node);
        nodes.put(node.getRole(), nodesWithSameRole);
        config.getJoin().accept(node);
      } else if (!nodesWithSameRole.contains(node)) {
        nodesWithSameRole.add(node);
        config.getJoin().accept(node);
      }
    }

    private void handleJoin(Node node) throws IOException {
      handleJoinResponse(node);
      send(JOIN_RESPONSE);
    }
  }
}
