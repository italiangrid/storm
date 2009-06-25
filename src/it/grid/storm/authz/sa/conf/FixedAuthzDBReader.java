package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;

import java.util.List;


public class FixedAuthzDBReader implements AuthzDBReaderInterface {

    public FixedAuthzDBReader() {
    }

    /*
     * 
     */
    public void addAuthzDB(String dbFileName) throws AuthzDBReaderException {
        // TODO Auto-generated method stub

    }

    /*
     * 
     */
    public AuthzDBInterface getAuthzDB(String authzDBName) throws AuthzDBReaderException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * 
     */
    public List<String> getAuthzDBNames() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * 
     */
    public long getLastParsed(String dbFileName) throws AuthzDBReaderException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * 
     */
    public void onChangeAuthzDB(String authzDBName) throws AuthzDBReaderException {
        // TODO Auto-generated method stub

    }
}
