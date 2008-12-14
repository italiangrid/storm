package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import org.apache.commons.logging.Log;
import it.grid.storm.authz.AuthzDirector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import it.grid.storm.griduser.FQAN;

public abstract class FQANMatchingRule {

  protected final Log log = AuthzDirector.getLogger();
  protected static final String ADMIT_ALL = ".*";
  protected String groupPatternMatching = null;
  protected String rolePatternMatching = null;
  protected Pattern groupPattern = null;
  protected Pattern rolePattern = null;

  abstract void validateMR() throws AuthzDBReaderException;

  public boolean match(FQAN fqan) {
    boolean result = false;
    CharSequence groupSequence = fqan.getGroup();
    CharSequence roleSequence = fqan.getRole();
    Matcher groupMatcher = groupPattern.matcher(groupSequence);
    Matcher roleMatcher = rolePattern.matcher(roleSequence);
    result = groupMatcher.matches() && roleMatcher.matches();
    return result;
}

  public String toString() {
    StringBuffer result = new StringBuffer();
    String sep = System.getProperty("line.separator");
    result.append(" FQAN.GroupPatternMatchingRule = " + groupPatternMatching + sep);
    result.append(" FQAN.RolePatternMatchinRule = " + rolePatternMatching + sep);
    return result.toString();
  }


}
