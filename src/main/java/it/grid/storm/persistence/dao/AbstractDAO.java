/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */

package it.grid.storm.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.pool.impl.DefaultDatabaseConnectionPool;

public abstract class AbstractDAO {

  private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

  private final DefaultDatabaseConnectionPool connectionPool;

  public AbstractDAO(DefaultDatabaseConnectionPool connectionPool) {

    this.connectionPool = connectionPool;
  }

  protected Connection getConnection() throws SQLException {

    Connection con = connectionPool.getConnection();
    con.setAutoCommit(true);
    return con;
  }

  protected Connection getManagedConnection() throws SQLException {

    Connection con = connectionPool.getConnection();
    con.setAutoCommit(false);
    return con;
  }

  protected void closeResultSet(ResultSet resultSet) {

    try {
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException e) {
      handleSQLException(e);
    }
  }

  protected void closeStatement(Statement statement) {

    try {
      if (statement != null) {
        statement.close();
      }
    } catch (SQLException e) {
      handleSQLException(e);
    }
  }

  protected void closeConnection(Connection connection) {

    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      handleSQLException(e);
    }
  }

  private void handleSQLException(SQLException e) {

    log.error("SQL Error: {}, SQLState: {}, VendorError: {}.", e.getMessage(), e.getSQLState(),
        e.getErrorCode(), e);
    e.printStackTrace();

  }
}