package it.grid.storm.common;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;

public class SpaceHelper {
    
    private static final int ADD = 0;
    private static final int REMOVE  = 1;
    
    
    
    
    /**
     * @param log
     * @param funcName
     * @param user
     * @param surl
     */
    
    private void updateFreeSpaceForSA(Logger log,
            String funcName,VomsGridUser user, TSURL surl, int operation, long filesize) {
        
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
            String funcName,VomsGridUser user, TSURL surl, long fileSize) {
        
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

}
