package it.grid.storm.namespace.model;

import java.util.*;

import org.apache.commons.logging.*;
import it.grid.storm.namespace.*;
import it.grid.storm.balancer.Balancer;
import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.ftp.FTPNode;
import it.grid.storm.balancer.StrategyType;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */

public class Capability implements CapabilityInterface {

    private Log log = NamespaceDirector.getLogger();
    private ACLMode aclMode = ACLMode.UNDEF;
    private Quota quota = null;
    // List of TransportProtocol by Protocol.
    private Map<Protocol, TransportProtocol> transpProtocolsByScheme = new Hashtable<Protocol,TransportProtocol>();
    // List of TransportProtocol by Protocol.
    private Map<Integer, TransportProtocol> transpProtocolsByID = new Hashtable<Integer,TransportProtocol>();
    // List of TransportProtocol by Entering order.
    private List<TransportProtocol> transpProtocolsList = new ArrayList<TransportProtocol>();
    //List of ProtocolPool.
    private Map<Protocol, ProtocolPool> protocolPoolsByScheme = new Hashtable<Protocol,ProtocolPool>();
    //List of Balancer.
    private Map<Protocol, Balancer<? extends Node>> balancerByScheme = new Hashtable<Protocol,Balancer<? extends Node>>();

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
     *  BUILDING METHODs
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
        transpProtocolsByScheme.put(protocol,trasfProt);
    }

    public void addTransportProtocolByID(int protocolIndex, TransportProtocol trasfProt) {
        transpProtocolsByID.put(new Integer(protocolIndex),trasfProt);
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

    public void addProtocolPoolBySchema(Protocol protocol, ProtocolPool protPool) {
      protocolPoolsByScheme.put(protocol, protPool);
      //Building Balancer and put it into Map of Balancers
      Balancer balancer = null;
      if (protocol.equals(Protocol.GSIFTP)) {
        balancer = new Balancer<FTPNode>();
        StrategyType strat = null;
        if (protPool.getBalanceStrategy().equals("round-robin"))
          strat = StrategyType.ROUNDROBIN;
        else if (protPool.getBalanceStrategy().equals("random"))
          strat = StrategyType.ROUNDROBIN;
        else if (protPool.getBalanceStrategy().equals("weight"))
          strat = StrategyType.ROUNDROBIN;
        else  {
          log.error("The current version manage only 'round-robin', 'random' and 'weight' strategy");
        }
        balancer.setStrategy(strat);
        for (PoolMember member: protPool.getPoolMembers()) {
          String hostname = member.getMemeberProtocol().getAuthority().getServiceHostname();
          int port =  member.getMemeberProtocol().getAuthority().getServicePort();
          int weight = member.getMemberWeight();
          
          log.debug("toooreemove member ppol: "+hostname );
          FTPNode ftpNode = new FTPNode(hostname, port, weight);
          balancer.addElement(ftpNode);
        }
        balancerByScheme.put(protocol,balancer);
      } else {
        log.error("The current version manage only GSIFTP POOL.");
      }
    }

    /*****************************************************************************
     *  READ METHODs
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
     *  BUSINESS METHODs
     ****************************************************************************/


    public boolean isAllowedProtocol(String protocolScheme) {
        boolean result = false;
        /**
         * @todo IMPLEMENT THIS!
         */
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = System.getProperty("line.separator");
        sb.append(sep + "  Cap.aclMode : '" + this.aclMode + "'" + sep);
        sb.append("  Cap.Protocol : " + sep);

        /**
        for (Map.Entry entry : m.entrySet()) {
          String key = entry.getKey();
          Vector value = entry.getValue();
        }
        **/

       // Print TransportProtocol
       int count = 0;
       for (Map.Entry<Protocol, TransportProtocol> transP : transpProtocolsByScheme.entrySet()) {
         count++;
         sb.append("[TP("+count+")] " + (transP.getKey() + ": " + transP.getValue()));
       }
       // Print ProtocolPool
       count = 0;
       for (Map.Entry<Protocol, ProtocolPool> protPool : protocolPoolsByScheme.entrySet()) {
         count++;
         sb.append("[TP("+count+")] " + (protPool.getKey() + ": " + protPool.getValue()));
       }
        return sb.toString();
    }

    /******************************************
     *           VERSION 1.4                  *
  *******************************************/

  public Balancer<? extends Node> getPoolByScheme(Protocol protocol) {
    Balancer balancer = null;
    boolean isPresent = balancerByScheme.containsKey(protocol);
    if (isPresent) {
      balancer = balancerByScheme.get(protocol);
    }
    return balancer;
  }

  public List<TransportProtocol> getManagedProtocolByScheme(Protocol protocol) {
    List<TransportProtocol> result = new ArrayList<TransportProtocol>();
    for (TransportProtocol tp: transpProtocolsList) {
      if (tp.getProtocol().equals(protocol)) {
        result.add(tp);
      }
    }
    return result;
  }

  public List<Protocol> getAllManagedProtocols() {
    List<Protocol> result = new ArrayList<Protocol>();
    result.addAll(transpProtocolsByScheme.keySet());
    return result;
  }

  public boolean isPooledProtocol(Protocol protocol) {
    boolean result = false;
    result = protocolPoolsByScheme.containsKey(protocol);
    return result;
  }

  public TransportProtocol getProtocolByID(int id) {
    TransportProtocol tProt = null;
    boolean isPresent = transpProtocolsByID.containsKey(id); //Use of generics AUTO-BOXING
    if (isPresent) {
      tProt = transpProtocolsByID.get(id);
    }
    return tProt;
  }

  /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2006</p>
     *
     * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
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
            }
            else if (aclMode.toLowerCase().equals(ACLMode.JUST_IN_TIME.toString().toLowerCase())) {
                result = ACLMode.JUST_IN_TIME;
            }
            else {
                throw new NamespaceException("ACL Mode is not recognized!");
            }
            return result;
        }

        public String toString() {
            return aclMode;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof ACLMode) {
                ACLMode aclMode = (ACLMode) obj;
                if (aclMode.toString().toLowerCase().equals(this.toString().toLowerCase())) {
                    return true;
                }
            }
            else {
                return false;
            }
            return false;
        }

    }

}
