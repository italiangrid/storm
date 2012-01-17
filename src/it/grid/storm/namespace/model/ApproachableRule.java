/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.namespace.model;

import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.DNMatchingRule;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VONameMatchingRule;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;

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

    private Logger log = NamespaceDirector.getLogger();

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
    public boolean match(GridUserInterface gUser)
    {
        boolean response = false;
        String dnString = gUser.getDn();
        GridUserInterface gridUser = null;
        DistinguishedName dn = new DistinguishedName(dnString);
        boolean dnMatch = dnMatchingRule.match(dn);
        if (dnMatch)
        { // DN Match.
            // ---- Check if VOMS Attributes are required ----
            if (!vomsCertRequired)
            {
                response = true; // VOMS Attributes aren't required.
            }
            else
            {
                // VOMS Attribute required.
                // ---- Check if gUSER is a USER with VOMS Attributes ----
                if (gUser instanceof AbstractGridUser && ((AbstractGridUser)gUser).hasVoms())
                {
                    AbstractGridUser absGridUser = (AbstractGridUser) gUser;
                    log.debug("Grid User Requestor   : " + absGridUser.toString());
                    boolean voNameMatch = voNameMatchingRule.match(absGridUser.getVO().getValue());
                    if (voNameMatch)
                    {
                        response = true;
                    }
                }
            }
        }
        return response;
    }

    @Override
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


    @Override
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

    
    @Override
    public int hashCode() {
    	int result = 17;
    	result = 31 * result + (ruleName!=null?ruleName.hashCode():0);
    	result = 31 * result + (subjectRules!=null?subjectRules.hashCode():0);
    	result = 31 * result + (dnMatchingRule!=null?dnMatchingRule.hashCode():0);
    	result = 31 * result + (voNameMatchingRule!=null?voNameMatchingRule.hashCode():0);
    	return result;
    }

}
