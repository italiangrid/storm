package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.*;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class EGEEFQANMatchingRule extends FQANMatchingRule {

  //To verify the Regular Expression visit the site "http://www.fileformat.info/tool/regex.htm"
  static private Pattern fqanWildcardPattern = Pattern.compile("/[\\w-\\.]+(((/[\\w-\\.]+)*)|(/\\u002A))(/Role=(([\\w-\\.]+)|(\\u002A)))?");

  private String fqanRE = null;
  private String voName = null;
  private String groupPattern = null;
  private String rolePattern = null;
  private boolean checkFormedness;

  /**
   * CONSTRUCTOR
   **/

  public EGEEFQANMatchingRule(String fqanRE) throws AuthzDBReaderException {
          this(fqanRE, true);
  }


  public EGEEFQANMatchingRule(String fqanRE, boolean checkFormedness) throws AuthzDBReaderException {
    this.checkFormedness = checkFormedness;
    this.fqanRE = fqanRE;
    if (validateMR())
      generatePattern();
  }


  /**
   * PRIVATE SETTER
   **/

  private void generatePattern() {
      StringTokenizer stk = new StringTokenizer(this.fqanRE, "/");
      int tokens = stk.countTokens();
      this.voName = stk.nextToken();
      if (!stk.hasMoreTokens()) {
        groupPattern = null;
        rolePattern = null;
      } else { //building (sub)group pattern

      }


      if (tokens > 1) {

      }
  }






  /**
   * validateMR
   */
  protected boolean validateMR() throws AuthzDBReaderException {
    // Matches to the specification.
    Matcher m = fqanWildcardPattern.matcher(this.fqanRE);
    if (!m.matches()) {
      if (checkFormedness)
        throw new IllegalArgumentException("FQAN '" + this.fqanRE +
            "' is malformed (syntax: /VO[(/subgroup(s)|*)]][/Role=(role|*)])");
      else
        return false;
      }
    return true;
  }





}

