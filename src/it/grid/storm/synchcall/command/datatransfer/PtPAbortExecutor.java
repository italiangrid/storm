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

/**
 * This is the Abort executor for a PtP request.
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.PtPChunkCatalog;
//import it.grid.storm.catalogs.PtPData;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
//import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.space.ReserveSpaceCommand;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PtPAbortExecutor implements AbortExecutorInterface {

    private static final Logger log = LoggerFactory.getLogger(PtPAbortExecutor.class);

    //TODO DEFINE THE TIMEOUT FROM CONFIGURATION
    // CMQ sotto i 3 minuti per discorso tcp timeout?

    static Configuration  config        = Configuration.getInstance();
    private static int maxLoopTimes = PtPAbortExecutor.config.getMaxLoop();
    //private static int maxLoopTimes = 100;

    private NamespaceInterface namespace;



    public PtPAbortExecutor() {};

    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData) {
        //Used to delete the physical file
        namespace = NamespaceDirector.getNamespace();

        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        ArrayOfTSURLReturnStatus arrayOfTSurlRetStatus = new ArrayOfTSURLReturnStatus();

//        boolean requestFailure, requestSuccess;
        TReturnStatus globalStatus = null;

        PtPAbortExecutor.log.debug("srmAbortRequest: Start PtPAbortExecutor...");

        ArrayOfSURLs surlArray = inputData.getArrayOfSURLs();

        /**
         * 0) Get all Chunk related to the specified request according with user specification (in case of AbortFiles).
         * 1) Wait until the request goes in SRM_SPACE_AVAILABLE status.
         * 2) Rollback. For a PtP request the rollback means to:
         * - PtP without space reservation:
         * Remove the file (if not existing? And if partially wrote?).
         * - PtP with implicit space reservation (size specified):
         * Remove the file.(the space is associated directly to the file.)
         * - PtP with SpaceToken:
         * Recreaete the SpaceFile, update the SpaceCatalog and remove the file.
         * - PtP with SpaceToken and size
         * Recreate the space, update the space file, update the space catalog
         * and remove the files
         */


        /****************** Phase 0 ******************/

        /* Query the DB to find all the Chunk associated to the request token */
//        PtPChunkCatalog putCatalog = PtPChunkCatalog.getInstance();

        TRequestToken token = inputData.getRequestToken();
        
        //Get the CHunk collection
//        Collection<PtPPersistentChunkData> chunks = putCatalog.lookup(inputData.getRequestToken());
        Map<TSURL, TReturnStatus> surlStatusMap;
        try
        {
            surlStatusMap = SurlStatusManager.getSurlsStatus(token);
        } catch(IllegalArgumentException e)
        {
            log.error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: " + e);
            throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
        } catch(UnknownTokenException e)
        {
            PtPAbortExecutor.log.debug("PtPAbortExecutor: Request - Invalid request token");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        } catch(ExpiredTokenException e)
        {
            log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
            globalStatus = manageStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }
        
//      if(chunks.isEmpty()) { // No Chunk found
      if(surlStatusMap.isEmpty()) {
            PtPAbortExecutor.log.debug("PtPAbortExecutor: Request - Invalid request token");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /**
         * Get only the SURL requested in a AbortFile Request
         * Define a new Collection to contains the "epurated" chunk, removing the ones not specified
         * in input request
         */

//        ArrayList newChunk = new ArrayList();

        if(inputData.getType() ==  AbortGeneralInputData.ABORT_FILES) {
            PtPAbortExecutor.log.debug("srmAbortFiles: PtPAbortExecutor: Case of AbortFile request. Purge Chunks with SurlArray.");

            /**
             * Get the related Chunk for each SURL in the input SurlArray.
             * If a Surl requested is not founf, the TSURLReturnStatus related is setted to SRM_INVALID_PATH
             */

//            for(int i=0;i<surlArray.size();i++){
            ArrayList<TSURL> surlList = surlArray.getArrayList();
            surlStatusMap.keySet().retainAll(surlList);
            if(!surlStatusMap.keySet().containsAll(surlList))
            {
                for(TSURL surl : surlList)
                {
                    if(!surlStatusMap.containsKey(surl))
                    {
                        log.debug("srmAbortFiles: Abort: SURL NOT found, invalid Chunk!Set TSURLReturnStatus");
                        TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
                        surlReturnStatus.setSurl(surl);
                        surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INVALID_PATH, "SURL specified does not referes to this request token."));
                        log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+token+"] for [SURL:"+surl+"] failed  with [status: "+surlReturnStatus.getStatus()+"]");
                        arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
                    }
                }
            }
//                TSURL surl = surlArray.getTSURL(i);
//                PtPAbortExecutor.log.debug("srmAbortFiles: Checking SURL[" + i + "]: " + surl.toString());

                // auxChunkData stores information about a single item (SURL) in the catalog
//                PtPData auxChunkData = null;

                /* Search SURL[i] (specified in the request) inside the catalog */
//                Iterator j = chunks.iterator();
//                boolean surlFound = false;
//
//                while ((j.hasNext())&&!surlFound) {
//                    auxChunkData = (PtPData) j.next();
//                    if (surl.equals(auxChunkData.getSURL())) {
//                        surlFound = true;
//                    }
//                }

//                if(!surlFound) {
//                    /**
//                     * Surl not found.
//                     * Specifiy an appropriate output status and add to the global SURLStatusArray
//                     */
//                    PtPAbortExecutor.log.debug("srmAbortFiles: Abort: SURL NOT found, invalid Chunk!Set TSURLReturnStatus");
//                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
//                    surlReturnStatus.setSurl(surl);
//                    surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INVALID_PATH, "SURL specified does not referes to this request token."));
//                    PtPAbortExecutor.log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+(i+1)+" of "+surlArray.size()+" [SURL:"+surlArray.getTSURL(i)+"] failed  with [status: "+surlReturnStatus.getStatus()+"]");
//                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
//
//                } else {
//                    /**
//                     * Add this chunk to the new chunk Container.
//                     * The new Chunk container contains only the chunk to abort according on the user specification
//                     * (using the SurlArray) parameter
//                     */
//                    PtPAbortExecutor.log.debug("Abort: Surl FOUND. Valid Chunk. Added to new chunk container.");
//                    newChunk.add(auxChunkData);
//                }

//            } //for

            //Substitute the old Chunks to with the "epurated" version.
//            PtPAbortExecutor.log.debug("Abort: Old chunk size ="+chunks.size());
//            chunks = newChunk;
//            PtPAbortExecutor.log.debug("Abort: New chunk size ="+chunks.size());


        }//if

        /********* Check here the new chunks container is not empty! ******/
//        if(chunks.isEmpty()) { // No Chunk found
        if(surlStatusMap.isEmpty()) 
        {
            PtPAbortExecutor.log.debug("Abort Request - No surl specified associated to request token");
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "All surl specified does not referes to the request token.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }



        /********* Phase 1 Wait until the request goes in a status different from REQUEST_QUEUED ********/

        int  chunkAborted = 0;

        //To avoid deadlock
        int counter = 0;
        //NumberOfFailure for globalStatus
        int errorCount = 0;
        //Total initial Size
//        int totalSize = chunks.size();
        int totalSize = surlStatusMap.size();

        while((chunkAborted<totalSize)&&(counter<PtPAbortExecutor.maxLoopTimes)) {
            //Increment loop times
            counter++;

            // auxChunkData stores information about a single item (SURL) in the catalog
//            PtPPersistentChunkData auxChunkData = null;

            //This new Collection is used to avoid the ConcurrentModification exception
            //on the chsunks Collection

//            ArrayList<PtPPersistentChunkData> listOfChunk = new ArrayList<PtPPersistentChunkData>();
//            listOfChunk.addAll(chunks);
//            Iterator<PtPPersistentChunkData> j = listOfChunk.iterator();
            Iterator<Map.Entry<TSURL, TReturnStatus>> iterator = surlStatusMap.entrySet().iterator();
            int numOfSurl=0;
//            while((j.hasNext())) {
            while(iterator.hasNext())
            {
                Map.Entry<TSURL, TReturnStatus> surlStatus = iterator.next();
                numOfSurl++;
//                auxChunkData = j.next();

                /*
                 * To GET the actual status from DB the Chunk must be updated!
                 */
//                PtPAbortExecutor.log.debug("PtPAbortExecutor: refresh chunk...");
//                auxChunkData = putCatalog.refreshStatus(auxChunkData);
//                PtPAbortExecutor.log.debug("PtPAbortExecutor: refresh done.");

                /*
                 * Check if any chunk have a status different from SRM_REQUEST_INPROGESS.
                 * That means the request execution is finished, and the rollback start.
                 */

//                if (!(auxChunkData.getStatus().getStatusCode().equals(TStatusCode.SRM_REQUEST_INPROGRESS ))) {
                if (!(surlStatus.getValue().getStatusCode().equals(TStatusCode.SRM_REQUEST_INPROGRESS ))) {
                    /*
                     * If an EXECUTED CHUNK is found, then it is ABORTED.
                     */
                    PtPAbortExecutor.log.debug("PtPAbortExecutor: PtPChunk not in IN_PROGRESS state. Ready for Abort.");
                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();

                    /******** Phase (3) of Abort *************/
                    PtPAbortExecutor.log.debug("PtPAbortExecutor: start Phase(3)");

                    /*
                     * AvancePicker HACK!
                     * Due to a thread issue in the advancePicker.abortRequest(...) we have to
                     * check here if the chunk has already been aborted frome the picker.
                     * In that case, the manageAbort is not needed
                     */
//                    if((auxChunkData.getStatus().getStatusCode().equals(TStatusCode.SRM_ABORTED))) {
                    if((surlStatus.getValue().getStatusCode().equals(TStatusCode.SRM_ABORTED))) {
                        //The AdvancedPicker have already aborted the chunk.
                        PtPAbortExecutor.log.debug("PtPAbortExecutor: CHUNK already aborted!");
                        surlReturnStatus.setSurl(surlStatus.getKey());
                        surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
                    } else {
                        PtPAbortExecutor.log.debug("PtPAbortExecutor:CHUNK to abort.");
                        //Chunk not ABORTED. We have to work...
//                        surlReturnStatus = manageAuthorizedAbort(auxChunkData, inputData.getUser());
                        surlReturnStatus = manageAuthorizedAbort(token, surlStatus.getKey(), surlStatus.getValue(), inputData.getUser());
                    }

                    //Remove this chunks from the other to abort.
//                    chunks.remove(auxChunkData);
                    iterator.remove();


                    if((surlReturnStatus.getStatus().getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
                        log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+token+"] for SURL "+numOfSurl+" of "+totalSize+" [SURL:"+surlStatus.getKey()+"] successfully done  with [status: "+surlReturnStatus.getStatus()+"]");
                    } else {
//                        PtPAbortExecutor.log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+listOfChunk.size()+" [SURL:"+auxChunkData.getSURL()+"] failed with [status: "+surlReturnStatus.getStatus()+"]");
                        log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+totalSize+" [SURL:"+surlStatus.getKey()+"] failed with [status: "+surlReturnStatus.getStatus()+"]");
                        errorCount++;
                    }
                    //Add returnStatus
                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);

                    //Increment number of chunk aborted
                    chunkAborted++;

                }

            } //while chunk Collection

            if (chunkAborted<totalSize) {
                PtPAbortExecutor.log.debug("PtPAbortExecutor: Wait for some time...");
                //Be patient until some chunk finish its work...
                //  try {
                //Sleep 1 Second
                PtPAbortExecutor.log.debug("Abort: I'm waiting...");
                //this.wait(1000);
                //Wait for 1 second
                //long start =  System.currentTimeMillis();
                //long index = 0;
                //while(index<start+1000)
                //    index = System.currentTimeMillis();
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }


                try {
                    long numMillisecondsToSleep = 100; // 1 seconds
                    Thread.sleep(numMillisecondsToSleep);
                } catch (InterruptedException e) {

                }


                PtPAbortExecutor.log.debug("PtPAbortExecutor: Wake up! It's time to work...");
                log.debug("srmAbortRequest: PtPAbortExecutor: refresh surl status");
                try
                {
                    surlStatusMap = SurlStatusManager.getSurlsStatus(token, surlStatusMap.keySet());
                } catch(IllegalArgumentException e)
                {
                    log.error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: " + e);
                    throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
                } catch(UnknownTokenException e)
                {
                    PtPAbortExecutor.log.debug("PtPAbortExecutor: Request - Invalid request token, probably it is expired");
                    globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Expired request token");
                    outputData.setReturnStatus(globalStatus);
                    outputData.setArrayOfFileStatuses(null);
                    PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
                    return outputData;
                } catch(ExpiredTokenException e)
                {
                    log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
                    globalStatus = manageStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
                    outputData.setReturnStatus(globalStatus);
                    outputData.setArrayOfFileStatuses(null);
                    PtPAbortExecutor.log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
                    return outputData;
                }
                log.debug("srmAbortRequest: PtPAbortExecutor: refresh done.");
            }

        }

        // LoopTimes Exceeded?
//        if(counter == PtPAbortExecutor.maxLoopTimes) {
        if(chunkAborted<totalSize) {
            //The ABORT execution is interrupted to prevent a deadlock situation
            PtPAbortExecutor.log.debug("Abort: Timeout exceeded.");
            globalStatus = manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"TimeOut for abort execution exceeded.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            PtPAbortExecutor.log.error("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        if(errorCount == totalSize) {
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE,"Abort failed.");
        } else if(errorCount > 0) {
            if(inputData.getType() == AbortGeneralInputData.ABORT_REQUEST) {
                globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "Some chunks failed.");
            } else {
                globalStatus = manageStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Some chunks failed.");
            }
        } else {
            globalStatus = manageStatus(TStatusCode.SRM_SUCCESS,"Abort request completed.");
            if((inputData.getType() == AbortGeneralInputData.ABORT_REQUEST)) {
                TReturnStatus requestStatus = manageStatus(TStatusCode.SRM_ABORTED, "Request Aborted.");
                RequestSummaryCatalog.getInstance().updateGlobalStatus(inputData.getRequestToken(), requestStatus);
            }
        }
        //Set output data
        outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
        outputData.setReturnStatus(globalStatus);
        PtPAbortExecutor.log.error("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] failed with [status: "+globalStatus+"]");
        return outputData;

    }


    /**
     *
     * @param statusCode statusCode
     * @param explanation explanation string
     * @return returnStatus returnStatus
     */

    private TReturnStatus manageStatus(TStatusCode statusCode,String explanation) {
        TReturnStatus returnStatus = null;
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            PtPAbortExecutor.log.debug("AbortExecutor : Error creating returnStatus " + ex1);
        }
        return returnStatus;
    }


    /**
     *
     * Manage the roll back needed to execute an abort request.
     * @param chunkData PtGChunkData
     * @return returnStatus TSURLReturnStatus
     */

//    private TSURLReturnStatus manageAuthorizedAbort(PtPPersistentChunkData chunkData, GridUserInterface guser) {
    private TSURLReturnStatus manageAuthorizedAbort(TRequestToken token, TSURL surl, TReturnStatus status, GridUserInterface guser)
    {
        boolean failure = false;
        //Used for ReserveSpace Operation
        namespace = NamespaceDirector.getNamespace();



        /* Query the DB update the desired Chunk */
//        PtPChunkCatalog putCatalog = PtPChunkCatalog.getInstance();

        /******************************* Phase (3) ************************************/

        PtPAbortExecutor.log.debug("PtPAbortExecutor: Phase(3) RollBack start.");

        TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
//        TSURL surl  =  chunkData.getSURL();
        StoRI stori = null;
        boolean res = false;

        //Check also for the SRM_QUEUED status, in case Phase 1 or Phase2

//        if((chunkData.getStatus().getStatusCode().equals(TStatusCode.SRM_SPACE_AVAILABLE))||
//                (chunkData.getStatus().getStatusCode().equals(TStatusCode.SRM_REQUEST_QUEUED)) ) { //some other status || ???  )
        if (TStatusCode.SRM_SPACE_AVAILABLE.equals(status.getStatusCode())
                || TStatusCode.SRM_REQUEST_QUEUED.equals(status.getStatusCode()))
        {
            // Valid Status
            PtPAbortExecutor.log.debug("PtPAbortExecutor: The Chunk is in a VALID state for the RollBack action!");
            if(!SurlStatusManager.isPersisted(token).isEmpty())
            {
                /*
                 * TODO TEMPORARY. catalog direct access needed until a RequestManager storing token and
                 * request infos will be available
                 */ 
                Collection<PtPPersistentChunkData> chunksData = PtPChunkCatalog.getInstance().lookup(token);  
                PtPPersistentChunkData chunkData = null;
                for(PtPPersistentChunkData surlChunkData  : chunksData)
                {
                    if(surlChunkData.getSURL().equals(surl))
                    {
                        chunkData = surlChunkData;
                        break;
                    }
                }
                //Manage Rollback

                if(chunkData == null)
                {
                    throw new IllegalStateException("Unexpected condition. token " + token
                            + " stored on the db but no associated surl " + surl + " found");
                }
                if(!(chunkData.getSpaceToken().isEmpty()))
                {
                    TSpaceToken sToken = chunkData.getSpaceToken();
                    TReturnStatus reserveSpaceStatus;
                    ReserveSpaceCommand rexec = new ReserveSpaceCommand();
    
                    //Space Token specified in the request
                    log.debug("PtPAbortExecutor: Space token found.");
                    /*
                     * We have now two possible cases:
                     * 1) With size specified.
                     * 2) Without size specified.
                     */
                    if (chunkData.expectedFileSize().isEmpty())
                    {
                        /*
                         * Without fileSize specified.
                         * In this case the whole space_file has been associated to
                         * the requested file.
                         * So the roll back must recreate the Space Catalog entry and
                         * update the information on used space.
                         */
                        reserveSpaceStatus = rexec.resetReservation(sToken);
                        if (!reserveSpaceStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
                        {
                            // Something goes wrong
                            failure = true;
                        }
                    }
                    else
                    {
                        /*
                         * With fileSize specified.
                         * In this case the spaceFile have to be increased
                         * from the free block of the removed put placeholder.
                         */
                        reserveSpaceStatus = rexec.updateReservation(chunkData.getSpaceToken(),
                                                         chunkData.expectedFileSize(), chunkData.getSURL());
                        if (!reserveSpaceStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
                        {
                            // Something goes wrong
                            failure = true;
                        }
                    }
                }
            }
            // The File Must be removed in any case!
            PtPAbortExecutor.log.debug("PtPAbortExecutor: No space token found.");
            PtPAbortExecutor.log.debug("PtPAbortExecutor: Start removing file from the file system...");
            LocalFile fileToRemove  = null;
            if (!surl.isEmpty()) 
            {
                try 
                {
                    stori = namespace.resolveStoRIbySURL(surl, guser);
                }
                catch (IllegalArgumentException e)
                {
                    PtPAbortExecutor.log.error("Unable to build StoRI by SURL and user", e);   
//                    chunkData.changeStatusSRM_INTERNAL_ERROR("Unable to build StoRI by SURL and user");
//                    putCatalog.update(chunkData);
                    try
                    {
                        SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl, TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL and user");
                    } catch(UnknownSurlException e1)
                    {
                        PtPAbortExecutor.log.error("Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ", e);
                    }
                    surlReturnStatus.setSurl(surl);
                    surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"Unable to build StoRI by SURL and user"));
                    return surlReturnStatus;
                } catch (NamespaceException ex) {
                    PtPAbortExecutor.log.error("Unable to build StoRI by SURL and user", ex);
//                    chunkData.changeStatusSRM_INTERNAL_ERROR("Unable to build StoRI by SURL and user");
//                    putCatalog.update(chunkData);
                    try
                    {
                        SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl, TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL and user");
                    } catch(UnknownSurlException e)
                    {
                        PtPAbortExecutor.log.error("Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ", e);
                    }
                    surlReturnStatus.setSurl(surl);
                    surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"Unable to build StoRI by SURL and user"));
                    return surlReturnStatus;
                }
            }

            //Get LocalFile and Remove
            fileToRemove = stori.getLocalFile();
            res = fileToRemove.delete();
            res = true;


            PtPAbortExecutor.log.debug("PtPAbortExecutor: File removed.");


            //This method is used to mark as expired the specified entry
            //in the JitCatalog. In this way the next time the GarbageCollector
            //will wake up, it will remove the entry as expired.
            //The file is already removed, but the garbage collection is
            //smart enought to manage the case.

//            VolatileAndJiTCatalog jitCat = VolatileAndJiTCatalog.getInstance();

            //jitCat.expireNowGet(PFN, LocalUser, ACL )
            //}


            //Remove the File from the JIT Catalog
            //JitCatalog.removeSomething

            //Change status to aborted
            if(failure) {
                //Something goes wrong during the update catalog operation
//                chunkData.changeStatusSRM_ABORTED("Request aborted.");
//                putCatalog.update(chunkData);
                try
                {
                    SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl, TStatusCode.SRM_ABORTED, "Request aborted.");
                } catch(UnknownSurlException e)
                {
                    PtPAbortExecutor.log.error("Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ", e);
                }
                surlReturnStatus.setSurl(surl);
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"Database Not Updated."));
                return surlReturnStatus;
            }

            if(res) {
//                chunkData.changeStatusSRM_ABORTED("Request Aborted");
//                putCatalog.update(chunkData);
                try
                {
                    SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl, TStatusCode.SRM_ABORTED, "Request aborted.");
                } catch(UnknownSurlException e)
                {
                    PtPAbortExecutor.log.error("Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ", e);
                }
                surlReturnStatus.setSurl(surl);
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
                return surlReturnStatus;
            } else {
//                chunkData.changeStatusSRM_ABORTED("Request aborted.");
//                putCatalog.update(chunkData);
                try
                {
                    SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl, TStatusCode.SRM_ABORTED, "Request aborted.");
                } catch(UnknownSurlException e)
                {
                    PtPAbortExecutor.log.error("Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ", e);
                }
                surlReturnStatus.setSurl(surl);
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"File not removed."));
                return surlReturnStatus;
            }

        } else {
            //The Chunk is in a SRM_Status from wich an Abort operation is not possible! (SRM_FAILURE or SRM_SUCCESS)
            surlReturnStatus.setSurl(surl);

            //Create different case that depends on the CHUNK status!

            if((status.getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Request is in a final status. Abort not allowed."));
            } else {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Abort request not executed."));
            }

            return surlReturnStatus;

        }

    }




}
