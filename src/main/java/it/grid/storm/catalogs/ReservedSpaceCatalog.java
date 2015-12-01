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
 * ReservedSpaceCatalog
 */

package it.grid.storm.catalogs;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.DAOFactory;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */

public class ReservedSpaceCatalog {

	private static final Logger log = LoggerFactory
		.getLogger(ReservedSpaceCatalog.class);
	private static HashSet<TSpaceToken> voSA_spaceTokenSet = new HashSet<TSpaceToken>();
	private static HashMap<TSpaceToken, Date> voSA_UpdateTime = new HashMap<TSpaceToken, Date>();

	private static final long NOT_INITIALIZED_SIZE_VALUE = -1L;

	private final DAOFactory daoFactory;
	private StorageSpaceDAO ssDAO;

	/*********************************************
	 * STATIC METHODS
	 *********************************************/
	public static void addSpaceToken(TSpaceToken token) {

		voSA_spaceTokenSet.add(token);
		voSA_UpdateTime.put(token, null);
	}

	public static HashSet<TSpaceToken> getTokenSet() {

		return voSA_spaceTokenSet;
	}

	public static void clearTokenSet() {

		voSA_spaceTokenSet.clear();
		voSA_UpdateTime.clear();
	}

	public static void setUpdateTime(TSpaceToken token, Date updateTime) {

		if (voSA_UpdateTime.containsKey(token)) {
			voSA_UpdateTime.put(token, updateTime);
		} else {
			log.warn("Failing while Trying to set update time in Catalog cache.");
		}
	}

	public static Date getUpdateTime(TSpaceToken token) {

		Date result = null;
		if (voSA_UpdateTime.containsKey(token)) {
			result = voSA_UpdateTime.get(token);
		} else {
			log.warn("Failing while Trying to set update time in Catalog cache.");
		}
		return result;
	}

	/*********************************************
	 * CLASS METHODS
	 *********************************************/
	/**
	 * Default constructor
	 */
	public ReservedSpaceCatalog() {

		log.debug("Building Reserve Space Catalog...");
		// Binding to the persistence component
		daoFactory = PersistenceDirector.getDAOFactory();
	}

	/**
	 * Basic method used to retrieve all the information about a StorageSpace -
	 * StorageSpace is selected by SpaceToken
	 * 
	 * @param spaceToken
	 *          TSpaceToken
	 * @return StorageSpaceData, null if no-one SS exists with the specified
	 *         spaceToken
	 * @throws DataAccessException
	 */
	public StorageSpaceData getStorageSpace(TSpaceToken spaceToken)
		throws TransferObjectDecodingException, DataAccessException {

		StorageSpaceData result = null;
		ssDAO = daoFactory.getStorageSpaceDAO();
		log.debug("Storage Space DAO retrieved.");
		StorageSpaceTO ssTO = ssDAO.getStorageSpaceByToken(spaceToken.getValue());
		log.debug("Storage Space retrieved by Token. ");
		if (ssTO != null) {
			try {
				result = new StorageSpaceData(ssTO);
			} catch (IllegalArgumentException e) {
				log.error("Error building StorageSpaceData from StorageSpaceTO "
					+ "IllegalArgumentException: {}", e.getLocalizedMessage(), e);
				throw new TransferObjectDecodingException(
					"Unable to build StorageSpaceData from StorageSpaceTO");
			}
		} else {
			log.info("Unable to build StorageSpaceData. No StorageSpaceTO built "
				+ "from the DB");
		}
		return result;
	}

	/**
	 * Create a new StorageSpace entry into the DB. It is used for - STATIC Space
	 * Creation - DYNAMIC Space Reservation
	 * 
	 * @param ssd
	 * @throws NoDataFoundException
	 * @throws InvalidRetrievedDataException
	 * @throws MultipleDataEntriesException
	 */
	public void addStorageSpace(StorageSpaceData ssd) throws DataAccessException {

		log.debug("ADD StorageSpace Start...");
		StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
		log.debug("Storage Space TO Created");
		ssTO.setUpdateTime(new Date());
		ssDAO = daoFactory.getStorageSpaceDAO();
		log.debug("Storage Space DAO retrieved.");
		ssDAO.addStorageSpace(ssTO);
		log.debug("StorageSpaceTO inserted in Persistence");
	}

	/**
	 * Update all the fields apart from the alias of a storage space row given the
	 * input StorageSpaceData
	 * 
	 * @param ssd
	 */
	public void updateStorageSpace(StorageSpaceData ssd) {

		updateStorageSpace(ssd, null);
	}

	/**
	 * @param ssd
	 * @param updateTime
	 */
	public void updateStorageSpace(StorageSpaceData ssd, Date updateTime) {

		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(), 
				daEx);
		}

		StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
		log.debug("Storage Space TO Created");

		if (updateTime == null) {
			// The update time of the information is now
			ssTO.setUpdateTime(new Date());
		} else {
			ssTO.setUpdateTime(updateTime);
		}
		try {
			ssDAO.updateStorageSpace(ssTO);
			log.debug("StorageSpaceTO updated in Persistence");
		} catch (DataAccessException daEx) {
			log.error(
				"Error while inserting new row in StorageSpace", daEx);
		}

	}

	/**
	 * @param ssd
	 */
	public void updateStorageSpaceFreeSpace(StorageSpaceData ssd)
		throws DataAccessException {

		ssDAO = daoFactory.getStorageSpaceDAO();
		log.debug("Storage Space DAO retrieved.");
		StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
		log.debug("Storage Space TO Created");
		ssTO.setUpdateTime(new Date());
		ssDAO.updateStorageSpaceFreeSpace(ssTO);
		log.debug("StorageSpaceTO updated in Persistence");

	}

	/**
	 * @param ssd
	 * @throws NoDataFoundException
	 * @throws InvalidRetrievedDataException
	 * @throws MultipleDataEntriesException
	 */
	public void updateAllStorageSpace(StorageSpaceData ssd)
		throws NoDataFoundException, InvalidRetrievedDataException,
		MultipleDataEntriesException {

		updateAllStorageSpace(ssd, null);
	}

	/**
	 * Update StorageSpace. This method is used to update the StorageSpace into
	 * the ReserveSpace Catalog. The update operation take place after a
	 * AbortRequest for a PrepareToPut operation done with the spaceToken.(With or
	 * without the size specified).
	 */

	public void updateAllStorageSpace(StorageSpaceData ssd, Date updateTime)
		throws NoDataFoundException, InvalidRetrievedDataException,
		MultipleDataEntriesException {

		log.debug("UPDATE StorageSpace Start...");
		// Build StorageSpaceTO from SpaceData
		StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
		log.debug("Storage Space TO Created");
		if (updateTime == null) {
			// The update time of the information is now
			ssTO.setUpdateTime(new Date());
		} else {
			ssTO.setUpdateTime(updateTime);
		}

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}

		// Add the row to the persistence..
		try {
			ssDAO.updateAllStorageSpace(ssTO);
			log.debug("StorageSpaceTO updated in Persistence");
		} catch (DataAccessException daEx) {
			log.error("Error while inserting new row in StorageSpace: {}", 
				daEx.getMessage(), daEx);
		}
	}

	/**
	 * @param desc
	 * @return
	 */
	public StorageSpaceData getStorageSpaceByAlias(String desc) {

		StorageSpaceData result = null; // new StorageSpaceData();
		log.debug("Retrieve Storage Space start... ");

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}

		// Get StorageSpaceTO form persistence
		try {
			Collection<StorageSpaceTO> cl = ssDAO.getStorageSpaceByAliasOnly(desc);
			if (cl != null && !cl.isEmpty()) {
				log.debug("Storage Space retrieved by Token. ");
				// Build the result
				try {
					result = new StorageSpaceData(cl.toArray(new StorageSpaceTO[0])[0]);
				} catch (IllegalArgumentException e) {
					log.error("unable to build StorageSpaceData from StorageSpaceTO "
						+ "IllegalArgumentException: {}", e.getMessage(), e);
				}
			}
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpace: {}", daEx.getMessage(),
				daEx);
		}

		return result;
	}

	/**
	 * Provides a list of storage spaces not initialized by comparing the used
	 * space stored against the well know not initialized value
	 * <code>NOT_INITIALIZED_SIZE_VALUE<code>
	 * 
	 * @return SpaceData
	 */
	public List<StorageSpaceData> getStorageSpaceNotInitialized() {

		log.debug("Retrieve Storage Space not initialized start ");
		LinkedList<StorageSpaceData> result = new LinkedList<StorageSpaceData>();
		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}
		// Get StorageSpaceTO form persistence
		try {
			Collection<StorageSpaceTO> storagesSpaceTOCollection = ssDAO
				.getStorageSpaceByUnavailableUsedSpace(NOT_INITIALIZED_SIZE_VALUE);
			log.debug("Storage Space retrieved by not initialized used space. ");
			for (StorageSpaceTO storagesSpaceTO : storagesSpaceTOCollection) {
				if (storagesSpaceTO != null) {
					try {
						result.add(new StorageSpaceData(storagesSpaceTO));
					} catch (IllegalArgumentException e) {
						log.error("unable to build StorageSpaceData from StorageSpaceTO. "
							+ "IllegalArgumentException: {}", e.getMessage(), e);
					}
				} else {
					log.warn("Received a collection of StorageSpaceTO containing null "
						+ "elements, skipping them");
				}
			}
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpace", daEx);
		}
		return result;
	}

	/**
	 * Provides a list of storage spaces not updated since the provided timestamp
	 * 
	 * @param lastUpdateTimestamp
	 * @return
	 */

	public List<StorageSpaceData> getStorageSpaceByLastUpdate(
		Date lastUpdateTimestamp) {

		log.debug("Retrieve Storage Space not initialized start ");
		LinkedList<StorageSpaceData> result = new LinkedList<StorageSpaceData>();
		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}
		// GetStorageSpaceTO form persistence
		try {
			Collection<StorageSpaceTO> storagesSpaceTOCollection = ssDAO
				.getStorageSpaceByPreviousLastUpdate(lastUpdateTimestamp);
			log.debug("Storage Space retrieved by Token previous last update. ");
			for (StorageSpaceTO storagesSpaceTO : storagesSpaceTOCollection) {
				if (storagesSpaceTO != null) {
					try {
						result.add(new StorageSpaceData(storagesSpaceTO));
					} catch (IllegalArgumentException e) {
						log.error("unable to build StorageSpaceData from StorageSpaceTO "
							+ "IllegalArgumentException: {}", e.getMessage(), e);
					}
				} else {
					log.warn("Received a collection of StorageSpaceTO containing null "
						+ "elements, skipping them");
				}
			}
		} catch (DataAccessException daEx) {
			log.debug("Error while retrieving StorageSpace: {}", daEx.getMessage(),
				daEx);
		}
		return result;
	}

	/**
	 * 
	 * @param user
	 *          VomsGridUser
	 * @param spaceAlias
	 *          String
	 * @return ArrayOfTSpaceToken
	 */
	public ArrayOfTSpaceToken getSpaceTokens(GridUserInterface user,
		String spaceAlias) {

		ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

		log.debug("Retrieving space tokens...");

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}

		// Get StorageSpaceTO form persistence
		try {

			Collection<?> listOfStorageSpace = ssDAO.getStorageSpaceByOwner(user,
				spaceAlias);

			int nItems = listOfStorageSpace.size();
			log.debug("getSpaceTokens : Number of Storage spaces retrieved with "
				+ "Alias '{}': {}", spaceAlias, nItems);
			Iterator<?> j_ssTO = listOfStorageSpace.iterator();

			while (j_ssTO.hasNext()) {
				StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
				try {
					TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
					result.addTSpaceToken(spaceToken);
				} catch (InvalidTSpaceTokenAttributesException ex2) {
					log.error("Retrieved invalid Space token from DB");
				}
			}
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpace: {}", daEx.getMessage(),
				daEx);
		} catch (Exception e) {
			log.error("Exception while retrieving Storage Space: {}", e.getMessage(),
				e);
		}
		return result;
	}

	/**
	 * This method is used for the VOspaceArea Check.
	 * 
	 * @param spaceAlias
	 * @return
	 */

	public ArrayOfTSpaceToken getSpaceTokensByAlias(String spaceAlias) {

		ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

		log.debug("Retrieving space tokens...");

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}

		// Get StorageSpaceTO form persistence
		try {
			Collection<?> listOfStorageSpace = ssDAO
				.getStorageSpaceByAliasOnly(spaceAlias);

			int nItems = listOfStorageSpace.size();
			log.debug("Number of Storage spaces retrieved: {}", nItems);
			Iterator<?> j_ssTO = listOfStorageSpace.iterator();

			while (j_ssTO.hasNext()) {
				StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
				try {
					TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
					result.addTSpaceToken(spaceToken);
				} catch (InvalidTSpaceTokenAttributesException ex2) {
					log.error("Retrieved invalid Space token from DB");
				}
			}

		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpace: {}", daEx.getMessage(),
				daEx);
		} catch (Exception e) {
			log.error("Error getting data! Error: {}", e.getMessage(), e);
		}
		return result;
	}

	/**
	 * This method is used for the VOspaceArea Check.
	 * 
	 * @param VOname
	 * @return
	 */

	public ArrayOfTSpaceToken getSpaceTokensBySpaceType(String stype) {

		ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

		log.debug("Retrieving space tokens...");

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(),
				daEx);
		}

		// Get StorageSpaceTO form persistence
		try {
			Collection<?> listOfStorageSpace = ssDAO.getStorageSpaceBySpaceType(stype);

			int nItems = listOfStorageSpace.size();
			log.debug("Number of Storage spaces retrieved: {}", nItems);
			Iterator<?> j_ssTO = listOfStorageSpace.iterator();

			while (j_ssTO.hasNext()) {
				StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
				try {
					TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
					result.addTSpaceToken(spaceToken);
				} catch (InvalidTSpaceTokenAttributesException ex2) {
					log.error("Retrieved invalid Space token from DB");
				}
			}

		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpace: {}", daEx.getMessage(), daEx);
		} catch (Exception e) {
			log.error("Generic Error while retrieving StorageSpace: {}", e.getMessage(), e);
		}
		return result;
	}

	// ************************ CHECH BELOW METHODS ***************************

	/**
	 * 
	 * @param user
	 *          GridUserInterface
	 * @param spaceToken
	 *          TSpaceToken
	 * @return boolean
	 */
	public boolean release(GridUserInterface user, final TSpaceToken spaceToken) {

		log.debug("Delete storage spaceToken info from persistence: {}", spaceToken);

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", 
				daEx.getMessage(), daEx);
		}
		boolean rowRemoved = true;
		// Delete the row from persistence.
		try {
			ssDAO.removeStorageSpace(user, spaceToken.getValue());
			log.debug("spaceToken removed from DB.");
		} catch (DataAccessException daEx) {
			log.error("spaceToken not found in the DB: {}", spaceToken.getValue());
			rowRemoved = false;
		}
		return rowRemoved;
	}

	/**
	 * Method that purges the catalog, removing expired space reservation. The
	 * spacefile with lifetime expired are removed from the file systems.
	 * 
	 */
	public void purge() {

		log.debug("Space Garbage Collector start!");
		Calendar rightNow = Calendar.getInstance();

		// Retrieve the Data Access Object from the factory
		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}", daEx.getMessage(), 
				daEx);
		}
		// Get the Collection of Space Resrvation Expired
		Collection<?> expiredSpaceTO;
		try {
			expiredSpaceTO = ssDAO.getExpired(rightNow.getTimeInMillis() / 1000);
		} catch (DataAccessException e) {
			// No space expired FOUND
			log.debug("Space Garbage Collector: no space expired found.");
			return;
		}

		// For each entry expired
		// 1) Delete the related space file
		// 2) Remove the entry from the DB

		StorageSpaceTO spaceTO = null;
		log.debug("Space Garbage Collector: Number of SpaceFile to remove {}.", 
			expiredSpaceTO.size());

		for (Iterator<?> i = expiredSpaceTO.iterator(); i.hasNext();) {
			spaceTO = (StorageSpaceTO) i.next();
			// Deleteing space File
			String spaceFileName = spaceTO.getSpaceFile();
			File sfile = new File(spaceFileName);
			log.debug("Space Garbage Collector: SpaceFile to remove {}.", spaceFileName);

			if (sfile.delete()) {
				log.debug("Space Garbage Collector: SpaceFile {} removed.", spaceFileName);
			} else {
				log.warn("Space Garbage Collector: problem removing {}", spaceFileName);
			}

			// Removing space entry from the DB
			try {
				ssDAO.removeStorageSpace(spaceTO.getSpaceToken());
			} catch (DataAccessException e) {
				log.warn("Space Garbage Collector: error removing space entry from catalog.");
			}

		}

	}
	
	public boolean increaseUsedSpace(String spaceToken, Long usedSpaceToAdd) {
	
		log.debug("Increase {} the used space of storage spaceToken: {}",
			usedSpaceToAdd, spaceToken);

		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}",
				daEx.getMessage(), daEx);
			return false;
		}
		int n = 0;
		try {
			n = ssDAO.increaseUsedSpace(spaceToken, usedSpaceToAdd);
		} catch (DataAccessException daEx) {
			log.error(
				"Error during the increase of used space for spaceToken {}: {}",
				spaceToken, daEx.getMessage());
			return false;
		}
		if (n == 0) {
			log.warn(
				"No errors caught but it seems no used space updates done on space token {}",
				spaceToken);
		}
		return n > 0;
	}

	public boolean decreaseUsedSpace(String spaceToken, Long usedSpaceToRemove) {
		
		log.debug("Decrease {} the used space of storage spaceToken: {}",
			usedSpaceToRemove, spaceToken);

		try {
			ssDAO = daoFactory.getStorageSpaceDAO();
			log.debug("Storage Space DAO retrieved.");
		} catch (DataAccessException daEx) {
			log.error("Error while retrieving StorageSpaceDAO: {}",
				daEx.getMessage(), daEx);
			return false;
		}
		int n = 0;
		try {
			n = ssDAO.decreaseUsedSpace(spaceToken, usedSpaceToRemove);
		} catch (DataAccessException daEx) {
			log.error(
				"Error during the decrease of used space for spaceToken {}: {}",
				spaceToken, daEx.getMessage());
			return false;
		}
		if (n == 0) {
			log.warn(
				"No errors caught but it seems no used space updates done on space token {}",
				spaceToken);
		}
		return n > 0;
	}
}
