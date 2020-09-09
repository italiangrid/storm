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

/**
 * 
 */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.SubjectAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zappi
 * 
 */
public class DNPattern implements SubjectPattern {

  private static final String ADMIT_ALL = ".*";

  private String dnPatternString = null;

  private String countryPatternString = null;
  private String organizationPatternString = null;
  private String organizationalUnitPatternString = null;
  private String localityPatternString = null;
  private String commonNamePatternString = null;
  private String domainComponentPatternString = null;

  private Pattern countryPattern = null;
  private Pattern organizationPattern = null;
  private Pattern organizationalUnitPattern = null;
  private Pattern localityPattern = null;
  private Pattern commonNamePattern = null;
  private Pattern domainComponentPattern = null;

  private static Pattern dnWildcardPattern = Pattern.compile("");

  protected boolean checkValidity;

  /**
   * CONSTRUCTOR
   */

  public DNPattern(String dnPatternString) throws AuthzDBReaderException {

    this(dnPatternString, true);
  }

  public DNPattern(String dnPatternString, boolean checkValidity) throws AuthzDBReaderException {

    this.dnPatternString = dnPatternString;
    this.checkValidity = checkValidity;
    if (isValidPattern()) {
      generatePattern();
    }
  }

  private void generatePattern() {

    if ((this.dnPatternString == null) || (this.dnPatternString.equals("*"))) {
      init("*", "*", "*", "*", "*", "*");
    } else {
      // Split the rule into the attribute rules
      String[] rules = this.dnPatternString.split("/");
      if (rules != null) {
        int length = rules.length;
        for (int i = 0; i < length; i++) {
          if (rules[i].startsWith("C=")) {
            countryPatternString = rules[i].substring(2, rules[i].length());
          }
          if (rules[i].startsWith("O=")) {
            organizationPatternString = rules[i].substring(2, rules[i].length());
          }
          if (rules[i].startsWith("OU=")) {
            organizationalUnitPatternString = rules[i].substring(3, rules[i].length());
          }
          if (rules[i].startsWith("L=")) {
            localityPatternString = rules[i].substring(2, rules[i].length());
          }
          if (rules[i].startsWith("CN=")) {
            commonNamePatternString = rules[i].substring(3, rules[i].length());
          }
          if (rules[i].startsWith("DC=")) {
            domainComponentPatternString = rules[i].substring(3, rules[i].length());
          }

        }
      } else {
        countryPatternString = ADMIT_ALL;
        organizationPatternString = ADMIT_ALL;
        organizationalUnitPatternString = ADMIT_ALL;
        localityPatternString = ADMIT_ALL;
        commonNamePatternString = ADMIT_ALL;
        domainComponentPatternString = ADMIT_ALL;
      }
      init(countryPatternString, organizationPatternString, organizationalUnitPatternString,
          localityPatternString, commonNamePatternString, domainComponentPatternString);
    }
  }

  /**
   * private method used to initialize everything
   * 
   * @param countryPatternString String
   * @param organizationPatternString String
   * @param organizationalUnitPatternString String
   * @param localityPatternString String
   * @param commonNameString String
   */
  protected void init(String countryPatternString, String organizationPatternString,
      String organizationalUnitPatternString, String localityPatternString,
      String commonNamePatternString, String domainComponentPatternString) {

    this.countryPatternString = countryPatternString;
    this.organizationPatternString = organizationPatternString;
    this.organizationalUnitPatternString = organizationalUnitPatternString;
    this.localityPatternString = localityPatternString;
    this.commonNamePatternString = commonNamePatternString;
    this.domainComponentPatternString = domainComponentPatternString;

    // C country
    if (countryPatternString != null) {
      if (countryPatternString.equals("*")) {
        this.countryPatternString = ".*";
      }
      countryPattern = Pattern.compile(this.countryPatternString);
    } else {
      countryPatternString = ADMIT_ALL;
      countryPattern = Pattern.compile(ADMIT_ALL);
    }

    // O organization
    if (organizationPatternString != null) {
      if (organizationPatternString.equals("*")) {
        this.organizationPatternString = ".*";
      }
      organizationPattern = Pattern.compile(this.organizationPatternString);
    } else {
      organizationPatternString = ADMIT_ALL;
      organizationPattern = Pattern.compile(ADMIT_ALL);
    }

    // OU organizationalUnit
    if (organizationalUnitPatternString != null) {
      if (organizationalUnitPatternString.equals("*")) {
        this.organizationalUnitPatternString = ".*";
      }
      organizationalUnitPattern = Pattern.compile(this.organizationalUnitPatternString);
    } else {
      organizationalUnitPatternString = ADMIT_ALL;
      organizationalUnitPattern = Pattern.compile(ADMIT_ALL);
    }

    // L locality
    if (localityPatternString != null) {
      if (localityPatternString.equals("*")) {
        this.localityPatternString = ".*";
      }
      localityPattern = Pattern.compile(this.localityPatternString);
    } else {
      localityPatternString = ADMIT_ALL;
      localityPattern = Pattern.compile(ADMIT_ALL);
    }

    // CN Common Name
    if (commonNamePatternString != null) {
      if (commonNamePatternString.equals("*")) {
        this.commonNamePatternString = ".*";
      }
      commonNamePattern = Pattern.compile(this.commonNamePatternString);
    } else {
      commonNamePatternString = ADMIT_ALL;
      commonNamePattern = Pattern.compile(ADMIT_ALL);
    }

    // DC Domain Component
    if (domainComponentPatternString != null) {
      if (domainComponentPatternString.equals("*")) {
        this.domainComponentPatternString = ".*";
      }
      domainComponentPattern = Pattern.compile(this.domainComponentPatternString);
    } else {
      domainComponentPatternString = ADMIT_ALL;
      domainComponentPattern = Pattern.compile(ADMIT_ALL);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.authz.sa.model.SubjectPattern#match(it.grid.storm.griduser .FQAN)
   */
  // @Override
  public boolean match(SubjectAttribute subjectAttribute) {

    boolean result = false;
    if (subjectAttribute instanceof DistinguishedName) {
      DistinguishedName dn = (DistinguishedName) subjectAttribute;

      boolean countryMatch = false;
      boolean organizationMatch = false;
      boolean localityMatch = false;
      boolean organizationalUnitMatch = false;
      boolean commonNameMatch = false;
      boolean domainComponentMatch = false;

      // C
      String countryName = dn.getCountryName();
      if (countryName != null) {
        CharSequence country = countryName.subSequence(0, countryName.length());
        Matcher countryMatcher = countryPattern.matcher(country);
        countryMatch = countryMatcher.find();
      } else {
        countryMatch = countryPatternString.equals(ADMIT_ALL);
      }
      if (!(countryMatch)) {
        return false;
      }

      // O
      String organizationName = dn.getOrganizationName();
      if (organizationName != null) {
        CharSequence organization = organizationName.subSequence(0, organizationName.length());
        Matcher organizationMatcher = organizationPattern.matcher(organization);
        organizationMatch = organizationMatcher.find();
      } else {
        organizationMatch = organizationPatternString.equals(ADMIT_ALL);
      }
      if (!(organizationMatch)) {
        return false;
      }

      // L
      String localityName = dn.getLocalityName();
      if (localityName != null) {
        CharSequence locality = localityName.subSequence(0, localityName.length());
        Matcher localityMatcher = localityPattern.matcher(locality);
        localityMatch = localityMatcher.find();
      } else {
        localityMatch = localityPatternString.equals(ADMIT_ALL);
      }
      if (!(localityMatch)) {
        return false;
      }

      // OU ArrayList
      ArrayList<String> organizationalUnitNames = dn.getOrganizationalUnitNames();
      if ((organizationalUnitNames != null) && (!(organizationalUnitNames.isEmpty()))) {
        CharSequence organizationalUnit = null;
        String nameStr = null;
        Matcher organizationalUnitMatcher = null;
        for (Iterator<String> name = organizationalUnitNames.iterator(); name.hasNext();) {
          nameStr = name.next();
          organizationalUnit = nameStr.subSequence(0, nameStr.length());
          organizationalUnitMatcher = organizationalUnitPattern.matcher(organizationalUnit);
          organizationalUnitMatch = organizationalUnitMatcher.find();
          if (organizationalUnitMatch) {
            break;
          }
        }
      } else {
        organizationalUnitMatch = organizationalUnitPatternString.equals(ADMIT_ALL);
      }
      if (!(organizationalUnitMatch)) {
        return false;
      }

      // CN ArrayList
      ArrayList<String> commonNames = dn.getCommonNames();
      if ((commonNames != null) && (!(commonNames.isEmpty()))) {
        CharSequence commonName = null;
        String commonNameStr = null;
        Matcher commonNameMatcher = null;
        for (Iterator<String> scanCN = commonNames.iterator(); scanCN.hasNext();) {
          commonNameStr = scanCN.next();
          commonName = commonNameStr.subSequence(0, commonNameStr.length());
          commonNameMatcher = commonNamePattern.matcher(commonName);
          commonNameMatch = commonNameMatcher.find();
          if (commonNameMatch) {
            break;
          }
        }
      } else {
        commonNameMatch = commonNamePatternString.equals(ADMIT_ALL);
      }
      if (!(commonNameMatch)) {
        return false;
      }

      // DC ArrayList
      ArrayList<String> domainComponents = dn.getDomainComponents();
      if ((domainComponents != null) && (!(domainComponents.isEmpty()))) {
        CharSequence domainComponent = null;
        String domainComponentStr = null;
        Matcher domainComponentMatcher = null;
        for (Iterator<String> scanDC = domainComponents.iterator(); scanDC.hasNext();) {
          domainComponentStr = scanDC.next();
          domainComponent = domainComponentStr.subSequence(0, domainComponentStr.length());
          domainComponentMatcher = domainComponentPattern.matcher(domainComponent);
          domainComponentMatch = domainComponentMatcher.find();
          if (domainComponentMatch) {
            break;
          }
        }
      } else {
        domainComponentMatch = commonNamePatternString.equals(ADMIT_ALL);
      }
      if (!(domainComponentMatch)) {
        return false;
      }

      // Total Result
      // NOTE : At this point result should be always TRUE!
      result = countryMatch && organizationMatch && organizationalUnitMatch && localityMatch
          && commonNameMatch && domainComponentMatch;
      return result;
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.grid.storm.authz.sa.model.SubjectPattern#validatePattern()
   */
  // @Override
  public boolean isValidPattern() throws AuthzDBReaderException {

    // Matches to the specification.
    Matcher m = DNPattern.dnWildcardPattern.matcher(dnPatternString);
    if (!m.matches()) {
      if (checkValidity) {
        throw new IllegalArgumentException("DN '" + dnPatternString + "'");
      }
      return false;
    }
    return true;
  }
}
