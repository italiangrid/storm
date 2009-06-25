package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.SubjectAttribute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public abstract class FQANPattern implements SubjectPattern {

    protected Logger log = AuthzDirector.getLogger();
    protected static final String ADMIT_ALL = ".*";
    protected String groupPatternString = null;
    protected String rolePatternString = null;
    protected Pattern groupPattern = null;
    protected Pattern rolePattern = null;


    public abstract boolean isValidPattern() throws AuthzDBReaderException ;

    /* (non-Javadoc)
     * @see it.grid.storm.authz.sa.model.SubjectPattern#match(it.grid.storm.griduser.FQAN)
     */
    public boolean match(SubjectAttribute sa) {
        boolean result = false;
        if (sa instanceof FQAN) {
            FQAN fqan = (FQAN) sa;
            CharSequence groupSequence = fqan.getGroup();
            CharSequence roleSequence = fqan.getRole();
            Matcher groupMatcher = groupPattern.matcher(groupSequence);
            Matcher roleMatcher = rolePattern.matcher(roleSequence);
            result = groupMatcher.matches() && roleMatcher.matches();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        String sep = System.getProperty("line.separator");
        result.append(" FQAN.GroupPatternMatchingRule = "
                + groupPatternString + sep);
        result.append(" FQAN.RolePatternMatchinRule = " + rolePatternString
                + sep);
        return result.toString();
    }

}
