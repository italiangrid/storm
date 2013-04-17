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

/*
 * (c)2004 INFN / ICTP-eGrid This file can be distributed and/or modified under
 * the terms of the INFN Software License. For a copy of the licence please
 * visit http://www.cnaf.infn.it/license.html
 */

/**
 * MemoryDAOFactory
 */
package it.grid.storm.persistence;

import it.grid.storm.persistence.dao.CopyChunkDAO;
import it.grid.storm.persistence.dao.PermissionDAO;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.dao.StorageAreaDAO;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;

/**
 * @author Riccardo Zappi - riccardo.zappi AT cnaf.infn.it
 * @version $Id: MemoryDAOFactory.java,v 1.3 2005/06/06 10:47:37 rzappi Exp $
 *          $Revision: 1.3 $
 */
public class MemoryDAOFactory implements DAOFactory {

	private final String factoryName = "MEMORY DAO Factory";

	/**
	 * getCopyChunkDAO
	 * 
	 * @return CopyChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public CopyChunkDAO getCopyChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPermissionDAO
	 * 
	 * @return PermissionDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public PermissionDAO getPermissionDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPtGChunkDAO
	 * 
	 * @return PtGChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public PtGChunkDAO getPtGChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getPtPChunkDAO
	 * 
	 * @return PtPChunkDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public PtPChunkDAO getPtPChunkDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getRequestSummaryDAO
	 * 
	 * @return RequestSummaryDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public RequestSummaryDAO getRequestSummaryDAO() throws DataAccessException {

		return null;
	}

	/**
	 * getStorageAreaDAO
	 * 
	 * @return StorageAreaDAO
	 * @throws DataAccessException
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public StorageAreaDAO getStorageAreaDAO() throws DataAccessException {

		return null;
	}

	/**
	 * Returns an implementation of StorageSpaceDAO, specific to a particular
	 * datastore.
	 * 
	 * @throws DataAccessException
	 * @return StorageSpaceDAO
	 * @todo Implement this it.grid.storm.catalog.persistence.CatalogFactory
	 *       method
	 */
	public StorageSpaceDAO getStorageSpaceDAO() throws DataAccessException {

		return null;
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

		return null;
	}

	@Override
	public String toString() {

		return factoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.persistence.DAOFactory#getTapeRecallDAO(boolean)
	 */
	public TapeRecallDAO getTapeRecallDAO(boolean test)
		throws DataAccessException {

		// TODO Auto-generated method stub
		return null;
	}
}
