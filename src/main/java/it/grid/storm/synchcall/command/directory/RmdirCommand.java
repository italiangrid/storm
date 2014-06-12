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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
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

  public static final Logger log = LoggerFactory.getLogger(RmdirCommand.class);
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
					  log.info("Unable to build a stori for surl {} for user {}: {}",
					    surl,
					    DataHelper.getRequestor(inputData),
					    e.getMessage());
					  
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					} catch (NamespaceException e) {
					  log.info("Unable to build a stori for surl {} for user {}: {}",
					    surl,
					    DataHelper.getRequestor(inputData),
					    e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					} catch (InvalidSURLException e) {
					  log.info("Unable to build a stori for surl {} for user {}: {}",
					    surl,
					    DataHelper.getRequestor(inputData),
					    e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INVALID_PATH, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					}
				} else {
					try {
						stori = namespace.resolveStoRIbySURL(surl);
					} catch (UnapprochableSurlException e) {
					  log.info("Unable to build a stori for surl {}: {}",
					    surl,
					    e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					} catch (NamespaceException e) {
					  log.info("Unable to build a stori for surl {}: {}",
					    surl,
					    e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					} catch (InvalidSURLException e) {
					  log.info("Unable to build a stori for surl {}: {}",
					    surl,
					    e.getMessage());
						returnStatus = CommandHelper.buildStatus(
							TStatusCode.SRM_INVALID_PATH, e.getMessage());
						printRequestOutcome(returnStatus, inputData);
						return new RmdirOutputData(returnStatus);
					}
				}
			} catch (IllegalArgumentException e) {
			  log.error("StoRI from surl build error: {}",
			    e.getMessage(), e);
				returnStatus = CommandHelper.buildStatus(
					TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
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
			log.debug("User not authorized to perform srmRmdir on SA: {}", token);
			returnStatus = CommandHelper.buildStatus(
				TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User not authorized to perform srmRmdir on storage area");
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
			log.debug("srmRmDir authorized for {}. Dir={}. Recursive={}",
			  DataHelper.getRequestor(inputData), 
			  stori.getPFN(),
			  recursive);
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

				log.debug("{}: Recursive deletion will remove directory and contents.", SRM_COMMAND);

				if (!deleteDirectoryContent(directory)) {
					return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
						"Unable to delete some files within directory.");
				}
			}

			if (!removeFile(directory)) {
				returnStatus = CommandHelper.buildStatus(
					TStatusCode.SRM_NON_EMPTY_DIRECTORY, "Directory is not empty");
			} else {
				returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
					"Directory removed with success!");
			}
		} else {
			log.debug("{}: request with invalid directory specified!", SRM_COMMAND);
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
					
					log.info("{} : Unable to delete non-empty directory {}", 
							SRM_COMMAND, file);

				} else {
					result = file.delete();
				}
			} else {
				result = file.delete();
			}
		} else {
			result = false;
			log.debug("{}: file {} does not exist", SRM_COMMAND, file);
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