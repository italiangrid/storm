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

package it.grid.storm.persistence.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.pool.DBConnectionPool;

public abstract class AbstractDAO {

  private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

  private final DBConnectionPool connectionPool;

  public AbstractDAO(DBConnectionPool connectionPool) {

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
