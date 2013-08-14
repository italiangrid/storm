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

package it.grid.storm.persistence.util.db;

import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.exceptions.PersistenceException;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnectionPool implements DataSourceConnectionFactory {

	private static final Logger log = LoggerFactory
		.getLogger(DBConnectionPool.class);
	private DataBaseStrategy db;
	private static SharedPoolDataSource sharedDatasource;
	private static DBConnectionPool instance = new DBConnectionPool();
	private static long handle = -1;

	/***********************************************************
	 * CLASS Constructors
	 */

	/**
	 * Private constructor. Singleton pattern.
	 */
	private DBConnectionPool() {

		super();
	}

	public static DBConnectionPool getPoolInstance() {

		if (handle == -1) {
			return null; 
		} else {
			return instance;
		}
	}

	/**
	 * 
	 * @param db
	 *          DataBase
	 * @param maxActive
	 *          int
	 * @param maxWait
	 *          int
	 */
	public static void initPool(DataBaseStrategy db, int maxActive, int maxWait)
		throws PersistenceException {

		instance.init(db, maxActive, maxWait);
	}

	/***********************************************************
	 * PUBLIC METHODs
	 */

	/**
	 * 
	 * @return Connection
	 * @throws PersistenceException
	 */
	public Connection borrowConnection() throws PersistenceException {

		Connection result = null;
		if (handle == -1) {
			throw new PersistenceException("Connection Pool is not initialized!");
		}
		try {
			result = sharedDatasource.getConnection();
		} catch (SQLException sqle) {
			log.error("SQLException: " + sqle.getMessage() + "/n" + "SQLState: "
				+ sqle.getSQLState() + "/n" + "VendorError: " + sqle.getErrorCode(),
				sqle);
			throw new PersistenceException("Problem retrieving connection from pool",
				sqle);
		}
		return result;
	}

	/**
	 * 
	 * @param con
	 *          Connection
	 * @throws PersistenceException
	 */
	public void giveBackConnection(Connection con) throws PersistenceException {

		if (con != null) {
			try {
				shutdown(con);
			} catch (SQLException sqle) {
				log.error("SQLException: " + sqle.getMessage() + "/n" + "SQLState: "
					+ sqle.getSQLState() + "/n" + "VendorError: " + sqle.getErrorCode(),
					sqle);
				throw new PersistenceException("Closing existing connection problem",
					sqle);
			}
		} else {
			throw new PersistenceException("Closing NON-Existing connection");
		}
	}

	/********************************************
	 * VALIDATION Methods
	 */

	/**
	 * 
	 * @return String
	 * @throws PersistenceException
	 */
	public String getPoolInfo() throws PersistenceException {

		String result = "";
		if (handle == -1) {
			throw new PersistenceException("Connection Pool is not initialized!");
		}
		if (sharedDatasource.getValidationQuery() != null) {
			result += "Validation query = " + sharedDatasource.getValidationQuery()
				+ "\n";
		}
		if (sharedDatasource.getDescription() != null) {
			result += "Description = " + sharedDatasource.getDescription() + "\n";
		}
		result += "Nr Connection Active = " + sharedDatasource.getNumActive()
			+ "\n";
		result += "Nr Connection Idle = " + sharedDatasource.getNumIdle() + "\n";
		result += "Nr Max Active Connection = " + sharedDatasource.getMaxActive()
			+ "\n";

		return result;
	}

	/***********************************************************
	 * PRIVATE METHODs
	 */

	/**
	 * 
	 * @param db
	 *          DataBase
	 * @param maxActive
	 *          int
	 * @param maxWait
	 *          int
	 */
	private void init(DataBaseStrategy db, int maxActive, int maxWait) {

		instance.setDatabaseStrategy(db);
		DriverAdapterCPDS connectionPoolDatasource = new DriverAdapterCPDS();
		try {
			connectionPoolDatasource.setDriver(db.getDriverName());
		} catch (Exception ex) {
			log.error("Exception while getting driver", ex);
		}

		connectionPoolDatasource.setUrl(db.getConnectionString());
		connectionPoolDatasource.setUser(db.getDbUsr());
		connectionPoolDatasource.setPassword(db.getDbPwd());

		sharedDatasource = new SharedPoolDataSource();
		sharedDatasource.setConnectionPoolDataSource(connectionPoolDatasource);

		sharedDatasource.setMaxActive(maxActive);
		sharedDatasource.setMaxWait(maxWait);

		handle = System.currentTimeMillis();
	}

	/**
	 * 
	 * @throws SQLException
	 */
	private void shutdown(Connection conn) throws SQLException {

		conn.close();
		conn = null;
	}
	
	public static void printInfo(DBConnectionPool pool) {

		try {
			log.info("DATABASE POOL INFO: " + pool.getPoolInfo());
		} catch (PersistenceException ex2) {
			log.error(ex2.getMessage());
		}

	}

	public DataBaseStrategy getDatabaseStrategy() {

		return db;
	}

	private void setDatabaseStrategy(DataBaseStrategy db) {

		this.db = db;
	}

}
