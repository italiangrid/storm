package it.grid.storm.authz.sa;

import org.apache.commons.configuration.PropertiesConfiguration;


import it.grid.storm.authz.sa.conf.*;

public interface AuthzDBReaderInterface {

    public void setAutomaticReload(boolean autoReload, int timeIntervalInSeconds);

    public void bindDB(String dbName)  throws AuthzDBReaderException;

    public void loadAuthZDB() throws AuthzDBReaderException;

    public PropertiesConfiguration getAuthzDB();

    public void printAuthzDB();

}
