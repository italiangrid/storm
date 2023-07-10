/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.node.FTPNode;
import it.grid.storm.balancer.node.HttpNode;
import it.grid.storm.balancer.node.HttpsNode;
import it.grid.storm.balancer.strategy.BalancingStrategyFactory;
import it.grid.storm.namespace.NamespaceException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capability {

  private Logger log = LoggerFactory.getLogger(Capability.class);
  private ACLMode aclMode;
  private Quota quota;
  // List of TransportProtocol by Protocol.
  private Map<Protocol, TransportProtocol> transpProtocolsByScheme;
  // List of TransportProtocol by Protocol.
  private Map<Integer, TransportProtocol> transpProtocolsByID;
  // List of TransportProtocol by Entering order.
  private List<TransportProtocol> transpProtocolsList;
  // List of Pools
  private List<ProtocolPool> protocolPools;
  // List of ProtocolPool.
  private Map<Protocol, ProtocolPool> protocolPoolsByScheme;
  // List of Balancer.
  private Map<Protocol, BalancingStrategy> balancerByScheme;

  private DefaultACL defaultACL;

  /** Constructor */
  public Capability(ACLMode aclMode) throws NamespaceException {

    setACLMode(aclMode);
    quota = null;
    transpProtocolsByScheme = Maps.newHashMap();
    transpProtocolsByID = Maps.newHashMap();
    transpProtocolsList = Lists.newArrayList();
    protocolPools = Lists.newArrayList();
    protocolPoolsByScheme = Maps.newHashMap();
    balancerByScheme = Maps.newHashMap();
    defaultACL = new DefaultACL();
  }

  public Capability() throws NamespaceException {

    this(ACLMode.UNDEF);
  }

  /**
   * *************************************************************************** BUILDING METHODs
   * **************************************************************************
   */
  public void setACLMode(ACLMode aclMode) throws NamespaceException {

    this.aclMode = aclMode;
  }

  /**
   * addProtocol
   *
   * @param prot Protocol
   */
  public void addTransportProtocolByScheme(Protocol protocol, TransportProtocol trasfProt) {

    transpProtocolsByScheme.put(protocol, trasfProt);
  }

  public void addProtocolPool(ProtocolPool protocolPool) {

    protocolPools.add(protocolPool);
  }

  public List<ProtocolPool> getProtocolPools() {

    return protocolPools;
  }

  public void addTransportProtocolByID(int protocolIndex, TransportProtocol trasfProt) {

    transpProtocolsByID.put(Integer.valueOf(protocolIndex), trasfProt);
  }

  public void addTransportProtocol(TransportProtocol trasfProt) {

    transpProtocolsList.add(trasfProt);
  }

  public void addACLEntry(ACLEntry aclEntry) {

    if (defaultACL == null) {
      defaultACL = new DefaultACL();
    }
    defaultACL.addACLEntry(aclEntry);
  }

  public void setQuota(Quota quota) {

    this.quota = quota;
  }

  public void addProtocolPoolBySchema(Protocol protocol, ProtocolPool protPool)
      throws NamespaceException {

    protocolPoolsByScheme.put(protocol, protPool);

    // Building Balancer and put it into Map of Balancers
    if (Protocol.GSIFTP.equals(protocol)
        || Protocol.HTTP.equals(protocol)
        || Protocol.HTTPS.equals(protocol)) {

      List<Node> nodeList = Lists.newLinkedList();
      Node node = null;
      boolean weighedPool = protPool.getBalanceStrategy().requireWeight();
      for (PoolMember member : protPool.getPoolMembers()) {
        String hostname = member.getMemberProtocol().getAuthority().getServiceHostname();
        int port = member.getMemberProtocol().getAuthority().getServicePort();
        int id = member.getMemberID();
        if (weighedPool) {
          try {
            node = buildNode(protocol, id, hostname, port, member.getMemberWeight());
          } catch (Exception e) {
            log.error("Unable to build a node for protocol " + protocol);
            throw new NamespaceException("Unable to build pool for protocol " + protocol);
          }
        } else {
          try {
            node = buildNode(protocol, id, hostname, port);
          } catch (Exception e) {
            log.error("Unable to build a node for protocol " + protocol);
            throw new NamespaceException("Unable to build pool for protocol " + protocol);
          }
        }
        nodeList.add(node);
      }

      try {
        BalancingStrategy balancingStrategy =
            BalancingStrategyFactory.getBalancingStrategy(protPool.getBalanceStrategy(), nodeList);
        balancerByScheme.put(protocol, balancingStrategy);
      } catch (IllegalArgumentException e) {
        log.error(
            "Unable to get {} balacing strategy for nodes {}",
            protPool.getBalanceStrategy().toString(),
            nodeList.toString());
        throw new NamespaceException("Unable to create a balancing schema from the protocol pool");
      }

    } else {
      log.error("The current version manage only GSIFTP.");
    }
  }

  private Node buildNode(Protocol protocol, int id, String hostname, int port) throws Exception {

    if (Protocol.GSIFTP.equals(protocol)) {
      return new FTPNode(id, hostname, port);
    }
    if (Protocol.HTTP.equals(protocol)) {
      return new HttpNode(id, hostname, port);
    }
    if (Protocol.HTTPS.equals(protocol)) {
      return new HttpsNode(id, hostname, port);
    }
    throw new Exception("Unsupported protocol, no node type available: " + protocol);
  }

  private Node buildNode(Protocol protocol, int id, String hostname, int port, int memberWeight)
      throws Exception {

    if (Protocol.GSIFTP.equals(protocol)) {
      return new FTPNode(id, hostname, port, memberWeight);
    }
    if (Protocol.HTTP.equals(protocol)) {
      return new HttpNode(id, hostname, port, memberWeight);
    }
    if (Protocol.HTTPS.equals(protocol)) {
      return new HttpsNode(id, hostname, port, memberWeight);
    }
    throw new Exception("Unsupported protocol, no node type available: " + protocol);
  }

  /**
   * *************************************************************************** READ METHODs
   * **************************************************************************
   */

  /**
   * getACLMode
   *
   * @return String
   */
  public ACLMode getACLMode() {

    return aclMode;
  }

  public Quota getQuota() {

    return this.quota;
  }

  public DefaultACL getDefaultACL() {

    return this.defaultACL;
  }

  /**
   * *************************************************************************** BUSINESS METHODs
   * **************************************************************************
   */
  public boolean isAllowedProtocol(String protocolScheme) {

    boolean result = false;
    /** @todo IMPLEMENT THIS! */
    return result;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    String sep = System.getProperty("line.separator");
    sb.append(sep + "  Cap.aclMode : '" + this.aclMode + "'" + sep);
    sb.append("  Cap.Protocol : " + sep);

    // Print TransportProtocol
    int count = 0;
    for (Map.Entry<Protocol, TransportProtocol> transP : transpProtocolsByScheme.entrySet()) {
      count++;
      sb.append("[TP(" + count + ")] " + (transP.getKey() + ": " + transP.getValue()));
    }
    // Print ProtocolPool
    count = 0;
    for (Map.Entry<Protocol, ProtocolPool> protPool : protocolPoolsByScheme.entrySet()) {
      count++;
      sb.append("[TP(" + count + ")] " + (protPool.getKey() + ": " + protPool.getValue()));
    }
    return sb.toString();
  }

  /**
   * **************************************** VERSION 1.4 *
   * *****************************************
   */
  public ProtocolPool getPoolByScheme(Protocol protocol) {

    ProtocolPool poll = null;
    boolean isPresent = protocolPoolsByScheme.containsKey(protocol);
    if (isPresent) {
      poll = protocolPoolsByScheme.get(protocol);
    }
    return poll;
  }

  public BalancingStrategy getBalancingStrategyByScheme(Protocol protocol) {

    if (balancerByScheme.containsKey(protocol)) {
      return balancerByScheme.get(protocol);
    }
    return null;
  }

  public List<TransportProtocol> getManagedProtocolByScheme(Protocol protocol) {

    List<TransportProtocol> result = Lists.newArrayList();
    transpProtocolsList.forEach(
        tp -> {
          if (tp.getProtocol().equals(protocol)) {
            result.add(tp);
          }
        });
    return result;
  }

  public List<Protocol> getAllManagedProtocols() {

    return Lists.newArrayList(transpProtocolsByScheme.keySet());
  }

  public boolean isPooledProtocol(Protocol protocol) {

    return protocolPoolsByScheme.containsKey(protocol);
  }

  public TransportProtocol getProtocolByID(int id) {

    if (transpProtocolsByID.containsKey(id)) {
      return transpProtocolsByID.get(id);
    }
    return null;
  }
}
