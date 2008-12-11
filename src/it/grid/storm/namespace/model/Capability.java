package it.grid.storm.namespace.model;

import java.util.*;

import org.apache.commons.logging.*;
import it.grid.storm.namespace.*;
import it.grid.storm.balancer.Balancer;

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
    private List<TransportProtocol> transfProtocols; // List of TransportProtocol.
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
    public void addTransportProtocol(TransportProtocol trasfProt) {
        if (transfProtocols == null) {
            transfProtocols = new ArrayList<TransportProtocol>();
        }
        transfProtocols.add(trasfProt);
    }

    /**
     * addACL Entry
     *
     * @param prot Protocol
     */
    public void addACLEntry(ACLEntry aclEntry) {
        if (defaultACL == null) {
            defaultACL = new DefaultACL();
        }
        defaultACL.addACLEntry(aclEntry);
    }

    /**
     *
     * @param quota Quota
     */
    public void setQuota(Quota quota) {
      this.quota = quota;
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

    /**
     * getManagedProtocols
     *
     * @return Collection
     */
    public List getManagedProtocols() {
        return this.transfProtocols;
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
        Iterator scan = transfProtocols.iterator();
        while (scan.hasNext()) {
            sb.append("    " + (TransportProtocol) scan.next() + sep);
        }
        return sb.toString();
    }

    /******************************************
     *           VERSION 1.4                  *
  *******************************************/

  public Balancer getPool() {
    /** @todo IMPLEMENT */
    return null;
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
