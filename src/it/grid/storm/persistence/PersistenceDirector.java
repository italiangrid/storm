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

package it.grid.storm.persistence;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.PersistenceException;
import it.grid.storm.persistence.util.db.DBConnection;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DataBaseStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistenceDirector {

    private static final Logger log = LoggerFactory.getLogger("persistence");
    private static Configuration config = Configuration.getInstance();
    private static String dbVendor;
    private static DataBaseStrategy dbMan;
    private static DAOFactory daoFactory;
    private static DataSourceConnectionFactory connFactory;

    static {
        log.debug("Inizializating Persistence Director...");
        dbMan = initializeDataBase();
        daoFactory = initializeFactory();
        connFactory = connectToDateSource();
    }

    private static DataBaseStrategy initializeDataBase()
    {
        dbVendor = config.getBEPersistenceDBVendor();
        log.debug("DBMS Vendor =  "+dbVendor);
        log.debug("DBMS URL    =  "+config.getBEPersistenceDBMSUrl());
        return DataBaseStrategy.getInstance(dbVendor);
    }

    /**
     * Use an ad hoc String mapping scheme, and introduce an if-else
     * branch for each alternative.
     */
    private static DAOFactory initializeFactory() {
        if (dbVendor.equalsIgnoreCase("MySql")) {
            return MySqlDAOFactory.getInstance();
        }
        else if (dbVendor.equalsIgnoreCase("memory")) {
            return new MemoryDAOFactory();
        }
        else if (dbVendor.equalsIgnoreCase("mock")) {
            return new MockDAOFactory();
        }
        else {
            log.error("Persistence Data Source was setted in a BAD way..");
            throw new IllegalArgumentException("Unknown datastore identifier.");
        }
    }

    private static DataSourceConnectionFactory connectToDateSource()
    {
        DataSourceConnectionFactory result = null;
        //Collect Logging information
        StringBuffer sf = new StringBuffer();
        sf.append("Connecting to Data Source..."+"\n");
        sf.append("  Connection String = "+dbMan.getConnectionString()+"\n");

        boolean poolMode = config.getBEPersistencePoolDB();
        int maxActive = config.getBEPersistencePoolDBMaxActive();
        int maxWait = config.getBEPersistencePoolDBMaxWait();
        sf.append("  Pool Mode = "+poolMode+"\n");
        if (poolMode)
        {
            sf.append("    pool Max Active = "+ maxActive +"\n");
            sf.append("    pool Max Wait =  "+ maxWait +"\n");
        }
        log.debug(sf.toString());

        if (poolMode)
        {
            log.debug("Setup Connection Pool");
            try {
                DBConnectionPool.initPool(dbMan, maxActive, maxWait);
                result = DBConnectionPool.getPoolInstance();
            }
            catch (PersistenceException ex1) {
                log.error("Connection In Pool Mode to Data Source FAIL ", ex1);
            }
        }
        else
        {
            log.debug("Setup Shared Connection");
            try {
                result = new DBConnection(dbMan);
            }
            catch (PersistenceException ex) {
                log.error("Connection to Data Source FAIL", ex);
            }
        }
        return result;
    }



    public static DAOFactory getDAOFactory()
    {
        return daoFactory;
    }

    public static DataBaseStrategy getDataBase()
    {
        return dbMan;
    }

    public static DataSourceConnectionFactory getConnectionFactory()
    {
        return connFactory;
    }

    public static Logger getLogger() {
        return log;
    }

}
