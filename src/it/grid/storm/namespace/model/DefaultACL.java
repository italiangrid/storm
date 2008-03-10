package it.grid.storm.namespace.model;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;

public class DefaultACL {

  private static final Logger LOG = Logger.getLogger("namespace");
  private List<ACLEntry> acl = new ArrayList<ACLEntry>();

  /**
   *
   */
  public DefaultACL() {
    super();
  }

  /**
   *
   * @param aclEntry ACLEntry
   */
  public void addACLEntry(ACLEntry aclEntry) {
    acl.add(aclEntry);
    if (LOG.getEffectiveLevel().equals(Level.DEBUG)) {
      LOG.debug("Added to Default ACL : " + aclEntry );
    }
  }

  /**
   *
   * @return boolean
   */
  public boolean isEmpty() {
    return acl.isEmpty();
  }

  /**
   *
   * @return List
   */
  public List<ACLEntry> getACL() {
    return acl;
  }

  /**
   *
   * @return String
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i<acl.size(); i++) {
      sb.append("ACL["+i+"] = ( ").append(acl.get(i)).append( ") \n");
    }
    return sb.toString();
  }

}
