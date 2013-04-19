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

package it.grid.storm.persistence;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.dao.CopyChunkDAO;
import it.grid.storm.persistence.dao.PermissionDAO;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.exceptions.PersistenceException;
import it.grid.storm.persistence.impl.mysql.StorageSpaceDAOMySql;
import it.grid.storm.persistence.impl.mysql.TapeRecallDAOMySql;
import it.grid.storm.persistence.util.db.DBConnection;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DataBaseStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDAOFactory implements DAOFactory {

	public static final String factoryName = "JDBC - MySQL DAO Factory";

	private static final Logger log = LoggerFactory
		.getLogger(MySqlDAOFactory.class);
	private static final DataBaseStrategy datasource = DataBaseStrategy.MYSQL;
	private static DataSourceConnectionFactory connFactory = null;
	private static MySqlDAOFactory factory = new MySqlDAOFactory();

	static {
		MySqlDAOFactory.initializeDataSource();
	}

	/**
     *
     */
	private MySqlDAOFactory() {

		super();
		MySqlDAOFactory.log.info("Choose " + MySqlDAOFactory.factoryName);
	}

	public static MySqlDAOFactory getInstance() {

		return MySqlDAOFactory.factory;
	}

	private static void initializeDataSource() {

		Configuration config = Configuration.getInstance();
		MySqlDAOFactory.datasource.setDbUrl(config.getBEPersistenceDBMSUrl());
		MySqlDAOFactory.datasource.setDbName(config.getBEPersistenceDBName());
		MySqlDAOFactory.datasource.setDbUsr(config.getBEPersistenceDBUserName());
		MySqlDAOFactory.datasource.setDbPwd(config.getBEPersistenceDBPassword());

		boolean pool = config.getBEPersistencePoolDB();
		if (pool) {
			int maxActive = config.getBEPersistencePoolDBMaxActive();
			int maxWait = config.getBEPersistencePoolDBMaxWait();
			try {
				DBConnectionPool.initPool(MySqlDAOFactory.datasource, maxActive,
					maxWait);
			} catch (PersistenceException ex) {
				MySqlDAOFactory.log.error("Exception while setting up Pool", ex);
			}
			MySqlDAOFactory.connFactory = DBConnectionPool.getPoolInstance();
		} else {
			try {
				MySqlDAOFactory.connFactory = new DBConnection(
					MySqlDAOFactory.datasource);
			} catch (PersistenceException ex1) {
				MySqlDAOFactory.log.error("Exception while setting up DB connection",
					ex1);
			}
		}
		log.debug("RECALL TABLE Catalog/DAO ");

	}

	/**
	 * Returns an implementation of StorageSpaceCatalog, specific to a particular
	 * datastore.
	 * 
	 * @throws DataAccessException
	 * @return StorageSpaceDAO
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException {

		return new StorageSpaceDAOMySql();
	}

	/**
	 * Returns an implementation of TapeRecallCatalog, specific to a particular
	 * datastore.
	 * 
	 * @throws DataAccessException
	 * @return TapeReallDAO
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public TapeRecallDAO getTapeRecallDAO() {

		return new TapeRecallDAOMySql();
	}

	/**
	 * @return String
	 */
	@Override
	public String toString() {

		return MySqlDAOFactory.factoryName;
	}

	/**
	 * NOT IMPLEMENTED!
	 */

	/**
	 * getCopyChunkDAO
	 * 
	 * @return CopyChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public CopyChunkDAO getCopyChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPermissionDAO
	 * 
	 * @return PermissionDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public PermissionDAO getPermissionDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPtGChunkDAO
	 * 
	 * @return PtGChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public PtGChunkDAO getPtGChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPtPChunkDAO
	 * 
	 * @return PtPChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public PtPChunkDAO getPtPChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getRequestSummaryDAO
	 * 
	 * @return RequestSummaryDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public RequestSummaryDAO getRequestSummaryDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getStorageAreaDAO
	 * 
	 * @return StorageAreaDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.persistence.DAOFactory method
	 */
	public StorageAreaDAO getStorageAreaDAO() throws DataAccessException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.persistence.DAOFactory#getTapeRecallDAO(boolean)
	 */
	public TapeRecallDAO getTapeRecallDAO(boolean test)
		throws DataAccessException {

		return new TapeRecallDAOMySql();
	}

}
