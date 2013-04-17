/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestOutputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * 
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 10, 2008
 * 
 */

public class AbortRequestCommand extends DataTransferCommand implements Command {

	private static final Logger log = LoggerFactory
		.getLogger(AbortRequestCommand.class);
	private AdvancedPicker advancedPicker = null;
	private AbortExecutorInterface executor = null;

	public AbortRequestCommand() {

	};

	/**
	 * This executor performs a SrmAbortRequests. This function prematurely
	 * terminate asynchronous requests of any types. The effects of
	 * SrmAbortRequests() depends on the type of request.
	 */

	public OutputData execute(InputData data) {

		advancedPicker = new AdvancedPicker();
		AbortRequestOutputData outputData = new AbortRequestOutputData();
		AbortInputData inputData = (AbortInputData) data;

		// Risultato Parziale
		boolean res = false;
		// Risultato Finale

		TReturnStatus globalStatus = null;

		AbortRequestCommand.log.debug("Started AbortRequest function.");

		/******************** Check for malformed request: missing mandatory input parameters ****************/

		if (inputData == null || inputData.getRequestToken() == null
			|| (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES))) {
			AbortRequestCommand.log
				.debug("SrmAbortRequest: Invalid input parameter specified");
			globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Missing mandatory parameters");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);
			AbortRequestCommand.log
				.error("srmAbortRequest: <> Request for [token:] [SURL:] failed with [status: "
					+ globalStatus + "]");
			return outputData;
		}

		/**
		 * !!! LocalUser is unnecessary !!!
		 * 
		 * // Maps the VOMS Grid user into Local User LocalUser lUser = null; try {
		 * lUser = user.getLocalUser(); } catch (CannotMapUserException e) {
		 * log.error("AbortRequest : Unable to map the user '" + user +
		 * "' in a local user.", e); globalStatus =
		 * manageStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
		 * "Unable to map the user"); outputData.setReturnStatus(globalStatus);
		 * outputData.setArrayOfFileStatuses(null); return outputData; }
		 **/

		/********************************** Start to manage the request ***********************************/

		/**
		 * We can identify 3 different phases of execution:
		 * 
		 * 1) Look for the request into the pending DB table, in such case the
		 * request is still in SRM_QUEUED status and the AbortRequest can be
		 * satisfied simply removing the request from the pending table, updating
		 * the request status to SRM_ABORTED and copying it into the appropriate
		 * table.
		 * 
		 * 2) If we are not in the first case, look for the request into the
		 * scheduler internal structures. If the request is found and removed, the
		 * request status into the appropriate table should be updated to
		 * SRM_ABORTED.
		 * 
		 * 3) In this case the request to abort is under execution. The behaviour is
		 * different depending on the request type. For the SrmPrepareToPut and
		 * SrmPrepareToGet, we decide to wait until the ending of execution, and
		 * then perform a rollback and mark the request as SRM_ABORTED. In case of
		 * SrmCopy, we need to stop the Copy execution so the dedicated
		 * AbortExecutor invoke an appropriate abort method.
		 * 
		 */

		TRequestToken requestToken = inputData.getRequestToken();
		AbortRequestCommand.log.debug("srmAbortRequest: requestToken="
			+ requestToken.toString());

		/****************************** PHASE (1) LOOKING INTO PENDING DB AND ADVANCED PICKER ***************************/

		/******* Phase 1.A Look in the Summary Catalog ************/

		/*
		 * Insert Security Here! Add the GridUser field in the catalog abortRequest
		 * method to verify if the request is associated to the same user that want
		 * to abort it.?
		 */

		// CONTROLLO SE GRIDUSER ASSOCIATO AL TOKEN e' il RICHIEDENTE.
		// MA SI DEVE FARE COSI? E IL VOMANAGER?

		/**
		 * Note: If a global request if found to be be in SRM_QUEUED status in the
		 * SummaryCatalog it means that both the global status and each chunk are
		 * still in SRM_QUEUED. There is not the possibility of partial execution,
		 * to abort it is sufficient transit both global status and each chunk in
		 * SRM_ABORTED.
		 */

		if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {
			// SrmAbortRequest case
			AbortRequestCommand.log
				.debug("Phase (1.A) AbortRequest: SurlArray Not specified.");
			// Update the request Status both for global request and for each *chunk
			// to SRM_ABORTED.

			// TODO REMOVE THIS COMMENT!
			// summaryCat.abortRequest(inputData.getRequestToken());
			try {
				SurlStatusManager.checkAndUpdateStatus(inputData.getRequestToken(),
					TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED,
					"User aborted request!");
			} catch (UnknownTokenException e) {
				log.info("Unable to update surls status on token " + requestToken
					+ " .UnknownTokenException: " + e.getMessage());
				globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
					"Invalid request token");
				outputData.setArrayOfFileStatuses(null);
				log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + inputData.getRequestToken()
					+ "] failed with [status: " + globalStatus + "]");
				return outputData;
			} catch (ExpiredTokenException e) {
				log.info("Unable to update surls status on token " + requestToken
					+ " .ExpiredTokenException: " + e.getMessage());
				globalStatus = manageStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
					"Request expired");
				outputData.setArrayOfFileStatuses(null);
				log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
					+ "> Request for [token:" + inputData.getRequestToken()
					+ "] failed with [status: " + globalStatus + "]");
				return outputData;
			}

			RequestSummaryCatalog.getInstance().updateFromPreviousGlobalStatus(
				inputData.getRequestToken(), TStatusCode.SRM_REQUEST_QUEUED,
				TStatusCode.SRM_ABORTED, "User aborted request!");

			res = false;

		} else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
			// SrmAbortFiles case
			AbortRequestCommand.log
				.debug("Phase (1.A) AbortRequest: SurlArray Specified.");
			// Update the request Status both for global request and for each *chunk
			// to SRM_ABORTED.

			// TODO REMOVE THIS COMMENT!
			// summaryCat.abortRequest(inputData.getRequestToken(), surlArray);
			res = false;
		} else {
			// failure?
		}

		if (res == false) {
			AbortRequestCommand.log
				.debug("Phase (1.A) AbortRequest: Token not found.");
		}

		/******** Phase 1.B Look in the AdvancedPicker ************/
		/**
		 * Note: There is the possibility that the global request status is changed
		 * in SRM_IN_PROGESS but each chunk is not really yet executed, (each chunk
		 * status is still in SRM_QUEUED). The only component able to manage this
		 * situation is the advanced picker. There is not the possibility of partial
		 * execution, to abort it is sufficient ask to advancePicker to transit both
		 * global status and each chunk in SRM_ABORTED.
		 */

		if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {
			AbortRequestCommand.log
				.debug("Phase (1.B) AbortRequest: SurlArray Not specified.");
			// Update the request Status both for global request and for each *chunk
			// to SRM_ABORTED.

			// TODO REMOVE THIS COMMENT!
			advancedPicker.abortRequest(inputData.getRequestToken());
			res = false;

		} else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
			AbortRequestCommand.log
				.debug("Phase (1.B) AbortRequest: SurlArray Specified.");
			// Update the request Status both for global request and for each *chunk
			// to SRM_ABORTED.

			// TODO REMOVE THIS COMMENT!
			// advancedPicker.abortRequest(inputData.getRequestToken(), surlArray);
			res = false;
		}

		if (res == false) {
			AbortRequestCommand.log
				.debug("Phase (1.B) AbortRequest: Token not found.");
		}

		/***************************** PHASE (2) LOOKING INTO THE SCHEDULER **************************/

		/**
		 * Insert Secutiry Here!.
		 */

		// TODO REMOVE THIS COMMENT!
		/*
		 * if((surlArray == null)&&(scheduler.remove(requestToken)) {
		 * log.debug("Phase (2) AbortRequest: Token FOUND."); //Update the request
		 * Status both for global request and for each *chunk to SRM_ABORTED.
		 * summaryCat.abortRequest(inputData.getRequestToken()); } else
		 * if((surlArray != null)&&(scheduler.remove(requestToken,surlArray))) {
		 * log.debug("Phase (2) AbortRequest: Token and SURL FOUND."); //Update the
		 * request Status both for global request and for each *chunk to
		 * SRM_ABORTED. summaryCat.abortRequest(inputData.getRequestToken(),
		 * surlArray); } else {
		 * log.debug("Phase (2) AbortRequest: Token not found."); }
		 */
		/************* PHASE (3) [WAIT END AND MANAGE ROLLBACK] OR [SEND ABORT TO COPY] *************/

		// First of all, identify the request type.

		/**
		 * Add Security Check Here CHeck if user associated with global request is
		 * the requester of Abort.
		 * 
		 * @todo
		 */

		// TRequestType rtype = summaryCat.typeOf(requestToken);
		TRequestType rtype = SurlStatusManager.isPersisted(requestToken);

		if (rtype == TRequestType.PREPARE_TO_GET) {
			executor = new PtGAbortExecutor();
			return executor.doIt(inputData);
		} else if (rtype == TRequestType.PREPARE_TO_PUT) {
			executor = new PtPAbortExecutor();
			return executor.doIt(inputData);
		} else if (rtype == TRequestType.COPY) {
			executor = new CopyAbortExecutor();
			return executor.doIt(inputData);
		} else {
			// This case is really possibile?
			AbortRequestCommand.log.debug("This case is really possibile?");
			AbortRequestCommand.log
				.debug("SrmAbortRequest : Invalid input parameter specified");
			globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Invalid request token. Abort only works for PtG, PtP and Copy.");
			AbortRequestCommand.log.error("srmAbortRequest: <"
				+ DataHelper.getRequestor(inputData) + "> Request for [token:"
				+ inputData.getRequestToken() + "] failed with [status: "
				+ globalStatus + "]");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);
			return outputData;
		}

		/*
		 * if(requestFailure) {
		 * log.error("SrmAbortRequest : Invalid input parameter specified");
		 * globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
		 * "Request Token does not referes to existing known requests.");
		 * outputData.setReturnStatus(globalStatus);
		 * outputData.setArrayOfFileStatuses(null); return outputData; }
		 */
	}

	/**
	 * 
	 * @param statusCode
	 *          statusCode
	 * @param explanation
	 *          explanation string
	 * @return returnStatus returnStatus
	 */
	private TReturnStatus manageStatus(TStatusCode statusCode, String explanation) {

		TReturnStatus returnStatus = null;
		try {
			returnStatus = new TReturnStatus(statusCode, explanation);
		} catch (InvalidTReturnStatusAttributeException ex1) {
			AbortRequestCommand.log
				.debug("AbortExecutor : Error creating returnStatus " + ex1);
		}
		return returnStatus;
	}

}
