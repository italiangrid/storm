package it.grid.storm.authz;

import it.grid.storm.authz.sa.SpaceDBAuthz;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.conf.FileAuthzDBReader;
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthzDirector {

    private static final Logger log = LoggerFactory.getLogger(AuthzDirector.class);
    private static int refreshInSeconds = 5; //Default value;
    private static String configurationPATH;
    private static String authzDBPath;
    private static String configurationFileName;

    //Map between 'SpaceToken' and the related 'SpaceAuthz'
    private static Map<TSpaceToken,SpaceAuthzInterface> spaceAuthzs = null;

    private static FileAuthzDBReader authzDBReader;

    //Map between 'Path' and the related 'PathAuthz'  @TODO: to implement !!
    private static Map<String,PathAuthzInterface> pathAuthzs = null;

    private static boolean initialized = false;

    
    
 
    /**
     * private AuthzDirector() { super(); }
     **/

    private static void initializeDirector(boolean testingMode) throws AuthzDBReaderException {
        log.info("AUTHZ DIRECTOR : Inizializating ...");
        Configuration config = Configuration.getInstance();

        if (testingMode) {
            log.info(" ####################### ");
            log.info(" ####  TESTING MODE #### ");
            log.info(" ####################### ");
            configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
            configurationFileName = configurationPATH + File.separator + "storm_test.properties";
            config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));

            authzDBPath = config.getAuthzDBPath();
            refreshInSeconds = config.getRefreshRateAuthzDBfilesInSeconds(); //Default = "5 seconds"

        }
        else {
            log.info(" +++++++++++++++++++++++ ");
            log.info("    Production Mode      ");
            log.info(" +++++++++++++++++++++++ ");
            configurationPATH = config.getNamespaceConfigPath(); //Default = "./etc/"
            // configurationFileName = "storm.properties"; //Default name
            authzDBPath = config.getAuthzDBPath();
            refreshInSeconds = config.getRefreshRateAuthzDBfilesInSeconds(); //Default = "5 seconds"

        }

        //Initialize the AuthzDBReader
        authzDBReader = new FileAuthzDBReader(refreshInSeconds*1000, authzDBPath);

        // Build Space Authzs MAP
        buildSpaceAuthzsMAP();

        // Build Path Authzs MAP
        // @todo

        log.debug("AuthZ Configuration PATH : " + configurationPATH);
        log.debug("AuthZ Configuration Watching RATE : " + refreshInSeconds);

        log.debug("[AuthZ Director] Initialization done!");
        initialized = true;
    }

    /**
     * Scan the Namespace.xml to retrieve the list of file AuthZDB to digest
     * 
     * @return
     * @throws AuthzDBReaderException
     */
    private static void buildSpaceAuthzsMAP() throws AuthzDBReaderException {
        
        //Retrieve the list of VFS from Namespace
        NamespaceInterface ns = NamespaceDirector.getNamespace();
        ArrayList<VirtualFSInterface> vfss;
        try {
            vfss = new ArrayList<VirtualFSInterface>(ns.getAllDefinedVFS());
            for (VirtualFSInterface vfs : vfss) {
                String vfsName = vfs.getAliasName();
                SAAuthzType authzTp = vfs.getStorageAreaAuthzType();
                String authzName = "";
                if (authzTp.equals(SAAuthzType.AUTHZDB)) {
                    //The Space Authz is based on Authz DB
                    authzName = vfs.getStorageAreaAuthzDB();
                    log.debug("Loading AuthzDB '"+authzName+"'");
                    if (existsAuthzDBFile(authzName))  {
                        // Digest the Space AuthzDB File
                        addSpaceAuthz(vfs, authzName);
                    } else {
                        throw new AuthzDBReaderException("File AuthzDB '"+authzName+"' does not exists.");
                    }
                } else {
                    authzName = vfs.getStorageAreaAuthzFixed();
                }
                log.debug("VFS ['"+vfsName+"'] = "+authzTp+" : "+authzName );
            }
        } catch (NamespaceException e) {
            log.warn("Unable to initialize AUTHZ DB!" + e.getMessage());
            initialized = true;
            log.warn(".. (Workaround): AuthzDirector INITIALIZATED evenly..");
            throw new AuthzDBReaderException(e.getMessage());
        }       
    }
    
    /**
     * Utility method
     * 
     * @param dbFileName
     * @return
     * @throws AuthzDBReaderException
     */
    private static boolean existsAuthzDBFile(String dbFileName) throws AuthzDBReaderException {
        String fileName = configurationPATH + File.separator + dbFileName;
        boolean exists = (new File(fileName)).exists();
        if (!(exists)) {
            throw new AuthzDBReaderException("The AuthzDB File '" + dbFileName + "' does not exists");
        }
        return exists;
    }

    
    /**
     * Command the parsing of SpaceAuthzDB File
     * 
     * @param vfs
     * @param dbFileName
     * @throws AuthzDBReaderException
     */
    private static void addSpaceAuthz(VirtualFSInterface vfs, String dbFileName) throws AuthzDBReaderException {
        TSpaceToken spaceToken;
        String vfsName = "unknown";
        log.debug("Authz DB '"+dbFileName+"' exists? "+existsAuthzDBFile(dbFileName));
        try {
            vfsName = vfs.getAliasName();
            spaceToken = vfs.getSpaceToken();
            SpaceAuthzInterface spaceAuthz = new SpaceDBAuthz(dbFileName);
            spaceAuthzs.put(spaceToken, spaceAuthz);

        } catch (NamespaceException e) {
            throw new AuthzDBReaderException("Unable to retrieve Space Token for VFS '"+vfsName+"' ");
        }
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

    /**
     * Retrieve the Space Authorization module related to the Space Token
     * 
     * @param token
     * @return
     */
    public static SpaceAuthzInterface getSpaceAuthz(TSpaceToken token) {
        try {
            if (!(initialized)) {
                initializeDirector(false);
            }
        } catch (AuthzDBReaderException e) {
            log.error("Unable to initialize AuthZDB");
            e.printStackTrace();
        }

        SpaceAuthzInterface spaceAuthz = new MockSpaceAuthz();
        // Retrieve the SpaceAuthz related to the Space Token
        if ((spaceAuthzs!=null)&&(spaceAuthzs.containsKey(token))) {
            spaceAuthz = spaceAuthzs.get(token);
        } else {
            log.debug("Space Authz related to S.Token ='" + token + "' does not exists. Use the MOCK one.");
        }
        return spaceAuthz;
    }

    /**
     * Retrieve the Path Authorization module related to the specified PATH
     * 
     * @todo: To implement this.
     */
    public static PathAuthzInterface getPathAuthz(String path) {
        return null;
    }

 
    
    
}
