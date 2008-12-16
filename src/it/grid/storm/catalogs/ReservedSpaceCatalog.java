/*
 * ReservedSpaceCatalog
 *
 */

package it.grid.storm.catalogs;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.*;
import it.grid.storm.common.types.*;
import it.grid.storm.persistence.*;
import it.grid.storm.persistence.dao.*;
import it.grid.storm.persistence.exceptions.*;
import it.grid.storm.persistence.model.*;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.*;
import it.grid.storm.common.*;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.GridUserFactory;
import it.grid.storm.griduser.GridUserManager;

/**
 *
 */

public class ReservedSpaceCatalog {
  /**
   *Logger.
   */
  private static final Logger log = Logger.getLogger("catalogs");
  public static HashSet voSA_spaceTokenSet = new HashSet();
  private DAOFactory daoFactory;
  private StorageSpaceDAO ssDAO;
  private Configuration config;

  /**
   * Default constructor
   */
  public ReservedSpaceCatalog() {
    log.debug("Building Reserve Space Catalog...");
    //Binding to the persistence component
    daoFactory = PersistenceDirector.getDAOFactory();

  }

  public void addStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
      MultipleDataEntriesException {

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
   * Update StorageSpace.
   * This method is used to update the StorageSpace into the ReserveSpace Catalog.
   * The update operation take place after a AbortRequest for a PrepareToPut operation
   * done with the spaceToken.(With or without the size specified).
   */

  public void updateStorageSpace(StorageSpaceData ssd) throws NoDataFoundException, InvalidRetrievedDataException,
      MultipleDataEntriesException {

    log.debug("UPDATE StorageSpace Start...");
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
      ssDAO.updateStorageSpace(ssTO);
      log.debug("StorageSpaceTO updated in Persistence");
    }
    catch (DataAccessException daEx) {
      log.error("Error while inserting new row in StorageSpace", daEx);
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
   *
   * @param spaceToken TSpaceToken
   * @return SpaceData
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
      log.debug("Error while retrieving StorageSpaceDAO.", daEx);
    }

    //Get StorageSpaceTO form persistence
    try {
      ssTO = ssDAO.getStorageSpaceByToken(spaceToken.getValue());
      log.debug("Storage Space retrieved by Token. ");
      //Build the result
      if (ssTO != null) {
        result = new StorageSpaceData(ssTO);
      }
    }
    catch (DataAccessException daEx) {
      log.debug("Error while retrieving StorageSpace", daEx);
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
   log.debug("Retrieve Storage Space start... ");

   StorageSpaceTO ssTO = null;

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
       Collection cl = ssDAO.getStorageSpaceByAliasOnly(desc);
       Iterator iter = cl.iterator();
       ssTO = ( StorageSpaceTO)iter.next();
     log.debug("Storage Space retrieved by Token. ");
     //Build the result
     if (ssTO != null) {
       result = new StorageSpaceData(ssTO);
     }
   }
   catch (DataAccessException daEx) {
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

    log.debug("Retrieve StorageSpace Start...");
    // Build StorageSpaceTO from SpaceData
    StorageSpaceTO ssTO = new StorageSpaceTO(ssData);
    log.debug("Storage Space TO Created");

    // Retrieve the Data Access Object from the factory
    try {
      ssDAO = daoFactory.getStorageSpaceDAO();
      log.debug("Storage Space DAO retrieved.");
    }
    catch (DataAccessException daEx) {
      log.debug("Error while retrieving StorageSpaceDAO.", daEx);
    }

    // Add the row to the persistence..
    try {
      ssDAO.updateStorageSpace(ssTO);
      log.debug("StorageSpaceTO inserted in Persistence");
    }
    catch (DataAccessException daEx) {
      log.debug("Error while inserting new row in StorageSpace", daEx);
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

    log.debug("Retrieving space tokens...");

    // Retrieve the Data Access Object from the factory
    try {
      ssDAO = daoFactory.getStorageSpaceDAO();
      log.debug("Storage Space DAO retrieved.");
    }
    catch (DataAccessException daEx) {
      log.error("Error while retrieving StorageSpaceDAO.", daEx);
    }

    // Get StorageSpaceTO form persistence
    try {

      Collection listOfStorageSpace = ssDAO.getStorageSpaceByOwner(user, spaceAlias);

      int nItems = listOfStorageSpace.size();
      log.debug("getSpaceTokens : Number of Storage spaces retrieved with Alias '"+spaceAlias+"': "+nItems);
      Iterator j_ssTO = listOfStorageSpace.iterator();

      while (j_ssTO.hasNext()) {
        StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
        try {
          TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
          result.addTSpaceToken(spaceToken);
        }
        catch (InvalidTSpaceTokenAttributesException ex2) {
          log.error("Retrieved invalid Space token from DB");
        }
      }
    }
    catch (DataAccessException daEx) {
      log.error("Error while retrieving StorageSpace", daEx);
    }
    catch (Exception e) {
      log.error("Exception while retrieving Storage Space", e);
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

    log.debug("Retrieving space tokens...");

    // Retrieve the Data Access Object from the factory
    try {
      ssDAO = daoFactory.getStorageSpaceDAO();
      log.debug("Storage Space DAO retrieved.");
    }
    catch (DataAccessException daEx) {
      log.error("Error while retrieving StorageSpaceDAO.", daEx);
    }

    // Get StorageSpaceTO form persistence
    try {
      Collection listOfStorageSpace = ssDAO.getStorageSpaceByAliasOnly(spaceAlias);

      int nItems = listOfStorageSpace.size();
      log.debug("Number of Storage spaces retrieved: " + nItems);
      Iterator j_ssTO = listOfStorageSpace.iterator();

      while (j_ssTO.hasNext()) {
        StorageSpaceTO ssTO = (StorageSpaceTO) j_ssTO.next();
        try {
          TSpaceToken spaceToken = TSpaceToken.make(ssTO.getSpaceToken());
          result.addTSpaceToken(spaceToken);
        }
        catch (InvalidTSpaceTokenAttributesException ex2) {
          log.error("Retrieved invalid Space token from DB");
        }
      }

    }
    catch (DataAccessException daEx) {
      log.error("Error while retrieving StorageSpace", daEx);
    }
    catch (Exception e) {
      //...
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
       log.error("getMetaDataSpace: Retrieved invalid Space token from DB",e);
     }
     catch (InvalidTSizeAttributesException e) {
       log.error("ReservedSpaceCatalog Exception!",e);
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
    log.debug("Delete storage spaceToken info from persistence: " + spaceToken);

    // Retrieve the Data Access Object from the factory
    try {
      ssDAO = daoFactory.getStorageSpaceDAO();
      log.debug("Storage Space DAO retrieved.");
    }
    catch (DataAccessException daEx) {
      log.error("Error while retrieving StorageSpaceDAO.", daEx);
    }
    boolean rowRemoved = true;
    // Delete the row from persistence.
    try {
      ssDAO.removeStorageSpace(user, spaceToken.getValue());
      log.debug("spaceToken removed from DB.");
    }
    catch (DataAccessException daEx) {
      log.error("spaceToken not found in the DB: " + spaceToken.getValue());
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
    log.debug("Space Garbage Collector start!");
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
      log.info("Space Garbage Collector: no space expired found.");
      return;
    }

    //For each entry expired
    //1) Delete the related space file
    //2) Remove the entry from the DB

    StorageSpaceTO spaceTO = null;
    log.debug("Space Garbage Collector: Number of SpaceFile to remove " + expiredSpaceTO.size() + ".");

    for (Iterator i = expiredSpaceTO.iterator(); i.hasNext(); ) {
      spaceTO = (StorageSpaceTO) i.next();
      //Deleteing space File
      String spaceFileName = spaceTO.getSpaceFile();
      File sfile = new File(spaceFileName);
      log.debug("Space Garbage Collector: SpaceFile to remove " + spaceFileName + ".");

      if (sfile.delete()) {
        log.debug("Space Garbage Collector: SpaceFile " + spaceFileName + " removed.");
      }
      else {
        log.warn("Space Garbage Collector: problem removing " + spaceFileName + "!");
      }

      //Removing space entry from the DB
      try {
        ssDAO.removeStorageSpace(spaceTO.getSpaceToken());
      }
      catch (DataAccessException e) {
        log.warn("Space Garbage Collector: error removing space entry from catalog.");
      }

    }

  }

  /**
   * This method is used by the namespace parser component to
   * insert a new Space Token Description data into the space catalog.
   * In this way a standard Space Token is created, making it work
   * for the GetSpaceMetaData request an SrmPreparateToPut with SpaceToken.
   *
   * The following code check if a SA_token with the same space description is already
   * present into the caltalog, if no data are found the new data are inserted, if yes
   * the new data and the data already present are compared, and if needed an update
   *  operation is performed.
   *
   * The mandatory paramter are:
   * @param spaceTokenAlias the space token description the user have to specify into the namespace.xml file
   * @param totalOnLineSize the size the user have to specify into the namespace.xml file
   * @param date
   * @param spaceFileName the space file name will be used to get the free size. It is the StFNRoot.
   */
  public void FAKE_createVOSA_Token(String spaceTokenAlias, TSizeInBytes totalOnLineSize, String spaceFileName) {
  return;
  }


  public void createVOSA_Token(String spaceTokenAlias, TSizeInBytes totalOnLineSize, String spaceFileName) {

    ArrayOfTSpaceToken tokenArray;

    /*
     * Build the storage space Data
     *
     * A Fake Grid User is needed
     */
     GridUserInterface stormServiceUser = GridUserManager.makeStoRMGridUser();

    //Try with fake user, if it does not work remove it and use different method

    // First, check if the same VOSpaceArea already exists

    //tokenArray = this.getSpaceTokensByAlias(spaceTokenAlias);
    tokenArray = this.getSpaceTokens(stormServiceUser, spaceTokenAlias);

    if (tokenArray.size() == 0) {
      //the VOSpaceArea does not exist yet
      log.debug("VoSpaceArea " + spaceTokenAlias + " still does not exists. Start creation process.");

      PFN sfname = null;
      try {
        sfname = PFN.make(spaceFileName);
      }
      catch (InvalidPFNAttributeException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      StorageSpaceData ssd = null;

      try {
        ssd = new StorageSpaceData(stormServiceUser, TSpaceType.VOSPACE, spaceTokenAlias, totalOnLineSize,
                                   totalOnLineSize, null, null, null, sfname);
      }
      catch (InvalidSpaceDataAttributesException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      try {

        this.addStorageSpace(ssd);
        //Track into global set to remove obsolete SA_token
        voSA_spaceTokenSet.add(ssd.getSpaceToken());

      }
      catch (NoDataFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (InvalidRetrievedDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (MultipleDataEntriesException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    else {
      //the VOspaceArea already exists. Compare new data and data already present
      // to check if the parameter has changed or not, and
      //then perform update operation into catalog if it is needed.
      log.debug("VOSpaceArea for space token description " + spaceTokenAlias + " already present into  DB.");

      boolean equal = false;
      TSpaceToken token = tokenArray.getTSpaceToken(0);
      StorageSpaceData catalog_ssd = this.getStorageSpace(token);

      if (catalog_ssd != null) {

        if (catalog_ssd.getSpaceTokenAlias().equals(spaceTokenAlias)
            && (catalog_ssd.getGuaranteedSize().value() == totalOnLineSize.value())
            && (catalog_ssd.getSpaceFileName().toString().equals(spaceFileName))) {
          equal = true;
        }

      }

      //false otherwise
      if (equal) {
        //Do nothing if equals, everythings are already present into the DB
        log.debug("VOSpaceArea for space token description " + spaceTokenAlias + " is already up to date.");
        voSA_spaceTokenSet.add(token);

      } else {
        //If the new data has been modified, update the data into the catalog
        log.debug("VOSpaceArea for space token description " + spaceTokenAlias +
                  " is different in some parameters. Updating the catalog.");
        try {

            catalog_ssd.setGuaranteedSize(totalOnLineSize);
            catalog_ssd.setDesiredSize(totalOnLineSize);
            catalog_ssd.setTotalSize(totalOnLineSize);

            //WARNIGN THIS WILL UPDATE THE FREE SIZE
            catalog_ssd.setUnusedSize(totalOnLineSize);
            
            PFN sfn = null;
            try {
                sfn = PFN.make(spaceFileName);
            }
            catch (InvalidPFNAttributeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catalog_ssd.setSpaceFileName(sfn);

            this.updateAllStorageSpace(catalog_ssd);
            voSA_spaceTokenSet.add(token);

        }
        catch (NoDataFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (InvalidRetrievedDataException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (MultipleDataEntriesException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }

      //Warning. CHeck if there are multiple token with same alisa, this is not allowed
      if (tokenArray.size() > 1) {
        log.error("Error: multiple Space Token found for the same space Alias: " + spaceTokenAlias +
                  ". Only one has been evaluated!");
      }

    }

  }

  /**
   * This method should be use at the end of the namespace
   * insert process (through the createVO_SA_token(...)) to remmove
   * from the database the old VO_SA_token inserted from the previous
   * namsespace.xml configuration
   *
   */
   public void FAKEpurgeOldVOSA_token() {
	return;
   }
   public void purgeOldVOSA_token() {
    log.debug("VO SA: garbage collecting obsolete VOSA_token");

    Iterator iter = voSA_spaceTokenSet.iterator();
    while (iter.hasNext()) {
      log.debug("VO SA token REGISTRED:" + ( (TSpaceToken) iter.next()).getValue());
    }

    GridUserInterface stormServiceUser = GridUserManager.makeStoRMGridUser();

    //Remove obsolete space
    ArrayOfTSpaceToken token_a = this.getSpaceTokens(stormServiceUser, null);
    for (int i = 0; i < token_a.size(); i++) {
      log.debug("VO SA token IN CATALOG:" + token_a.getTSpaceToken(i).getValue());
    }

    if (token_a != null) {
      for (int i = 0; i < token_a.size(); i++) {

        if (!voSA_spaceTokenSet.contains(token_a.getTSpaceToken(i))) {
          //This VOSA_token is no more used, removing it from persistence
          TSpaceToken tokenToRemove = token_a.getTSpaceToken(i);
          log.debug("VO SA token " + tokenToRemove + " is no more used, removing it from persistence.");
          this.release(stormServiceUser, tokenToRemove);
        }
      }
    }
    else {
      log.warn("Space Catalog garbage SA_Token: no SA TOKENs specified. Please check your namespace.xml file.");
    }

    voSA_spaceTokenSet.clear();

  }

}
