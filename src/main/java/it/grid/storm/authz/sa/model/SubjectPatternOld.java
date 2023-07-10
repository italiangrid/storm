/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.AuthzDBReaderException;
import it.grid.storm.griduser.DNMatchingRule;

public class SubjectPatternOld {

  private String dnPatternStr = null;
  private String fqanPatternStr = null;
  private DNMatchingRule dnMR = null;
  private EGEEFQANPattern fqanMR = null;

  private static DNMatchingRule DEFAULT_DN_PATTERN = DNMatchingRule.buildMatchAllDNMatchingRule();

  public SubjectPatternOld(String dnPattern, String fqanPattern) throws AuthzDBReaderException {

    this.dnPatternStr = dnPattern;
    this.dnMR = new DNMatchingRule(dnPattern);
    this.fqanPatternStr = fqanPattern;
    this.fqanMR = new EGEEFQANPattern(fqanPattern);
  }

  public SubjectPatternOld(String fqanPattern) {

    this.dnPatternStr = ".*";
    this.dnMR = SubjectPatternOld.DEFAULT_DN_PATTERN;
    this.fqanPatternStr = fqanPattern;
  }

  public String getDNPatternStr() {

    return this.dnPatternStr;
  }

  public String getFQANPatternStr() {

    return this.fqanPatternStr;
  }

  public DNMatchingRule getDNPattern() {

    return this.dnMR;
  }

  public EGEEFQANPattern getFQANPattern() {

    return this.fqanMR;
  }
}
