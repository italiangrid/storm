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

import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.DAOFactory;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.StorageSpaceDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.StorageSpaceTO;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTMetaDataSpaceAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TMetaDataSpace;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */

public class ReservedSpaceCatalog {
    /**
     *Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ReservedSpaceCatalog.class);
    public static HashSet voSA_spaceTokenSet = new HashSet();
    private final DAOFactory daoFactory;
    private StorageSpaceDAO ssDAO;
    private Configuration config;

    /**
     * Default constructor
     */
    public ReservedSpaceCatalog() {
        ReservedSpaceCatalog.log.debug("Building Reserve Space Catalog...");
        //Binding to the persistence component
        daoFactory = PersistenceDirector.getDAOFactory();

    }

    public void addStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
    MultipleDataEntriesException {

        ReservedSpaceCatalog.log.debug("ADD StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        ReservedSpaceCatalog.log.debug("Storage Space TO Created");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
        try {
            ssDAO.addStorageSpace(ssTO);
            ReservedSpaceCatalog.log.debug("StorageSpaceTO inserted in Persistence");
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

    public void updateStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
    MultipleDataEntriesException {

        ReservedSpaceCatalog.log.debug("UPDATE StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        ReservedSpaceCatalog.log.debug("Storage Space TO Created");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
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

    public void updateAllStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
    MultipleDataEntriesException {

        ReservedSpaceCatalog.log.debug("UPDATE StorageSpace Start...");
        // Build StorageSpaceTO from SpaceData
        StorageSpaceTO ssTO = new StorageSpaceTO(ssd);
        ReservedSpaceCatalog.log.debug("Storage Space TO Created");

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
        try {
            ssDAO.updateAllStorageSpace(ssTO);
            ReservedSpaceCatalog.log.debug("StorageSpaceTO updated in Persistence");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while inserting new row in StorageSpace", daEx);
        }

    }

    /**
     *
     * @param spaceToken TSpaceToken
     * @return SpaceData
     */
    public StorageSpaceData getStorageSpace(TSpaceToken spaceToken) {

        StorageSpaceData result = null; //new StorageSpaceData();
        ReservedSpaceCatalog.log.debug("Retrieve Storage Space start... ");

        StorageSpaceTO ssTO = null;

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }

        //Get StorageSpaceTO form persistence
        try {
            ssTO = ssDAO.getStorageSpaceByToken(spaceToken.getValue());
            ReservedSpaceCatalog.log.debug("Storage Space retrieved by Token. ");
            //Build the result
            if (ssTO != null) {
                result = new StorageSpaceData(ssTO);
            }
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while retrieving StorageSpace", daEx);
        }

        return result;
    }


    /**
     *
     * @param spaceToken TSpaceToken
     * @return SpaceData
     */
    public StorageSpaceData getStorageSpaceByAlias(String desc) {

        StorageSpaceData result = null; //new StorageSpaceData();
        ReservedSpaceCatalog.log.debug("Retrieve Storage Space start... ");

        StorageSpaceTO ssTO = null;

        // Retrieve the Data Access Object from the factory
        try {
            ssDAO = daoFactory.getStorageSpaceDAO();
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }

        //Get StorageSpaceTO form persistence
        try {
            Collection cl = ssDAO.getStorageSpaceByAliasOnly(desc);
            Iterator iter = cl.iterator();
            ssTO = ( StorageSpaceTO)iter.next();
            ReservedSpaceCatalog.log.debug("Storage Space retrieved by Token. ");
            //Build the result
            if (ssTO != null) {
                result = new StorageSpaceData(ssTO);
            }
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while retrieving StorageSpace", daEx);
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
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while retrieving StorageSpaceDAO.", daEx);
        }

        // Add the row to the persistence..
        try {
            ssDAO.updateStorageSpace(ssTO);
            ReservedSpaceCatalog.log.debug("StorageSpaceTO inserted in Persistence");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.debug("Error while inserting new row in StorageSpace", daEx);
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








    //************************ CHECH BELOW METHODS ***************************

    /**
     *
     * @param spaceToken TSpaceToken
     * @return TMetaDataSpace
     */
    public TMetaDataSpace getMetaDataSpace(TSpaceToken spaceToken) {

        StorageSpaceData spaceData = getStorageSpace(spaceToken);

        //Convert Space Data in TMetaDataSpace
        TMetaDataSpace metaData = null;

        try {
            if (spaceData != null) {
                metaData = new TMetaDataSpace(spaceData);
            }
            else { //Unable to retrieve information about Space pointed by token

            }
            return metaData;
        }
        catch (InvalidTMetaDataSpaceAttributeException e) {
            ReservedSpaceCatalog.log.error("getMetaDataSpace: Retrieved invalid Space token from DB",e);
        }
        catch (InvalidTSizeAttributesException e) {
            ReservedSpaceCatalog.log.error("ReservedSpaceCatalog Exception!",e);
        }

        return null;
    }


    public TSizeInBytes getTotalReservedSpace(final TSpaceToken spaceToken) {
        return null;
    }

    public TSizeInBytes getActualUsedSpace(final TSpaceToken spaceToken) {
        return null;
    }


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
            ReservedSpaceCatalog.log.debug("Storage Space DAO retrieved.");
        }
        catch (DataAccessException daEx) {
            ReservedSpaceCatalog.log.error("Error while retrieving StorageSpaceDAO.", daEx);
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
