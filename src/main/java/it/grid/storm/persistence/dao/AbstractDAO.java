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

    /**
     * 
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractDAO.class);

    /**
     * 
     */
    private DataSourceConnectionFactory connFactory;


    /**
     * 
     */
    public AbstractDAO()
    {
        connFactory = PersistenceDirector.getConnectionFactory();
    }


    /**
     * @param conn
     */
    protected void commit(Connection conn)
    {
        try
        {
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (SQLException e)
        {
            log.error("Cannot commit transaction", e);
        }
    }

    /**
     * Retrieve a connection Accessor method.
     * 
     * @return Connection
     * @throws DataAccessException
     */
    protected Connection getConnection() throws DataAccessException
    {
        // Retrieve a Connection
        Connection conn = null;
        try
        {
            conn = connFactory.borrowConnection();
        }
        catch (PersistenceException ex)
        {
            throw new DataAccessException(ex);
        }
        return conn;
    }

    /**
     * Retrieve a Statement from connection Accessor method.
     * 
     * @param conn Connection
     * @return Statement
     * @throws DataAccessException
     */
    protected Statement getStatement(Connection conn) throws DataAccessException
    {
        Statement stat = null;
        if (conn == null)
        {
            throw new DataAccessException("No Connection available to create a Statement");
        }
        else
        {
            try
            {
                stat = conn.createStatement();
            }
            catch (SQLException ex1)
            {
                log.error("Error while creating the statement");
                throw new DataAccessException(ex1);
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
    protected void releaseConnection(ResultSet[] resultSets, Statement[] statements, Connection connection) throws DataAccessException {
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
    
    private void closeResultSet(ResultSet resultSet) throws DataAccessException
    {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex1) {
                log.error("Error while releasing the result set");
                throw new DataAccessException(ex1);
            }
        }
    }
    

    private void closeStatement(Statement statement) throws DataAccessException {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex2) {
                log.error("Error while releasing the statement");
                throw new DataAccessException(ex2);
            }
        }
    }
    

    private void closeConnection(Connection connection) throws DataAccessException {
        if (connection != null) {
            try {
                connFactory.giveBackConnection(connection);
            }
            catch (PersistenceException ex3) {
                log.error("Error while releasing the connection");
                throw new DataAccessException(ex3);
            }
        }
    }

    
    /**
     * @param conn
     */
    protected void rollback(Connection conn) {
        try {
            
            conn.rollback();
            conn.setAutoCommit(true);
            
        } catch (SQLException e) {
            log.error("Cannot rollback transaction", e);
        }
    }

}
