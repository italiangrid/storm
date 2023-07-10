/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.exceptions.PersistenceException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDAO {

  private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

  private DataSourceConnectionFactory connFactory;

  public AbstractDAO() {
    connFactory = PersistenceDirector.getConnectionFactory();
  }

  protected void commit(Connection conn) {

    try {
      conn.commit();
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
  }

  protected Connection getConnection() throws DataAccessException {

    Connection conn = null;
    try {
      conn = connFactory.borrowConnection();
    } catch (PersistenceException ex) {
      throw new DataAccessException(ex);
    }
    return conn;
  }

  protected Statement getStatement(Connection conn) throws DataAccessException {

    Statement stat = null;
    if (conn == null) {
      throw new DataAccessException("No Connection available to create a Statement");
    } else {
      try {
        stat = conn.createStatement();
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DataAccessException(e);
      }
    }
    return stat;
  }

  /**
   * Release a connection Accessor method.
   *
   * @param resultSet ResultSet
   * @param statement Statement
   * @param connection Connection
   * @throws DataAccessException
   */
  protected void releaseConnection(ResultSet resultSet, Statement statement, Connection connection)
      throws DataAccessException {

    // Release the ResultSet
    closeResultSet(resultSet);

    // Close the statement
    closeStatement(statement);

    // Release the connection
    closeConnection(connection);
  }

  /**
   * Release a connection and a list of statements and result sets Accessor method.
   *
   * @param resultSets
   * @param statements
   * @param connection
   * @throws DataAccessException
   */
  protected void releaseConnection(
      ResultSet[] resultSets, Statement[] statements, Connection connection)
      throws DataAccessException {

    // Release the ResultSets
    if (resultSets != null) {
      for (ResultSet resultSet : resultSets) {
        closeResultSet(resultSet);
      }
    }
    // Close the statement
    if (statements != null) {
      for (Statement statement : statements) {
        closeStatement(statement);
      }
    }
    // Release the connection
    closeConnection(connection);
  }

  private void closeResultSet(ResultSet resultSet) throws DataAccessException {

    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DataAccessException(e);
      }
    }
  }

  private void closeStatement(Statement statement) throws DataAccessException {

    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DataAccessException(e);
      }
    }
  }

  private void closeConnection(Connection connection) throws DataAccessException {

    if (connection != null) {
      try {
        connFactory.giveBackConnection(connection);
      } catch (PersistenceException e) {
        log.error(e.getMessage(), e);
        throw new DataAccessException(e);
      }
    }
  }

  /** @param conn */
  protected void rollback(Connection conn) {

    try {

      conn.rollback();
      conn.setAutoCommit(true);

    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
  }
}
