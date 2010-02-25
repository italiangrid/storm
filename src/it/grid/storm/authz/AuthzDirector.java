package it.grid.storm.authz;

import it.grid.storm.authz.path.PathAuthz;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;
import it.grid.storm.authz.sa.SpaceDBAuthz;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.test.MockSpaceAuthz;
import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.SAAuthzType;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthzDirector {

    private static final Logger log = LoggerFactory.getLogger(AuthzDirector.class);
    private static int refreshInSeconds = 5; // Default value;
    private static String configurationPATH;
    private static String stormPropertiesFileName;

    // Map between 'SpaceToken' and the related 'SpaceAuthz'
    private static Map<TSpaceToken, SpaceAuthzInterface> spaceAuthzs = null;

    // PathAuthz is only one, shared by all SAs
    private static PathAuthzInterface pathAuthz = null;

    /**
     * private AuthzDirector() { super(); }
     **/
/**
    private static void initializeDirector(boolean testingMode) throws AuthzDBReaderException {
        log.info("AUTHZ DIRECTOR : Inizializating ...");
        Configuration config = Configuration.getInstance();

        if (testingMode) {
            log.info(" ####################### ");
            log.info(" ####  TESTING MODE #### ");
            log.info(" ####################### ");
            configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
            stormPropertiesFileName = configurationPATH + File.separator + "storm_test.properties";
            config.setConfigReader(new ConfigReader(stormPropertiesFileName, refreshInSeconds));

            authzDBPath = config.getAuthzDBPath();
            refreshInSeconds = config.getRefreshRateAuthzDBfilesInSeconds(); // Default = "5 seconds"

        } else {
            log.info(" +++++++++++++++++++++++ ");
            log.info("    Production Mode      ");
            log.info(" +++++++++++++++++++++++ ");
            configurationPATH = config.getNamespaceConfigPath(); // Default = "./etc/"
            // configurationFileName = "storm.properties"; //Default name
            authzDBPath = config.getAuthzDBPath();
            refreshInSeconds = config.getRefreshRateAuthzDBfilesInSeconds(); // Default = "5 seconds"

        }

        log.debug("[AuthZ Director] Initialization done!");
        initialized = true;
    }
**/
    /**
     * Scan the Namespace.xml to retrieve the list of file AuthZDB to digest
     */
    private static Map<TSpaceToken, SpaceAuthzInterface> buildSpaceAuthzsMAP() {

        HashMap<TSpaceToken, SpaceAuthzInterface> spaceAuthzMap = new HashMap<TSpaceToken, SpaceAuthzInterface>();

        // Retrieve the list of VFS from Namespace
        NamespaceInterface ns = NamespaceDirector.getNamespace();
        ArrayList<VirtualFSInterface> vfss;
        try {
            vfss = new ArrayList<VirtualFSInterface>(ns.getAllDefinedVFS());
            for (VirtualFSInterface vfs : vfss) {
                String vfsName = vfs.getAliasName();
                SAAuthzType authzTp = vfs.getStorageAreaAuthzType();
                String authzName = "";
                if (authzTp.equals(SAAuthzType.AUTHZDB)) {
                    // The Space Authz is based on Authz DB
                    authzName = vfs.getStorageAreaAuthzDB();
                    log.debug("Loading AuthzDB '" + authzName + "'");
                    if (existsAuthzDBFile(authzName)) {
                        // Digest the Space AuthzDB File
                        TSpaceToken spaceToken = vfs.getSpaceToken();
                        SpaceAuthzInterface spaceAuthz = new SpaceDBAuthz(authzName);
                        spaceAuthzMap.put(spaceToken, spaceAuthz);
                    } else {
                        log.error("File AuthzDB '" + authzName + "' related to '" + vfsName + "' does not exists.");
                    }
                } else {
                    authzName = vfs.getStorageAreaAuthzFixed();
                }
                log.debug("VFS ['" + vfsName + "'] = " + authzTp + " : " + authzName);
            }
        } catch (NamespaceException e) {
            log.warn("Unable to initialize AUTHZ DB!" + e.getMessage());
            log.warn(".. (Workaround): AuthzDirector INITIALIZATED evenly..");
        }

        return spaceAuthzMap;
    }

    /**
     * Utility method
     * 
     * @param dbFileName
     * @return
     * @throws AuthzDBReaderException
     */
    private static boolean existsAuthzDBFile(String dbFileName) {
        String fileName = configurationPATH + File.separator + dbFileName;
        boolean exists = (new File(fileName)).exists();
        if (!(exists)) {
            log.warn("The AuthzDB File '" + dbFileName + "' does not exists");
        }
        return exists;
    }

    // ****************************************
    // PUBLIC METHODS
    // ****************************************

    /**
     * Retrieve the Logger used in all the package AUTHZ
     */
    public static Logger getLogger() {
        return log;
    }

    /******************************
     * SPACE AUTHORIZATION ENGINE
     ******************************/
    public static void initializeSpaceAuthz() {
        // Build Space Authzs MAP
        spaceAuthzs = buildSpaceAuthzsMAP();
    }

    /**
     * Retrieve the Space Authorization module related to the Space Token
     * 
     * @param token
     * @return
     */
    public static SpaceAuthzInterface getSpaceAuthz(TSpaceToken token) {

        SpaceAuthzInterface spaceAuthz = new MockSpaceAuthz();
        // Retrieve the SpaceAuthz related to the Space Token
        if ((spaceAuthzs != null) && (spaceAuthzs.containsKey(token))) {
            spaceAuthz = spaceAuthzs.get(token);
            log.debug("Space Authz related to S.Token ='" + token + "' is '" + spaceAuthz.getSpaceAuthzID() + "'");
        } else {
            log.debug("Space Authz related to S.Token ='" + token + "' does not exists. Use the MOCK one.");
        }
        return spaceAuthz;
    }

    /******************************
     * PATH AUTHORIZATION ENGINE
     ******************************/

    /**
     * Initializating the Path Authorization engine
     * 
     * @param pathAuthz2
     */
    public static void initializePathAuthz(String pathAuthzDBFileName) {
        PathAuthzDBReader authzDBReader = new PathAuthzDBReader(pathAuthzDBFileName);
        PathAuthzDB pathAuthzDB = authzDBReader.getPathAuthzDB();
        PathAuthz pathAuthz = new PathAuthz(pathAuthzDB);
        AuthzDirector.pathAuthz = pathAuthz;
    }

    /**
     * Retrieve the Path Authorization module
     * 
     * @todo: To implement this.
     */
    public static PathAuthzInterface getPathAuthz() {
        return AuthzDirector.pathAuthz;
    }

}
