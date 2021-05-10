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

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnectionPool implements DataSourceConnectionFactory {

	private static final Logger log = LoggerFactory
		.getLogger(DBConnectionPool.class);
	private DataBaseStrategy db;
	private static SharedPoolDataSource sharedDatasource;
	private static DBConnectionPool instance = new DBConnectionPool();
	private static long handle = -1;

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

	public static void initPool(DataBaseStrategy db, int maxActive, int maxWait)
		throws PersistenceException {
		instance.init(db, maxActive, maxWait);
	}
	
	
	private void handleSQLException(SQLException e) throws PersistenceException{
	  
		  log.error("SQL Error: {}, SQLState: {}, VendorError: {}.",
		    e.getMessage(),
		    e.getSQLState(),
		    e.getErrorCode(),
		    e);

			throw new PersistenceException(e);
	  
	}
	public Connection borrowConnection() throws PersistenceException {

		Connection result = null;
		if (handle == -1) {
			throw new PersistenceException("Connection Pool is not initialized!");
		}
		try {
			result = sharedDatasource.getConnection();
		} catch (SQLException e) {
		  handleSQLException(e);
		}
		return result;
	}

	public void giveBackConnection(Connection con) throws PersistenceException {

		if (con != null) {
			try {
				shutdown(con);
			} catch (SQLException e) {
			  handleSQLException(e);
			}
		} else {
			throw new PersistenceException("Closing NON-Existing connection");
		}
	}

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
		result += "Nr Max Active Connection = " + sharedDatasource.getMaxTotal()
			+ "\n";

		return result;
	}

	private void init(DataBaseStrategy db, int maxActive, int maxWait) {

		instance.setDatabaseStrategy(db);
		DriverAdapterCPDS connectionPoolDatasource = new DriverAdapterCPDS();
		try {
			connectionPoolDatasource.setDriver(db.getDriverName());
		} catch (Exception ex) {
			log.error("Exception while getting driver: {}", ex.getMessage(), ex);
		}

		connectionPoolDatasource.setUrl(db.getConnectionString());
		log.debug("connection string: {}", db.getConnectionString());
		connectionPoolDatasource.setUser(db.getDbUsr());
		connectionPoolDatasource.setPassword(db.getDbPwd());

		sharedDatasource = new SharedPoolDataSource();
		sharedDatasource.setConnectionPoolDataSource(connectionPoolDatasource);

		sharedDatasource.setMaxTotal(maxActive);
		sharedDatasource.setDefaultMaxWaitMillis(maxWait);
		sharedDatasource.setValidationQuery("SELECT 1");
		sharedDatasource.setDefaultTestOnBorrow(true);

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
			log.info("DATABASE POOL INFO: {}" , pool.getPoolInfo());
		} catch (PersistenceException ex2) {
			log.error(ex2.getMessage(),ex2);
		}

	}

	public DataBaseStrategy getDatabaseStrategy() {

		return db;
	}

	private void setDatabaseStrategy(DataBaseStrategy db) {

		this.db = db;
	}

}
