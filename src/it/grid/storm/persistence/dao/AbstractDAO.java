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

    // private DataBaseStrategy db;

    public AbstractDAO() {
        // db = PersistenceDirector.getDataBase();
        connFactory = PersistenceDirector.getConnectionFactory();
    }

    /**
     * Retrieve a connection Accessor method.
     * 
     * @return Connection
     * @throws DataAccessException
     */
    public Connection getConnection() throws DataAccessException {
        // Retrieve a Connection
        Connection conn = null;
        try {
            conn = connFactory.borrowConnection();
        } catch (PersistenceException ex) {
            throw new DataAccessException(ex);
        }
        return conn;
    }

    /**
     * Release a connection Accessor method.
     * 
     * @param resultSet ResultSet
     * @param statement Statement
     * @param connection Connection
     * @throws DataAccessException
     */
    public void releaseConnection(ResultSet resultSet, Statement statement, Connection connection)
            throws DataAccessException {

        // Release the ResultSet
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex1) {
                log.error("Error while releasing the result set");
                throw new DataAccessException(ex1);
            }
        }

        // Close the statement
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex2) {
                log.error("Error while releasing the statement");
                throw new DataAccessException(ex2);
            }
        }

        // Release the connection
        if (connection != null) {
            try {
                connFactory.giveBackConnection(connection);
            } catch (PersistenceException ex3) {
                log.error("Error while releasing the connection");
                throw new DataAccessException(ex3);
            }
        }

    }

    /**
     * Retrieve a Statement from connetion Accessor method.
     * 
     * @param conn Connection
     * @return Statement
     * @throws DataAccessException
     */
    public Statement getStatement(Connection conn) throws DataAccessException {
        Statement stat = null;
        if (conn == null) {
            throw new DataAccessException("No Connection available to create a Statement");
        } else {
            try {
                stat = conn.createStatement();
            } catch (SQLException ex1) {
                log.error("Error while creating the statement");
                throw new DataAccessException(ex1);
            }
        }
        return stat;
    }

}
