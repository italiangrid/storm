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

import it.grid.storm.acl.AclManagerFS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
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
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;

import static it.grid.storm.srm.types.TStatusCode.SRM_AUTHORIZATION_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_DUPLICATION_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_INTERNAL_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_INVALID_PATH;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmAuthorizationFailure;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmDuplicationError;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmFailure;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmInternalError;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmInvalidPath;
import static java.lang.String.format;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MkdirException extends SRMCommandException {

	private static final long serialVersionUID = 1L;

	private MkdirException(TStatusCode code, String message) {

		super(code, message);
	}

	public static MkdirException srmInvalidPath(String message) {
		return new MkdirException(SRM_INVALID_PATH, message);
	}

	public static MkdirException srmDuplicationError(String message) {
		return new MkdirException(SRM_DUPLICATION_ERROR, message);
	}

	public static MkdirException srmInternalError(String message) {
		return new MkdirException(SRM_INTERNAL_ERROR, message);
	}

	public static MkdirException srmFailure(String message) {
		return new MkdirException(SRM_FAILURE, message);
	}

	public static MkdirException srmAuthorizationFailure(String message) {
		return new MkdirException(SRM_AUTHORIZATION_FAILURE, message);
	}
}

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class MkdirCommand extends DirectoryCommand implements Command {

	public static final Logger log = LoggerFactory.getLogger(MkdirCommand.class);

	private static final String SRM_COMMAND = "SrmMkdir";
	private final NamespaceInterface namespace;

	public MkdirCommand() {

		namespace = NamespaceDirector.getNamespace();
	}

	/**
	 * Method that provide SrmMkdir functionality.
	 * 
	 * @param inputData Contains information about input data for Mkdir request.
	 * @return MkdirOutputData Contains output data
	 */
	public OutputData execute(InputData data) {

		MkdirOutputData outputData = null;
		log.debug("SrmMkdir: Start execution.");
		checkInputData(data);
		outputData = doMkdir((MkdirInputData) data);
		log.debug("srmMkdir return status: {}", outputData.getStatus());
		printRequestOutcome(outputData.getStatus(), (MkdirInputData) data);
		return outputData;
	}

	private MkdirOutputData doMkdir(MkdirInputData data) {

		TSURL surl = null;
		GridUserInterface user = null;
		StoRI stori = null;
		TReturnStatus returnStatus = null;
		try {
			surl = getSURL(data);
			user = getUser(data);
			stori = resolveStoRI(surl, user);
			checkUserAuthorization(stori, user);
			log.debug("srmMkdir authorized for {} for directory = {}", userToString(user), stori.getPFN());
			createFolder(stori.getLocalFile());
			returnStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Directory created with success");
			log.debug("srmMkdir: updating used space info ...");
			try {
				increaseUsedSpaceInfo(stori.getLocalFile());
			} catch (NamespaceException e) {
				log.error("srmMkdir: {}", e.getMessage());
				returnStatus.extendExplaination("Unable to increase used space info: " + e.getMessage());
			}
			log.debug("srmMkdir: managing ACL ...");
			try {
				manageAcl(stori, user, returnStatus);
			} catch (Exception e) {
				log.error("srmMkdir: {}", e.getMessage());
				returnStatus.extendExplaination("Unable to set ACL: " + e.getMessage());
			}
		} catch (MkdirException e) {
			log.error("srmMkdir: {}", e.getMessage());
			returnStatus = e.getReturnStatus();
		}
		log.debug("srmMkdir return status: {}", returnStatus);
		return new MkdirOutputData(returnStatus);
	}

	private void createFolder(LocalFile file) throws MkdirException {

		boolean response = false;

		try {
			response = file.mkdir();
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
			throw srmInternalError(e.getMessage());
		}

		if (!response) {
			log.debug("SrmMkdir: Request failed!");
			if (file.exists()) {
				log.debug("srmMkdir: {} exists", file.getPath());
				if (file.isDirectory()) {
					log.debug("srmMkdir: {} is a directory", file.getPath());
					throw srmDuplicationError("Path exists and it's a directory.");
				}
				log.debug("srmMkdir: {} is a file", file);
				throw srmInvalidPath("Path exists and it's a file.");
			}
			log.debug("srmMkdir: {} doesn't exist", file.getPath());
			LocalFile parent = file.getParentFile();
			if (parent == null) {
				log.debug("srmMkdir: null Parent directory. Is {} a root directory?", file.getPath());
				throw srmInternalError("Unexpected null parent directory");
			}
			log.debug("srmMkdir: Parent directory is {}", parent.getPath());
			if (!parent.exists()) {
				log.debug("srmMkdir: Parent directory {} doesn't exist.", parent.getPath());
				throw srmInvalidPath("Parent directory doesn't exist. Recursive directory creation Not Allowed");
			}
			log.debug("srmMkdir: Parent directory {} exists.", parent.getPath());
			if (!parent.isDirectory()) {
				log.debug("srmMkdir: Parent directory {} is not a directory.", parent.getPath());
				throw srmInvalidPath("Parent directory is not a directory.");
			}
			throw srmInternalError("Parent directory exists but some other unexpected error occoured.");
		}

		log.debug("SrmMkdir: Request successful!");
		return;
	}

	private void checkInputData(InputData data) throws IllegalArgumentException {

		if (data == null) {
			throw new IllegalArgumentException("Invalid input data: NULL");
		}
		if (!(data instanceof MkdirInputData)) {
			throw new IllegalArgumentException("Invalid input data type");
		}
	}

	private TSURL getSURL(MkdirInputData data) throws MkdirException {

		TSURL surl = data.getSurl();
		if (surl == null) {
			throw srmFailure("SURL specified is NULL");
		}
		if (surl.isEmpty()) {
			throw srmFailure("SURL specified is empty");
		}
		return surl;
	}

	private StoRI resolveStoRI(TSURL surl, GridUserInterface user) throws MkdirException {

		try {
			return namespace.resolveStoRIbySURL(surl, user);
		} catch (UnapprochableSurlException e) {
			log.error(e.getMessage());
			throw srmAuthorizationFailure(e.getMessage());
		} catch (NamespaceException e) {
			log.error(e.getMessage());
			throw srmInternalError(e.getMessage());
		} catch (InvalidSURLException e) {
			log.error(e.getMessage());
			throw srmInvalidPath(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw srmInternalError(e.getMessage());
		}
	}

	private boolean isAnonymous(GridUserInterface user) {

		return (user == null);
	}

	private void checkUserAuthorization(StoRI stori, GridUserInterface user) throws MkdirException {

		TSpaceToken token;
		try {
			token = stori.getVirtualFileSystem().getSpaceToken();
		} catch (NamespaceException e) {
			log.error(e.getMessage());
			throw srmInternalError(e.getMessage());
		}
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

		boolean isSpaceAuthorized;
		if (isAnonymous(user)) {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.MD);
		} else {
			isSpaceAuthorized = spaceAuth.authorize(user, SRMSpaceRequest.MD);
		}
		if (!isSpaceAuthorized) {
			String msg = format("User not authorized to perform srmMkdir request on the storage area: %s", token);
			log.debug("srmMkdir:{}", msg);
			throw srmAuthorizationFailure(msg);
		}

		AuthzDecision decision;
		if (isAnonymous(user)) {
			decision = AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.MD, stori.getStFN());
		} else {
			decision = AuthzDirector.getPathAuthz().authorize(user, SRMFileRequest.MD, stori);
		}
		if (!decision.equals(AuthzDecision.PERMIT)) {
			String msg = "User is not authorized to make a new directory";
			log.debug("srmMkdir: {}", msg);
			throw srmAuthorizationFailure(msg);
		}
	}

	private String userToString(GridUserInterface user) {

		return isAnonymous(user) ? "anonymous" : user.getDn();
	}

	private GridUserInterface getUser(InputData data) {

		if (data instanceof IdentityInputData) {
			return ((IdentityInputData) data).getUser();
		}
		return null;
	}

	private void increaseUsedSpaceInfo(LocalFile dir) throws NamespaceException {

		NamespaceDirector.getNamespace().resolveVFSbyLocalFile(dir).increaseUsedSpace(dir.getSize());
	}

	private void manageAcl(StoRI stori, GridUserInterface user, TReturnStatus returnStatus) throws Exception {

		FilesystemPermission permission;
		if (Configuration.getInstance().getEnableWritePermOnDirectory()) {
			permission = FilesystemPermission.ListTraverseWrite;
		} else {
			permission = FilesystemPermission.ListTraverse;
		}
		if (isAnonymous(user)) {
			manageDefaultACL(stori.getLocalFile(), permission);
			setHttpsServiceAcl(stori.getLocalFile(), permission);
		} else {
			setAcl(user, stori.getLocalFile(), stori.hasJustInTimeACLs(), permission);
			manageDefaultACL(stori.getLocalFile(), permission);
		}
	}

	private void setAcl(GridUserInterface user, LocalFile file, boolean hasJiTACL, FilesystemPermission permission)
			throws Exception {

		/*
		 * Add Acces Control List (ACL) in directory created. ACL allow user to
		 * read-write-list the new directory Call wrapper to set ACL on file created.
		 */
		log.debug("SrmMkdir: Adding ACL for directory '{}' group:g_name:--x", file);

		/*
		 * Set permission on directory In case of local auth source enable also write
		 */
		if (hasJiTACL) {
			// Jit Case: with JiT Model the ACL for directory is not needed.
			return;
		}
		LocalUser localUser = null;
		try {
			localUser = user.getLocalUser();
		} catch (CannotMapUserException e) {
			log.warn("SrmMkdir: ACL setup error. {}", e.getMessage(), e);
			throw new Exception("ACL setup error. Local mapping error.");
		} finally {
			if (localUser == null) {
				log.warn("SrmMkdir: Unable to setting up the ACL. LocalUser is null!");
				throw new Exception("ACL setup error. Invalid local user.");
			}
		}
		AclManagerFS.getInstance().grantGroupPermission(file, localUser, permission);
	}

	private void manageDefaultACL(LocalFile dir, FilesystemPermission permission) throws Exception {

		VirtualFSInterface vfs;
		try {
			vfs = NamespaceDirector.getNamespace().resolveVFSbyLocalFile(dir);
		} catch (NamespaceException e) {
			log.error("srmMkdir: {}", e.getMessage());
			throw new Exception("Default ACL setup error: " + e.getMessage());
		}
		DefaultACL dacl = vfs.getCapabilities().getDefaultACL();
		if ((dacl == null) || (dacl.isEmpty())) {
			log.debug("srmMkdir: default acl NULL or empty");
			return;
		}
		for (ACLEntry ace : dacl.getACL()) {
			/*
			 * TODO ATTENTION: here we never set the acl contained in the ACE, we just add
			 * xr or xrw in respect to getEnableWritePermOnDirectory
			 */
			log.debug("Adding DefaultACL for the gid: {} with permission: {}", ace.getGroupID(),
					ace.getFilePermissionString());

			LocalUser user = new LocalUser(ace.getGroupID(), ace.getGroupID());
			AclManagerFS.getInstance().grantGroupPermission(dir, user, permission);
		}
	}

	private void setHttpsServiceAcl(LocalFile file, FilesystemPermission permission) {

		log.debug("SrmMkdir: Adding default ACL for directory {}: {}", file, permission);
		AclManagerFS.getInstance().grantHttpsServiceGroupPermission(file, permission);
	}

	private void printRequestOutcome(TReturnStatus status, MkdirInputData inputData) {

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
