/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;
import it.grid.storm.authz.sa.model.FileAuthzDB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;


public class FileAuthzDBReader implements AuthzDBReaderInterface {

    private final Logger log = AuthzDirector.getLogger();

    private long period = 5000; // repeat every sec.

    private String authzDBPath;

    //Watcher for changin on AuthzDB files
    private final FileAuthzDBWatcher authzDBWatcher;

    //Mapping between AuthzDBName and FileAuthzDB
    private Map<String, FileAuthzDB> authzDBs;

    //Mapping between AuthzDBName and Parsed time
    private Map<String, Long> parsedTime;


    /**
     * @throws AuthzDBReaderException
     *
     */
    public FileAuthzDBReader(long period, String authzDBPath) throws AuthzDBReaderException {
        this.authzDBPath = authzDBPath;
        authzDBWatcher = new FileAuthzDBWatcher(period, authzDBPath);
        authzDBs = new HashMap<String, FileAuthzDB>();
    }


    /*
     * 
     */
    public void addAuthzDB(String dbFileName) throws AuthzDBReaderException {
        authzDBWatcher.watchAuthzDBFile(getAbsoluteAuthzDBName(dbFileName));
    }





    public AuthzDBInterface getAuthzDB(String dbFileName) throws AuthzDBReaderException {
        String authzDBName = getAuthzDBName(dbFileName);
        if (authzDBs.containsKey(authzDBName)) {
            return authzDBs.get(authzDBName);
        } else {
            throw new AuthzDBReaderException("Unable to retrieve '"+authzDBName+"'.");
        }
    }


    public List<String> getAuthzDBNames() {
        return new ArrayList<String>(authzDBs.keySet());
    }


    public long getLastParsed(String dbFileName) throws AuthzDBReaderException {
        String authzDBFileName = getAbsoluteAuthzDBName(dbFileName);
        File authzFile = new File(authzDBFileName);
        authzFile.lastModified();
        return 0;
    }


    /*
     * 
     */
    public void onChangeAuthzDB(String authzDBName) throws AuthzDBReaderException {
        //Parsing of Authz DB.
        try {
            PropertiesConfiguration authzdb = new PropertiesConfiguration(authzDBName);
            FileAuthzDB fileAuthzDB = new FileAuthzDB(authzdb);
            authzDBs.put(authzDBName, fileAuthzDB);
            long pTime = System.currentTimeMillis();
            parsedTime.put(authzDBName, new Long(pTime));
            log.debug("Bound FileAuthzDBReader with " + authzDBName);
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            throw new AuthzDBReaderException("Unable to parse the AuthzDB '"+authzDBName+"'.");
        }
        //Notify the watcher the accomplished parsing
        authzDBWatcher.authzDBParsed(authzDBName);
    }


    /***********************************************
     * UTILITY Methods
     */

    private String getAbsoluteAuthzDBName(String dbFileName) {
        String absoluteName = null;
        if (!(dbFileName.contains(File.separator))) {
            //dbFileName is only the name
            if ((authzDBPath==null) || (authzDBPath.equals(""))) {
                authzDBPath = System.getProperty("user.dir") + File.separator + "etc";
            }
            dbFileName = authzDBPath + File.separator + dbFileName;
        }
        File f = new File(dbFileName);
        absoluteName = f.getAbsolutePath();
        return absoluteName;
    }

    private String getAuthzDBName(String dbFileName) {
        String authzName = dbFileName;
        if (dbFileName.contains(File.separator)) {
            authzName = dbFileName.substring(dbFileName.indexOf(File.separator));
        }
        return authzName;
    }

}

