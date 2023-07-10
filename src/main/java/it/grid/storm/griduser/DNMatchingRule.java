/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 *
 * http://www.cnaf.infn.it/license.html
 *
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 *
 * $Id: DNMatchingRule.java,v 1.4 2007/05/22 19:54:54 rzappi Exp $
 */

package it.grid.storm.griduser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DNMatchingRule {

  private static final Logger log = LoggerFactory.getLogger(DNMatchingRule.class);

  private enum DNFields {
    COUNTRY("C"),
    ORGANIZATION("O"),
    ORGANIZATIONALUNIT("OU"),
    LOCALITY("L"),
    COMMONNAME("CN"),
    DOMAINCOMPONENT("DC"),
    UNKNOWN("");

    private final String code;

    private DNFields(String code) throws IllegalArgumentException {

      if (code == null) {
        throw new IllegalArgumentException("Received null code argument");
      }
      this.code = code;
    }

    public static DNFields fromString(String code) {

      for (DNFields field : DNFields.values()) {
        if (field.code.equals(code)) {
          return field;
        }
      }
      return UNKNOWN;
    }
  }

  private static final String ADMIT_ALL = ".*";

  private String countryPatternString;
  private String organizationPatternString;
  private String organizationalUnitPatternString;
  private String localityPatternString;
  private String commonNamePatternString;
  private String domainComponentPatternString;

  private Pattern countryPattern;
  private Pattern organizationPattern;
  private Pattern organizationalUnitPattern;
  private Pattern localityPattern;
  private Pattern commonNamePattern;
  private Pattern domainComponentPattern;

  public static DNMatchingRule buildMatchAllDNMatchingRule() {

    return new DNMatchingRule(ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL, ADMIT_ALL);
  }

  /**
   * Constructor with implicit Pattern String
   *
   * @param regularExpressionRule String
   */
  public DNMatchingRule(String regularExpressionRule) {

    countryPatternString = ADMIT_ALL;
    organizationPatternString = ADMIT_ALL;
    organizationalUnitPatternString = ADMIT_ALL;
    localityPatternString = ADMIT_ALL;
    commonNamePatternString = ADMIT_ALL;
    domainComponentPatternString = ADMIT_ALL;
    if (!(regularExpressionRule == null
        || regularExpressionRule.trim().equals("*")
        || regularExpressionRule.trim().equals(".*"))) {
      for (String rule : regularExpressionRule.split("/")) {
        if (!rule.contains("=")) {
          if (!(rule.trim().isEmpty() || rule.equals(ADMIT_ALL))) {
            log.warn(
                "Malformed DN regex element '{}' "
                    + "it does not contains \'=\' key-value separator",
                rule);
          }
          continue;
        }
        String[] ruleCoupple = rule.split("=");
        if (ruleCoupple.length != 2) {
          log.warn(
              "Malformed DN regex element '{}' it does not contains "
                  + "the key or contains more \'=\' separators",
              rule);
          continue;
        }
        switch (DNFields.fromString(ruleCoupple[0])) {
          case COUNTRY:
            countryPatternString = ruleCoupple[1];
            break;
          case ORGANIZATION:
            organizationPatternString = ruleCoupple[1];
            break;
          case ORGANIZATIONALUNIT:
            organizationalUnitPatternString = ruleCoupple[1];
            break;
          case LOCALITY:
            localityPatternString = ruleCoupple[1];
            break;
          case COMMONNAME:
            commonNamePatternString = ruleCoupple[1];
            break;
          case DOMAINCOMPONENT:
            domainComponentPatternString = ruleCoupple[1];
            break;
          default:
            break;
        }
      }
    }
    initPatterns();
  }

  private void initPatterns() {

    // C country
    if (isMatchAll(countryPatternString)) {
      countryPattern = Pattern.compile(ADMIT_ALL);
    } else {
      countryPattern = Pattern.compile(this.countryPatternString);
    }

    // O organization
    if (isMatchAll(organizationPatternString)) {
      organizationPattern = Pattern.compile(ADMIT_ALL);
    } else {
      organizationPattern = Pattern.compile(this.organizationPatternString);
    }

    // OU organizationalUnit
    if (isMatchAll(organizationalUnitPatternString)) {
      organizationalUnitPattern = Pattern.compile(ADMIT_ALL);
    } else {
      organizationalUnitPattern = Pattern.compile(this.organizationalUnitPatternString);
    }

    // L locality
    if (isMatchAll(localityPatternString)) {
      localityPattern = Pattern.compile(ADMIT_ALL);
    } else {
      localityPattern = Pattern.compile(this.localityPatternString);
    }

    // CN Common Name
    if (isMatchAll(commonNamePatternString)) {
      commonNamePattern = Pattern.compile(ADMIT_ALL);
    } else {
      commonNamePattern = Pattern.compile(this.commonNamePatternString);
    }

    // DC Domain Component
    if (isMatchAll(domainComponentPatternString)) {
      domainComponentPattern = Pattern.compile(ADMIT_ALL);
    } else {
      domainComponentPattern = Pattern.compile(this.domainComponentPatternString);
    }
  }

  private static boolean isMatchAll(String pattern) {

    return pattern == null || pattern.trim().equals("*") || pattern.trim().equals(".*");
  }

  public DNMatchingRule(
      String countryPatternString,
      String organizationPatternString,
      String organizationalUnitPatternString,
      String localityPatternString,
      String commonNamePatternString,
      String domainComponentPatternString) {

    this.countryPatternString = countryPatternString;
    this.organizationPatternString = organizationPatternString;
    this.organizationalUnitPatternString = organizationalUnitPatternString;
    this.localityPatternString = localityPatternString;
    this.commonNamePatternString = commonNamePatternString;
    this.domainComponentPatternString = domainComponentPatternString;
    initPatterns();
  }

  public boolean isMatchAll() {

    return isMatchAll(countryPatternString)
        && isMatchAll(organizationPatternString)
        && isMatchAll(organizationalUnitPatternString)
        && isMatchAll(localityPatternString)
        && isMatchAll(commonNamePatternString)
        && isMatchAll(domainComponentPatternString);
  }

  public boolean match(DistinguishedName principalDN) throws IllegalArgumentException {

    if (principalDN == null) {
      throw new IllegalArgumentException(
          "Unable to perform rule matching. Received null argument: principalDN=" + principalDN);
    }
    if (this.isMatchAll()) {
      return true;
    }
    boolean result = false;
    boolean countryMatch = false;
    boolean organizationMatch = false;
    boolean localityMatch = false;
    boolean organizationalUnitMatch = false;
    boolean commonNameMatch = false;
    boolean domainComponentMatch = false;

    // C
    String countryName = principalDN.getCountryName();
    if (countryName != null) {
      CharSequence country = countryName.subSequence(0, countryName.length());
      Matcher countryMatcher = countryPattern.matcher(country);
      countryMatch = countryMatcher.find();
    } else {
      countryMatch = countryPatternString.equals(ADMIT_ALL);
    }
    if (!(countryMatch)) return false;

    // O
    String organizationName = principalDN.getOrganizationName();
    if (organizationName != null) {
      CharSequence organization = organizationName.subSequence(0, organizationName.length());
      Matcher organizationMatcher = organizationPattern.matcher(organization);
      organizationMatch = organizationMatcher.find();
    } else {
      organizationMatch = organizationPatternString.equals(ADMIT_ALL);
    }
    if (!(organizationMatch)) return false;

    // L
    String localityName = principalDN.getLocalityName();
    if (localityName != null) {
      CharSequence locality = localityName.subSequence(0, localityName.length());
      Matcher localityMatcher = localityPattern.matcher(locality);
      localityMatch = localityMatcher.find();
    } else {
      localityMatch = localityPatternString.equals(ADMIT_ALL);
    }
    if (!(localityMatch)) return false;

    // OU ArrayList
    ArrayList<String> organizationalUnitNames = principalDN.getOrganizationalUnitNames();
    if ((organizationalUnitNames != null) && (!(organizationalUnitNames.isEmpty()))) {
      CharSequence organizationalUnit = null;
      String nameStr = null;
      Matcher organizationalUnitMatcher = null;
      for (Iterator<String> name = organizationalUnitNames.iterator(); name.hasNext(); ) {
        nameStr = name.next();
        organizationalUnit = nameStr.subSequence(0, nameStr.length());
        organizationalUnitMatcher = organizationalUnitPattern.matcher(organizationalUnit);
        organizationalUnitMatch = organizationalUnitMatcher.find();
        if (organizationalUnitMatch) break;
      }
    } else {
      organizationalUnitMatch = organizationalUnitPatternString.equals(ADMIT_ALL);
    }
    if (!(organizationalUnitMatch)) return false;

    // CN ArrayList
    ArrayList<String> commonNames = principalDN.getCommonNames();
    if ((commonNames != null) && (!(commonNames.isEmpty()))) {
      CharSequence commonName = null;
      String commonNameStr = null;
      Matcher commonNameMatcher = null;
      for (Iterator<String> scanCN = commonNames.iterator(); scanCN.hasNext(); ) {
        commonNameStr = scanCN.next();
        commonName = commonNameStr.subSequence(0, commonNameStr.length());
        commonNameMatcher = commonNamePattern.matcher(commonName);
        commonNameMatch = commonNameMatcher.find();
        if (commonNameMatch) break;
      }
    } else {
      commonNameMatch = commonNamePatternString.equals(ADMIT_ALL);
    }
    if (!(commonNameMatch)) return false;

    // DC ArrayList
    ArrayList<String> domainComponents = principalDN.getDomainComponents();
    if ((domainComponents != null) && (!(domainComponents.isEmpty()))) {
      CharSequence domainComponent = null;
      String domainComponentStr = null;
      Matcher domainComponentMatcher = null;
      for (Iterator<String> scanDC = domainComponents.iterator(); scanDC.hasNext(); ) {
        domainComponentStr = scanDC.next();
        domainComponent = domainComponentStr.subSequence(0, domainComponentStr.length());
        domainComponentMatcher = domainComponentPattern.matcher(domainComponent);
        domainComponentMatch = domainComponentMatcher.find();
        if (domainComponentMatch) break;
      }
    } else {
      domainComponentMatch = commonNamePatternString.equals(ADMIT_ALL);
    }
    if (!(domainComponentMatch)) return false;

    // Total Result
    // NOTE : At this point result should be always TRUE!
    result =
        countryMatch
            && organizationMatch
            && organizationalUnitMatch
            && localityMatch
            && commonNameMatch
            && domainComponentMatch;
    return result;
  }

  public String toString() {

    StringBuilder result = new StringBuilder();
    result.append(" C=" + countryPatternString);
    result.append(" O=" + organizationPatternString);
    result.append(" OU=" + organizationalUnitPatternString);
    result.append(" L=" + localityPatternString);
    result.append(" CN=" + commonNamePatternString);
    return result.toString();
  }

  public String toShortSlashSeparatedString() {

    StringBuilder result = new StringBuilder();
    if (!countryPatternString.equals(ADMIT_ALL)) {
      result.append("/C=" + countryPatternString);
    }
    if (!organizationPatternString.equals(ADMIT_ALL)) {
      result.append("/O=" + organizationPatternString);
    }
    if (!organizationalUnitPatternString.equals(ADMIT_ALL)) {
      result.append("/OU=" + organizationalUnitPatternString);
    }
    if (!localityPatternString.equals(ADMIT_ALL)) {
      result.append("/L=" + localityPatternString);
    }
    if (!commonNamePatternString.equals(ADMIT_ALL)) {
      result.append("/CN=" + commonNamePatternString);
    }
    return result.toString();
  }
}
