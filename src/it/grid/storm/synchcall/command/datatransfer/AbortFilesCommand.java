/**
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.asynch.SchedulerFacade;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.scheduler.Scheduler;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestOutputData;

import org.apache.log4j.Logger;

public class AbortFilesCommand extends DataTransferCommand implements Command
{
    private static final Logger log = Logger.getLogger("dataTransfer");
    private RequestSummaryCatalog summaryCat = null;
    private Scheduler scheduler = null;
    private AdvancedPicker advancedPicker = null;
    private AbortExecutorInterface executor = null;

    public AbortFilesCommand() {};

    /**
     * SrmAbortRequest and SrmAbortFiles request differs only for
     * the SURL array specified in the AbortFiles request.
     * We can view the SrmAbortRequest as a special case of SrmAbortFiles with
     * an empty SURLArray.
     */

    public AbortRequestOutputData execute(AbortRequestInputData inputData) {

        log.debug("srmAbort: AbortExecutor input request:"+inputData);

        //Create the AbortFiles input data with the null SURLArray from
        //the AbortRequest input data.
        AbortGeneralInputData newInputData = AbortGeneralInputData.make(inputData);
        log.debug("srmAbort: abortExecutor: GeneralInputData created.");

        //Call the Abort* executor
        AbortGeneralOutputData newOutData = (AbortGeneralOutputData) execute((InputData)newInputData);

        //Creat a AbortRequestOutputData to send to the syncall server from
        //the general AbortFilesOutputData
        return AbortRequestOutputData.make(newOutData);
    }

    public AbortFilesOutputData doIt(AbortFilesInputData inputData) {

        //Create the AbortFiles input data with the null SURLArray from
        //the AbortRequest input data.
        AbortGeneralInputData newInputData = AbortGeneralInputData.make(inputData);

        //Call the Abort* executor
        AbortGeneralOutputData newOutData = (AbortGeneralOutputData) execute ((InputData)newInputData);

        //Creat a AbortFilesOutputData to send to the syncall server from
        //the general AbortGeneralOutputData
        return AbortFilesOutputData.make(newOutData);
    }




    /**
     * This executor performs a SrmAbortRequests.
     * This function prematurely terminate asynchronous requests of any types.
     * The effects of SrmAbortRequests() depends on the type of request.
     */

    public OutputData execute(InputData data)
    {
        summaryCat = RequestSummaryCatalog.getInstance();
        scheduler = SchedulerFacade.getInstance().crusherScheduler();
        advancedPicker = new AdvancedPicker();
        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        AbortGeneralInputData inputData = (AbortGeneralInputData) data;

        boolean requestFailure, requestSuccess;
        //Risultato Parziale
        boolean res = false;
        //Risultato Finale
        boolean done = false;
        boolean found = false;

        TReturnStatus globalStatus = null;
        ArrayOfTSURLReturnStatus arrayOfTSURLReturnStatus = null;

        log.debug("Started AbortRequest function.");

        /******************** Check for malformed request: missing mandatory input parameters ****************/

        requestFailure = false;
        if (inputData == null)
            requestFailure = true;
        else if (inputData.getRequestToken() == null)
            requestFailure = true;
        else if((inputData.getType() == AbortGeneralInputData.ABORT_FILES)&&(inputData.getArrayOfSURLs() == null))
            requestFailure = true;

        if (requestFailure) {
            log.debug("SrmAbortRequest: Invalid input parameter specified");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Missing mandatory parameters");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmAbortRequest: <> Request for [token:] [SURL:] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        VomsGridUser user = (VomsGridUser) inputData.getUser();
        if (user == null) {
            log.error("srmAbortRequest : the user field is NULL");
            globalStatus = manageStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmAbortRequest: <> Request for [token:] [SURL:] failed with [status: "+globalStatus+"]");
            return outputData;
        }

        /**
         * !!! LocalUser is unnecessary !!!

        // Maps the VOMS Grid user into Local User
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        }
        catch (CannotMapUserException e) {
            log.error("AbortRequest : Unable to map the user '" + user + "' in a local user.", e);
            globalStatus = manageStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, "Unable to map the user");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            return outputData;
        }

        **/

        /********************************** Start to manage the request ***********************************/

        /**
         * We can identify 3 different phases of execution:
         *
         * 1) Look for the request into the pending DB table, in such case the request is still in SRM_QUEUED status
         * and the AbortRequest can be satisfied simply removing the request from the pending table, updating the
         * request status to SRM_ABORTED and copying it into the
         * appropriate table.
         *
         * 2) If we are not in the first case, look for the request into the scheduler internal structures.
         * If the request is found and removed, the request status into the appropriate table should be updated to
         * SRM_ABORTED.
         *
         * 3) In this case the request to abort is under execution.
         * The behaviour is different depending on the request type.
         * For the SrmPrepareToPut and SrmPrepareToGet, we decide to wait until the ending of execution, and then
         * perform a rollback and mark the request as SRM_ABORTED.
         * In case of SrmCopy, we need to stop the Copy execution so the dedicated AbortExecutor invoke an
         * appropriate abort method.
         *
         */

        TRequestToken requestToken = inputData.getRequestToken();
        ArrayOfSURLs surlArray  = inputData.getArrayOfSURLs();
        log.debug("srmAbortRequest: requestToken=" + requestToken.toString());


        /******************************   PHASE (1) LOOKING INTO PENDING DB AND ADVANCED PICKER   ***************************/

        /******* Phase 1.A Look in the Summary Catalog ************/

        /*
         * Insert Security Here!
         * Add the GridUser field in the catalog abortRequest method to verify if the
         * request is associated to the same user that want to abort it.?
         */

        //CONTROLLO SE GRIDUSER ASSOCIATO AL TOKEN e' il RICHIEDENTE.
        //MA SI DEVE FARE COSI? E IL VOMANAGER?

        /**
         * Note:
         * If a global request if found to be be in SRM_QUEUED status in the SummaryCatalog it means that
         * both the global status and each chunk are still in SRM_QUEUED.
         * There is not the possibility of partial execution, to abort it is sufficient transit both global status
         * and each chunk in SRM_ABORTED.
         */

        if(inputData.getType() == AbortGeneralInputData.ABORT_REQUEST) {
            //SrmAbortRequest case
            log.debug("Phase (1.A) AbortRequest: SurlArray Not specified.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.

            //TODO REMOVE THIS COMMENT!
            summaryCat.abortRequest(inputData.getRequestToken());
            res = false;

        } else if(inputData.getType() == AbortGeneralInputData.ABORT_FILES) {
            //SrmAbortFiles case
            log.debug("Phase (1.A) AbortRequest: SurlArray Specified.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.

            //TODO REMOVE THIS COMMENT!
            //summaryCat.abortRequest(inputData.getRequestToken(), surlArray);
            res = false;
        } else {
            //failure?
        }

        if(res == false){
            log.debug("Phase (1.A) AbortRequest: Token not found.");
        } else {
            /**
             *  Request Successfully Aborted.
             *  Return here! Build outputdata and return!
             */
            arrayOfTSURLReturnStatus = new ArrayOfTSURLReturnStatus();
            globalStatus = manageStatus(TStatusCode.SRM_SUCCESS, "Abort sucessfully completed.");
            outputData.setReturnStatus(globalStatus);

            if(inputData.getType() == AbortGeneralInputData.ABORT_FILES){
                for(int i=0;i<surlArray.size();i++) {
                    TSURLReturnStatus surlRetStatus = new TSURLReturnStatus();
                    surlRetStatus.setSurl(surlArray.getTSURL(i));
                    surlRetStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request aborted."));
                    log.info("srmAbortFiles: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+(i+1)+" of "+surlArray.size()+" [SURL:"+surlArray.getTSURL(i)+"] successfully done with [status: "+surlRetStatus.getStatus()+"]");
                    arrayOfTSURLReturnStatus.addTSurlReturnStatus(surlRetStatus);
                }
                log.info("srmAbortFiles: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] successfully done with [status: "+globalStatus+"]");
            } else {
                outputData.setArrayOfFileStatuses(null);
                log.info("srmAbortRequest: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] successfully done with [status: "+globalStatus+"]");
            }
            return outputData;
        }

        /******** Phase 1.B Look in the AdvancedPicker ************/
        /**
         * Note:
         * There is the possibility that the global request status is changed in SRM_IN_PROGESS but each chunk is
         * not really yet executed, (each chunk status is still in SRM_QUEUED).
         * The only component able to manage this situation is the advanced picker.
         * There is not the possibility of partial execution, to abort it is sufficient ask to advancePicker
         * to transit both global status and each chunk in SRM_ABORTED.
         */

        if(inputData.getType() == AbortGeneralInputData.ABORT_REQUEST) {
            log.debug("Phase (1.B) AbortRequest: SurlArray Not specified.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.

            //TODO REMOVE THIS COMMENT!
            advancedPicker.abortRequest(inputData.getRequestToken());
            res = false;

        } else if(inputData.getType() == AbortGeneralInputData.ABORT_FILES) {
            log.debug("Phase (1.B) AbortRequest: SurlArray Specified.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.

            //TODO REMOVE THIS COMMENT!
            //advancedPicker.abortRequest(inputData.getRequestToken(), surlArray);
            res = false;
        }

        if(res == false){
            log.debug("Phase (1.B) AbortRequest: Token not found.");
        } else {

            /**
             *  Request Successfully Aborted.
             *  Return here! Build outputdata and return!
             */
            arrayOfTSURLReturnStatus = new ArrayOfTSURLReturnStatus();
            globalStatus = manageStatus(TStatusCode.SRM_SUCCESS, "Abort sucessfully completed.");
            outputData.setReturnStatus(globalStatus);
            if(inputData.getType() == AbortGeneralInputData.ABORT_FILES){
                for(int i=0;i<surlArray.size();i++) {
                    TSURLReturnStatus surlRetStatus = new TSURLReturnStatus();
                    surlRetStatus.setSurl(surlArray.getTSURL(i));
                    surlRetStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,"File request aborted."));
                    log.info("srmAbortFiles: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for SURL "+(i+1)+" of "+surlArray.size()+" [SURL:"+surlArray.getTSURL(i)+"] successfully done with [status: "+surlRetStatus.getStatus()+"]");
                    arrayOfTSURLReturnStatus.addTSurlReturnStatus(surlRetStatus);
                }
                log.info("srmAbortFiles: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] for [SURL:"+surlArray+"] successfully done with [status: "+globalStatus+"]");
            } else {
                    outputData.setArrayOfFileStatuses(null);
                    log.info("srmAbortRequest: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] successfully done with [status: "+globalStatus+"]");
            }
            return outputData;

        }



        /*****************************   PHASE (2) LOOKING INTO THE SCHEDULER   **************************/

        /**
         * Insert Secutiry Here!.
         */

        //TODO REMOVE THIS COMMENT!
/*
        if((surlArray == null)&&(scheduler.remove(requestToken)) {
            log.debug("Phase (2) AbortRequest: Token FOUND.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.
            summaryCat.abortRequest(inputData.getRequestToken());
        } else if((surlArray != null)&&(scheduler.remove(requestToken,surlArray))) {
            log.debug("Phase (2) AbortRequest: Token and SURL FOUND.");
            //Update the request Status both for global request and for each *chunk to SRM_ABORTED.
            summaryCat.abortRequest(inputData.getRequestToken(), surlArray);
        } else {
            log.debug("Phase (2) AbortRequest: Token not found.");
        }

*/
        /*************   PHASE (3) [WAIT END AND MANAGE ROLLBACK] OR [SEND ABORT TO COPY]   *************/

        //First of all, identify the request type.

        /**
         * Add Security Check Here
         * CHeck if user associated with global request is the requester of Abort.
         * @todo
         */

        TRequestType rtype = null;
        rtype = summaryCat.typeOf(requestToken);

        if (rtype==TRequestType.PREPARE_TO_GET) {
            executor = new PtGAbortExecutor();
            return executor.doIt(inputData);
        } else if (rtype==TRequestType.PREPARE_TO_PUT) {
            executor = new PtPAbortExecutor();
            return executor.doIt(inputData);
        } else if (rtype==TRequestType.COPY) {
            executor = new CopyAbortExecutor();
            return executor.doIt(inputData);
        } else {
            //This case is really possibile?
            log.debug("This case is really possibile?");
            log.debug("SrmAbortRequest : Invalid input parameter specified");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token. Abort only works for PtG, PtP and Copy.");
            log.error("srmAbortRequest: <"+user+"> Request for [token:"+inputData.getRequestToken()+"] failed with [status: "+globalStatus+"]");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            return outputData;
        }

/*
        if(requestFailure) {
            log.error("SrmAbortRequest : Invalid input parameter specified");
            globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST, "Request Token does not referes to existing known requests.");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            return outputData;
        }
*/
    }

    /**
    *
    * @param statusCode statusCode
    * @param explanation explanation string
    * @return returnStatus returnStatus
    */
   private TReturnStatus manageStatus(TStatusCode statusCode, String explanation) {
       TReturnStatus returnStatus = null;
       try {
           returnStatus = new TReturnStatus(statusCode, explanation);
       } catch (InvalidTReturnStatusAttributeException ex1) {
           log.debug("AbortExecutor : Error creating returnStatus " + ex1);
       }
       return returnStatus;
   }

}
