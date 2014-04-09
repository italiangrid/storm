package it.grid.storm.catalogs;

import java.sql.Connection;

public interface ConnectionPoolManager {

	/**
	 * Auxiliary method that sets up the connection to the DB, as well as the
	 * prepared statement.
	 */
	public Connection setUpConnection();
	
	
	/**
	 * Auxiliary method that tales down a connection to the DB.
	 */
	public void takeDownConnection(Connection con);
	
	
}
