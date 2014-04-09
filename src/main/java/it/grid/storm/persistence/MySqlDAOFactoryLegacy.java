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
import it.grid.storm.persistence.util.db.DBConnection;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DBConnectionPoolLegacy;
import it.grid.storm.persistence.util.db.DataBaseStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDAOFactoryLegacy  {

	public static final String factoryName = "JDBC - MySQL DAO Factory";

	private static final Logger log = LoggerFactory
		.getLogger(MySqlDAOFactoryLegacy.class);

	private static final DataBaseStrategy datasource = DataBaseStrategy.MYSQL;
	private static DataSourceConnectionFactory connFactory = null;
	private static MySqlDAOFactoryLegacy factory = new MySqlDAOFactoryLegacy();

	static {
		MySqlDAOFactoryLegacy.initializeDataSource();
	}

	/**
     *
     */
	private MySqlDAOFactoryLegacy() {
		log.info("DAO factory: {}", MySqlDAOFactoryLegacy.factoryName);
	}

	public static MySqlDAOFactoryLegacy getInstance() {

		return MySqlDAOFactoryLegacy.factory;
	}

	private static void initializeDataSource() {

		Configuration config = Configuration.getInstance();

		datasource.setDbUrl(config.getBEPersistenceDBMSUrl());
		datasource.setDbName(config.getBEPersistenceDBNameLegacy());		
		datasource.setDbUsr(config.getBEPersistenceDBUserName());
		datasource.setDbPwd(config.getBEPersistenceDBPassword());

		boolean pool = config.getBEPersistencePoolDB();
		if (pool) {
			int maxActive = config.getBEPersistencePoolDBMaxActive();
			int maxWait = config.getBEPersistencePoolDBMaxWait();
			try {
				DBConnectionPoolLegacy.initPool(MySqlDAOFactoryLegacy.datasource, maxActive,
					maxWait);
			} catch (PersistenceException e) {
			  log.error(e.getMessage(), e);
			}
			MySqlDAOFactoryLegacy.connFactory = DBConnectionPoolLegacy.getPoolInstance();
		} else {
			try {
				MySqlDAOFactoryLegacy.connFactory = new DBConnection(
					MySqlDAOFactoryLegacy.datasource);
			} catch (PersistenceException e) {
			  log.error(e.getMessage(), e);
			}
		}
	}

	

	/**
	 * @return String
	 */
	@Override
	public String toString() {

		return MySqlDAOFactoryLegacy.factoryName;
	}


}
