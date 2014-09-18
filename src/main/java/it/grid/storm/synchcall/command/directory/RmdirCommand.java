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
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.space.SpaceUpdaterHelperFactory;
import it.grid.storm.space.SpaceUpdaterHelperInterface;
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

	class RmdirException extends Exception {

		private static final long serialVersionUID = 1L;
		
		private TReturnStatus returnStatus;
		
		public RmdirException(TStatusCode code, String message) {
			super(message);
			this.returnStatus = CommandHelper.buildStatus(code, message);
		}
		
		public TReturnStatus getReturnStatus() {
			return returnStatus;
		}
	}
	
  public static final Logger log = LoggerFactory.getLogger(RmdirCommand.class);
	private static final String SRM_COMMAND = "srmRmdir";
	private final NamespaceInterface namespace;
	private long size;

	public RmdirCommand() {

		namespace = NamespaceDirector.getNamespace();
		
	}

	private void validate(InputData data) throws IllegalArgumentException, 
		RmdirException {
		
		if (data == null) {
			throw new IllegalArgumentException("Invalid input data: NULL");
		}
		if (!(data instanceof RmdirInputData)) {
			throw new IllegalArgumentException("Invalid input data type");
		}
		if (((RmdirInputData) data).getSurl() == null) {
			throw new RmdirException(TStatusCode.SRM_FAILURE,
				"SURL specified is NULL");
		}
		if (((RmdirInputData) data).getSurl().isEmpty()) {
			throw new RmdirException(TStatusCode.SRM_FAILURE,
				"Invalid empty SURL specified");
		}
	}
	
	private RmdirOutputData exitWithStatus(TReturnStatus returnStatus,
		RmdirInputData data) {
		
		printRequestOutcome(returnStatus, data);
		return new RmdirOutputData(returnStatus);
	}
	
	private StoRI resolveStoRI(TSURL surl, GridUserInterface user)
		throws RmdirException {

		try {
			return namespace.resolveStoRIbySURL(surl, user);
		} catch (UnapprochableSurlException e) {
			log.error(e.getMessage());
			throw new RmdirException(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				e.getMessage());
		}	catch (NamespaceException e) {
			log.error(e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}	catch (InvalidSURLException e) {
			log.error(e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INVALID_PATH, e.getMessage());
		}	catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}
	}
	
	private boolean isAnonymous(GridUserInterface user) {
		
		return (user == null);
	}
	
	private boolean isUserAuthorized(StoRI stori, GridUserInterface user) 
		throws RmdirException {
		
		TSpaceToken token;
		try {
			token = stori.getVirtualFileSystem().getSpaceToken();
		} catch (NamespaceException e) {
			log.error(e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);
		
		boolean isSpaceAuthorized;
		if (isAnonymous(user)) {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.RMD);
		} else {
			isSpaceAuthorized = spaceAuth.authorize(user, SRMSpaceRequest.RMD);
		}
		if (!isSpaceAuthorized) {
			log.debug("srmRmdir: User not authorized to perform srmRmdir request "
			  + "on the storage area: {}", token);
			return false;
		}
		
		AuthzDecision decision;
		if (isAnonymous(user)) {
			decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
				SRMFileRequest.RMD, stori.getStFN());
		} else {			
			decision = AuthzDirector.getPathAuthz().authorize(user,
				SRMFileRequest.RMD, stori);
		}
		if (!decision.equals(AuthzDecision.PERMIT)) {
			log.debug("srmRmdir: User is not authorized to delete the directory");
			return false;
		}
		
		return true;
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
		try {
			validate(data);
		} catch (RmdirException e) {
			log.error("srmRmdir: {}", e.getMessage());
			return exitWithStatus(e.getReturnStatus(), (RmdirInputData) data);
		}
		
		RmdirInputData inputData = (RmdirInputData) data;
		TSURL surl = inputData.getSurl();
		GridUserInterface user = (data instanceof IdentityInputData) ? 
			((IdentityInputData) data).getUser() : null;
		StoRI stori = null;

		try {
			stori = resolveStoRI(surl, user);
		} catch (RmdirException e) {
			log.error("Unable to build a stori for surl {} for user {}: {}",
		    surl, DataHelper.getRequestor(inputData), e.getMessage());
			return exitWithStatus(e.getReturnStatus(), inputData);
		}

		boolean isAuthorized = false;
		try {
			isAuthorized = isUserAuthorized(stori, user);
		} catch (RmdirException e) {
			log.error(e.getMessage());
			return exitWithStatus(e.getReturnStatus(), inputData);
		}
		if (!isAuthorized) {
			log.debug("srmRmdir not authorized for {} for directory = {}", 
			  DataHelper.getRequestor(inputData), stori.getPFN());
			return exitWithStatus(CommandHelper.buildStatus(
				TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to remove the directory"), inputData);
		}
		
		log.debug("srmRmDir authorized for {}. Dir={}. Recursive={}",
			DataHelper.getRequestor(inputData), stori.getPFN(), 
			inputData.getRecursive());
		
		TReturnStatus returnStatus = manageAuthorizedRMDIR(stori, inputData.getRecursive().booleanValue());
		log.debug("srmMkdir return status: {}", returnStatus);
		return exitWithStatus(returnStatus, inputData);
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
	private TReturnStatus manageAuthorizedRMDIR(StoRI stori,
		boolean recursive) {

		LocalFile directory = stori.getLocalFile();
		
		if (!directory.exists()) {
			return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
				"Directory does not exists");
		}
		if (!directory.isDirectory()) {
			return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
				"Not a directory");
		}
		/* directory exists and is a directory */

		size = 0;
		if (recursive) {

			log.debug("{}: Recursive deletion will remove directory and contents.", 
				SRM_COMMAND);

			if (!deleteDirectoryContent(directory)) {
				return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
					"Unable to delete some files within directory.");
			}
		}

		if (!removeEmptyDirectory(directory)) {
			return CommandHelper.buildStatus(
				TStatusCode.SRM_NON_EMPTY_DIRECTORY, "Directory is not empty");
		}
		log.debug("{}: Total used space to remove = {} bytes", SRM_COMMAND, size);
		
		SpaceUpdaterHelperInterface sh = SpaceUpdaterHelperFactory
			.getSpaceUpdaterHelper(stori.getVirtualFileSystem());		
		sh.decreaseUsedSpace(stori.getVirtualFileSystem(), size);
		
		return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
				"Directory removed with success!");
	}

	/**
	 * Recursive function for deleteAll
	 */
	private boolean deleteDirectoryContent(LocalFile directory) {

		if (!directory.exists()) {
			log.error("{}: directory {} does not exist", SRM_COMMAND, directory);
			return false;
		}
		if (!directory.isDirectory()) {
			log.error("{}: directory {} is not a directory", SRM_COMMAND, directory);
			return false;
		}
		
		boolean result = true;
		LocalFile[] list = directory.listFiles();
		for (LocalFile element : list) {
			if (element.isDirectory()) {
				result &= deleteDirectoryContent(element);
				result &= removeEmptyDirectory(element);
			} else {
				result &= removeFile(element);
			}
		}
		return result;
	}

	private boolean removeEmptyDirectory(LocalFile directory) {
		
		if (!directory.exists()) {
			log.error("{}: directory {} does not exist", SRM_COMMAND, directory);
			return false;
		}
		if (!directory.isDirectory()) {
			log.error("{}: directory {} is not a directory", SRM_COMMAND, directory);
			return false;
		}
		long dirSize = directory.length();
		if (!directory.delete()) {
			log.error("{}: Unable to delete directory {}", SRM_COMMAND, directory);
			return false;
		}
		size += dirSize;
		return true;
	}
	
	private boolean removeFile(LocalFile file) {
		
		if (!file.exists()) {
			log.error("{}: fie {} does not exist", SRM_COMMAND, file);
			return false;
		}
		if (file.isDirectory()) {
			log.error("{}: file {} is a directory", SRM_COMMAND, file);
			return false;
		}
		long fileSize = file.length();
		if (!file.delete()) {
			log.error("{}: Unable to delete file {}", SRM_COMMAND, file);
			return false;
		}
		size += fileSize;
		log.debug("RmdirCommand: add {} to size", fileSize);
		return true;
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