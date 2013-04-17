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

import java.util.Arrays;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirOutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class RmdirCommand extends DirectoryCommand implements Command {

	private static final String SRM_COMMAND = "srmRmdir";
	private final NamespaceInterface namespace;

	public RmdirCommand() {

		namespace = NamespaceDirector.getNamespace();
	}

	/**
	 * Method that provide SrmRmdir functionality.
	 * 
	 * @param inputData
	 *          Contains information about input data for Rmdir request.
	 * @return TReturnStatus Contains output data
	 */
	public OutputData execute(InputData data) {

		log.debug("srmRmdir: Start execution.");
		TReturnStatus returnStatus = null;

		RmdirInputData inputData = (RmdirInputData) data;
		RmdirOutputData outData = null;

		/**
		 * Validate RmdirInputData. The check is done at this level to separate
		 * internal StoRM logic from xmlrpc specific operation.
		 */

		if ((inputData == null)
			|| ((inputData != null) && (inputData.getSurl() == null))) {
			returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
				"Invalid parameter specified.");
			printRequestOutcome(returnStatus, inputData);
			outData = new RmdirOutputData(returnStatus);
			return outData;
		}

		TSURL surl = inputData.getSurl();
		StoRI stori = null;
		if (!surl.isEmpty()) {
			try {
				if (inputData instanceof IdentityInputData) {
					try {
						stori = namespace.resolveStoRIbySURL(surl,
							((IdentityInputData) inputData).getUser());
					} catch (UnapprochableSurlException e) {
						log.info("Unable to build a stori for surl " + surl + " for user "
							+ DataHelper.getRequestor(inputData)
							+ " UnapprochableSurlException: " + e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INVALID_PATH, "Invalid SURL path specified");
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					}
				} else {
					try {
						stori = namespace.resolveStoRIbySURL(surl);
					} catch (UnapprochableSurlException e) {
						log.info("Unable to build a stori for surl " + surl
							+ " UnapprochableSurlException: " + e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INVALID_PATH, "Invalid SURL path specified");
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					}
				}
			} catch (IllegalArgumentException e) {
				log
					.error("srmRmdir: Unable to build StoRI by surl and user. IllegalArgumentException: "
						+ e.getMessage());
				returnStatus = CommandHelper.buildStatus(
					TStatusCode.SRM_INTERNAL_ERROR, "Unable to get StoRI for surl");
				printRequestOutcome(returnStatus, inputData);
				outData = new RmdirOutputData(returnStatus);
				return outData;
			}
		} else {
			returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
				"Invalid SURL specified");
			printRequestOutcome(returnStatus, inputData);
			outData = new RmdirOutputData(returnStatus);
			return outData;
		}

		// Check here if recursive flag is not specifed
		// in input parameter.Use default value
		Boolean recursive = inputData.getRecursive();
		if (recursive == null) {
			recursive = new Boolean(SRMConstants.recursiveFlag);
		}

		/**
		 * From version 1.4 Add the control for Storage Area using the new authz for
		 * space component.
		 */

		TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, stori);
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);
		boolean isSpaceAuthorized;
		if (inputData instanceof IdentityInputData) {
			isSpaceAuthorized = spaceAuth.authorize(
				((IdentityInputData) inputData).getUser(), SRMSpaceRequest.RMD);
		} else {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.RMD);
		}
		if (!isSpaceAuthorized) {
			log
				.debug("srmRmdir: User not authorized to perform srmRmdir request on the storage area: "
					+ token);
			returnStatus = CommandHelper.buildStatus(
				TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User not authorized to perform srmRmdir request on the storage area");
			printRequestOutcome(returnStatus, inputData);
			outData = new RmdirOutputData(returnStatus);
			return outData;

		}

		AuthzDecision decision;
		if (inputData instanceof IdentityInputData) {
			decision = AuthzDirector.getPathAuthz().authorize(
				((IdentityInputData) inputData).getUser(), SRMFileRequest.RMD, stori);
		} else {
			decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
				SRMFileRequest.RMD, stori.getStFN());
		}
		if (decision.equals(AuthzDecision.PERMIT)) {
			log.debug("RMDIR is authorized for " + DataHelper.getRequestor(inputData)
				+ " and the directory = " + stori.getPFN() + " with recursove opt = "
				+ recursive);
			returnStatus = manageAuthorizedRMDIR(stori.getLocalFile(),
				recursive.booleanValue());
		} else {
			returnStatus = CommandHelper.buildStatus(
				TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to delete the directory");
		}
		printRequestOutcome(returnStatus, inputData);
		outData = new RmdirOutputData(returnStatus);
		return outData;
	}

	/**
	 * This method of FileSystem remove file and dir both from file system and
	 * from DataBase
	 * 
	 * @param user
	 *          VomsGridUser
	 * @param stori
	 *          StoRI
	 * @param recursive
	 *          boolean
	 * @return TReturnStatus
	 */
	private TReturnStatus manageAuthorizedRMDIR(LocalFile directory,
		boolean recursive) {

		TReturnStatus returnStatus;

		if ((directory.exists()) && (directory.isDirectory())) {
			if (recursive) {
				// All directory and files contained are removed.
				log.debug(SRM_COMMAND
					+ ": Recursive deletion. Removing dir with all files included! ");
				if (!deleteDirectoryContent(directory)) {
					return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
						"Unable to delete some files within directory.");
				}
			}
			// Now Directory should be Empty;
			// NON-Recursive Option
			if (!removeFile(directory)) {
				returnStatus = CommandHelper.buildStatus(
					TStatusCode.SRM_NON_EMPTY_DIRECTORY, "Directory is not empty");
			} else {
				returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
					"Directory removed with success!");
			}
		} else {
			log.debug("RMDIR : request with invalid directory specified!");
			if (!directory.exists()) {
				returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
					"Directory does not exists");
			} else {
				returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
					"Not a directory");
			}
		}
		return returnStatus;
	}

	/**
	 * Recursive function for deleteAll
	 */
	private boolean deleteDirectoryContent(LocalFile directory) {

		boolean result = true;
		if (directory.exists()) {
			if (directory.isDirectory()) {
				LocalFile[] list = directory.listFiles();
				if (list.length > 0) {
					for (LocalFile element : list) {
						result = result && deleteDirectoryContent(element);
						if (element.exists()) {
							result = result && removeFile(element);
						}
					}
				} else {
					// The directory is empty and it is deleted by the if in the
					// for loop above
				}
			} else {
				result = removeFile(directory);
			}
		}
		return result;
	}

	private boolean removeFile(LocalFile file) {

		boolean result = false;
		LocalFile[] list;
		if (file.exists()) {
			if (file.isDirectory()) {
				list = file.listFiles();
				if (list.length > 0) {
					result = false;
					log.info(SRM_COMMAND + ": Unable to delete the target file '" + file
						+ "' . It is a not-empty directory.");
				} else {
					result = file.delete();
				}
			} else {
				result = file.delete();
			}
		} else {
			result = false;
			log.debug("RMDIR : the target file '" + file + "' does not exists! ");
		}
		return result;
	}

	private void printRequestOutcome(TReturnStatus status,
		RmdirInputData inputData) {

		if (inputData != null) {
			if (inputData.getSurl() != null) {
				CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
					Arrays.asList(inputData.getSurl().toString()));
			} else {
				CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
			}

		} else {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
		}
	}

}