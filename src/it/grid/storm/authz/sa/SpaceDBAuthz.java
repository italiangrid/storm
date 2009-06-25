/**
 * 
 */
package it.grid.storm.authz.sa;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;

import java.io.File;

/**
 * @author zappi
 *
 */
public class SpaceDBAuthz extends SpaceAuthz {

    private static String configurationPATH;

    public SpaceDBAuthz(String authzDBName) throws AuthzDBReaderException {
        Configuration config = Configuration.getInstance();
        configurationPATH = config.getNamespaceConfigPath();
    }

    /**
     * 
     */
    @Override
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        // TODO Auto-generated method stub
        return false;
    }


    /**********************************************************************
     * BUILDINGs METHODS
     */


    private boolean existsAuthzDBFile(String dbFileName) throws AuthzDBReaderException {
        String fileName = configurationPATH + File.separator + dbFileName;
        boolean exists = (new File(fileName)).exists();
        if (!(exists) ) {
            throw new AuthzDBReaderException("The AuthzDB File '"+dbFileName+"' does not exists");
        }
        return exists;
    }

}
