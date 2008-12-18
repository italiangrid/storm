package it.grid.storm.authz.sa.model;

import it.grid.storm.griduser.DNMatchingRule;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;

public class SubjectPattern {

  private String dnPatternStr = null;
  private String fqanPatternStr = null;
  private DNMatchingRule dnMR = null;
  private EGEEFQANMatchingRule fqanMR = null;

  private static DNMatchingRule DEFAULT_DN_PATTERN = new DNMatchingRule();

  public SubjectPattern(String dnPattern, String fqanPattern) throws AuthzDBReaderException {
    this.dnPatternStr = dnPattern;
    this.dnMR = new DNMatchingRule(dnPattern);
    this.fqanPatternStr = fqanPattern;
    this.fqanMR = new EGEEFQANMatchingRule(fqanPattern);
  }

  public SubjectPattern(String fqanPattern) {
    this.dnPatternStr = ".*";
    this.dnMR = SubjectPattern.DEFAULT_DN_PATTERN;
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

  public EGEEFQANMatchingRule getFQANPattern() {
    return this.fqanMR;
  }

}
