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

import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.VirtualFSInterface;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

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
public class ApproachableRule implements Comparable<ApproachableRule> {

  private Logger log = NamespaceDirector.getLogger();

  private final String ruleName;
  private final SubjectRules subjectRules;

  private String relativePath = null;
  private LinkedList<VirtualFSInterface> appFS = new LinkedList<VirtualFSInterface>();

  private final boolean anonymousHttpReadAccess;

  public ApproachableRule(String rulename, SubjectRules subjectRules, String relativePath,
      boolean anonymousHttpReadAccess) {

    this.ruleName = rulename;
    this.subjectRules = subjectRules;
    this.relativePath = relativePath;
    this.anonymousHttpReadAccess = anonymousHttpReadAccess;
  }

  public ApproachableRule(String rulename, SubjectRules subjectRules, String relativePath) {

    this.ruleName = rulename;
    this.subjectRules = subjectRules;
    this.relativePath = relativePath;
    this.anonymousHttpReadAccess = false;
  }

  public boolean isAdmitAll() {

    return subjectRules.getDNMatchingRule().isMatchAll()
        && subjectRules.getVONameMatchingRule().isMatchAll();
  }

  public void addApproachableVFS(VirtualFSInterface vfs) {

    this.appFS.add(vfs);
  }

  public List<VirtualFSInterface> getApproachableVFS() {

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

  public boolean getAnonymousHttpReadAccess() {

    return this.anonymousHttpReadAccess;
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

    return matchDN(gUser.getDn()) && matchVoms(gUser);
  }

  private boolean matchVoms(GridUserInterface gUser) {

    // ---- Check if VOMS Attributes are required ----
    if (subjectRules.getVONameMatchingRule().isMatchAll()) {
      return true;
    }
    // VOMS Attribute required.
    if (gUser instanceof AbstractGridUser && ((AbstractGridUser) gUser).hasVoms()) {
      log.debug("Grid User Requestor   : " + ((AbstractGridUser) gUser).toString());
      if (subjectRules.getVONameMatchingRule()
        .match(((AbstractGridUser) gUser).getVO().getValue())) {
        return true;
      }
    }
    return false;
  }

  private boolean matchDN(String dnString) {

    if (dnString == null) {
      return subjectRules.getDNMatchingRule().isMatchAll();
    }
    DistinguishedName dn = new DistinguishedName(dnString);
    return subjectRules.getDNMatchingRule().match(dn);
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    String sep = System.getProperty("line.separator");
    sb.append(sep + "  --- APPROACHABLE RULE NAME ---" + sep);
    sb.append("   Approachable Rule Name : " + this.ruleName + sep);
    sb.append("     SUBJECT - dn         : " + this.getSubjectRules().getDNMatchingRule() + sep);
    if (!this.getSubjectRules().getVONameMatchingRule().isMatchAll()) {
      sb.append("     -- VOMS cert IS MANDATORY!" + sep);
      sb.append("       -- SUBJECT - vo_name    : " + this.getSubjectRules().getVONameMatchingRule()
          + sep);
    } else {
      sb.append("     -- VOMS cert is not mandatory" + sep);
    }
    sb.append("     Relative-Path for Space : " + this.getSpaceRelativePath() + sep);
    sb.append("     Approachable VFS        : " + this.appFS + sep);
    return sb.toString();
  }

  public int compareTo(ApproachableRule o) {

    return this.getRuleName().compareTo(o.getRuleName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((appFS == null) ? 0 : appFS.hashCode());
    result = prime * result + ((log == null) ? 0 : log.hashCode());
    result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
    result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
    result = prime * result + ((subjectRules == null) ? 0 : subjectRules.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ApproachableRule other = (ApproachableRule) obj;
    if (appFS == null) {
      if (other.appFS != null) {
        return false;
      }
    } else if (!appFS.equals(other.appFS)) {
      return false;
    }
    if (log == null) {
      if (other.log != null) {
        return false;
      }
    } else if (!log.equals(other.log)) {
      return false;
    }
    if (relativePath == null) {
      if (other.relativePath != null) {
        return false;
      }
    } else if (!relativePath.equals(other.relativePath)) {
      return false;
    }
    if (ruleName == null) {
      if (other.ruleName != null) {
        return false;
      }
    } else if (!ruleName.equals(other.ruleName)) {
      return false;
    }
    if (subjectRules == null) {
      if (other.subjectRules != null) {
        return false;
      }
    } else if (!subjectRules.equals(other.subjectRules)) {
      return false;
    }
    return true;
  }
}
