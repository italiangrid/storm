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

package it.grid.storm.asynch;

import java.util.Calendar;
import java.util.UUID;
import it.grid.storm.catalogs.CopyData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Public super class from which both the PushCopyChunk and the PullCopyChunk
 * are derived.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date september, 2005
 */
public abstract class Copy implements Delegable, Chooser, Request {

	// private GetOperationResult getResult = null; //Results of the GET operation
	// private PutOperationResult putResult = null; //Results of the PUT operation
	// private TransferResult transferResult = null; //Results of the transfer
	// operation

	private final String COPY_PREFIX = "COPY-";
	/**
	 * TRequestToken used to identify the local GET/PUT
	 */
	protected final TRequestToken localrt;

	/**
	 * Integer representing a progressive counter of the chunks being handled.
	 */
	protected final int n;

	/**
	 * CopyData that holds the specific info for this chunk
	 */
	protected final CopyData requestData;

	/**
	 * GridUser that made the request
	 */
	protected final GridUserInterface gu;

	/**
	 * boolean that indicates if this chunks state is failure
	 */
	protected boolean failure = false;

	private static Logger log = LoggerFactory.getLogger(Copy.class);

	/**
	 * Constructor requiring the GridUser, the RequestSummaryData, the
	 * CopyChunkData about this chunk, and the integer representing the progessive
	 * number of the chunk being handled, and the GlobalStatusManager. If the
	 * supplied attributes are null or the counter is negative, an
	 * InvalidCopyChunkAttributesException is thrown.
	 */
	public Copy(GridUserInterface gu, CopyData requestData, int n)
		throws InvalidCopyAttributesException {

		if (gu == null || n < 0 || requestData == null) {
			throw new InvalidCopyAttributesException(gu, requestData, n);
		}
		this.gu = gu;
		this.requestData = requestData;
		this.n = n;
		try {
			this.localrt = new TRequestToken(COPY_PREFIX.concat(UUID.randomUUID()
				.toString().substring(COPY_PREFIX.length())), Calendar.getInstance()
				.getTime());
		} catch (InvalidTRequestTokenAttributesException e) {
			// never thrown
			log.error("Unexpected InvalidTRequestTokenAttributesException: {}",
				e.getMessage(), e);
			throw new IllegalStateException(
				"Unexpected InvalidTRequestTokenAttributesException");
		}
	}

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
	 * the SRM status is not SRM_DONE: then the copyChunk fails with SRM_ABORT and
	 * appropriate explanation string which reports the local SRM STATUS.
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

		log.debug("Handling Copy chunk for user DN: {}; fromSURL: {}; toSURL: {}",
			gu.getDn(), requestData.getSURL(), requestData.getDestinationSURL());
		
		log.debug("RequestToken used for local operation: {}", localrt);
		GetOperationResult getResult = executeGetOperation();
		log.debug("Result from get: {}", getResult);
		
		if (getResult.successful()
			&& TStatusCode.SRM_FILE_PINNED.equals(getResult.status().getStatusCode())) {
			
			PutOperationResult putResult = executePutOperation(getResult.filesize());
			log.debug("Result from put: {}", putResult);
			/*
			 * ATTENTION! the following check for SRM_SUCCESS is done to keep
			 * compatibility between StoRM servers which use a hack thereby switching
			 * to state SRM_SUCCESS instead of transiting to SRM_SPACE_AVAILABLE!!!
			 * But for any other SRM2.2 compliant server, SRM_SPACE_AVAILABLE
			 * suffices!!!
			 */
			if (putResult.successful()
				&& ((putResult.status().getStatusCode()
					.equals(TStatusCode.SRM_SPACE_AVAILABLE)) || (putResult.status()
					.getStatusCode().equals(TStatusCode.SRM_SUCCESS)))) {
				
				TransferResult transferResult = executeTransfer(getResult, putResult);
				log.debug("Result from transfer: {}", transferResult);
				if (transferResult.successful()) {
					requestData.changeStatusSRM_SUCCESS("srmCopy successfully handled!");
					log.debug("SRM Copy successful!");
					this.failure = false; // gsm.successfulChunk(chunkData);
				} else {
					String message = String.format("GSIFTP transfer failed! %s",
						transferResult.failureExplanation());
					log.error(message);
					requestData.changeStatusSRM_FAILURE(message);
					this.failure = true; // gsm.failedChunk(chunkData);
				}
			} else {
				// The put operation was problematic!
				String message = "PUT part of srmCopy failed! ";
				log.error(message);
				requestData.changeStatusSRM_FAILURE(message);
				this.failure = true;
			}
		} else {
			// the get operation was problematic!
			String message = "GET part of srmCopy failed! ";
			log.error(message);
			requestData.changeStatusSRM_FAILURE(message);
			this.failure = true;
		}
		log.info("Finished handling Copy chunk for user DN: {}; fromSURL: {}; "
			+ "toSURL: {} result is: {}", gu.getDn(), requestData.getSURL(), 
			requestData.getDestinationSURL(), requestData.getStatus());
	}

	/**
	 * Method that supplies a String describing this PushCopyChunk - for scheduler
	 * Log purposes! It returns the request token of This request.
	 */
	public String getName() {

		return String.format("Copy for SURL %s to SURL %s", requestData.getSURL(),
			requestData.getDestinationSURL());
	}

	@Override
	public String getUserDN() {

		return gu.getDn();
	}

	@Override
	public String getSURL() {

		return requestData.getSURL().toString();
	}

	@Override
	public boolean isResultSuccess() {

		return requestData.getStatus().isSRM_SUCCESS();
	}

	abstract protected GetOperationResult executeGetOperation();

	abstract protected PutOperationResult executePutOperation(
		TSizeInBytes getFileSize);

	abstract protected TransferResult executeTransfer(GetOperationResult get,
		PutOperationResult put);

	public interface Result {

		/**
		 * Method that returns a boolean indicating the result of the transfer.
		 */
		public boolean successful();

		/**
		 * Method that returns a String explaining the failure of the transfer: an
		 * empty String is returned in cawe of successful transfer.
		 */
		public String failureExplanation();
	}

	public enum ResultType {
		PUT, GET, TRANSFER
	}

	/**
	 * Protected auxiliary class holding the results from a Transfer. It contains
	 * a boolean indicating whether the operation completed normally or could not
	 * proceed; in this case it also contains an explanation string of the
	 * failure.
	 * 
	 * @author EGRID - ICTP Trieste
	 * @verson 1.0
	 * @date September, 2005
	 */
	protected class TransferResult implements Result {

		private boolean successful = false;
		private String failureExplanation = "";

		/**
		 * Constructor used to indicate a failed transfer: it requires a String
		 * explaining the failure.
		 */
		public TransferResult(String failureExplanation) {

			if (failureExplanation == null) {
				failureExplanation = "";
			}
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
	 * Protected auxiliary class holding the results from a GetOperation. It
	 * contains a boolean indicating whether the GetOperation was successful or
	 * could not complete normally: beware that it does _not_ consider the SRM
	 * status. It only indicates whether the internal steps were all completed
	 * normally.
	 * 
	 * It also contains the TReturnStatus, the TTURL and the TFileSize, as well as
	 * a failureExplanation String.
	 */
	protected class GetOperationResult implements Result {

		/**
		 * boolean indicating if the operation was successful
		 */
		private boolean successful = false;

		/**
		 * TReturnStatus from srmPrepareToGet
		 */
		private TReturnStatus status;

		/**
		 * TURL from srmPrepareToGet
		 */
		private TTURL getTURL = TTURL.makeEmpty();

		/**
		 * filesize from srmPrepareToGet
		 */
		private TSizeInBytes filesize = TSizeInBytes.makeEmpty();

		/**
		 * String containing an explanation of failure
		 */
		private String failureExplanation = "";

		/**
		 * request token assigned to PtG request
		 */
		private TRequestToken rt = null;

		/**
		 * Constructor that returns a successful GetOperationResult containing the
		 * TReturnStatus, TTURL, filesize and TRequestToken. If any of the supplied
		 * parameters is null, a failed GetOperationResult is returned containing
		 * the empty String as error explanation.
		 */
		public GetOperationResult(TReturnStatus status, TTURL getTURL,
			TSizeInBytes filesize, TRequestToken rt) {

			if ((status != null) && (getTURL != null) && (filesize != null)
				&& (rt != null)) {
				this.successful = true;
				this.status = status;
				this.getTURL = getTURL;
				this.filesize = filesize;
				this.rt = rt;
			} else {
				this.successful = false;
				this.status = new TReturnStatus(TStatusCode.SRM_CUSTOM_STATUS);
			}
		}

		/**
		 * Constructor that returns a failed GetOperationResult containing a
		 * failureExplanation String; if it is null, an empty String is used
		 * instead.
		 */
		public GetOperationResult(String failureExplanation) {

			if (failureExplanation == null) {
				failureExplanation = "";
			}
			this.successful = false;
			this.failureExplanation = failureExplanation;
			this.status = new TReturnStatus(TStatusCode.SRM_CUSTOM_STATUS);
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

		@Override
		public String toString() {

			return String.format("GetOperationResult: successful=%b; status=%s; "
				+ "getTURL=%s; filesize=%s; requestToken=%s", successful, status, 
				getTURL, filesize, rt);
		}
	}

	/**
	 * Private auxiliary class holding the results from a PutOperation. It
	 * contains a boolean indicating whether the PutOperation was successful or
	 * could not complete normally: beware that it does _not_ consider the SRM
	 * status. It only indicates whether the internal steps were all completed
	 * normally.
	 * 
	 * It also contains the TReturnStatus and the TURL, as well as a
	 * failureExplanation String and the TRequestToken associated with that
	 * request.
	 */
	protected class PutOperationResult implements Result {

		private boolean successful = false;
		private TReturnStatus status;
		private TTURL putTURL = TTURL.makeEmpty();
		private String failureExplanation = "";
		private TRequestToken rt = null;

		/**
		 * Constructor to make a successful PutOperationResult containing the
		 * TReturnStatus and the TURL. If any of the supplied parameters is null, a
		 * failed PutOperationResult is returned containing the empty String as
		 * error explanation.
		 */
		public PutOperationResult(TReturnStatus status, TTURL putTURL,
			TRequestToken rt) {

			if ((status != null) && (putTURL != null) && (rt != null)) {
				this.successful = true;
				this.status = status;
				this.putTURL = putTURL;
				this.rt = rt;
			} else {
				this.successful = false;
				this.status = new TReturnStatus(TStatusCode.SRM_CUSTOM_STATUS);
			}
		}

		/**
		 * Constructor to make a failed PutOperationResult containing a
		 * failureExplanation String; if it is null, an empty String is used
		 * instead.
		 */
		public PutOperationResult(String failureExplanation) {

			if (failureExplanation == null) {
				failureExplanation = "";
			}
			this.successful = false;
			this.failureExplanation = failureExplanation;
			this.status = new TReturnStatus(TStatusCode.SRM_CUSTOM_STATUS);
		}

		/**
		 * Method that returns true if the PutOperation completed all its internal
		 * steps.
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

		@Override
		public String toString() {

			return String.format("PutOperationResult: successful=%b; status=%s; "
				+ "putTURL=%s; failureExplanation=%s", successful, status, putTURL, 
				failureExplanation);
		}
	}
}
