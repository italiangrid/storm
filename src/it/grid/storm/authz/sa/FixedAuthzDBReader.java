package it.grid.storm.authz.sa;

import org.apache.commons.configuration.PropertiesConfiguration;
import it.grid.storm.authz.sa.conf.*;

public class FixedAuthzDBReader implements AuthzDBReaderInterface {
    public FixedAuthzDBReader() {
    }

    /**
     * bindDB
     *
     * @param dbName String
     * @throws AuthzDBReaderException
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void bindDB(String dbName) throws AuthzDBReaderException {
    }


    /**
     * loadAuthZDB
     *
     * @throws AuthzDBReaderException
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void loadAuthZDB() throws AuthzDBReaderException {
    }

    /**
     * printAuthzDB
     *
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void printAuthzDB() {
    }

    /**
     * setAutomaticReload
     *
     * @param autoReload boolean
     * @param timeIntervalInSeconds int
     * @todo Implement this it.grid.storm.authz.sa.AuthzDBReaderInterface
     *   method
     */
    public void setAutomaticReload(boolean autoReload, int timeIntervalInSeconds) {
    }

    public PropertiesConfiguration getAuthzDB() {
        return null;
    }
}
