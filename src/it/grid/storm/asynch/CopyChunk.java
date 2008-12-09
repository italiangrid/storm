package it.grid.storm.asynch;

import org.apache.log4j.Logger;

import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Streets;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.CopyChunkData;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.griduser.GridUserInterface;


/**
 * Public super class from which both the PushCopyChunk and the PullCopyChunk are derived.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    september, 2005
 */
public abstract class CopyChunk implements Delegable, Chooser {
    private GetOperationResult getResult = null;  //Results of the GET operation
    private PutOperationResult putResult = null;  //Results of the PUT operation
    private TransferResult transferResult = null; //Results of the transfer operation

    protected TRequestToken localrt = null; //TRequestToken used to identify the local GET/PUT
    protected int n=-1;                     //Integer representing a progressive counter of the chunks being handled.
    protected CopyChunkData chunkData=null; //PtPChunkData that holds the specific info for this chunk
    protected RequestSummaryData rsd=null;  //RequestSummaryData containing all the statistics for the originating srmPrepareToPutRequest
    protected GridUserInterface gu=null;         //GridUser that made the request
    protected GlobalStatusManager gsm = null;
    protected boolean failure = false; //boolean that indicates if this chunks state is failure


    private static Logger log = Logger.getLogger("asynch");

    /**
     * Method used in a callback fashion in the scheduler for separately handling
     * PtG, PtP and Copy chunks.
     */
    public void choose(Streets s) {
        s.copyStreet(this);
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out
     * the task.
     *
     * It creates the appropriate TRequestToken for the srmPrepareToGet/Put that
     * takes place locally, it executes the getOperation, then the putOperation,
     * and finally executes the transferOperation. The new requestToken is created
     * by concatenating the one of this srmCopy request with the string "-copy-"
     * and the supplied integer n, which is the counter of a multifile srmCopy
     * request.
     *
     * The local get/put operation may fail because it could not start, or because
     * the SRM status is not SRM_DONE: then the copyChunk fails with SRM_ABORT
     * and appropriate explanation string which reports the local SRM STATUS.
     *
     * The remote get/put operation may fail because it could not start, the
     * SRMClient failed, the operation timed-out, or a state other than SRM_DONE
     * is returned. The srmCopy request fails with SRM_ABORT and appropriate
     * explanation strings may include the remote SRM STATUS.
     *
     * The Transfer could fail, and in that case the status of the srmCopy changes
     * to SRM_ABORT and explanation string reporting the GridFTP client error.
     */
    public void doIt() {
        log.info("Handling Copy chunk for user DN: "+this.gu.getDn()+"; fromSURL: "+this.chunkData.fromSURL()+"; toSURL: "+this.chunkData.toSURL()+"; for requestToken: "+this.rsd.requestToken());
        try {
            this.localrt = new TRequestToken(makeRequestTokenID()); //set the requestToken that will be used for the operaton carried out locally
            log.debug("RequestToken used for local operation: "+localrt);
            //go on with srmPrepareToGet opeartion
            this.getResult = executeGetOperation();
            log.debug("Result from get: "+getResult);
            if (getResult.successful() && getResult.status().getStatusCode()==TStatusCode.SRM_FILE_PINNED) {
                //go on with srmPreapareToPut operation
                this.putResult = executePutOperation(getResult.filesize());
                log.debug("Result from put: "+putResult);
                //ATTENTION! the following check for SRM_SUCCESS is done to keep compatibility between StoRM servers which use a hack thereby switching to state SRM_SUCCESS instead of transiting to SRM_SPACE_AVAILABLE!!! But for any other SRM2.2 compliant server, SRM_SPACE_AVAILABLE suffices!!!
                if (putResult.successful() && ((putResult.status().getStatusCode()==TStatusCode.SRM_SPACE_AVAILABLE) || (putResult.status().getStatusCode()==TStatusCode.SRM_SUCCESS)) ) {
                    //go on with GridFTP Client
                    this.transferResult = executeTransfer(getResult,putResult);
                    log.debug("Result from transfer: "+transferResult);
                    if (transferResult.successful()) {
                        //complete srmCopy!
                        chunkData.changeStatusSRM_SUCCESS("srmCopy successfully handled!");
                        log.debug("SRM Copy successful!");
                        this.failure = false; //gsm.successfulChunk(chunkData);
                    } else {
                        //transfer unsuccessfull!
                        String message = "GSIFTP transfer failed! "+transferResult.failureExplanation();
                        log.error(message);
                        chunkData.changeStatusSRM_FAILURE(message);
                        this.failure = true; //gsm.failedChunk(chunkData);
                    }
                } else {
                    //The put operation was problematic!
                    String message = "PUT part of srmCopy failed! ";
                    if (putResult.successful()) message = message + putResult.status().toString();
                    log.error(message);
                    chunkData.changeStatusSRM_FAILURE("PUT part of srmCopy failed! "+message);
                    this.failure = true; //gsm.failedChunk(chunkData);
                }
            } else {
                //the get operation was problematic!
                String message = "GET part of srmCopy failed! ";
                if (getResult.successful()) message = message + getResult.status().toString();
                log.error(message);
                chunkData.changeStatusSRM_FAILURE(message);
                this.failure = true; //gsm.failedChunk(chunkData);
            }
            log.info("Finished handling Copy chunk for user DN: "+this.gu.getDn()+"; fromSURL: "+this.chunkData.fromSURL()+"; toSURL: "+this.chunkData.toSURL()+"; for requestToken: "+this.rsd.requestToken()+"; result is: "+this.chunkData.status());
        } catch (InvalidTRequestTokenAttributesException e) {
            chunkData.changeStatusSRM_FAILURE("Internal error for local Get/Put does not allow srmCopy to start!");
            log.error("ERROR in CopyChunk! Attempt to create a new TRequestToken for local operation failed! "+e);
            this.failure = true; //gsm.failedChunk(chunkData);
        }
        //update statistics and status! It is the same for both normal completion and exception throwing!
        CopyChunkCatalog.getInstance().update(chunkData);
        if (this.failure) gsm.failedChunk(chunkData); else gsm.successfulChunk(chunkData);
    }

    /**
     * Private auxiliary method that returns a String to use as request token
     */
    private String makeRequestTokenID() {
        return  "copy-" + n + "-" + chunkData.requestToken().toString();
    }

    /**
     * Method that supplies a String describing this PushCopyChunk - for scheduler Log
     * purposes! It returns the request token of This request.
     */
    public String getName() {
        return "RequestToken of srmCopy: " + chunkData.requestToken();
    }

    abstract protected GetOperationResult executeGetOperation();

    abstract protected PutOperationResult executePutOperation(TSizeInBytes getFileSize);

    abstract protected TransferResult executeTransfer(GetOperationResult get, PutOperationResult put);





    /**
     * Protected auxiliary class holding the results from a Transfer. It contains a boolean
     * indicating whether the operation completed normally or could not proceed; in this case
     * it also contains an explanation string of the failure.
     *
     * @author EGRID - ICTP Trieste
     * @verson 1.0
     * @date   September, 2005
     */
    protected class TransferResult {
        private boolean successful = false;
        private String failureExplanation = "";

        /**
         * Constructor used to indicate a failed transfer: it requires a String
         * explaining the failure.
         */
        public TransferResult(String failureExplanation) {
            if (failureExplanation==null) failureExplanation="";
            this.successful = false;
            this.failureExplanation = failureExplanation;
        }

        /**
         * Constructor used to indicate a succesful transfer.
         */
        public TransferResult() {
            this.successful = true;
            this.failureExplanation = "";
        }

        /**
         * Method that returns a boolean indicating the result of the transfer.
         */
        public boolean successful() {
            return successful;
        }

        /**
         * Method that returns a String explaining the failure of the transfer: an
         * empty String is returned in cawe of successful transfer.
         */
        public String failureExplanation() {
            return failureExplanation;
        }
    }

    /**
     * Protected auxiliary class holding the results from a GetOperation. It contains a boolean
     * indicating whether the GetOperation was successful or could not complete normally: beware
     * that it does _not_ consider the SRM status. It only indicates whether the internal steps
     * were all completed normally.
     *
     * It also contains the TReturnStatus, the TTURL and the TFileSize, as well as a failureExplanation
     * String.
     */
    protected class GetOperationResult {
        private boolean successful = false; //boolean indicating if the operation was successful
        private TReturnStatus status = new TReturnStatus(); //TReturnStatus from srmPrepareToGet
        private TTURL getTURL = TTURL.makeEmpty(); //TURL from srmPrepareToGet
        private TSizeInBytes filesize = TSizeInBytes.makeEmpty(); //filesize fro msrmPrepareToGet
        private String failureExplanation = ""; //String containing an explanation of failure
        private TRequestToken rt = null; //request token assigned to PtG request

        /**
         * Constructor that returns a successful GetOperationResult containing the
         * TReturnStatus, TTURL, filesize and TRequestToken. If any of the supplied
         * parameters is null, a failed GetOperationResult is returned containing
         * the empty String as error explanation.
         */
        public GetOperationResult(TReturnStatus status, TTURL getTURL, TSizeInBytes filesize, TRequestToken rt) {
            if ((status!=null) && (getTURL!=null) && (filesize!=null) && (rt!=null)) {
                this.successful = true;
                this.status = status;
                this.getTURL = getTURL;
                this.filesize = filesize;
                this.rt=rt;
            } else {
                this.successful = false;
            }
        }

        /**
         * Constructor that returns a failed GetOperationResult containing a failureExplanation
         * String; if it is null, an empty String is used instead.
         */
        public GetOperationResult (String failureExplanation) {
            if (failureExplanation==null) failureExplanation="";
            this.successful = false;
            this.failureExplanation = failureExplanation;
        }

        /**
         * Method that returns true if the GetOperation completed successfully.
         */
        public boolean successful() {
            return this.successful;
        }

        /**
         * Method that returns the final status of the request
         */
        public TReturnStatus status() {
            return status;
        }

        /**
         * Method that returns the String with the explanation of the failure.
         */
        public String failureExplanation() {
            return failureExplanation;
        }

        /**
         * Method that returns the TURL.
         */
        public TTURL getTURL() {
            return getTURL;
        }

        /**
         * Method that returns the filesize.
         */
        public TSizeInBytes filesize() {
            return filesize;
        }

        /**
         * Method that returns the TRequestToken associated with the PtG.
         */
        public TRequestToken requetToken() {
            return rt;
        }

        public String toString() {
            return "GetOperationResult: successful="+successful+"; status="+status+"; getTURL="+getTURL+"; filesize="+filesize+"; requestToken="+rt;
        }
    }

    /**
     * Private auxiliary class holding the results from a PutOperation. It contains a boolean
     * indicating whether the PutOperation was successful or could not complete normally: beware
     * that it does _not_ consider the SRM status. It only indicates whether the internal steps
     * were all completed normally.
     *
     * It also contains the TReturnStatus and the TURL, as well as a failureExplanation
     * String and the TRequestToken associated with that request.
     */
    protected class PutOperationResult {
        private boolean successful = false; //boolean indicating if the operation was successful
        private TReturnStatus status = new TReturnStatus(); //TReturnStatus from srmPrepareToGet
        private TTURL putTURL = TTURL.makeEmpty(); //TURL from srmPrepareToGet
        private String failureExplanation = ""; //String containing an explanation of failure
        private TRequestToken rt = null; //TRequestToken associated to PtP

        /**
         * Constructor to make a successful PutOperationResult containing the
         * TReturnStatus and the TURL. If any of the supplied parameters is null,
         * a failed PutOperationResult is returned containing the empty String as
         * error explanation.
         */
        public PutOperationResult(TReturnStatus status, TTURL putTURL, TRequestToken rt) {
            if ((status!=null) && (putTURL!=null) && (rt!=null)) {
                this.successful = true;
                this.status = status;
                this.putTURL = putTURL;
                this.rt = rt;
            } else {
                this.successful = false;
            }
       }

        /**
         * Constructor to make a failed PutOperationResult containing a failureExplanation
         * String; if it is null, an empty String is used instead.
         */
        public PutOperationResult(String failureExplanation) {
            if (failureExplanation==null) failureExplanation="";
            this.successful = false;
            this.failureExplanation = failureExplanation;
        }

        /**
         * Method that returns true if the PutOperation completed all its internal steps.
         */
        public boolean successful() {
            return this.successful;
        }

        /**
         * Method that returns the final status reached by the request
         */
        public TReturnStatus status() {
            return this.status;
        }

        /**
         * Method that returns the String with the explanation of the failure.
         */
        public String failureExplanation() {
            return failureExplanation;
        }

        /**
         * Method that returns the TURL.
         */
        public TTURL putTURL() {
            return putTURL;
        }

        /**
         * Method that returns the TRequestToken associated to the PtP
         */
        public TRequestToken requestToken() {
            return this.rt;
        }

        public String toString() {
            return "PutOperationResult: successful="+successful+"; status="+status+"; putTURL="+putTURL+"; failureExplanation="+failureExplanation;
        }
    }
}
