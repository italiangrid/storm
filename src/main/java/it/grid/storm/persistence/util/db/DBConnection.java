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
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection implements DataSourceConnectionFactory

{

	private static final Logger log = LoggerFactory.getLogger(DBConnection.class);
	private Connection connection = null;
	private DataBaseStrategy db;

	public DBConnection(DataBaseStrategy db) throws PersistenceException {

		this.db = db;

		try {
			Class.forName(db.getDriverName()).newInstance();
		} catch (Exception ex) {
			log.error("Exception while getting JDBC driver: {}", ex.getMessage(), ex);
			throw new PersistenceException("Driver loading problem", ex);
		}
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
		try {
			result = getConnection();
		} catch (SQLException e) {
		  handleSQLException(e);
		}
		return result;
	}

	public void giveBackConnection(Connection con) throws PersistenceException {

		if (connection != null) {
			try {
				shutdown();
			} catch (SQLException e) {
			  handleSQLException(e);
			}
		} else {
			throw new PersistenceException("Closing NON-Existing connection");
		}
	}

	private Connection getConnection() throws SQLException {

		if (connection == null) {
			String url = db.getConnectionString();
			connection = DriverManager.getConnection(url, db.getDbUsr(),
				db.getDbPwd());
		}
		return connection;
	}

	private void shutdown() throws SQLException {

		connection.close(); // if there are no other open connection
		connection = null;
	}

}
