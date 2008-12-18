/**
 * This is the Abort executor for a PtG request. 
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.catalogs.*;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.griduser.CannotMapUserException;

public class PtGAbortExecutor implements AbortExecutorInterface {
    
    //TODO DEFINE THE TIMEOUT FROM CONFIGURATION
    // CMQ sotto i 3 minuti per discorso tcp timeout?
    static Configuration  config        = Configuration.getInstance();
    private static int maxLoopTimes = config.getMaxLoop();
    //private static int maxLoopTimes = 100;
    
    private static final Logger log = Logger.getLogger("dataTransfer");
    private RequestSummaryCatalog summaryCat = null;
    
    public PtGAbortExecutor() {};
    
    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData) {
        
        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        ArrayOfTSURLReturnStatus arrayOfTSurlRetStatus = new ArrayOfTSURLReturnStatus();
        
        summaryCat = RequestSummaryCatalog.getInstance();
        
        boolean requestFailure, requestSuccess;
        TReturnStatus globalStatus = null;

        log.debug("srmAbortRequest: Started PtGAbortExecutor");
        
        ArrayOfSURLs surlArray = inputData.getArrayOfSURLs();
        
        /**
         * 0) Get all Chunk related to the specified request according with user specification (in case of AbortFiles).
         * 1) Wait until a chunk goes in SRM_FILE_PINNED status.(or any other status different from SRM_QUEUED).
         * 2) Rollback. For a PtG request the rollback only means to remove the acl eventually inserted
         * into the Volatile-JiT catalog.
         */
        
        /**
         * PROBLEMA! LO STATO DEI CHUNK CAMBIA SOLO AL MOMENTO DEL LOOKUP NEL DB.
         * DOPO NON CAMBIA PIU!
         * IL CAMBIAMENTO DI STATO FATTO SUL CHUNK DEVE ESSERE SALVATO.
         */
        
        /****************** Phase 0 ******************/
        
        /* Query the DB to find all the Chunk associated to the request token */
        PtGChunkCatalog getCatalog = PtGChunkCatalog.getInstance();
        Collection chunks = getCatalog.lookup(inputData.getRequestToken());

        if(chunks.isEmpty()) { // No Chunk found
            log.debug("PtGAbortExecutor: Request - Invalid request token");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] successfully done with [status: "+globalStatus+"]");
            return outputData;
        }
 
        /**
         * Get only the SURL requested in a AbortFile Request
         * Define a new Collection to contains the "epurated" chunk, removing the ones not specified 
         * in input request
         */ 
        
        ArrayList newChunk = new ArrayList();
        
        if(inputData.getType() ==  AbortGeneralInputData.ABORT_FILES) {
            log.debug("PtGAbortExecutor: Case of AbortFile request. Purge Chunks with SurlArray.");
            
            /**
             * Get the related Chunk for each SURL in the input SurlArray.
             * If a Surl requested is not founf, the TSURLReturnStatus related is setted to SRM_INVALID_PATH
             */
            
            for(int i=0;i<surlArray.size();i++){
                
                TSURL surl = surlArray.getTSURL(i);
                log.debug("srmAbortFiles: PTGAbortExecutor: Checking SURL[" + i + "]: " + surl.toString());
                
                // auxChunkData stores information about a single item (SURL) in the catalog
                PtGChunkData auxChunkData = null;
                
                /* Search SURL[i] (specified in the request) inside the catalog */
                Iterator j = chunks.iterator();
                boolean surlFound = false;
                
                while ((j.hasNext())&&!surlFound) {
                    auxChunkData = (PtGChunkData) j.next();
                    if (surl.equals(auxChunkData.fromSURL())) {
                        surlFound = true;
                    }
                }
                
                if(!surlFound) {
                    /**
                     * Surl not found.
                     * Specifiy an appropriate output status and add to the global SURLStatusArray
                     */
                    log.debug("PtGAbortExecutor: SURL NOT found, invalid Chunk!");
                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
                    surlReturnStatus.setSurl(surl);
                    surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_INVALID_PATH, "SURL specified does not referes to this request token."));
                    log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+(i+1)+" of "+surlArray.size()+" [SURL:"+surlArray.getTSURL(i)+"] failed  with [status: "+surlReturnStatus.getStatus()+"]");
                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
                } else {
                    /**
                     * Add this chunk to the new chunk Container.
                     * The new Chunk container contains only the chunk to abort according on the user specification
                     * (using the SurlArray) parameter
                     */
                    log.debug("PtGAbortExecutor: Surl FOUND. Valid Chunk. Added to new chunk container.");
                    newChunk.add(auxChunkData);
                }
            
            } //for
            
            //Substitute the old Chunks to with the "epurated" version.
            log.debug("Abort: Old chunk size ="+chunks.size());
            chunks = newChunk;
            log.debug("Abort: New chunk size ="+chunks.size());
            
            
        }//if

        /********* Check here the new chunks container is not empty! ******/
        if(chunks.isEmpty()) { // No Chunk found
            log.debug("Abort Request - No surl specified associated to request token");
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "All surl specified does not referes to the request token.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }
        
        
        /********* Phase 1 Wait until the request goes in a status different from REQUEST_QUEUED ********/
  
        int  chunkAborted = 0;
        
        //To avoid deadlock
        int counter = 0;
        //NumberOfFailure for globalStatus
        int errorCount = 0;
        
        int totalSize = chunks.size();
        
        while(chunkAborted<totalSize && (counter<maxLoopTimes)) {
            //Increment loop times
            counter++;
            
            // auxChunkData stores information about a single item (SURL) in the catalog
            PtGChunkData auxChunkData = null;
            
            //This new Collection is used to avoid the ConcurrentModification exception 
            //on the chsunks Collection
                
            Collection listOfChunk = new ArrayList();
            listOfChunk.addAll(chunks);
            Iterator j = listOfChunk.iterator();
            int numOfSurl=0;
            while((j.hasNext())) {
            	numOfSurl++;
                auxChunkData = (PtGChunkData) j.next();
                
                /*
                 * This refresStatus operation refresh only the chunk status.
                 * TODO The entire CHUNK must be updated to prevent override of evenutally modified field.
                 */  
                
                log.debug("srmAbortRequest: PtGAbortExecutor: refresh chunk...");
                auxChunkData = getCatalog.refreshStatus(auxChunkData);
                log.debug("srmAbortRequest: PtGAbortExecutor: refresh done.");
                /*
                 * Check if any chunk have a status different from SRM_REQUEST_INPROGESS.
                 * That means the request execution is finished, and the rollback start.
                 */
                
                if (!(auxChunkData.status().getStatusCode().equals(TStatusCode.SRM_REQUEST_INPROGRESS ))) {
                    /*
                     * If an EXECUTED CHUNK is found, then it is ABORTED.
                     */
                    log.debug("srmAbortRequest: PtGAbortExecutor: PtGChunk not in IN_PROGRESS state. Ready for ABORT.");
                    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
                    
                    /******** Phase (3) of Abort *************/
                    log.debug("srmAbortRequest: PtGAbortExecutor: start Phase(3)");
                    
                    /*
                     * AvancePicker HACK!
                     * Due to a thread issue in the advancePicker.abortRequest(...) we have to
                     * check here if the chunk has already been aborted frome the picker.
                     * In such case, the manageAuthorizedAbort is not needed 
                     */
                    if((auxChunkData.status().getStatusCode().equals(TStatusCode.SRM_ABORTED))) {
                        //The AdvancedPicker have already aborted the chunk. 
                        log.debug("PtGAbortExecutor: CHUNK already aborted!");
                        surlReturnStatus.setSurl(auxChunkData.fromSURL());
                        surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
                    } else {
                        //Chunk not ABORTED. We have to work...
                        log.debug("PtPAbortExecutor: CHUNK to abort!");
                        surlReturnStatus = manageAuthorizedAbort(auxChunkData);
                    }
                    
                    //Remove this chunks from the other to abort.
                    chunks.remove(auxChunkData);
                    
                    if((surlReturnStatus.getStatus().getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
                        log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+listOfChunk.size()+" [SURL:"+auxChunkData.fromSURL()+"] successfully done  with [status: "+surlReturnStatus.getStatus()+"]");
                    } else {
                        log.info("srmAbortFiles: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+numOfSurl+" of "+listOfChunk.size()+" [SURL:"+auxChunkData.fromSURL()+"] failed with [status: "+surlReturnStatus.getStatus()+"]");
                       	errorCount++;
                    }
                    //Add returnStatus 
                    arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
                    
                    //Increment number of chunk aborted
                    chunkAborted++;
                }
            }
            if (chunkAborted<totalSize) {
                //Sleep 1 Second
                log.debug("PtGAbortExecutor: I'm waiting...");
                //Wait for 1 second
                //long start =  System.currentTimeMillis();
                //long index = 0;
                //while(index<start+1000)
                //    index = System.currentTimeMillis();
                
                try {
                    long numMillisecondsToSleep = 100; // 0.1 seconds
                    Thread.sleep(numMillisecondsToSleep);
                } catch (InterruptedException e) {

                }
                
                
                
                log.debug("PtGAbortExecutor: Wake up! It's time to work...");
            }
            
        }//WHILE
        log.debug("PtGAbortExecutor: Cycles done.");
        
        // LoopTimes Exceeded?
        if(counter == maxLoopTimes) {
            //The ABORT execution is interrupted to prevent a deadlock situation
            log.debug("Abort: Timeout exceeded.");
            globalStatus = manageStatus(TStatusCode.SRM_INTERNAL_ERROR,"TimeOut for abort execution exceeded.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
            log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            return outputData;
        }
        
        if(errorCount == totalSize)
            globalStatus = manageStatus(TStatusCode.SRM_FAILURE,"Abort failed.");
        else if(errorCount > 0) {
            if(inputData.getType() == AbortGeneralInputData.ABORT_REQUEST)
                globalStatus = manageStatus(TStatusCode.SRM_FAILURE, "Some chunks failed.");
            else
                globalStatus = manageStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Some chunks failed.");
        } else {
            globalStatus = manageStatus(TStatusCode.SRM_SUCCESS,"Abort request completed.");
            if((inputData.getType() == AbortGeneralInputData.ABORT_REQUEST)) {
                RequestSummaryCatalog cat = RequestSummaryCatalog.getInstance();
                TReturnStatus requestStatus = null;
                try {
                    requestStatus = new TReturnStatus(TStatusCode.SRM_ABORTED, "Request Aborted.");
                } catch (InvalidTReturnStatusAttributeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cat.updateGlobalStatus(inputData.getRequestToken(), requestStatus);
            }
	} 
        //Set output data
        outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
        outputData.setReturnStatus(globalStatus);
        log.info("srmAbortRequest: <"+inputData.getUser()+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
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
           log.debug("AbortExecutor : Error creating returnStatus " + ex1);
       }
       return returnStatus;
    }
    

    /**
    *
    * Manage the roll back needed to execute an abort request.
    * @param chunkData PtGChunkData
    * @return returnStatus TSURLReturnStatus
    */

    private TSURLReturnStatus manageAuthorizedAbort(PtGChunkData chunkData) { 
        
        /* Query the DB to update the Chunk status */
        PtGChunkCatalog getCatalog = PtGChunkCatalog.getInstance();
        
        
        
        /******************************* Phase (3) ************************************/
        
        TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
        
        //Check also for the SRM_QUEUED status, in case Phase 1 or Phase2
        if((chunkData.status().getStatusCode().equals(TStatusCode.SRM_FILE_PINNED))||
                (chunkData.status().getStatusCode().equals(TStatusCode.SRM_REQUEST_QUEUED)) ) { //some other status || ???  ) 
            // Valid Status 
            
            //Manage Rollback
            
            VolatileAndJiTCatalog jitCat = VolatileAndJiTCatalog.getInstance();
            
            //This method is used to mark as expired the specified entry
            //in the JitCatalog. In this way the next time the GarbageCollector 
            //will wake up, it will remove the entry as expired.
            
            //jitCat.expireNowGet(PFN, LocalUser, ACL )
            
            
            //Change status to aborted
            chunkData.changeStatusSRM_ABORTED("Request aborted.");
            getCatalog.update(chunkData);
            
            surlReturnStatus.setSurl(chunkData.fromSURL());
            surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request successfully aborted."));
            return surlReturnStatus;
            
        } else {
            //The Chunk is in a SRM_Status from wich an Abort operation is not possible! (SRM_FAILURE or SRM_SUCCESS)
            surlReturnStatus.setSurl(chunkData.fromSURL());
            
            //Create different case that depends on the CHUNK status!
            
            if((chunkData.status().getStatusCode().equals(TStatusCode.SRM_FILE_LIFETIME_EXPIRED))) {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Request is in a final status. Abort not allowed."));
            } else if((chunkData.status().getStatusCode().equals(TStatusCode.SRM_RELEASED))) {
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Request is in a final status. Abort not allowed."));
            } else {   
                surlReturnStatus.setStatus(manageStatus(TStatusCode.SRM_FAILURE,"Abort request not executed."));
            }
            
            return surlReturnStatus;
        
        }

    }

}
