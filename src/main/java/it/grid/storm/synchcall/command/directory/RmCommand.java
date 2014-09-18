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

package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.space.SpaceUpdaterHelperFactory;
import it.grid.storm.space.SpaceUpdaterHelperInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmInputData;
import it.grid.storm.synchcall.data.directory.RmOutputData;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class RmCommand implements Command {

	class RmException extends Exception {

		private static final long serialVersionUID = 1L;
		
		private TReturnStatus returnStatus;
		
		public RmException(TStatusCode code, String message) {
			super(message);
			this.returnStatus = CommandHelper.buildStatus(code, message);
		}
		
		public TReturnStatus getReturnStatus() {
			return returnStatus;
		}
	}
	
	private static final String SRM_COMMAND = "srmRm";
	private static final Logger log = LoggerFactory.getLogger(RmCommand.class);
	private final String funcName = "srmRm";
	private final NamespaceInterface namespace;

	public RmCommand() {

		namespace = NamespaceDirector.getNamespace();
		
	}

	private void checkIfAcceptable(InputData data) throws IllegalArgumentException {
		
		if (data == null) {
			throw new IllegalArgumentException("Invalid input data: NULL");
		}
		if (!(data instanceof RmInputData)) {
			throw new IllegalArgumentException("Invalid input data type");
		}
	}
	
	private void checkInputData(RmInputData inputData) throws RmException {
		
		if (inputData.getSurlArray() == null) {
			throw new RmException(TStatusCode.SRM_FAILURE,
				"Invalid SURL array: NULL");
		}
		if (inputData.getSurlArray().size() == 0) {
			throw new RmException(TStatusCode.SRM_FAILURE,
				"Invalid SURL array specified");
		}
	}

	private RmOutputData exitWithStatus(TReturnStatus returnStatus,
		RmInputData data) {

		printRequestOutcome(returnStatus, data);
		return new RmOutputData(returnStatus);
	}
	
	
	/**
	 * Method that provide SrmRm functionality.
	 * 
	 * @param inputData
	 *          Contains information about input data for rm request.
	 * @return RmOutputData Contains output data
	 */
	public OutputData execute(InputData data) {

		log.debug("{}: Start execution!", funcName);
		log.debug("{}: Check input data ...", funcName);
		checkIfAcceptable(data);
		RmInputData inputData = (RmInputData) data;
		try {
			checkInputData(inputData);
		} catch (RmException e) {
			log.error("{}: {}", funcName, e.getMessage());
			return exitWithStatus(e.getReturnStatus(), inputData);
		}
		ArrayOfSURLs surlArray = inputData.getSurlArray();
		log.debug("{}: Received srmRm on {} surls", funcName, surlArray.size());
		ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();
		
		
		TReturnStatus globalStatus = null;


		boolean atLeastOneSuccess = false;
		boolean atLeastOneFailure = false;
		for (TSURL surl : inputData.getSurlArray().getArrayList()) {
			log.debug("{}: Rm SURL: {}", funcName, surl);
			TReturnStatus returnStatus = null;
			returnStatus = doRm(surl, inputData);
			try {
				arrayOfFileStatus.addTSurlReturnStatus(new TSURLReturnStatus(surl,
					returnStatus));
			} catch (InvalidTSURLReturnStatusAttributeException e) {
				log.error(e.getMessage());
				continue;
			}
			printSurlOutcome(returnStatus, inputData, surl);
			atLeastOneSuccess |= returnStatus.isSRM_SUCCESS();
			atLeastOneFailure |= !returnStatus.isSRM_SUCCESS();
		}
		
		if (atLeastOneSuccess) {
			if (atLeastOneFailure) {
				globalStatus = CommandHelper.buildStatus(
					TStatusCode.SRM_PARTIAL_SUCCESS, "Some files were not removed");
			} else {
				globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
					"All files removed");
			}
		} else {
			globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
				"No files removed");
		}
		printRequestOutcome(globalStatus, inputData);
		return new RmOutputData(globalStatus, arrayOfFileStatus);
	}

	private TReturnStatus doRm(TSURL surl, InputData inputData) {
		
		if (surl.isEmpty()) {
			log.error("{}: doRm SURL {} is empty", funcName, surl);
			return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
				"Invalid SURL");
		}
		// get user data or null
		GridUserInterface user = null;
		if (inputData instanceof IdentityInputData) {
			user = ((IdentityInputData) inputData).getUser();
		}
		// get StoRI from SURL
		StoRI stori = null;
		try {
			stori = namespace.resolveStoRIbySURL(surl, user);
		} catch (UnapprochableSurlException e) {
			log.error("Unable to build a stori for surl {} for user {}. {}", surl, 
				DataHelper.getRequestor(inputData), e.getMessage());
			return CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, 
				e.getMessage());
		} catch (NamespaceException e) {
			log.error("Unable to build a stori for surl {} for user {}. {}", surl, 
				DataHelper.getRequestor(inputData), e.getMessage());
			return CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, 
				e.getMessage());
		} catch (InvalidSURLException e) {
			log.error("Unable to build a stori for surl {} for user {}. {}", surl,
				DataHelper.getRequestor(inputData), e.getMessage());
			return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, 
				e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("SrmRm: StoRI from surl error: {}", e.getMessage(), e);
			return CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, 
				e.getMessage());
		}

		TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, stori);
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);
		boolean isSpaceAuthorized;
		if (inputData instanceof IdentityInputData) {
			isSpaceAuthorized = spaceAuth.authorize(
				((IdentityInputData) inputData).getUser(), SRMSpaceRequest.RM);
		} else {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.RM);
		}
		if (!isSpaceAuthorized) {
			log.debug("srmRm: User not authorized to perform srmRm on SA: {}", 
			  token);
			return CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User not authorized to perform srmRm request on the storage area");
		}
		AuthzDecision decision;
		if (inputData instanceof IdentityInputData) {
			decision = AuthzDirector.getPathAuthz().authorize(
				((IdentityInputData) inputData).getUser(), SRMFileRequest.RM, stori);
		} else {
			decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
				SRMFileRequest.RM, stori.getStFN());
		}
		if (!decision.equals(AuthzDecision.PERMIT)) {
			log.debug("srmRm: User not authorized to delete file. AuthzDecision: {}",
			  decision);
			return CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to delete the file");
		}

		log.debug("srmRm: authorized for {} on file= {}", 
			DataHelper.getRequestor(inputData) , stori.getPFN());

		long fileSize = stori.getLocalFile().getExactSize();
		TReturnStatus returnStatus = null;
		try {
			returnStatus = manageAuthorizedRM(surl, stori);
		} catch (IllegalArgumentException e) {
			log.error("srmRm: {}", e.getMessage(), e);
			return CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
				"Error while performing an authorized rm");
		} catch (UnknownSurlException e) {
			log.error("srmRm: {}", e.getMessage(), e);
			return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, 
				"The SURL is unknown");
		}
		if (returnStatus.isSRM_SUCCESS()) {
			updateUsedSpace(stori, fileSize);
		}
		return returnStatus;
	}
	
	private void updateUsedSpace(StoRI stori, long fileSize) {

		SpaceUpdaterHelperInterface sh = SpaceUpdaterHelperFactory
			.getSpaceUpdaterHelper(stori.getVirtualFileSystem());		
		sh.decreaseUsedSpace(stori.getVirtualFileSystem(), fileSize);
	}
	
	/**
	 * @param user
	 *          VomsGridUser
	 * @param stori
	 *          StoRI
	 * @return TReturnStatus
	 * @throws UnknownSurlException
	 * @throws IllegalArgumentException
	 */
	private TReturnStatus manageAuthorizedRM(TSURL surl, StoRI stori)
		throws IllegalArgumentException, UnknownSurlException {

		TReturnStatus returnStatus = null;
		boolean fileRemoved;
		String explanation = "";
		TStatusCode statusCode = TStatusCode.EMPTY;

		LocalFile file = stori.getLocalFile();

		if (!(file.exists())) {
			// The file does not exists!
			statusCode = TStatusCode.SRM_INVALID_PATH;
			explanation = "File does not exist";
		} else if ((file.isDirectory())) {
			// The file exists but it is a directory!
			statusCode = TStatusCode.SRM_INVALID_PATH;
			explanation = "The specified file is a directory. Not removed";
		
		} else {

			/**
			 * If there are SrmPrepareToPut active on the SURL specified change the
			 * SRM_STATUS from SRM_SPACE_AVAILABLE to SRM_ABORTED
			 */
			SurlStatusManager.checkAndUpdateStatus(TRequestType.PREPARE_TO_PUT, surl,
				TStatusCode.SRM_SPACE_AVAILABLE, TStatusCode.SRM_ABORTED,
				"File Removed by a SrmRm()");
			
			/**
			 * If there are SrmPrepareToGet active on the SURL specified change the
			 * SRM_STATUS from SRM_FILE_PINNED to SRM_ABORTED
			 */
			SurlStatusManager.checkAndUpdateStatus(TRequestType.PREPARE_TO_GET, surl,
				TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_ABORTED,
				"File Removed by a SrmRm()");
			
			// shall we check also for copy requests?
			
			// the file exists and it is not a directory
			fileRemoved = removeTarget(file/* , lUser */);

			if (!(fileRemoved)) {
			
				// Deletion failed for not enough permission.
				statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
				explanation = "File not removed, permission denied.";
			
			} else { // File removed with success from underlying file system
				// Remove file entry from Persistence

				/**
				 * @todo: Remove file entry from Persistence Check if the specified SURL
				 *        is associated to a certain space Token, in that case remove it
				 *        and update the used space.
				 */

				statusCode = TStatusCode.SRM_SUCCESS;
				explanation = "File removed";
			}
		}

		try {
			returnStatus = new TReturnStatus(statusCode, explanation);
		} catch (InvalidTReturnStatusAttributeException ex1) {
			log.debug("srmRm: {}", ex1.getMessage(), ex1);
		}

		return returnStatus;
	}

	private boolean removeTarget(LocalFile file) {

		boolean result = false;

		/**
		 * Same situation in Rmdir This check is really needed here? The check can
		 * not be done at this level. In Jit no ACE on file are setted!!! In any
		 * case add check for null permission.
		 */

		boolean canDelete = true;

		if (canDelete) {
			result = file.delete();
		} else {

			log.debug("srmRm : Unable to delete the file {}. Permission denied.", 
			  file);
		}
		return result;
	}

	private void printSurlOutcome(TReturnStatus status, RmInputData inputData,
		TSURL surl) {

		CommandHelper.printSurlOutcome(SRM_COMMAND, log, status, inputData, surl);
	}

	private void printRequestOutcome(TReturnStatus status, RmInputData inputData) {

		if (inputData != null) {
			if (inputData.getSurlArray() != null) {
				CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
					inputData.getSurlArray().asStringList());
			} else {
				CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
			}
		} else {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
		}
	}

}
