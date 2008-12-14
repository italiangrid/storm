package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.*;
import org.apache.commons.logging.Log;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.authz.AuthzDirector;

public class EGEEFQANMatchingRule extends FQANMatchingRule {




  public EGEEFQANMatchingRule(String regularExpressionRule) throws AuthzDBReaderException {
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

