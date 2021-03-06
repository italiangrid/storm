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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.impl.mysql.StorageSpaceDAOMySql;
import it.grid.storm.persistence.impl.mysql.TapeRecallDAOMySql;

public class MySqlDAOFactory implements DAOFactory {

	public static final String factoryName = "JDBC - MySQL DAO Factory";

	private static final Logger log = LoggerFactory
		.getLogger(MySqlDAOFactory.class);

	private static MySqlDAOFactory factory = new MySqlDAOFactory();

	/**
     *
     */
	private MySqlDAOFactory() {
		log.info("DAO factory: {}", MySqlDAOFactory.factoryName);
	}

	public static MySqlDAOFactory getInstance() {

		return MySqlDAOFactory.factory;
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
