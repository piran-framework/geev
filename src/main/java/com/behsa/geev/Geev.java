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
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Starting point for discovery, every application using Geev should create a new instance of
 * this class by the constructor and take care of this instance. This class create an internal
 * thread to handle discovery stuff.
 *
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

  /**
   * Geev constructor which take a {@link GeevConfig} instance as a argument and create a Geev
   * instance and an internal thread. it also broadcast join message to the network.
   *
   * @param config Geev configuration
   * @throws IOException if it can't open a datagram socket or bind it.
   */
  public Geev(GeevConfig config) throws IOException {
    internalInstance = new GeevInternal(config);
    internalThread = new Thread(internalInstance);
    internalThread.start();
  }

  /**
   * Access all nodes discovered
   *
   * @return all available nodes
   */
  public List<Node> allNodes() {
    List<Node> nodes = new LinkedList<>();
    internalInstance.nodes.values().forEach(nodes::addAll);
    return nodes;
  }

  /**
   * return all nodes discovered with the role specified
   *
   * @param role role to filter node based on
   * @return all nodes discovered with the role specified
   */
  public List<Node> allNodes(String role) {
    return internalInstance.nodes.get(role);
  }

  /**
   * Notify geev one node does not response and probably it's disconnected
   *
   * @param node the disconnected node
   */
  public void nodeDisconnected(Node node) {
    internalInstance.disconnect(node);
  }

  /**
   * destroy the geev instance cleanly
   */
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
    private final SocketAddress target;
    private Map<String, List<Node>> nodes = new ConcurrentHashMap<>();

    private GeevInternal(GeevConfig config) throws IOException {
      this.config = config;
      channel = DatagramChannel.open(StandardProtocolFamily.INET);

      if (config.isBroadcast())
        channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
      else {
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF,
                NetworkInterface.getByInetAddress(config.getMySelf().getIp()));
      }
      channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
      channel.configureBlocking(true);
      channel.bind(new InetSocketAddress(config.getDiscoveryPort()));
      if (config.isBroadcast()) { //broadcast
        target = new InetSocketAddress("255.255.255.255", config.getDiscoveryPort());
      } else { // multicast
        channel.join(config.getMulticastAddress(),
                NetworkInterface.getByInetAddress(config.getMySelf().getIp()));
        target = new InetSocketAddress(config.getMulticastAddress(), config.getDiscoveryPort());
      }
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
      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      buffer.put(PROTOCOL_NAME); //protocol name 4 byte
      buffer.put(PROTOCOL_VERSION); //protocol version 1 byte
      buffer.put(messageType); //message type 1 byte
      buffer.putChar((char) config.getMySelf().getPort()); //port 2 byte
      buffer.put(myRole.getBytes(ASCII));
      buffer.flip();
      channel.send(buffer, target);
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
      if (config.getMySelf().equals(node)) //messages from itself should be ignored
        return;
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
      if (config.getMySelf().equals(node)) //messages from itself should be ignored
        return;
      handleJoinResponse(node);
      send(JOIN_RESPONSE);
    }
  }
}
