package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.*;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class EGEEFQANMatchingRule extends FQANMatchingRule {

  static private Pattern fqanWildcardPattern = Pattern.compile("/[\\w-\\.]+(/[\\w-\\.]+)*(/Role=[\\w-\\.]+)?(/Capability=[\\w-\\.]+)?");


  private String voName = null;
  private String groupPattern = null;
  private String rolePattern = null;


  public EGEEFQANMatchingRule(String regularExpressionRule) throws AuthzDBReaderException {
    if (regularExpressionRule!=null) {
    /**
         StringTokenizer stk = new StringTokenizer(regularExpressionRule, "/");
         int tokens = stk.countTokens();
         if (tokens>1) {

         } else
     **/
     } else {

    }

    if ( (regularExpressionRule == null) || (regularExpressionRule.equals("*"))) {
      init("*", "*");
    }
    else { //Split the rule into the attribute rules
      String[] rules = regularExpressionRule.split("Role=");
      if (rules != null) {
        groupPatternMatching = rules[0];
        if (rules.length > 1) {
          rolePatternMatching = rules[1];
        }
      }
    }
  }

  private void init(String groupPatternMatching, String rolePatternMatching) throws AuthzDBReaderException{
    this.groupPatternMatching = groupPatternMatching;
    this.rolePatternMatching = rolePatternMatching;
    validateMR();
  }

  /**
   * validateMR
   */
  void validateMR() throws AuthzDBReaderException {

  }



}

