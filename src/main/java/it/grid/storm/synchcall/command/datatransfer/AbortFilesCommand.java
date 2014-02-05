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

/**
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortFilesCommand extends DataTransferCommand implements Command {

  private static final String SRM_COMMAND = "srmAbortFiles";

	private static final Logger log = LoggerFactory
		.getLogger(AbortFilesCommand.class);

	private AdvancedPicker advancedPicker;
	private AbortExecutorInterface executor;

	public OutputData execute(InputData data) {

		advancedPicker = new AdvancedPicker();
		AbortFilesOutputData outputData = new AbortFilesOutputData();
		AbortFilesInputData inputData = (AbortFilesInputData) data;

		boolean res = false;
		TReturnStatus globalStatus = null;
		ArrayOfTSURLReturnStatus arrayOfTSURLReturnStatus = null;

		AbortFilesCommand.log.debug("Started AbortRequest function.");

		if (inputData == null
			|| inputData.getRequestToken() == null
			|| (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES) && inputData
				.getArrayOfSURLs() == null)) {

			log.debug("srmAbortFiles: Invalid input parameter specified");

			globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Missing mandatory parameters");

			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);

			log.error("srmAbortFiles: <> Request for [token:] [SURL:] failed with "
				  + "[status: {}]", globalStatus);

			return outputData;
		}

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
		ArrayOfSURLs surlArray = inputData.getArrayOfSURLs();
		AbortFilesCommand.log.debug("srmAbortFiles: requestToken={}", 
		  requestToken);

		/****************************** PHASE (1) LOOKING INTO PENDING DB AND ADVANCED PICKER ***************************/

		/**
		 * Note: If a global request if found to be be in SRM_QUEUED status in the
		 * SummaryCatalog it means that both the global status and each chunk are
		 * still in SRM_QUEUED. There is not the possibility of partial execution,
		 * to abort it is sufficient transit both global status and each chunk in
		 * SRM_ABORTED.
		 */
		if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {

		  log.debug("AbortRequest: SurlArray Not specified.");

			try {
			  
				SurlStatusManager.checkAndUpdateStatus(
				  requestToken,
					TStatusCode.SRM_REQUEST_QUEUED, 
					TStatusCode.SRM_ABORTED,
					"User aborted request!");

			} catch (UnknownTokenException e) {

				log.info("Unable to update surls status on token {}: {}", 
				  requestToken, e.getMessage(), e);

				globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
					"Invalid request token");

				outputData.setArrayOfFileStatuses(null);

				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken());

				return outputData;

			} catch (ExpiredTokenException e) {

			  log.info("Expired token: {}. {}",
			    requestToken, e.getMessage(), e);
			  
				globalStatus = manageStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
					"Request expired");
				outputData.setArrayOfFileStatuses(null);
				
				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken());

				return outputData;
			}
			RequestSummaryCatalog.getInstance().updateFromPreviousGlobalStatus(
				requestToken, TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED,
				"User aborted request!");
			res = false;

		} else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)){

		  log.debug("Phase (1.A) AbortRequest: SurlArray Specified.");
			res = false;
		}
		
		if (res == false) {
			AbortFilesCommand.log.debug("Phase (1.A) AbortRequest: Token not found.");
		} else {

			arrayOfTSURLReturnStatus = new ArrayOfTSURLReturnStatus();
			globalStatus = manageStatus(TStatusCode.SRM_SUCCESS,
				"Abort sucessfully completed.");
			outputData.setReturnStatus(globalStatus);

			if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
				for (int i = 0; i < surlArray.size(); i++) {
					TSURLReturnStatus surlRetStatus = new TSURLReturnStatus();
					surlRetStatus.setSurl(surlArray.getTSURL(i));
					surlRetStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,
						"File request aborted."));
					
					CommandHelper.printSurlOutcome(
					  SRM_COMMAND, 
					  log, 
					  globalStatus,
					  inputData, 
					  inputData.getRequestToken(),
					  surlArray.getTSURL(i));
					
					arrayOfTSURLReturnStatus.addTSurlReturnStatus(surlRetStatus);
				}

				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken(), 
				  surlArray);

			} else {

				outputData.setArrayOfFileStatuses(null);

				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken());
			}
			return outputData;
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
			log.debug("Phase (1.B) AbortRequest: SurlArray Not specified.");
			advancedPicker.abortRequest(inputData.getRequestToken());
			res = false;

		} else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
			log.debug("Phase (1.B) AbortRequest: SurlArray Specified.");
			res = false;
		}

		if (res == false) {
			AbortFilesCommand.log.debug("Phase (1.B) AbortRequest: Token not found.");
		} else {

			arrayOfTSURLReturnStatus = new ArrayOfTSURLReturnStatus();
			globalStatus = manageStatus(TStatusCode.SRM_SUCCESS,
				"Abort sucessfully completed.");
			outputData.setReturnStatus(globalStatus);
			if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
				for (int i = 0; i < surlArray.size(); i++) {
					TSURLReturnStatus surlRetStatus = new TSURLReturnStatus();
					surlRetStatus.setSurl(surlArray.getTSURL(i));
					surlRetStatus.setStatus(manageStatus(TStatusCode.SRM_SUCCESS,
						"File request aborted."));

					CommandHelper.printSurlOutcome(
					  SRM_COMMAND, 
					  log, 
					  globalStatus,
					  inputData, 
					  inputData.getRequestToken(),
					  surlArray.getTSURL(i));

					arrayOfTSURLReturnStatus.addTSurlReturnStatus(surlRetStatus);
				}

				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken(), 
				  surlArray);

			} else {
				outputData.setArrayOfFileStatuses(null);

				CommandHelper.printRequestOutcome(
				  SRM_COMMAND, 
				  log, 
				  globalStatus,
				  inputData, 
				  inputData.getRequestToken());

			}
			return outputData;
		}

		TRequestType rtype = SurlStatusManager.isPersisted(requestToken);

		if (rtype == TRequestType.PREPARE_TO_GET) {
			executor = new PtGAbortExecutor();
			return executor.doIt(inputData);
		} else {
			if (rtype == TRequestType.PREPARE_TO_PUT) {
				executor = new PtPAbortExecutor();
				return executor.doIt(inputData);
			} else {
				if (rtype == TRequestType.COPY) {
					executor = new CopyAbortExecutor();
					return executor.doIt(inputData);
				} else {
				  log.debug("srmAbortFiles : Invalid input parameter specified");

					globalStatus = manageStatus(TStatusCode.SRM_INVALID_REQUEST,
						"Invalid request token. Abort only works for PtG, PtP and Copy.");
					
					CommandHelper.printRequestOutcome(
					  SRM_COMMAND, 
					  log, 
					  globalStatus,
					  inputData, 
					  inputData.getRequestToken());

					outputData.setReturnStatus(globalStatus);
					outputData.setArrayOfFileStatuses(null);
					return outputData;
				}
			}
		}
	}

	private TReturnStatus manageStatus(TStatusCode statusCode, String explanation) {

		TReturnStatus returnStatus = null;
		try {
			returnStatus = new TReturnStatus(statusCode, explanation);
		} catch (InvalidTReturnStatusAttributeException ex1) {
			log.debug("AbortExecutor : Error creating returnStatus: {}", 
			  ex1.getMessage(), ex1);
		}
		return returnStatus;
	}

}
