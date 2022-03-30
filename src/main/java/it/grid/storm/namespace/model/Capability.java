/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.namespace.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.node.FTPNode;
import it.grid.storm.balancer.node.HttpNode;
import it.grid.storm.balancer.node.HttpsNode;
import it.grid.storm.balancer.strategy.BalancingStrategyFactory;
import it.grid.storm.namespace.CapabilityInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF and ICTP/eGrid project
 * </p>
 * 
 * @author Riccardo Zappi
 * @version 1.0
 */

public class Capability implements CapabilityInterface {

  private Logger log = NamespaceDirector.getLogger();
  private ACLMode aclMode = ACLMode.UNDEF;
  private Quota quota = null;
  // List of TransportProtocol by Protocol.
  private Map<Protocol, TransportProtocol> transpProtocolsByScheme =
      new Hashtable<Protocol, TransportProtocol>();
  // List of TransportProtocol by Protocol.
  private Map<Integer, TransportProtocol> transpProtocolsByID =
      new Hashtable<Integer, TransportProtocol>();
  // List of TransportProtocol by Entering order.
  private List<TransportProtocol> transpProtocolsList = new ArrayList<TransportProtocol>();
  // List of ProtocolPool.
  private Map<Protocol, ProtocolPool> protocolPoolsByScheme =
      new Hashtable<Protocol, ProtocolPool>();
  // List of Balancer.
  private Map<Protocol, BalancingStrategy<? extends Node>> balancerByScheme =
      new Hashtable<Protocol, BalancingStrategy<? extends Node>>();

  private DefaultACL defaultACL = new DefaultACL();

  /**
   * Constructor
   * 
   */
  public Capability(String aclMode) throws NamespaceException {

    setACLMode(aclMode);
  }

  public Capability() throws NamespaceException {

  }

  /*****************************************************************************
   * BUILDING METHODs
   ****************************************************************************/

  public void setACLMode(String aclMode) throws NamespaceException {

    this.aclMode = ACLMode.makeFromString(aclMode);
  }

  /**
   * addProtocol
   * 
   * @param prot Protocol
   */
  public void addTransportProtocolByScheme(Protocol protocol, TransportProtocol trasfProt) {

    transpProtocolsByScheme.put(protocol, trasfProt);
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
    if (protocol.equals(Protocol.GSIFTP)) {
      BalancingStrategy<? extends Node> balancingStrategy = null;
      LinkedList<Node> nodeList = new LinkedList<Node>();
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
        balancingStrategy =
            BalancingStrategyFactory.getBalancingStrategy(protPool.getBalanceStrategy(), nodeList);
      } catch (IllegalArgumentException e) {
        log.error("Unable to get " + protPool.getBalanceStrategy().toString()
            + " balacing strategy for nodes " + nodeList.toString());
        throw new NamespaceException("Unable to create a balancing schema from the protocol pool");
      }
      balancerByScheme.put(protocol, balancingStrategy);
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

  /*****************************************************************************
   * READ METHODs
   ****************************************************************************/

  /**
   * getACLMode
   * 
   * @return String
   */
  public Capability.ACLMode getACLMode() {

    return aclMode;
  }

  public Quota getQuota() {

    return this.quota;
  }

  public DefaultACL getDefaultACL() {

    return this.defaultACL;
  }

  /*****************************************************************************
   * BUSINESS METHODs
   ****************************************************************************/

  public boolean isAllowedProtocol(String protocolScheme) {

    boolean result = false;
    /**
     * @todo IMPLEMENT THIS!
     */
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

  /******************************************
   * VERSION 1.4 *
   *******************************************/

  public ProtocolPool getPoolByScheme(Protocol protocol) {

    ProtocolPool poll = null;
    boolean isPresent = protocolPoolsByScheme.containsKey(protocol);
    if (isPresent) {
      poll = protocolPoolsByScheme.get(protocol);
    }
    return poll;
  }

  public BalancingStrategy<? extends Node> getBalancingStrategyByScheme(Protocol protocol) {

    if (balancerByScheme.containsKey(protocol)) {
      return balancerByScheme.get(protocol);
    }
    return null;
  }

  public List<TransportProtocol> getManagedProtocolByScheme(Protocol protocol) {

    List<TransportProtocol> result = Lists.newArrayList();
    transpProtocolsList.forEach(tp -> {
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

  /**
   * 
   * <p>
   * Title:
   * </p>
   * 
   * <p>
   * Description:
   * </p>
   * 
   * <p>
   * Copyright: Copyright (c) 2006
   * </p>
   * 
   * <p>
   * Company: INFN-CNAF and ICTP/eGrid project
   * </p>
   * 
   * @author Riccardo Zappi
   * @version 1.0
   */
  public static class ACLMode {

    public static final ACLMode JUST_IN_TIME = new ACLMode("JiT");
    public static final ACLMode AHEAD_OF_TIME = new ACLMode("AoT");
    public static final ACLMode UNDEF = new ACLMode("UNDEF");

    private String aclMode;

    private ACLMode(String mode) {

      this.aclMode = mode;
    }

    private static ACLMode makeFromString(String aclMode) throws NamespaceException {

      ACLMode result = ACLMode.UNDEF;
      if (aclMode.toLowerCase().equals(ACLMode.AHEAD_OF_TIME.toString().toLowerCase())) {
        result = ACLMode.AHEAD_OF_TIME;
      } else if (aclMode.toLowerCase().equals(ACLMode.JUST_IN_TIME.toString().toLowerCase())) {
        result = ACLMode.JUST_IN_TIME;
      } else {
        throw new NamespaceException("ACL Mode is not recognized!");
      }
      return result;
    }

    @Override
    public String toString() {

      return aclMode;
    }

    @Override
    public boolean equals(Object obj) {

      if (obj == null) {
        return false;
      }
      if (obj instanceof ACLMode) {
        ACLMode aclMode = (ACLMode) obj;
        if (aclMode.toString().toLowerCase().equals(this.toString().toLowerCase())) {
          return true;
        }
      } else {
        return false;
      }
      return false;
    }

    @Override
    public int hashCode() {

      int result = 17;
      result = 31 * result + (aclMode != null ? aclMode.hashCode() : 0);
      return result;
    }

  }

}
