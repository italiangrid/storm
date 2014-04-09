package it.grid.storm.catalogs;

import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.PersistenceDirectorLegacy;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.exceptions.PersistenceException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolManagerStrategy implements ConnectionPoolManager {

	private DataSourceConnectionFactory connFactory = PersistenceDirectorLegacy
			.getConnectionFactory();

	private static final Logger log = LoggerFactory
			.getLogger(ConnectionPoolManager.class);

	@Override
	public Connection setUpConnection() {

		Connection con = null;

		try {
			con = connFactory.borrowConnection();
		} catch (PersistenceException e) {
			log.error("REQUEST SUMMARY DAO! Exception in setUpConnection! {}",
					e.getMessage(), e);
		}

		return con;
	}

	@Override
	public void takeDownConnection(Connection con) {

		if (con != null) {
			try {
				connFactory.giveBackConnection(con);
			} catch (PersistenceException e) {
				log.error(
						"REQUEST SUMMARY DAO! Exception in takeDownConnection "
								+ "method: {}", e.getMessage(), e);
			}
		}
	}
}
