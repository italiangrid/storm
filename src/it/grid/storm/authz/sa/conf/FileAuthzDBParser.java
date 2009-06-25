package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;


public class FileAuthzDBParser implements Observer {

    private FileAuthzDBReader fileReader;
    private final PropertiesConfiguration authzFileDBProperties;
    private final AuthzDBInterface authzFileDB;
    private SpaceAuthzInterface spaceAuthz = null;

    private final Logger log = AuthzDirector.getLogger();


    public FileAuthzDBParser(SpaceAuthzInterface spaceAuthz, AuthzDBReaderInterface reader, boolean verboseLogging) {
        this.spaceAuthz = spaceAuthz;
        
        ////////TEMPORARY FIX 
        //////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
        authzFileDBProperties = null;
        // authzFileDBProperties = reader.getAuthzDB();
        
        if (reader instanceof FileAuthzDBReader) {
            fileReader = (FileAuthzDBReader) reader;
            //////// TEMPORARY FIX 
            //////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
            //fileReader.setObserver(this);
        }
        else {
            log.error("FileAuthzDBParser initialized with a invalid Reader.");
        }
        authzFileDB = parseAuthzDBFile(authzFileDBProperties);
        spaceAuthz.setAuthzDB(authzFileDB);
    }

    /**
     * parseAuthzDBFile
     *
     * @param authzFileDBProperties PropertiesConfiguration
     * @return AuthzDBInterface
     */
    private AuthzDBInterface parseAuthzDBFile(PropertiesConfiguration authzFileDBProperties) {
        log.debug("PARSING AUTHZ DB FILE");
        return null;
    }

    public AuthzDBInterface getAuthzDB() {
        return this.authzFileDB;
    }

    public void update(Observable observed, Object arg) {
        log.debug(arg + " Refreshing Namespace Memory Cache .. ");
        ////////TEMPORARY FIX 
        //////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
        //FileAuthzDBReader reader = (FileAuthzDBReader) observed;

        /**
         * @todo: Refreshing della copia Cache di AuthzDB memorizzata in SpaceAuthz
         */
        ////////TEMPORARY FIX 
        //////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
        //reader.setNotifyManaged();
        log.debug(" ... Cache Refreshing ended");
    }
    
    
    

}
