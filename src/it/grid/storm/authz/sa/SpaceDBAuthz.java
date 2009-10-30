/**
 * 
 */
package it.grid.storm.authz.sa;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.model.FileAuthzDB;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;

import java.io.File;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class SpaceDBAuthz extends SpaceAuthz {

    private final Logger log = AuthzDirector.getLogger();
    public final static String UNDEF = "undef-SpaceAuthzDB";
    private String spaceAuthzDBID = "not-defined";
    private static String configurationPATH;
    private String dbFileName;
    private FileAuthzDB authzDB;

    public SpaceDBAuthz() {

    }

    /**
     * @return
     */
    public static SpaceDBAuthz makeEmpty() {
        SpaceDBAuthz result = new SpaceDBAuthz();
        result.setSpaceAuthzDBID("default-SpaceAuthzDB");
        // * @todo other assignments
        return result;
    }

    public SpaceDBAuthz(String dbFileName) {
        Configuration config = Configuration.getInstance();
        configurationPATH = config.getNamespaceConfigPath();
        if (existsAuthzDBFile(dbFileName)) {
            this.dbFileName = dbFileName;
            spaceAuthzDBID = dbFileName;
        }
    }

    /**
     * @param string
     */
    void setSpaceAuthzDBID(String id) {
        spaceAuthzDBID = id;
    }

    /**
     * 
     */
    @Override
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        // TODO Auto-generated method stub

        // Check if the Cache is Locked (in the case, skip the use of the cache)

        // Check the presence of guser in the Cache

        // If requestor is present in the AuthzCache, retrieve the response for
        // the SpaceOp

        // Else, compute the Authz Answer for ALL the SpaceOp and insert into
        // the cache

        // Return the result
        return false;
    }

    /**********************************************************************
     * AUTHZ Algorithm
     */

    /**
     * Implementation of NFSv4.1 ACL evaluation algorithm - simplified version:
     * http://tools.ietf.org/html/draft-ietf-nfsv4-acl-mapping-02#section-2 - full version:
     * http://tools.ietf.org/html/rfc3530#section-5.11.2
     */
    private boolean nfs4AuthzAlgorithm(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        return false;
    }

    /**********************************************************************
     * CACHE mechanism
     */

    /**
     * Method to check the presence of Requestor within the Cache
     * 
     * @param guser
     * @return
     */
    private boolean isPresent(GridUserInterface guser) {
        return false;
    }

    /**
     * Method to add (and store somewhere) a new Requestor
     * 
     * @param guser
     */
    private void addRequestorToCache(GridUserInterface guser) {

    }

    private void refreshCache() {
        // Take the LOCK on the Cache

        // At the end, release the LOCK on the Cache
    }

    /**********************************************************************
     * BUILDINGs METHODS
     */

    /**
     * Check the existence of the AuthzDB file
     */
    private boolean existsAuthzDBFile(String dbFileName) {
        String fileName = configurationPATH + File.separator + dbFileName;
        boolean exists = (new File(fileName)).exists();
        if (!(exists)) {
            log.error("The AuthzDB File '" + dbFileName + "' does not exists");
        }
        return exists;
    }

    /**
     * Return the AuthzDB FileName
     * 
     * @return
     */
    String getAuthzDBFileName() {
        return dbFileName;
    }

    /**
     * @param authzDB
     */
    void setAuthzDB(FileAuthzDB authzDB) {
        // Refresh the cache

        // Set the updated authzDB
        this.authzDB = authzDB;
    }

    public String getSpaceAuthzID() {
        return spaceAuthzDBID;
    }

    public void refresh() {
        // TODO Auto-generated method stub

    }

}
