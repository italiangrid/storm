package it.grid.storm.persistence;

import java.sql.Connection;
import it.grid.storm.persistence.exceptions.PersistenceException;


public interface DataSourceConnectionFactory {

   public Connection borrowConnection() throws PersistenceException;

   public void giveBackConnection(Connection con) throws PersistenceException;

}
