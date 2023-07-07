/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.impl.mysql.StorageSpaceDAOMySql;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TSpaceToken;

public class ReservedSpaceCatalog {

  private static final Logger log = LoggerFactory.getLogger(ReservedSpaceCatalog.class);

  private static Set<TSpaceToken> voSA_spaceTokenSet = Sets.newHashSet();
  private static Map<TSpaceToken, Date> voSA_UpdateTime = Maps.newHashMap();

  private static final long NOT_INITIALIZED_SIZE_VALUE = -1L;

  private static ReservedSpaceCatalog instance;

  public static synchronized ReservedSpaceCatalog getInstance() {
    if (instance == null) {
      instance = new ReservedSpaceCatalog();
    }
    return instance;
  }

  private StorageSpaceDAO ssDAO;

  private ReservedSpaceCatalog() {

    log.debug("Building Reserve Space Catalog...");
    ssDAO = StorageSpaceDAOMySql.getInstance();
  }

  /*********************************************
   * STATIC METHODS
   *********************************************/
  public static void addSpaceToken(TSpaceToken token) {

    voSA_spaceTokenSet.add(token);
    voSA_UpdateTime.put(token, null);
  }

  public static Set<TSpaceToken> getTokenSet() {

    return voSA_spaceTokenSet;
  }

  public static void clearTokenSet() {

    voSA_spaceTokenSet.clear();
    voSA_UpdateTime.clear();
  }

  /**
   * Basic method used to retrieve all the information about a StorageSpace - StorageSpace is
   * selected by SpaceToken
   * 
   * @param spaceToken TSpaceToken
   * @return StorageSpaceData, null if no-one SS exists with the specified spaceToken
   * @throws DataAccessException
   */
  public StorageSpaceData getStorageSpace(TSpaceToken spaceToken)
      throws TransferObjectDecodingException, DataAccessException {

    StorageSpaceData result = null;
    ssDAO = StorageSpaceDAOMySql.getInstance();
    log.debug("Storage Space DAO retrieved.");
    StorageSpaceTO ssTO = ssDAO.getStorageSpaceByToken(spaceToken.getValue());
    log.debug("Storage Space retrieved by Token. ");
    if (ssTO != null) {
      try {
        result = new StorageSpaceData(ssTO);
      } catch (IllegalArgumentException e) {
        log.error(
            "Error building StorageSpaceData from StorageSpaceTO " + "IllegalArgumentException: {}",
            e.getLocalizedMessage(), e);
        throw new TransferObjectDecodingException(
            "Unable to build StorageSpaceData from StorageSpaceTO");
      }
    } else {
      log.info("Unable to build StorageSpaceData. No StorageSpaceTO built " + "from the DB");
    }
    return result;
  }

  /**
   * Create a new StorageSpace entry into the DB. It is used for - STATIC Space Creation - DYNAMIC
   * Space Reservation
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
    log.debug("Storage Space DAO retrieved.");
    ssDAO.addStorageSpace(ssTO);
    log.debug("StorageSpaceTO inserted in Persistence");
  }

  /**
   * Update all the fields apart from the alias of a storage space row given the input
   * StorageSpaceData
   * 
   * @param ssd
   * 
   * @throws DataAccessException
   */
  public void updateStorageSpace(StorageSpaceData ssd) throws DataAccessException {

    log.debug("Storage Space DAO retrieved.");

    StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
    ssTO.setCreated(null); // we don't want to update the creation timestamp
    ssTO.setUpdateTime(new Date());

    ssDAO.updateStorageSpace(ssTO);
    log.debug("StorageSpaceTO updated in Persistence");
  }

  /**
   * @param ssd
   */
  public void updateStorageSpaceFreeSpace(StorageSpaceData ssd) throws DataAccessException {

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
  public void updateAllStorageSpace(StorageSpaceData ssd) {

    updateAllStorageSpace(ssd, null);
  }

  /**
   * Update StorageSpace. This method is used to update the StorageSpace into the ReserveSpace
   * Catalog. The update operation take place after a AbortRequest for a PrepareToPut operation done
   * with the spaceToken.(With or without the size specified).
   */

  public void updateAllStorageSpace(StorageSpaceData ssd, Date updateTime) {

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

    // Add the row to the persistence..
    try {
      ssDAO.updateAllStorageSpace(ssTO);
      log.debug("StorageSpaceTO updated in Persistence");
    } catch (DataAccessException daEx) {
      log.error("Error while inserting new row in StorageSpace: {}", daEx.getMessage(), daEx);
    }
  }

  /**
   * @param desc
   * @return
   */
  public StorageSpaceData getStorageSpaceByAlias(String desc) {

    StorageSpaceData result = null; // new StorageSpaceData();
    log.debug("Retrieve Storage Space start... ");

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
      log.debug("Error while retrieving StorageSpace: {}", daEx.getMessage(), daEx);
    }

    return result;
  }

  /**
   * Provides a list of storage spaces not initialized by comparing the used space stored against
   * the well know not initialized value <code>NOT_INITIALIZED_SIZE_VALUE<code>
   * 
   * @return SpaceData
   */
  public List<StorageSpaceData> getStorageSpaceNotInitialized() {

    log.debug("Retrieve Storage Space not initialized start ");
    List<StorageSpaceData> result = Lists.newLinkedList();

    // Get StorageSpaceTO form persistence
    try {
      Collection<StorageSpaceTO> storagesSpaceTOCollection =
          ssDAO.getStorageSpaceByUnavailableUsedSpace(NOT_INITIALIZED_SIZE_VALUE);
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
   * 
   * @param user VomsGridUser
   * @param spaceAlias String
   * @return ArrayOfTSpaceToken
   */
  public ArrayOfTSpaceToken getSpaceTokens(GridUserInterface user, String spaceAlias) {

    ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

    log.debug("Retrieving space tokens...");

    try {

      Collection<StorageSpaceTO> listOfStorageSpace =
          ssDAO.getStorageSpaceByOwner(user, spaceAlias);
      int nItems = listOfStorageSpace.size();
      log.debug("getSpaceTokens : Number of Storage spaces retrieved with " + "Alias '{}': {}",
          spaceAlias, nItems);
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
      log.error("Exception while retrieving Storage Space: {}", e.getMessage(), e);
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

    try {

      Collection<?> listOfStorageSpace = ssDAO.getStorageSpaceByAliasOnly(spaceAlias);
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
      log.error("Error getting data! Error: {}", e.getMessage(), e);
    }
    return result;
  }

  // ************************ CHECH BELOW METHODS ***************************

  /**
   * 
   * @param user GridUserInterface
   * @param spaceToken TSpaceToken
   * @return boolean
   */
  public boolean release(GridUserInterface user, final TSpaceToken spaceToken) {

    log.debug("Delete storage spaceToken info from persistence: {}", spaceToken);

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
   * Method that purges the catalog, removing expired space reservation. The spacefile with lifetime
   * expired are removed from the file systems.
   * 
   */
  public void purge() {

    log.debug("Space Garbage Collector start!");
    Calendar rightNow = Calendar.getInstance();

    // Get the Collection of Space Reservation Expired
    Collection<StorageSpaceTO> expiredSpaceTO;
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
    log.debug("Space Garbage Collector: Number of SpaceFile to remove {}.", expiredSpaceTO.size());

    for (Iterator<?> i = expiredSpaceTO.iterator(); i.hasNext();) {
      spaceTO = (StorageSpaceTO) i.next();
      // Deleting space File
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

    log.debug("Increase {} the used space of storage spaceToken: {}", usedSpaceToAdd, spaceToken);

    int n = 0;
    try {
      n = ssDAO.increaseUsedSpace(spaceToken, usedSpaceToAdd);
    } catch (DataAccessException daEx) {
      log.error("Error during the increase of used space for spaceToken {}: {}", spaceToken,
          daEx.getMessage());
      return false;
    }
    if (n == 0) {
      log.warn("No errors caught but it seems no used space updates done on space token {}",
          spaceToken);
    }
    log.debug("{} increaseUsedSpace += {}", spaceToken, usedSpaceToAdd);
    return n > 0;
  }

  public boolean decreaseUsedSpace(String spaceToken, Long usedSpaceToRemove) {

    log.debug("Decrease {} the used space of storage spaceToken: {}", usedSpaceToRemove,
        spaceToken);

    int n = 0;
    try {
      n = ssDAO.decreaseUsedSpace(spaceToken, usedSpaceToRemove);
    } catch (DataAccessException daEx) {
      log.error("Error during the decrease of used space for spaceToken {}: {}", spaceToken,
          daEx.getMessage());
      return false;
    }
    if (n == 0) {
      log.warn("No errors caught but it seems no used space updates done on space token {}",
          spaceToken);
    }
    log.debug("{} decreaseUsedSpace -= {}", spaceToken, usedSpaceToRemove);
    return n > 0;
  }
}
