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
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
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

	private static final String SRM_COMMAND = "srmRm";
	private final Logger log = LoggerFactory.getLogger(RmCommand.class);
	private final String funcName = "srmRm";
	private final NamespaceInterface namespace;

	public RmCommand() {

		namespace = NamespaceDirector.getNamespace();
	}

	/**
	 * Method that provide SrmRm functionality.
	 * 
	 * @param inputData
	 *          Contains information about input data for rm request.
	 * @return RmOutputData Contains output data
	 */
	public OutputData execute(InputData inputDataGeneric) {

		log.debug("SrmRm: Dir Manager Rm start!");

		TReturnStatus globalStatus = null;

		RmOutputData outputData = new RmOutputData();
		RmInputData inputData = (RmInputData) inputDataGeneric;

		/**
		 * Validate RmInputData. The check is done at this level to separate
		 * internal StoRM logic from xmlrpc specific operation.
		 */
		if ((inputData == null)
			|| ((inputData != null) && (inputData.getSurlArray() == null))) {
			log.debug("srmRm : Invalid input parameter specified");
			globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
				"Invalid input parameters specified");
			printRequestOutcome(globalStatus, inputData);
			outputData.setStatus(globalStatus);
			outputData.setSurlStatus(null);
			return outputData;
		}

		ArrayOfSURLs surlArray = inputData.getSurlArray();
		ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();

		log.debug("srmRm: DirManager: Rm: SURLVectorSize: " + surlArray.size());
		for (TSURL surl : inputData.getSurlArray().getArrayList()) {
			log.debug("srmRm: DirManager: Rm: SURL: " + surl);
			TSURLReturnStatus fileStatus = new TSURLReturnStatus();
			fileStatus.setSurl(surl);
			TReturnStatus returnStatus = null;
			StoRI stori = null;
			if (!surl.isEmpty()) {
				try {
					if (inputData instanceof IdentityInputData) {
						try {
							stori = namespace.resolveStoRIbySURL(surl,
								((IdentityInputData) inputData).getUser());
						} catch (UnapprochableSurlException e) {
							log.info("Unable to build a stori for surl " + surl
								+ " for user " + DataHelper.getRequestor(inputData)
								+ " UnapprochableSurlException: " + e.getMessage());
							globalStatus = CommandHelper.buildStatus(
								TStatusCode.SRM_INVALID_PATH, "Invalid SURL path specified");
							printRequestOutcome(globalStatus, inputData);
							outputData.setStatus(globalStatus);
							outputData.setSurlStatus(null);
							return outputData;
						}
					} else {
						try {
							stori = namespace.resolveStoRIbySURL(surl);
						} catch (UnapprochableSurlException e) {
							log.info("Unable to build a stori for surl " + surl
								+ " UnapprochableSurlException: " + e.getMessage());
							globalStatus = CommandHelper.buildStatus(
								TStatusCode.SRM_INVALID_PATH, "Invalid SURL path specified");
							printRequestOutcome(globalStatus, inputData);
							outputData.setStatus(globalStatus);
							outputData.setSurlStatus(null);
							return outputData;
						}
					}
				} catch (IllegalArgumentException e) {
					log
						.error("SrmRm: Unable to build StoRI by surl and user. IllegalArgumentException: "
							+ e.getMessage());
					globalStatus = CommandHelper
						.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
							"Unable to build a STORI from surl");
					printRequestOutcome(globalStatus, inputData);
					outputData.setStatus(globalStatus);
					outputData.setSurlStatus(null);
					return outputData;
				}
			} else {
				log.error("srmRm: Malformed SURL passed from converter ");
				returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
					"Invalid SURL");
				printSurlOutcome(returnStatus, inputData, surl);
			}
			if (stori != null) {
				TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, stori);
				SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);
				boolean isSpaceAuthorized;
				if (inputData instanceof IdentityInputData) {
					isSpaceAuthorized = spaceAuth.authorize(
						((IdentityInputData) inputData).getUser(), SRMSpaceRequest.RM);
				} else {
					isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.RM);
				}
				if (isSpaceAuthorized) {
					AuthzDecision decision;
					if (inputData instanceof IdentityInputData) {
						decision = AuthzDirector.getPathAuthz().authorize(
							((IdentityInputData) inputData).getUser(), SRMFileRequest.RM,
							stori);
					} else {
						decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
							SRMFileRequest.RM, stori.getStFN());
					}
					if (decision.equals(AuthzDecision.PERMIT)) {

						log.debug("srmRm: authorized for "
							+ DataHelper.getRequestor(inputData) + " for file = "
							+ stori.getPFN());

						// Prior to delete the file get the actual file size to update
						// properly the DB
						long fileSize = 0;
						if (stori.getLocalFile().exists()) {
							fileSize = stori.getLocalFile().getExactSize();
						}
						try {
							returnStatus = manageAuthorizedRM(surl, stori);
						} catch (IllegalArgumentException e) {
							log
								.error("srmRm: IllegalArgumentException from manageAuthorizedRM: "
									+ e);
							globalStatus = CommandHelper.buildStatus(
								TStatusCode.SRM_INTERNAL_ERROR,
								"Error while performing an authorized rm");
							printRequestOutcome(globalStatus, inputData);
							outputData.setStatus(globalStatus);
							outputData.setSurlStatus(null);
							return outputData;
						} catch (UnknownSurlException e) {
							log.error("srmRm: UnknownSurlException from manageAuthorizedRM: "
								+ e);
							returnStatus = CommandHelper.buildStatus(
								TStatusCode.SRM_INVALID_PATH, "The SURL is unknown");
						}
						if (returnStatus.isSRM_SUCCESS()) {
							/*
							 * If Storage Area hard limit is enabled, update space on DB
							 */
							VirtualFSInterface fs = stori.getVirtualFileSystem();
							if (fs != null && fs.getProperties().isOnlineSpaceLimited()) {
								if (inputData instanceof IdentityInputData) {
									new SpaceHelper().increaseFreeSpaceForSA(log, funcName,
										((IdentityInputData) inputData).getUser(), surl, fileSize);
								} else {
									new SpaceHelper().increaseFreeSpaceForSA(log, funcName, surl,
										fileSize);
								}
							}
						}
					} else {
						log
							.debug("srmRm: User not authorized to delete the file AuthzDecision is :'"
								+ decision + "')");
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_AUTHORIZATION_FAILURE,
							"User is not authorized to delete the file");
					}
				} else {
					log
						.debug("srmRm: User not authorized to perform srmRm request on the storage area: "
							+ token);
					returnStatus = CommandHelper.buildStatus(
						TStatusCode.SRM_AUTHORIZATION_FAILURE,
						"User not authorized to perform "
							+ "srmRm request on the storage area");
				}
			}
			printSurlOutcome(returnStatus, inputData, surl);
			fileStatus.setStatus(returnStatus);
			arrayOfFileStatus.addTSurlReturnStatus(fileStatus);
		}

		boolean atLeastOneSuccess = false;
		boolean atLeastOneFailure = false;
		for (TSURLReturnStatus status : arrayOfFileStatus.getArray()) {
			if (atLeastOneFailure && atLeastOneSuccess) {
				break;
			}
			if (status.getStatus().isSRM_SUCCESS()) {
				atLeastOneSuccess = true;
			} else {
				atLeastOneFailure = true;
			}
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
		outputData.setStatus(globalStatus);
		outputData.setSurlStatus(arrayOfFileStatus);
		return outputData;
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

		// Build the ReturnStatus
		try {
			returnStatus = new TReturnStatus(statusCode, explanation);
		} catch (InvalidTReturnStatusAttributeException ex1) {
			log.debug("srmRm: Error creating returnStatus " + ex1);
		}

		return returnStatus;
	}

	private boolean removeTarget(LocalFile file/* , LocalUser lUser */) {

		boolean result = false;
		// Check Permission
		// FilesystemPermission groupPermission = null;
		// try {
		// groupPermission = file.getGroupPermission(lUser);
		// } catch (CannotMapUserException ex) {
		// /**
		// * @todo : Why this exception?
		// */
		// log.debug("srmRm: WHY THIS? " + ex);
		// }

		// FilesystemPermission userPermission = null;
		// try {
		// userPermission = file.getUserPermission(lUser);
		// } catch (CannotMapUserException ex1) {
		// /**
		// * @todo : Why this exception?
		// */
		// log.debug("srmRm: WHY THIS? " + ex1);
		// }

		/**
		 * Same situation in Rmdir This check is really needed here? The check can
		 * not be done at this level. In Jit no ACE on file are setted!!! In any
		 * case add check for null permission.
		 */

		// Check if user or group permission are null to prevent Null Pointer
		boolean canDelete = true;

		/*
		 * if(userPermission!=null) { canDelete = userPermission.canDelete();
		 * log.debug("removeTarget:userP:"+userPermission.canDelete()); } if
		 * ((groupPermission!=null)&&(!canDelete)) {
		 * log.debug("removeTarget:groupP:"+groupPermission.canDelete()); canDelete
		 * = groupPermission.canDelete(); }
		 */

		// if ( (userPermission.canDelete()) || (groupPermission.canDelete())) {
		if (canDelete) {
			result = file.delete();
		} else {
			log.debug("srmRm : Unable to delete the file '" + file
				+ "'. Permission denied.");
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
