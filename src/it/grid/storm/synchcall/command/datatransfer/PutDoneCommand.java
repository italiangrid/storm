package it.grid.storm.synchcall.command.datatransfer;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TFileType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TStorageSystemInfo;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.catalogs.*;
import it.grid.storm.common.SpaceHelper;
import it.grid.storm.common.StorageSpaceData;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.common.HiddenFileT1D1Plugin;
import it.grid.storm.synchcall.common.T1D1PluginInterface;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneOutputData;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.StorageClassType;
import it.grid.storm.namespace.model.VirtualFS;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author=lucamag luca.magnoniATcnaf.infn.it
 *     @author  Alberto Forti
 * @date = Oct 10, 2008
 *
 */


public class PutDoneCommand extends DataTransferCommand implements Command
{
    private static final Logger log = Logger.getLogger("dataTransfer");
    private final String funcName = "PutDone: ";
    private Configuration config;

    public PutDoneCommand() {};

    /**
     * Does a PutDone. Used to notify the SRM that the client completed a file transfer to the TransferURL in the allocated
     * space (by a PrepareToPut).
     */
    public OutputData execute(InputData absData)
    {
        
        PutDoneOutputData outputData = new PutDoneOutputData();
        PutDoneInputData inputData = (PutDoneInputData) absData;
        boolean requestFailure;
        TReturnStatus globalStatus = null;

        log.debug(funcName + "Started.");

        /******************** Check for malformed request: missing mandatory input parameters ****************/
        requestFailure = false;
        if (inputData == null)
            requestFailure = true;
        else if (inputData.getRequestToken() == null)
            requestFailure = true;
        else if (inputData.getArrayOfSURLs() == null)
            requestFailure = true;
        else if (inputData.getArrayOfSURLs().size() < 1)
            requestFailure = true;
        if (requestFailure) {
            log.debug(funcName + "Invalid input parameter specified");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Missing mandatory parameters");
            } catch (InvalidTReturnStatusAttributeException e) {
                log.warn(funcName + "dataTransferManger: Error creating returnStatus " + e);
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);

            if(inputData==null)
                log.error("srmPutDone: <> Request for [token:] [SURL:] failed with [status: "+globalStatus+"]");
            else
                log.error("srmPutDone: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"]failed with [status: "+globalStatus+"]");

            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        VomsGridUser user = (VomsGridUser) inputData.getUser();
        if (user == null) {
            log.error(funcName + "the user field is NULL");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                "Unable to get user credential!");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.debug(funcName + "dataTransferManger: Error creating returnStatus " + ex1);
            }

            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmPutDone: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"]failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /* Maps the VOMS Grid user into Local User */
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        } catch (CannotMapUserException e) {
            log.debug(funcName + "Unable to map the user '" + user + "' in a local user.", e);
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, "Unable to map the user");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                // Nothing to do, it will never be thrown
                log.error(funcName + "dataTransferManger: Error creating returnStatus ");
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmPutDone: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"]failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /********************************** Start to manage the request ***********************************/
        TRequestToken requestToken = inputData.getRequestToken();
        log.debug(funcName + "requestToken=" + requestToken.toString());

        /* Query the DB to find all the SURLs associated to the request token */
        PtPChunkCatalog dbCatalog = PtPChunkCatalog.getInstance();
        Collection chunks = dbCatalog.lookupReducedPtPChunkData(requestToken);

        if (chunks.isEmpty()) { // No SURLs found
            log.debug(funcName + "Invalid request token");

            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            } catch (InvalidTReturnStatusAttributeException ex) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "dataTransferManger: Error creating returnStatus " + ex);
            }

            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmPutDone: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"]failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /* Execute a PutDone for all the SURLs specified in the request (the following for loop).
         * Each requested SURL must be found in the catalog (as a SURL associated with the
         * specified request token), otherwise the return status of the missing SURL indicates
         * the error.
         */
        ArrayList listOfSURLs = inputData.getArrayOfSURLs().getArrayList();
        int num_SURLs = listOfSURLs.size();
        ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();

        // spaceAvailableSURLs will contain all the SURLs that must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        ArrayList spaceAvailableSURLs = new ArrayList(num_SURLs);

        requestFailure = true;
        boolean requestSuccess = true;
        boolean requestAborted = true;
        
        // ArrayOfSURLs is a mandatory parameter, therefore "num_SURLs" is > 0 and inside the for loop
        // the two boolean variables "requestFailure" and "requestSuccess" are correctly set.
        
        for (int i = 0; i < num_SURLs; i++) {
            TSURL surl = (TSURL) listOfSURLs.get(i);
            log.debug(funcName + "Checking SURL[" + i + "]: " + surl.toString());

            // auxChunkData stores information about a single item (SURL) in the catalog
            ReducedPtPChunkData auxChunkData = null;
            
            // Search SURL[i] (specified in the request) inside the catalog
            Iterator j = chunks.iterator();
            boolean surlFound = false;
            while (j.hasNext()) {
                auxChunkData = (ReducedPtPChunkData) j.next();
                if (surl.equals(auxChunkData.toSURL())) {
                    surlFound = true;
                    break;
                }
            }

            // Execute the PutDone on SURL[i]
            TReturnStatus surlReturnStatus = null;
            try {
                if (surlFound) { // SURL found, go on with PutDone
                    log.debug(funcName + "SURL[" + i + "] found!");
                    surlReturnStatus = auxChunkData.status();
                    if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_SPACE_AVAILABLE) {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                        // The db will be updated at once after this for loop
                        spaceAvailableSURLs.add(auxChunkData);
                        requestFailure = false;
                        requestAborted = false;
                    } else if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_SUCCESS) {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR, "");
                        requestSuccess = false;
                        requestAborted = false;
                    } else if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_ABORTED) {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                        "PtP status for this SURL is SRM_ABORTED");
                        requestSuccess = false;
                    } else {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
                        "Check StatusOfPutRequest for more information");
                        requestSuccess = false;
                        requestAborted = false;
                    }
                } else { // SURL not found, set the corresponding return status value
                    log.debug(funcName + "SURL[" + i + "] NOT found in the DB!");
                    surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                    "SURL does not refer to an existing file for the specified request token");
                    requestSuccess = false;
                    requestAborted = false;
                }
            } catch (InvalidTReturnStatusAttributeException e) {
                // Nothing to do, it will never be thrown
                surlReturnStatus = null;
                log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
            }

            /* Add the TSURLReturnStatus of SURL[i] to arrayOfFileStatus */
            try {
                if (surlReturnStatus == null)
                    surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
                    "Internal error: try again...");
                TSURLReturnStatus surlRetStatus = new TSURLReturnStatus(surl, surlReturnStatus);

                if (surlReturnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                    log.info("srmPutDone: <"+user+"> Request for [token:"+requestToken+"] for [SURL:"+surl+"] successfully done with: [status:"+surlReturnStatus+"]");
                } else {
                    log.error("srmPutDone: <"+user+"> Request for [token:"+requestToken+"] for [SURL:"+surl+"] failed with: [status:"+surlReturnStatus+"]");
                }
                arrayOfFileStatus.addTSurlReturnStatus(surlRetStatus);
            } catch (InvalidTReturnStatusAttributeException e1) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e1);
            } catch (InvalidTSURLReturnStatusAttributeException e2) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "Error InvalidTSURLReturnStatusAttributeException" + e2);
            }
        }

        // Now spaceAvailableSURLs contains all the SURLs for which to do a PutDone. To execute this operation
        // three steps are needed for each SURL:
        // 1- if the SURL is volatile than an entry in the table Volatile must be created;
        // 2- JiTs must me removed from the TURL;
        // 3- The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        // NEW: if the SA is T1D1, an hidden file is created.
        VolatileAndJiTCatalog volatileAndJiTCatalog = VolatileAndJiTCatalog.getInstance();
        for (int i=0; i<spaceAvailableSURLs.size(); i++) {
            ReducedPtPChunkData chunkData = (ReducedPtPChunkData) spaceAvailableSURLs.get(i);
            TSURL surl = chunkData.toSURL();
            StoRI stori = null;
            // Retrive the StoRI associate to the SURL
            try {
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                stori = namespace.resolveStoRIbySURL(surl, user);
            } catch (NamespaceException e1) {
                log.debug(funcName + "Unable to build StoRI by SURL: " + surl.toString(), e1);
                continue;
            }   
            // 1- if the SURL is volatile create an entry in the Volatile table
            if (chunkData.fileStorageType() == TFileStorageType.VOLATILE)
                volatileAndJiTCatalog.trackVolatile(stori.getPFN(), Calendar.getInstance(), chunkData.fileLifetime());
            // 2- JiTs must me removed from the TURL;
            if (stori.hasJustInTimeACLs()) {
                log.debug(funcName + "JiT case, removing ACEs on SURL: " + surl.toString());
                // Retrive the PFN of the SURL parents
                List storiParentsList = stori.getParents();
                List pfnParentsList = new ArrayList(storiParentsList.size());
                for (int j=0; j<storiParentsList.size(); j++) {
                    pfnParentsList.add(((StoRI) storiParentsList.get(j)).getPFN());
                }
                volatileAndJiTCatalog.expirePutJiTs(stori.getPFN(), lUser);
            }
            
            
            
            
            
            
            
            
            
            
        }
        //WARNING! This purge overload the mysqld in case of large volatile table and multiple PD requests
        //log.debug(funcName + "Purging VolatileAndJiTCatalog...");
        //volatileAndJiTCatalog.purge();

        // 3- The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        if (spaceAvailableSURLs.size() > 0)
            dbCatalog.transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(spaceAvailableSURLs);

        /* Set the request status */
        try {
            if (requestAborted) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_ABORTED, "Request " + requestToken.toString());
            } else if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
            } else if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
                "All file requests are successfully completed");
            } else {
                globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                "Details are on the file statuses");
            }

            log.debug(funcName + "Finished with status: " + globalStatus.toString());
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
        }

        /* 4-
         * NEW! Management of T1D1 Storage Class.
         * In case of T1D1 Storage Class, an hidden file is created to manage 
         * the migration to tape.
         */
        VirtualFSInterface fs = null;
        
        for (int i=0; i<spaceAvailableSURLs.size(); i++) {
            ReducedPtPChunkData chunkData = (ReducedPtPChunkData) spaceAvailableSURLs.get(i);
            TSURL surl = chunkData.toSURL();
            StoRI stori = null;
            // Retrive the StoRI associate to the SURL
            try {
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                stori = namespace.resolveStoRIbySURL(surl, user);
            } catch (NamespaceException e1) {
                log.debug(funcName + "Unable to build StoRI by SURL: " + surl.toString(), e1);
                continue;
            }   
            
            
            StorageClassType stype = null;
            fs = stori.getVirtualFileSystem();
            if(fs !=null) {
                try {
                    stype = fs.getStorageClassType();
                } catch (NamespaceException e) {
                log.debug("Error getting StorageClassType from FileSystem");
                }
            }
            
            if(stype !=null && stype.equals(StorageClassType.T1D1)) {
                //Get the T1D1Plugin
                config = Configuration.getInstance();
                String pluginName = config.getT1D1PluginName();
                String prefix = config.getT1D1HiddenFilePrefix();
                String className  = null;
                T1D1PluginInterface plugin = null;
                
                if(pluginName.equals("HiddenFile")) {
                    className = "it.grid.storm.dataTransfer.HiddenFileT1D1Plugin";
                    //Use Reflection here
                    plugin = new HiddenFileT1D1Plugin();
                } else {
                    log.debug("T1D1Plugin specified does not exists yet!.");
                }
                
                int error=0;
                if(plugin != null)
                    error=plugin.startMigration(stori, prefix);
                if(error!=0)
                    log.debug("T1D1Plugin error in managing the file migration.");
                    
            }
        }
        
        if (globalStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
            log.info("srmPutDone: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"] successfully done with: [status:"+globalStatus+"]");
        } else if (globalStatus.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
            log.info("srmPutDone: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"] partially done with: [status:"+globalStatus+"]");
        } else {
            log.error("srmPutDone: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+inputData.getArrayOfSURLs()+"] failed with: [status:"+globalStatus+"]");

        }
        
        
        SpaceHelper sh = new SpaceHelper();
        
        //Update the used space into Database
        sh.decreaseFreeSpaceForSA(log, funcName, user, spaceAvailableSURLs);
        
        
        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(arrayOfFileStatus);

        
        
        return outputData;
    }
}
