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
 * This is the Abort executor for a PtG request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtGAbortExecutor implements AbortExecutorInterface {

	static Configuration config = Configuration.getInstance();
	private static int maxLoopTimes = PtGAbortExecutor.config.getMaxLoop();
	// private static int maxLoopTimes = 100;

	private static final Logger log = LoggerFactory
		.getLogger(PtGAbortExecutor.class);

	public PtGAbortExecutor() {

	};

	public AbortGeneralOutputData doIt(AbortInputData inputData) {

		PtGAbortExecutor.log.debug("srmAbortRequest: Started PtGAbortExecutor");

		AbortGeneralOutputData outputData = new AbortGeneralOutputData();
		ArrayOfTSURLReturnStatus arrayOfTSurlRetStatus = new ArrayOfTSURLReturnStatus();

		TReturnStatus globalStatus = null;

		/**
		 * 0) Get all Chunk related to the specified request according with user
		 * specification (in case of AbortFiles). 1) Wait until a chunk goes in
		 * SRM_FILE_PINNED status.(or any other status different from SRM_QUEUED).
		 * 2) Rollback. For a PtG request the rollback only means to remove the acl
		 * eventually inserted into the Volatile-JiT catalog.
		 */

		Map<TSURL, TReturnStatus> surlStatusMap;
		
		SURLStatusManager checker = SURLStatusManagerFactory
		  .newSURLStatusManager();
		
		
		try {
			surlStatusMap = checker.getSURLStatuses(inputData
				.getRequestToken());
		} catch (IllegalArgumentException e) {
			log
				.error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: "
					+ e);
			throw new IllegalStateException("Unexpected IllegalArgumentException: "
				+ e.getMessage());
		} catch (UnknownTokenException e) {
			PtGAbortExecutor.log
				.debug("PtGAbortExecutor: Request - Invalid request token");
			globalStatus = createReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Invalid request token");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);
			PtGAbortExecutor.log.info("srmAbortRequest: <"
				+ DataHelper.getRequestor(inputData) + "> Request for [token:"
				+ inputData.getRequestToken() + "] successfully done with [status: "
				+ globalStatus + "]");
			return outputData;
		} catch (ExpiredTokenException e) {
			log.info("The request is expired: ExpiredTokenException: "
				+ e.getMessage());
			globalStatus = createReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
				"Request expired");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);
			log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
				+ "> Request for [token:" + inputData.getRequestToken()
				+ "] failed with [status: " + globalStatus + "]");
			return outputData;
		}
		
		
		if (surlStatusMap.isEmpty()) {
			PtGAbortExecutor.log
				.debug("PtGAbortExecutor: Request - Invalid request token");
			globalStatus = createReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Invalid request token");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(null);
			PtGAbortExecutor.log.info("srmAbortRequest: <"
				+ DataHelper.getRequestor(inputData) + "> Request for [token:"
				+ inputData.getRequestToken() + "] successfully done with [status: "
				+ globalStatus + "]");
			return outputData;
		}

		/**
		 * Get only the SURL requested in a AbortFile Request Define a new
		 * Collection to contains the "epurated" chunk, removing the ones not
		 * specified in input request
		 */

		if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
			PtGAbortExecutor.log
				.debug("PtGAbortExecutor: Case of AbortFile request. Purge Chunks with SurlArray.");

			/**
			 * Get the related Chunk for each SURL in the input SurlArray. If a Surl
			 * requested is not founf, the TSURLReturnStatus related is setted to
			 * SRM_INVALID_PATH
			 */

			ArrayList<TSURL> surlList = extractSurlArray(inputData).getArrayList();
			surlStatusMap.keySet().retainAll(surlList);
			if (!surlStatusMap.keySet().containsAll(surlList)) {
				for (TSURL surl : surlList) {
					if (!surlStatusMap.containsKey(surl)) {
						log
							.debug("PtGAbortExecutor: requested SURL NOT found, invalid file request");
						TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
						surlReturnStatus.setSurl(surl);
						surlReturnStatus.setStatus(createReturnStatus(
							TStatusCode.SRM_INVALID_PATH,
							"SURL specified does not referes to this request token."));
						log.info("srmAbortFiles: <" + DataHelper.getRequestor(inputData)
							+ "> Request for [token:" + inputData.getRequestToken()
							+ "] for [SURL:" + surl + "] failed  with [status: "
							+ surlReturnStatus.getStatus() + "]");
						arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
					}
				}
			}
		}

		/********* Check here the new chunks container is not empty! ******/
		if (surlStatusMap.isEmpty()) {
			log
				.debug("Abort Request - No surl specified associated to request token");
			globalStatus = createReturnStatus(TStatusCode.SRM_FAILURE,
				"All surl specified does not referes to the request token.");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
			log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
				+ "> Request for [token:" + inputData.getRequestToken()
				+ "] failed with [status: " + globalStatus + "]");
			return outputData;
		}

		/*********
		 * Phase 1 Wait until the request goes in a status different from
		 * REQUEST_QUEUED
		 ********/

		int chunkAborted = 0;

		// To avoid deadlock
		int counter = 0;
		// NumberOfFailure for globalStatus
		int errorCount = 0;

		int totalSize = surlStatusMap.size();

		while ((chunkAborted < totalSize)
			&& (counter < PtGAbortExecutor.maxLoopTimes)) {
			// Increment loop times
			counter++;

			int numOfSurl = 0;
			Iterator<Map.Entry<TSURL, TReturnStatus>> iterator = surlStatusMap
				.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<TSURL, TReturnStatus> surlStatus = iterator.next();
				numOfSurl++;
				/*
				 * Check if any chunk have a status different from
				 * SRM_REQUEST_INPROGESS. That means the request execution is finished,
				 * and the rollback start.
				 */

				if (!(surlStatus.getValue().getStatusCode()
					.equals(TStatusCode.SRM_REQUEST_INPROGRESS))) {
					/*
					 * If an EXECUTED CHUNK is found, then it is ABORTED.
					 */
					PtGAbortExecutor.log
						.debug("srmAbortRequest: PtGAbortExecutor: PtGChunk not in IN_PROGRESS state. Ready for ABORT.");
					TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();

					/******** Phase (3) of Abort *************/
					PtGAbortExecutor.log
						.debug("srmAbortRequest: PtGAbortExecutor: start Phase(3)");

					/*
					 * AvancePicker HACK! Due to a thread issue in the
					 * advancePicker.abortRequest(...) we have to check here if the chunk
					 * has already been aborted frome the picker. In such case, the
					 * manageAuthorizedAbort is not needed
					 */
					if ((surlStatus.getValue().getStatusCode()
						.equals(TStatusCode.SRM_ABORTED))) {
						// The AdvancedPicker have already aborted the chunk.
						PtGAbortExecutor.log
							.debug("PtGAbortExecutor: CHUNK already aborted!");
						surlReturnStatus.setSurl(surlStatus.getKey());
						surlReturnStatus.setStatus(createReturnStatus(TStatusCode.SRM_SUCCESS,
							"File request successfully aborted."));
					} else {
						// Chunk not ABORTED. We have to work...
						PtGAbortExecutor.log.debug("PtPAbortExecutor: CHUNK to abort!");
						try {
							surlReturnStatus = manageAuthorizedAbort(
								inputData.getRequestToken(), surlStatus.getKey(),
								surlStatus.getValue());
						} catch (ExpiredTokenException e) {
							log.info("The request is expired: ExpiredTokenException: "
								+ e.getMessage());
							globalStatus = createReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
								"Request expired");
							outputData.setReturnStatus(globalStatus);
							outputData.setArrayOfFileStatuses(null);
							log.info("srmAbortRequest: <"
								+ DataHelper.getRequestor(inputData) + "> Request for [token:"
								+ inputData.getRequestToken() + "] failed with [status: "
								+ globalStatus + "]");
							return outputData;
						}
					}

					// Remove this chunks from the other to abort.
					iterator.remove();

					if ((surlReturnStatus.getStatus().getStatusCode()
						.equals(TStatusCode.SRM_SUCCESS))) {
						PtGAbortExecutor.log.info("srmAbortFiles: <"
							+ DataHelper.getRequestor(inputData) + "> Request for [token:"
							+ inputData.getRequestToken() + "] for SURL " + numOfSurl
							+ " of " + totalSize + " [SURL:" + surlStatus.getKey()
							+ "] successfully done  with [status: "
							+ surlReturnStatus.getStatus() + "]");
					} else {
						PtGAbortExecutor.log.info("srmAbortFiles: <"
							+ DataHelper.getRequestor(inputData) + "> Request for [token:"
							+ inputData.getRequestToken() + "] for SURL " + numOfSurl
							+ " of " + totalSize + " [SURL:" + surlStatus.getKey()
							+ "] failed with [status: " + surlReturnStatus.getStatus() + "]");
						errorCount++;
					}
					// Add returnStatus
					arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);

					// Increment number of chunk aborted
					chunkAborted++;
				}
			}
			if (chunkAborted < totalSize) {
				// Sleep 1 Second
				log.debug("PtGAbortExecutor: I'm waiting...");
				try {
					long numMillisecondsToSleep = 300; // 0.1 seconds
					Thread.sleep(numMillisecondsToSleep);
				} catch (InterruptedException e) {

				}
				
				log.debug("srmAbortRequest: PtGAbortExecutor: refresh surl status");
				
				
				
				try {
				  List<TSURL> surls = new ArrayList<TSURL>(surlStatusMap.keySet());
					surlStatusMap = checker.getSURLStatuses(inputData.getRequestToken(),
					  surls);
					  
				} catch (IllegalArgumentException e) {
					log
						.error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: "
							+ e);
					throw new IllegalStateException(
						"Unexpected IllegalArgumentException: " + e.getMessage());
				} catch (UnknownTokenException e) {
					log
						.warn("PtGAbortExecutor: Request - Invalid request token, probably it is expired");
					globalStatus = createReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
						"Expired request token");
					outputData.setReturnStatus(globalStatus);
					outputData.setArrayOfFileStatuses(null);
					PtGAbortExecutor.log.info("srmAbortRequest: <"
						+ DataHelper.getRequestor(inputData) + "> Request for [token:"
						+ inputData.getRequestToken()
						+ "] successfully done with [status: " + globalStatus + "]");
					return outputData;
				} catch (ExpiredTokenException e) {
					log.info("The request is expired: ExpiredTokenException: "
						+ e.getMessage());
					globalStatus = createReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
						"Request expired");
					outputData.setReturnStatus(globalStatus);
					outputData.setArrayOfFileStatuses(null);
					log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
						+ "> Request for [token:" + inputData.getRequestToken()
						+ "] failed with [status: " + globalStatus + "]");
					return outputData;
				}
				log.debug("srmAbortRequest: PtGAbortExecutor: refresh done.");
			}

		}
		PtGAbortExecutor.log.debug("PtGAbortExecutor: Cycles done.");

		// LoopTimes Exceeded?
		if (chunkAborted < totalSize) {
			// The ABORT execution is interrupted to prevent a deadlock situation
			log.warn("Abort: Timeout exceeded.");
			globalStatus = createReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
				"TimeOut for abort execution exceeded.");
			outputData.setReturnStatus(globalStatus);
			outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
			log.warn("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
				+ "> Request for [token:" + inputData.getRequestToken()
				+ "] failed with [status: " + globalStatus + "]");
			return outputData;
		}

		if (errorCount == totalSize) {
			globalStatus = createReturnStatus(TStatusCode.SRM_FAILURE, "Abort failed.");
		} else {
			if (errorCount > 0) {
				if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {
					globalStatus = createReturnStatus(TStatusCode.SRM_FAILURE,
						"Some chunks failed.");
				} else {
					globalStatus = createReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
						"Some chunks failed.");
				}
			} else {
				globalStatus = createReturnStatus(TStatusCode.SRM_SUCCESS,
					"Abort request completed.");
				if ((inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES))) {
					TReturnStatus requestStatus = createReturnStatus(TStatusCode.SRM_ABORTED,
						"Request Aborted.");
					RequestSummaryCatalog.getInstance().updateGlobalStatus(
						inputData.getRequestToken(), requestStatus);
				}
			}
		}
		// Set output data
		outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
		outputData.setReturnStatus(globalStatus);
		PtGAbortExecutor.log.info("srmAbortRequest: <"
			+ DataHelper.getRequestor(inputData) + "> Request for [token:"
			+ inputData.getRequestToken() + "] failed with [status: " + globalStatus
			+ "]");
		return outputData;

	}

	private ArrayOfSURLs extractSurlArray(AbortInputData inputData) {

		switch (inputData.getType()) {
		case ABORT_REQUEST:
			throw new IllegalStateException(
				"Unable to get SurlArray from an ABORT_REQUEST input data");
		case ABORT_FILES:
			return ((AbortFilesInputData) inputData).getArrayOfSURLs();
		default:
			throw new IllegalStateException("Received an unknown AbortType: "
				+ inputData.getType());
		}
	}

	/**
	 * 
	 * @param statusCode
	 *          statusCode
	 * @param explanation
	 *          explanation string
	 * @return returnStatus returnStatus
	 */

	private TReturnStatus createReturnStatus(TStatusCode statusCode, 
	  String explanation) {

		TReturnStatus returnStatus = null;
		try {
			returnStatus = new TReturnStatus(statusCode, explanation);
		} catch (InvalidTReturnStatusAttributeException ex1) {
			PtGAbortExecutor.log.debug("AbortExecutor : Error creating returnStatus "
				+ ex1);
		}
		return returnStatus;
	}

	/**
	 * 
	 * Manage the roll back needed to execute an abort request.
	 * 
	 * @param chunkData
	 *          PtGChunkData
	 * @return returnStatus TSURLReturnStatus
	 * @throws ExpiredTokenException
	 */


	private TSURLReturnStatus manageAuthorizedAbort(TRequestToken token,
		TSURL surl, TReturnStatus status) throws ExpiredTokenException {

		TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
		surlReturnStatus.setSurl(surl);

		if (TStatusCode.SRM_FILE_PINNED.equals(status.getStatusCode())
			|| TStatusCode.SRM_REQUEST_QUEUED.equals(status.getStatusCode())) { 
		  
			try {
			  
				SurlStatusManager.updateStatus(token, surl, TStatusCode.SRM_ABORTED,
					"Request aborted.");
			} catch (IllegalArgumentException e) {
				log
					.error("Unexpected IllegalArgumentException during surl statuses update: "
						+ e);
				throw new IllegalStateException("Unexpected IllegalArgumentException: "
					+ e.getMessage());
			} catch (UnknownTokenException e) {
				log
					.error("Unexpected UnknownTokenException during surl statuses update: "
						+ e);
				throw new IllegalStateException("Unexpected UnknownTokenException: "
					+ e.getMessage());
			} catch (UnknownSurlException e) {
				log
					.error("Unexpected UnknownSurlException during surl statuses update: "
						+ e);
				throw new IllegalStateException("Unexpected UnknownSurlException: "
					+ e.getMessage());
			}

			
			surlReturnStatus.setStatus(createReturnStatus(TStatusCode.SRM_SUCCESS,
				"File request successfully aborted."));
		} else {
			
			if (TStatusCode.SRM_FILE_LIFETIME_EXPIRED.equals(status.getStatusCode())) {
				surlReturnStatus.setStatus(createReturnStatus(TStatusCode.SRM_FAILURE,
					"Request is in a final status. Abort not allowed."));
			} else {
				if (TStatusCode.SRM_RELEASED.equals(status.getStatusCode())) {
					surlReturnStatus.setStatus(createReturnStatus(TStatusCode.SRM_FAILURE,
						"Request is in a final status. Abort not allowed."));
				} else {
					surlReturnStatus.setStatus(createReturnStatus(TStatusCode.SRM_FAILURE,
						"Abort request not executed."));
				}
			}
		}
		return surlReturnStatus;
	}

}
