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

/*
 * ReservedSpaceCatalog
 *
 */

package it.grid.storm.catalogs;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.DAOFactory;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.space.StorageSpaceNotInitializedException;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTMetaDataSpaceAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TMetaDataSpace;
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

    private static final Logger log = LoggerFactory.getLogger(ReservedSpaceCatalog.class);
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
        ReservedSpaceCatalog.log.debug("Building Reserve Space Catalog...");
        //Binding to the persistence component
        daoFactory = PersistenceDirector.getDAOFactory();
    }
    
    
    /**
    * Basic method used to retrieve all the information about a StorageSpace 
    * - StorageSpace is selected by SpaceToken
    * 
    * @param spaceToken TSpaceToken
    * @return StorageSpaceData
    * @return null if no-one SS exists with the specified spaceToken 
    */
   public StorageSpaceData getStorageSpace(TSpaceToken spaceToken) {

       StorageSpaceData result = null; //new StorageSpaceData();
       log.debug("Retrieve Storage Space start... ");

       StorageSpaceTO ssTO = null;

       // Retrieve the Data Access Object from the factory
       try {
           ssDAO = daoFactory.getStorageSpaceDAO();
           log.debug("Storage Space DAO retrieved.");
       }
       catch (DataAccessException daEx) {
           log.error("Error while retrieving StorageSpaceDAO.", daEx);
       }

       //Get StorageSpaceTO form persistence
       try {
           ssTO = ssDAO.getStorageSpaceByToken(spaceToken.getValue());
           log.debug("Storage Space retrieved by Token. ");
           //Build the result
           if (ssTO != null) {
                try
                {
                    result = new StorageSpaceData(ssTO);
                }
                catch (IllegalArgumentException e)
                {
                    //this will never happen, we check ssTO to be not null
                    log.error("unable to build StorageSpaceData from StorageSpaceTO IllegalArgumentException: " + e.getLocalizedMessage());
                }
           }
       }
       catch (DataAccessException daEx) {
           log.error("Error while retrieving StorageSpace", daEx);
       }

       return result;
   }
    
    /**
     * Create a new StorageSpace entry into the DB.
     * It is used for 
     * - STATIC Space Creation
     * - DYNAMIC Space Reservation
     * 
     * @param ssd
     * @throws NoDataFoundException
     * @throws InvalidRetrievedDataException
     * @throws MultipleDataEntriesException
     */
    public void addStorageSpace(StorageSpaceData ssd) {

        log.debug("ADD StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        log.debug("Storage Space TO Created");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
        try {
            ssDAO.addStorageSpace(ssTO);
            log.debug("StorageSpaceTO inserted in Persistence");
        }
        catch (DataAccessException daEx) {
            log.error("Error while inserting new row in StorageSpace", daEx);
        }
    }



    /**
     * Update all the fields apart from the alias of a storage space row given 
     * the input StorageSpaceData
     * 
     * @param ssd
     */
    public void updateStorageSpace(StorageSpaceData ssd) {

        log.debug("UPDATE StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        log.debug("Storage Space TO Created");
        ssTO.setUpdateTime(new Date());

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Update the row in the persistence..
        try {
            ssDAO.updateStorageSpace(ssTO);
            ReservedSpaceCatalog.log.debug("StorageSpaceTO updated in Persistence");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while inserting new row in StorageSpace", daEx);
        }

    }
    
    /**
     * Update StorageSpace.
     * This method is used to update the StorageSpace into the ReserveSpace Catalog.
     * The update operation take place after a AbortRequest for a PrepareToPut operation
     * done with the spaceToken.(With or without the size specified).
     */
    public void updateStorageSpaceFreeSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
    MultipleDataEntriesException {

        log.debug("UPDATE StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        log.debug("Storage Space TO Created");
        ssTO.setUpdateTime(new Date());

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Update the row in the persistence..
        try {
            ssDAO.updateStorageSpaceFreeSpace(ssTO);
            ReservedSpaceCatalog.log.debug("StorageSpaceTO updated in Persistence");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while inserting new row in StorageSpace", daEx);
        }

    }

    /**
     * Update StorageSpace.
     * This method is used to update the StorageSpace into the ReserveSpace Catalog.
     * The update operation take place after a AbortRequest for a PrepareToPut operation
     * done with the spaceToken.(With or without the size specified).
     */

    public void updateAllStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
    MultipleDataEntriesException {

        log.debug("UPDATE StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        log.debug("Storage Space TO Created");
        ssTO.setUpdateTime(new Date());

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
        try {
            ssDAO.updateAllStorageSpace(ssTO);
            log.debug("StorageSpaceTO updated in Persistence");
        }
        catch (DataAccessException daEx) {
            log.error("Error while inserting new row in StorageSpace", daEx);
        }

    }

    /**
     * @param desc
     * @return
     */
    public StorageSpaceData getStorageSpaceByAlias(String desc) {

        StorageSpaceData result = null; //new StorageSpaceData();
        log.debug("Retrieve Storage Space start... ");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }

        //Get StorageSpaceTO form persistence
        try {
            Collection<StorageSpaceTO> cl = ssDAO.getStorageSpaceByAliasOnly(desc);
            if(cl != null && !cl.isEmpty())
            {
                log.debug("Storage Space retrieved by Token. ");
                //Build the result
                try
                {
                    result = new StorageSpaceData(cl.toArray(new StorageSpaceTO[0])[0]);
                }
                catch (IllegalArgumentException e)
                {
                    //this will never happen, we know there is at least one element in the collection
                    log.error("unable to build StorageSpaceData from StorageSpaceTO IllegalArgumentException: " + e.getLocalizedMessage());
                }
            }
        }
        catch (DataAccessException daEx) {
            log.debug("Error while retrieving StorageSpace", daEx);
        }

        return result;
    }


    /**
     * Provides a list of storage spaces not initialized by comparing the used space stored against the well know not initialized value
     * <code>NOT_INITIALIZED_SIZE_VALUE<code>
     * 
     * @return SpaceData
     */
    public List<StorageSpaceData> getStorageSpaceNotInitialized()
    {
        log.debug("Retrieve Storage Space not initialized start ");
        LinkedList<StorageSpaceData> result = new LinkedList<StorageSpaceData>();
        // Retrieve the Data Access Object from the factory
        try
        {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx)
        {
            log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }
        // Get StorageSpaceTO form persistence
        try
        {
            Collection<StorageSpaceTO> storagesSpaceTOCollection = ssDAO.getStorageSpaceByUnavailableUsedSpace(NOT_INITIALIZED_SIZE_VALUE);
            log.debug("Storage Space retrieved by not initialized used space. ");
            for (StorageSpaceTO storagesSpaceTO : storagesSpaceTOCollection)
            {
                if (storagesSpaceTO != null)
                {
                    try
                    {
                        result.add(new StorageSpaceData(storagesSpaceTO));
                    }
                    catch (IllegalArgumentException e)
                    {
                        //this will never happen, we check ssTO to be not null
                        log.error("unable to build StorageSpaceData from StorageSpaceTO IllegalArgumentException: " + e.getLocalizedMessage());
                    }
                }
                else
                {
                    log.warn("Received a collection of StorageSpaceTO containing null elements, skipping them");
                }
            }
        }
        catch (DataAccessException daEx)
        {
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
    public List<StorageSpaceData> getStorageSpaceByLastUpdate(Date lastUpdateTimestamp)
    {
        log.debug("Retrieve Storage Space not initialized start ");
        LinkedList<StorageSpaceData> result = new LinkedList<StorageSpaceData>();
        // Retrieve the Data Access Object from the factory
        try
        {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx)
        {
            log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }
        // Get StorageSpaceTO form persistence
        try
        {
            Collection<StorageSpaceTO> storagesSpaceTOCollection = ssDAO.getStorageSpaceByPreviousLastUpdate(lastUpdateTimestamp);
            log.debug("Storage Space retrieved by Token previous last update. ");
            for (StorageSpaceTO storagesSpaceTO : storagesSpaceTOCollection)
            {
                if (storagesSpaceTO != null)
                {
                    try
                    {
                        result.add(new StorageSpaceData(storagesSpaceTO));
                    }
                    catch (IllegalArgumentException e)
                    {
                        //this will never happen, we check ssTO to be not null
                        log.error("unable to build StorageSpaceData from StorageSpaceTO IllegalArgumentException: " + e.getLocalizedMessage());
                    }
                }
                else
                {
                    log.warn("Received a collection of StorageSpaceTO containing null elements, skipping them");
                }
            }
        }
        catch (DataAccessException daEx)
        {
            log.debug("Error while retrieving StorageSpace", daEx);
        }
        return result;
    }



    /**
     *
     * @param spaceToken TSpaceToken
     * @param usedSpace TSizeInBytes
     */
    public void setFreeSize(final StorageSpaceData ssData) {

        ReservedSpaceCatalog.log.debug("Retrieve StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssData);
        ReservedSpaceCatalog.log.debug("Storage Space TO Created");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }

        String alias = ssTO.getAlias();
        // Add the row to the persistence..
        try {
        	log.debug("Updating '"+alias+"' Storage Space.");
            ssDAO.updateStorageSpaceFreeSpace(ssTO);
            log.debug("StorageSpaceTO inserted in Persistence");
        }
        catch (DataAccessException daEx) {
            log.error("Error while updating  in StorageSpace", daEx);
        }

    }



    /**
     *
     * @param user VomsGridUser
     * @param spaceAlias String
     * @return ArrayOfTSpaceToken
     */
    public ArrayOfTSpaceToken getSpaceTokens(GridUserInterface user, String spaceAlias) {
        ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

        ReservedSpaceCatalog.log.debug("Retrieving space tokens...");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Get StorageSpaceTO form persistence
        try {

            Collection listOfStorageSpace = ssDAO.getStorageSpaceByOwner(user, spaceAlias);

            int nItems = listOfStorageSpace.size();
            ReservedSpaceCatalog.log.debug("getSpaceTokens : Number of Storage spaces retrieved with Alias '"+spaceAlias+"': "+nItems);
            Iterator j_ssTO = listOfStorageSpace.iterator();

            while (j_ssTO.hasNext()) {
                StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
                try {
                    TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
                    result.addTSpaceToken(spaceToken);
                }
                catch (InvalidTSpaceTokenAttributesException ex2) {
                    ReservedSpaceCatalog.log.error("Retrieved invalid Space token from DB");
                }
            }
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpace", daEx);
        }
        catch (Exception e) {
            ReservedSpaceCatalog.log.error("Exception while retrieving Storage Space", e);
        }
        return result;
    }


    /**
     * This method is used for the VOspaceArea Check.
     * @param spaceAlias
     * @return
     */

    public ArrayOfTSpaceToken getSpaceTokensByAlias(String spaceAlias) {
        ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

        ReservedSpaceCatalog.log.debug("Retrieving space tokens...");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Get StorageSpaceTO form persistence
        try {
            Collection listOfStorageSpace = ssDAO.getStorageSpaceByAliasOnly(spaceAlias);

            int nItems = listOfStorageSpace.size();
            ReservedSpaceCatalog.log.debug("Number of Storage spaces retrieved: " + nItems);
            Iterator j_ssTO = listOfStorageSpace.iterator();

            while (j_ssTO.hasNext()) {
                StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
                try {
                    TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
                    result.addTSpaceToken(spaceToken);
                }
                catch (InvalidTSpaceTokenAttributesException ex2) {
                    ReservedSpaceCatalog.log.error("Retrieved invalid Space token from DB");
                }
            }

        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpace", daEx);
        }
        catch (Exception e) {
            ReservedSpaceCatalog.log.error("Error getting data!",e);
        }
        return result;
    }



    /**
     * This method is used for the VOspaceArea Check.
     * @param VOname
     * @return
     */

    public ArrayOfTSpaceToken getSpaceTokensBySpaceType(String stype) {
        ArrayOfTSpaceToken result = new ArrayOfTSpaceToken();

        ReservedSpaceCatalog.log.debug("Retrieving space tokens...");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Get StorageSpaceTO form persistence
        try {
            Collection listOfStorageSpace = ssDAO.getStorageSpaceBySpaceType(stype);

            int nItems = listOfStorageSpace.size();
            ReservedSpaceCatalog.log.debug("Number of Storage spaces retrieved: " + nItems);
            Iterator j_ssTO = listOfStorageSpace.iterator();

            while (j_ssTO.hasNext()) {
                StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
                try {
                    TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
                    result.addTSpaceToken(spaceToken);
                }
                catch (InvalidTSpaceTokenAttributesException ex2) {
                    ReservedSpaceCatalog.log.error("Retrieved invalid Space token from DB");
                }
            }

        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpace", daEx);
        }
        catch (Exception e) {
            ReservedSpaceCatalog.log.error("Generic Error while retrieving StorageSpace", e);
        }
        return result;
    }










    /**
     * Method used to retrieve information returned by "srmGetSpaceMetadata" call
     *
     * @param spaceToken TSpaceToken
     * @return TMetaDataSpace
     */
    public TMetaDataSpace getMetaDataSRMSpace(TSpaceToken spaceToken) throws StorageSpaceNotInitializedException{

        TMetaDataSpace metaData = null;
        StorageSpaceData spaceData = getStorageSpace(spaceToken);

        if (spaceData != null) 
        {
            if(!spaceData.isInitialized())
            {
                log.warn("Unable to create a valid TMetaDataSpace for storage space token \'" + spaceToken
                        + "\', the space is not initialized");
                throw new StorageSpaceNotInitializedException("Unable to create a valid TMetaDataSpace for storage space token \'" + spaceToken
                        + "\', the space is not initialized");
            }
            //Convert Space Data in TMetaDataSpace
            try {
                    metaData = new TMetaDataSpace(spaceData);
            }
            catch (InvalidTMetaDataSpaceAttributeException e) {
                log.error("getMetaDataSpace: Retrieved invalid Space token from DB",e);
            }
            catch (InvalidTSizeAttributesException e) {
                log.error("ReservedSpaceCatalog Exception!",e);
            }
        }
        else
        {
          //Unable to retrieve information about Space pointed by token
            log.warn("getMetaDataSpace: unable to retrieve SpaceData for token: "+spaceToken);
        }
        return metaData;
    }

    //************************ CHECH BELOW METHODS ***************************
    
    /**
     *
     * @param user GridUserInterface
     * @param spaceToken TSpaceToken
     * @return boolean
     */
    public boolean release(GridUserInterface user, final TSpaceToken spaceToken) {
        ReservedSpaceCatalog.log.debug("Delete storage spaceToken info from persistence: " + spaceToken);

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }
        boolean rowRemoved = true;
        // Delete the row from persistence.
        try {
            ssDAO.removeStorageSpace(user, spaceToken.getValue());
            ReservedSpaceCatalog.log.debug("spaceToken removed from DB.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("spaceToken not found in the DB: " + spaceToken.getValue());
            rowRemoved = false;
        }
        return rowRemoved;
    }

    /**
     * Method that purges the catalog, removing expired space reservation.
     * The spacefile with lifetime expired are removed from the file systems.
     *
     */
    public void purge() {
        ReservedSpaceCatalog.log.debug("Space Garbage Collector start!");
        Calendar rightNow = Calendar.getInstance();

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }
        //Get the Collection of Space Resrvation Expired
        Collection expiredSpaceTO;
        try {
            expiredSpaceTO = ssDAO.getExpired(rightNow.getTimeInMillis() / 1000);
        }
        catch (DataAccessException e) {
            //No space expired FOUND
            ReservedSpaceCatalog.log.info("Space Garbage Collector: no space expired found.");
            return;
        }

        //For each entry expired
        //1) Delete the related space file
        //2) Remove the entry from the DB

        StorageSpaceTO spaceTO = null;
        ReservedSpaceCatalog.log.debug("Space Garbage Collector: Number of SpaceFile to remove " + expiredSpaceTO.size() + ".");

        for (Iterator i = expiredSpaceTO.iterator(); i.hasNext(); ) {
            spaceTO = (StorageSpaceTO) i.next();
            //Deleteing space File
            String spaceFileName = spaceTO.getSpaceFile();
            File sfile = new File(spaceFileName);
            ReservedSpaceCatalog.log.debug("Space Garbage Collector: SpaceFile to remove " + spaceFileName + ".");

            if (sfile.delete()) {
                ReservedSpaceCatalog.log.debug("Space Garbage Collector: SpaceFile " + spaceFileName + " removed.");
            }
            else {
                ReservedSpaceCatalog.log.warn("Space Garbage Collector: problem removing " + spaceFileName + "!");
            }

            //Removing space entry from the DB
            try {
                ssDAO.removeStorageSpace(spaceTO.getSpaceToken());
            }
            catch (DataAccessException e) {
                ReservedSpaceCatalog.log.warn("Space Garbage Collector: error removing space entry from catalog.");
            }

        }

    }


}
