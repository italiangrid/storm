/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.persistence;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.PersistenceException;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DataBaseStrategy;
import it.grid.storm.persistence.util.db.Databases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceDirector {

	private static final Logger log = LoggerFactory.getLogger("persistence");
	private static Configuration config;
	private static DataBaseStrategy dbMan;
	private static DAOFactory daoFactory;
	private static DataSourceConnectionFactory connFactory;

	static {
		log.trace("Initializing Persistence Director...");
		config = Configuration.getInstance();
		dbMan = Databases.getDataBaseStrategy("mysql");
		daoFactory = MySqlDAOFactory.getInstance();

		int maxActive = config.getBEPersistencePoolDBMaxActive();
		int maxWait = config.getBEPersistencePoolDBMaxWait();

		log.debug("Datasource connection string = {}", dbMan.getConnectionString());
		log.debug("Pool Max Active = {}", maxActive);
		log.debug("Pool Max Wait = {}", maxWait);

		try {
			DBConnectionPool.initPool(dbMan, maxActive, maxWait);
			connFactory = DBConnectionPool.getPoolInstance();
		} catch (PersistenceException e) {
		    log.error(e.getMessage(), e);
		    System.exit(1);
		}
	}

	public static DAOFactory getDAOFactory() {

		return daoFactory;
	}

	public static DataBaseStrategy getDataBase() {

		return dbMan;
	}

	public static DataSourceConnectionFactory getConnectionFactory() {

		return connFactory;
	}

	public static Logger getLogger() {

		return log;
	}

}
