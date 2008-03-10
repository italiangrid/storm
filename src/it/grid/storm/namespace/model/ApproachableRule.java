package it.grid.storm.namespace.model;

import java.util.*;

import it.grid.storm.namespace.*;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.DNMatchingRule;
import it.grid.storm.griduser.VONameMatchingRule;
import it.grid.storm.griduser.VomsGridUser;
import org.apache.commons.logging.Log;

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
public class ApproachableRule implements Comparable{

    private Log log = NamespaceDirector.getLogger();

    private String ruleName = null;
    private SubjectRules subjectRules = null;
    private DNMatchingRule dnMatchingRule = null;
    private VONameMatchingRule voNameMatchingRule = null;

    private boolean vomsCertRequired = false;

    private String relativePath = null;
    private Vector appFS = new Vector();


    public ApproachableRule(String rulename, SubjectRules subjectRules, String relativePath) throws NamespaceException {
        this.ruleName = rulename;
        this.subjectRules = subjectRules;
        dnMatchingRule = subjectRules.getDNMatchingRule();
        voNameMatchingRule = subjectRules.getVONameMatchingRule();
        vomsCertRequired = subjectRules.isVomsCertRequired();
        /**
         *     @todo : Check if relative Path is a path well formed.
         */
        this.relativePath = relativePath;
    }

    public ApproachableRule(String rulename, String relativePath) throws NamespaceException {
        this.ruleName = rulename;
        /**
         *     @todo : Check if relative Path is a path well formed.
         */
        this.relativePath = relativePath;
    }


    public void setSubject(String dn, String vo_name) {
        this.subjectRules = new SubjectRules(dn, vo_name);
    }

    public void setSubject(String dn) {
        this.subjectRules = new SubjectRules(dn);
    }

    public void addApproachableVFS(String vfsName) {
        this.appFS.add(vfsName);
    }

    /**
     * setApproachableVFSList
     *
     * @param appFS List
     */
    public void setApproachableVFSList(List appFS) {
        this.appFS = new Vector(appFS);
    }

    public List getApproachableVFS() {
        return this.appFS;
    }

    /**
     * getSpaceRelativePath
     *
     * @return String
     */
    public String getSpaceRelativePath() {
        return relativePath;
    }

    /**
     *
     * @return String
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     *
     * @return Subject
     */
    public SubjectRules getSubjectRules() {
        return this.subjectRules;
    }


    /**
     * MAIN METHOD
     *
     * @param gUser GridUserInterface
     * @return boolean
     */
    public boolean match(GridUserInterface gUser) {
         String dnString = gUser.getDn();
         VomsGridUser vomsUser = null;
         DistinguishedName dn = new DistinguishedName(dnString);
         boolean dnMatch = dnMatchingRule.match(dn);
         if (!dnMatch) {
           return false;
         }
         // Here DN is Matching
         if (!vomsCertRequired) {
           return true;
         }
         // Check
         /**
          * @todo Casting to VomsGridUser as well user is a normal Grid User.
          * Change it!
          */
         vomsUser = (VomsGridUser)gUser;
         log.debug("Grid User Requestor   : "+vomsUser.toString());
         log.debug("  holds a VOMS cert ? : "+vomsUser.hasVoms());
         if (vomsUser.hasVoms()) {
             boolean voNameMatch = voNameMatchingRule.match(vomsUser.getMainVo().getValue());
             if (voNameMatch) {
               return true;
             }
         } else {
             // The subject does not hold a VOMS Certificate (which is mandatory!)
             return false;
         }
         return false;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = System.getProperty("line.separator");
        sb.append(sep + "  --- APPROACHABLE RULE NAME ---" + sep);
        sb.append("   Approachable Rule Name : " + this.ruleName + sep);
        sb.append("     SUBJECT - dn         : " + this.getSubjectRules().getDNRule() + sep);
        if (this.getSubjectRules().isVomsCertRequired()) {
            sb.append("     -- VOMS cert IS MANDATORY!" + sep);
            sb.append("       -- SUBJECT - vo_name    : " + this.getSubjectRules().getVONameRule() + sep);
        } else {
            sb.append("     -- VOMS cert is not mandatory" + sep);
        }
        sb.append("     Relative-Path for Space : " + this.getSpaceRelativePath() + sep);
        sb.append("     Approachable VFS        : " + this.appFS + sep);
        return sb.toString();
    }


  public int compareTo( Object o ) {
    int result = 1;
    if ( o instanceof ApproachableRule ) {
      ApproachableRule other = ( ApproachableRule ) o;
      result = (this.getRuleName()).compareTo(other.getRuleName());
    }
    return result;
  }


  public boolean equals( Object o) {
    boolean result = false;
    if ( o instanceof ApproachableRule ) {
      ApproachableRule other = ( ApproachableRule ) o;
      if ( other.getRuleName().equals( this.getRuleName() ) ) {
        result = true;
      }
    }
    return result;
  }


}
