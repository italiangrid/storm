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
import it.grid.storm.srm.types.SRMCommandException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirOutputData;


class RmdirException extends SRMCommandException {

  private static final long serialVersionUID = 1L;

  public RmdirException(TStatusCode code, String message) {

    super(code, message);
  }
}

class TSize {
	
	private long size;
	
	TSize(long size) {
		this.size = size;
	}
	
	public void add(long n) {
		size += n;
	}
	
	public void dec(long n) {
		size -= n;
	}
	
	public long get() {
		return size;
	}
	
}

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
	 * @return OutputData Contains output data
	 */
	public OutputData execute(InputData data) {

		RmdirOutputData outputData = null;
		log.debug("SrmRmdir: Start execution.");
		checkInputData(data);
		outputData = doRmdir((RmdirInputData) data);
		log.debug("srmRmdir return status: {}", outputData.getStatus());
		printRequestOutcome(outputData.getStatus(), (RmdirInputData) data);
		return outputData;
		
	}
	
	private RmdirOutputData doRmdir(RmdirInputData data) {
		
		TSURL surl = null;
		GridUserInterface user = null;
		StoRI stori = null;
		TReturnStatus returnStatus = null;
		boolean recursion = false;
		TSize size = new TSize(0);
				
		try {
			surl = getSURL(data);
			user = getUser(data);
			recursion = isRecursive(data);
			stori = resolveStoRI(surl, user);
			checkUserAuthorization(stori, user);
			log.debug("srmRmdir: rmdir authorized for {}. Dir={}. Recursive={}",
				userToString(user), stori.getPFN(), recursion);
			returnStatus = removeFolder(stori.getLocalFile(), recursion, size);
			log.debug("srmRmdir: decrease used space of {} bytes", size.get());
			try {
				decreaseUsedSpace(stori.getLocalFile(), size.get());
			} catch (NamespaceException e) {
				log.error("srmRmdir: {}", e.getMessage());
				returnStatus.extendExplaination("Unable to decrease used space: "
					+ e.getMessage());
			}
		} catch (RmdirException e) {
			log.error("srmRmdir: {}", e.getMessage());
			returnStatus = e.getReturnStatus();
		}

		log.debug("srmRmdir: returned status is {}", returnStatus);
		return new RmdirOutputData(returnStatus);
	}
	
	private void checkInputData(InputData data)
		throws IllegalArgumentException {
		
		if (data == null) {
			throw new IllegalArgumentException("Invalid input data: NULL");
		}
		if (!(data instanceof RmdirInputData)) {
			throw new IllegalArgumentException("Invalid input data type");
		}
	}
	
	private StoRI resolveStoRI(TSURL surl, GridUserInterface user)
		throws RmdirException {

		String formatStr = "Unable to build a stori for surl {} for user {}: {}";
		try {
			return namespace.resolveStoRIbySURL(surl, user);
		} catch (UnapprochableSurlException e) {
			log.error(formatStr, surl, userToString(user), e.getMessage());
			throw new RmdirException(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				e.getMessage());
		}	catch (NamespaceException e) {
			log.error(formatStr, surl, userToString(user), e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}	catch (InvalidSURLException e) {
			log.error(formatStr, surl, userToString(user), e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INVALID_PATH, e.getMessage());
		}	catch (IllegalArgumentException e) {
			log.error(formatStr, surl, userToString(user), e.getMessage());
			throw new RmdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}
	}
	
	private boolean isAnonymous(GridUserInterface user) {
		
		return (user == null);
	}
	
	private String userToString(GridUserInterface user) {
		
		return isAnonymous(user) ? "anonymous" : user.getDn();
	}
	
	private void checkUserAuthorization(StoRI stori, GridUserInterface user) 
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
			throw new RmdirException(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to remove the directory on the storage area "
					+ token);
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
			throw new RmdirException(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to remove the directory");
		}
		return;
	}
	
	private GridUserInterface getUser(InputData data) {
		
		if (data instanceof IdentityInputData) {
			return ((IdentityInputData) data).getUser();
		}
		return null;
	}
	
	private TSURL getSURL(RmdirInputData data) throws RmdirException {
		
		TSURL surl = ((RmdirInputData) data).getSurl();
		if (surl == null) {
			throw new RmdirException(TStatusCode.SRM_FAILURE,
				"SURL specified is NULL");
		}
		if (surl.isEmpty()) {
			throw new RmdirException(TStatusCode.SRM_FAILURE,
				"SURL specified is empty");
		}
		return surl;
	}
	
	private boolean isRecursive(RmdirInputData data) {
		
		return data.getRecursive().booleanValue();
	}
	
	private void decreaseUsedSpace(LocalFile localFile, long sizeToRemove)
		throws NamespaceException {

		NamespaceDirector.getNamespace().resolveVFSbyLocalFile(localFile)
			.decreaseUsedSpace(sizeToRemove);
	}

	private TReturnStatus removeFolder(LocalFile dir, boolean recursive, TSize size)
		throws RmdirException {
		
		/* 
		 * Check if dir exists and is a directory, if recursion is enabled when 
		 * directory is not empty, etc...
		 */

		if (!dir.exists()) {
			return new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
				"Directory does not exists");
		}
		if (!dir.isDirectory()) {
			return new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Not a directory");
		}
		if (!recursive && (dir.listFiles().length > 0)) {
			return new TReturnStatus(TStatusCode.SRM_NON_EMPTY_DIRECTORY,
				"Directory is not empty");
		}

		if (recursive) {
			LocalFile[] list = dir.listFiles();
			log.debug("srmRmdir: removing {} content", dir);
			for (LocalFile element : list) {
				log.debug("srmRmdir: removing {}", element);
				if (element.isDirectory()) {
					removeFolder(element, recursive, size);
				} else {
					removeFile(element, size);
				}
			}
		}
		log.debug("srmRmdir: removing {}", dir);
		removeEmptyDirectory(dir, size);
		return new TReturnStatus(TStatusCode.SRM_SUCCESS, "Directory removed with success!");
	}

	private void removeEmptyDirectory(LocalFile directory, TSize size)
		throws RmdirException {
		
		removeFile(directory, size);
	}
	
	private void removeFile(LocalFile file, TSize size) throws RmdirException {
		
		long fileSize = file.length();
		if (!file.delete()) {
			log.error("srmRmdir: Unable to delete {}", file);
			throw new RmdirException(TStatusCode.SRM_FAILURE,
				"Unable to delete " + file.getAbsolutePath());
		}
		size.add(fileSize);
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