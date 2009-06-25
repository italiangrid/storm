/**
 * 
 */
package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.griduser.DistinguishedName;
import it.grid.storm.griduser.SubjectAttribute;

/**
 * @author zappi
 *
 */
public class DNEveryonePattern extends DNPattern implements Everyone {

    
    /**
     * CONSTRUCTOR
     */
    
    public DNEveryonePattern() throws AuthzDBReaderException {
        super("*");
        this.checkValidity = false;
        init("*", "*", "*", "*", "*", "*");
    }
        
    /*
     * Return always true because the pattern is built programmatically,
     * and it is supposed to be valid.
     * 
     * @see it.grid.storm.authz.sa.model.SubjectPattern#isValidPattern()
     */
    @Override
    public boolean isValidPattern() throws AuthzDBReaderException {
        return true;
    }
    
    /* (non-Javadoc)
     * @see it.grid.storm.authz.sa.model.SubjectPattern#match(it.grid.storm.griduser.SubjectAttribute)
     */
    //@Override
    @Override
    public boolean match(SubjectAttribute subjectAttribute) {
        boolean result = false;
        if (subjectAttribute instanceof DistinguishedName) {
            result = true;
        }
        return result;
    }

}
