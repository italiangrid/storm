package it.grid.storm.authz.sa.conf;

import java.util.Observable;
import java.util.Observer;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import it.grid.storm.authz.SpaceAuthzInterface;


public class FileAuthzDBParser implements Observer {

    private FileAuthzDBReader fileReader;
    private PropertiesConfiguration authzFileDBProperties;
    private AuthzDBInterface authzFileDB;
    private SpaceAuthzInterface spaceAuthz = null;

    private final Log log = AuthzDirector.getLogger();


    public FileAuthzDBParser(SpaceAuthzInterface spaceAuthz, AuthzDBReaderInterface reader, boolean verboseLogging) {
        this.spaceAuthz = spaceAuthz;
        authzFileDBProperties = (PropertiesConfiguration) reader.getAuthzDB();
        if (reader instanceof FileAuthzDBReader) {
            fileReader = (FileAuthzDBReader) reader;
            fileReader.setObserver(this);
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
        /** @todo IMPLEMENT */
        return null;
    }

    public AuthzDBInterface getAuthzDB() {
        return this.authzFileDB;
    }

    public void update(Observable observed, Object arg) {
        log.debug(arg + " Refreshing Namespace Memory Cache .. ");
        FileAuthzDBReader reader = (FileAuthzDBReader) observed;

        /**
         * @todo: Refreshing della copia Cache di AuthzDB memorizzata in SpaceAuthz
         */

        reader.setNotifyManaged();
        log.debug(" ... Cache Refreshing ended");
    }

}
