package it.grid.storm.space;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import it.grid.storm.catalogs.InvalidRetrievedDataException;
import it.grid.storm.catalogs.InvalidSpaceDataAttributesException;
import it.grid.storm.catalogs.MultipleDataEntriesException;
import it.grid.storm.catalogs.NoDataFoundException;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.InvalidPFNAttributeException;
import it.grid.storm.common.types.PFN;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TSpaceType;

public class SpaceHelper {
    
    private static final int ADD = 0;
    private static final int REMOVE  = 1;
    private Configuration config;
    
    
    
    public SpaceHelper() {
        config = Configuration.getInstance();
    }
    
    /**
     * @param log
     * @param funcName
     * @param user
     * @param surl
     */
    
    private void updateFreeSpaceForSA(Logger log,
            String funcName,GridUserInterface user, TSURL surl, int operation, long filesize) {
        
        VirtualFSInterface fs = null;
        
                
        log.debug(funcName+"Updating Storage Area free size on db"); 
        
        StoRI stori = null;
        // Retrieve the StoRI associate to the SURL
        try {
            NamespaceInterface namespace = NamespaceDirector.getNamespace();
            stori = namespace.resolveStoRIbySURL(surl, user);
        } catch (NamespaceException e1) {
            log.debug(funcName + "Unable to build StoRI by SURL: "
                    + surl.toString(), e1);
            return;
        }
        // Get Virtual FileSystem for DB information
        fs = stori.getVirtualFileSystem();

        // Get StorageSpaceData from the database
        String SSDesc;
        StorageSpaceData spaceData = null;

        try {
            SSDesc = fs.getSpaceTokenDescription();
            spaceData = fs.getSpaceByAlias(SSDesc);

        } catch (NamespaceException e1) {
            log.error("Unable to create storage space data", e1);
            return;
        }

        // Get the localELement to know the real file size, if exists
        LocalFile localElement = stori.getLocalFile();

        if ((spaceData != null) && ((localElement.exists()) || operation == SpaceHelper.ADD)) {

            //IF PutDone, calculate the real fileSize
            if(operation == SpaceHelper.REMOVE)
                filesize = localElement.getExactSize();
            //else in case of RM the filesize is passed from the client
            
            TSizeInBytes unusedSize = TSizeInBytes.makeEmpty();
            unusedSize = spaceData.getUnusedSizes();
            
            long remainingSize = unusedSize.value();
            
            if(operation == SpaceHelper.REMOVE)
                remainingSize = unusedSize.value() - filesize;
            else if (operation == SpaceHelper.ADD) {
                //The new remaining size cannot be greater than the total size
                remainingSize = unusedSize.value() + filesize;
                //Use Storage Area Total Size as upper limit for the new Unused Size
                long totalSize =  spaceData.getDesiredSize().value();
                remainingSize = (remainingSize > totalSize ) ? totalSize : remainingSize; 
            }
            
            //Prevent negative value
            if(remainingSize<0)
                remainingSize = 0;

            // Update the unused space size with new value
            TSizeInBytes newUnSize = unusedSize;
            try {
                newUnSize = TSizeInBytes
                        .make(remainingSize, SizeUnit.BYTES);
            } catch (InvalidTSizeAttributesException ex) {
                log.error(funcName+ "Unable to create  new free size, so the previous one are used ",ex);
            }

            spaceData.setUnusedSize(newUnSize);

            try {
                fs.storeSpaceByToken(spaceData);
            } catch (NamespaceException e) {
                log.error(funcName + "Unable to update the new free size.",
                          e);
            }

            log.info(funcName + "Storage Area free size updated to: "
                    + newUnSize.value());

        } else {
            // Nothing to do. Problem with DB?
            log.error(funcName + " Unable to update the DB free size!");
            return;
        }


       
    
    }

    /**
     *  This helper function is used to update the Unused Free Size for certain Storage Area.
     *  From a PtPReducedChunk array.
     * 
     *
     * @param log
     * @param funcName
     * @param user
     * @param spaceAvailableSURLs
     */


    
    public void decreaseFreeSpaceForSA(Logger log,
            String funcName,VomsGridUser user, ArrayList spaceAvailableSURLs) {

        

        // Update the Storage Area Free size into the DB for each PtPReducedChunkData specified

        for (int i = 0; i < spaceAvailableSURLs.size(); i++) {

            log.debug("srmPutDone: Updating Storage Area free size on db");

            ReducedPtPChunkData chunkData = (ReducedPtPChunkData) spaceAvailableSURLs.get(i);
            TSURL surl = chunkData.toSURL();

            updateFreeSpaceForSA(log, funcName, user, surl, SpaceHelper.REMOVE, 0);
        
        }

    }
    
    
    /**
     * @param log
     * @param funcName
     * @param user
     * @param surl
     */
    public void decreaseFreeSpaceForSA(Logger log,
            String funcName,VomsGridUser user, TSURL surl) {
        
        updateFreeSpaceForSA(log, funcName, user, surl, SpaceHelper.REMOVE, 0);
    
    }
    
    /**
     * Increase the free Storage Area free space in case of file removal
     * The file size have to be passed as parameter since the file does not exists anymore.
     * @param log
     * @param funcName
     * @param user
     * @param surl
     * @param fileSize
     */
    public void increaseFreeSpaceForSA(Logger log,
            String funcName,GridUserInterface user, TSURL surl, long fileSize) {
        
        updateFreeSpaceForSA(log, funcName, user, surl, SpaceHelper.ADD, fileSize);
    
    }
    
    
    public boolean isSAFull(Logger log, StoRI stori) {
      
        log.debug("Checking if the Storage Area is full");
        
        VirtualFSInterface fs = stori.getVirtualFileSystem();
        
        // Get StorageSpaceData from the database
        String SSDesc;
        StorageSpaceData spaceData = null;

        try {
            SSDesc = fs.getSpaceTokenDescription();
            spaceData = fs.getSpaceByAlias(SSDesc);

        } catch (NamespaceException e1) {
            log.error("Unable to create storage space data", e1);
            return false;
        }

        if ((spaceData != null) && (spaceData.getUnusedSizes().value() == 0) ) {
            log.debug("UnusedSize="+spaceData.getUnusedSizes().value());
            return true;
        } else 
            return false;
        
    }
    
    public long getSAFreeSpace (Logger log, StoRI stori) {   
        log.debug("Checking if the Storage Area is full");
        
        VirtualFSInterface fs = stori.getVirtualFileSystem();
        
        // Get StorageSpaceData from the database
        String SSDesc;
        StorageSpaceData spaceData = null;

        try {
            SSDesc = fs.getSpaceTokenDescription();
            spaceData = fs.getSpaceByAlias(SSDesc);

        } catch (NamespaceException e1) {
            log.error("Unable to create storage space data", e1);
            return -1;
        }
        
        if(spaceData != null)
            return spaceData.getUnusedSizes().value();
        else 
            return -1 ;
        
    }
    
    public TSpaceToken getTokenFromStoRI(Logger log, StoRI stori) {
        
        log.debug("SpaceHelper: getting space token from StoRI");
        TSpaceToken token = TSpaceToken.makeEmpty();
        VirtualFSInterface fs = stori.getVirtualFileSystem();
        
        // Get StorageSpaceData from the database
        String SSDesc;
        StorageSpaceData spaceData = null;

        try {
            SSDesc = fs.getSpaceTokenDescription();
            spaceData = fs.getSpaceByAlias(SSDesc);

        } catch (NamespaceException e1) {
            log.error("Unable to create storage space data", e1);
        }
        
        if(spaceData != null) 
            token = spaceData.getSpaceToken();
        
        return token;
        
    }
    
    
    
    
    /**
     * Returns the spaceTokens associated to the 'user' AND 'spaceAlias'. If 'spaceAlias' is NULL or
     * an empty string then this method returns all the space tokens this 'user' owns.
     * @param user VomsGridUser user.
     * @param spaceAlias User space token description.
     */
    private Boolean isDedfaultSpaceToken(TSpaceToken token) {
      Boolean found = false;

      this.config = Configuration.getInstance();
      List<String >tokens = config.getListOfDefaultSpaceToken();
      for (int i = 0; i < tokens.size(); i++) {
        if ( ( tokens.get(i)).toLowerCase().equals(token.getValue().toLowerCase())) {
          found = true;
        }
      }

      return found;
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


    public void createVOSA_Token(Logger log, String spaceTokenAlias, TSizeInBytes totalOnLineSize, String spaceFileName) {
        
        ReservedSpaceCatalog spacec =  new ReservedSpaceCatalog();

        ArrayOfTSpaceToken tokenArray;

        /*
         * Build the storage space Data
         *
         * A Fake Grid User is needed
         */
        GridUserInterface stormServiceUser = GridUserManager.makeStoRMGridUser();


        //tokenArray = this.getSpaceTokensByAlias(spaceTokenAlias);
        tokenArray = spacec.getSpaceTokens(stormServiceUser, spaceTokenAlias);

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

                spacec.addStorageSpace(ssd);
                //Track into global set to remove obsolete SA_token
                ReservedSpaceCatalog.voSA_spaceTokenSet.add(ssd.getSpaceToken());

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
            StorageSpaceData catalog_ssd = spacec.getStorageSpace(token);

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
                ReservedSpaceCatalog.voSA_spaceTokenSet.add(token);

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

                    spacec.updateAllStorageSpace(catalog_ssd);
                    ReservedSpaceCatalog.voSA_spaceTokenSet.add(token);

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
     public void purgeOldVOSA_token(Logger log) {
         ReservedSpaceCatalog spacec =  new ReservedSpaceCatalog();
         log.debug("VO SA: garbage collecting obsolete VOSA_token");

         Iterator iter = ReservedSpaceCatalog.voSA_spaceTokenSet.iterator();
         while (iter.hasNext()) {
             log.debug("VO SA token REGISTRED:" + ( (TSpaceToken) iter.next()).getValue());
         }

         GridUserInterface stormServiceUser = GridUserManager.makeStoRMGridUser();

         //Remove obsolete space
         ArrayOfTSpaceToken token_a = spacec.getSpaceTokens(stormServiceUser, null);
         for (int i = 0; i < token_a.size(); i++) {
             log.debug("VO SA token IN CATALOG:" + token_a.getTSpaceToken(i).getValue());
         }

         if (token_a != null) {
             for (int i = 0; i < token_a.size(); i++) {

                 if (!ReservedSpaceCatalog.voSA_spaceTokenSet.contains(token_a.getTSpaceToken(i))) {
                     //This VOSA_token is no more used, removing it from persistence
                     TSpaceToken tokenToRemove = token_a.getTSpaceToken(i);
                     log.debug("VO SA token " + tokenToRemove + " is no more used, removing it from persistence.");
                     spacec.release(stormServiceUser, tokenToRemove);
                 }
             }
         }
         else {
             log.warn("Space Catalog garbage SA_Token: no SA TOKENs specified. Please check your namespace.xml file.");
         }

         ReservedSpaceCatalog.voSA_spaceTokenSet.clear();

    }

    
    
    
    

}
