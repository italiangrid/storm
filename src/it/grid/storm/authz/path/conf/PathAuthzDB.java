/**
 * 
 */
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.PathACE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * @author zappi
 *
 */
public class PathAuthzDB {

    private final Logger log = AuthzDirector.getLogger();
    
    private List<PathACE> authzDB = new ArrayList<PathACE>();
 
    //===========  CONSTRUCTORs ============
    
    /**
     * @return
     */
    public static PathAuthzDB makeEmpty() {
        PathAuthzDB result = new PathAuthzDB();
        result.addPathACE(PathACE.PERMIT_ALL);
        return result;
    }
 
    
    public PathAuthzDB() {
    }
    
    public void addPathACE(PathACE pathAce) {
        authzDB.add(pathAce);
    }

    //=============  INFORMATIONALs ========
    
    public int getSize() {
        return authzDB.size();
    }

    
    
}
