/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.db;

import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.exceptions.PersistenceException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection implements DataSourceConnectionFactory {

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

  private void handleSQLException(SQLException e) throws PersistenceException {

    log.error(
        "SQL Error: {}, SQLState: {}, VendorError: {}.",
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
      connection = DriverManager.getConnection(url, db.getDbUsr(), db.getDbPwd());
    }
    return connection;
  }

  private void shutdown() throws SQLException {

    connection.close(); // if there are no other open connection
    connection = null;
  }
}
