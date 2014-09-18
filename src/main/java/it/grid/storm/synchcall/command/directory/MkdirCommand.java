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

import it.grid.storm.acl.AclManagerFSAndHTTPS;
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
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class MkdirCommand extends DirectoryCommand implements Command {

	class MkdirException extends Exception {

		private static final long serialVersionUID = 1L;
		
		private TReturnStatus returnStatus;
		
		public MkdirException(TStatusCode code, String message) {
			super(message);
			this.returnStatus = CommandHelper.buildStatus(code, message);
		}
		
		public TReturnStatus getReturnStatus() {
			return returnStatus;
		}
	}
	
  public static final Logger log = LoggerFactory.getLogger(MkdirCommand.class);
  
	private static final String SRM_COMMAND = "SrmMkdir";
	private final NamespaceInterface namespace;

	public MkdirCommand() {

		namespace = NamespaceDirector.getNamespace();
	}

	private void validate(InputData data) throws IllegalArgumentException, 
		MkdirException {
		
		if (data == null) {
			throw new IllegalArgumentException("Invalid input data: NULL");
		}
		if (!(data instanceof MkdirInputData)) {
			throw new IllegalArgumentException("Invalid input data type");
		}
		if (((MkdirInputData) data).getSurl() == null) {
			throw new MkdirException(TStatusCode.SRM_FAILURE,
				"SURL specified is NULL");
		}
		if (((MkdirInputData) data).getSurl().isEmpty()) {
			throw new MkdirException(TStatusCode.SRM_FAILURE,
				"Invalid empty SURL specified");
		}
	}
	
	private MkdirOutputData exitWithStatus(TReturnStatus returnStatus,
		MkdirInputData data) {
		
		printRequestOutcome(returnStatus, data);
		return new MkdirOutputData(returnStatus);
	}
	
	private StoRI resolveStoRI(TSURL surl, GridUserInterface user)
		throws MkdirException {

		try {
			return namespace.resolveStoRIbySURL(surl, user);
		} catch (UnapprochableSurlException e) {
			log.error(e.getMessage());
			throw new MkdirException(TStatusCode.SRM_AUTHORIZATION_FAILURE,
				e.getMessage());
		}	catch (NamespaceException e) {
			log.error(e.getMessage());
			throw new MkdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}	catch (InvalidSURLException e) {
			log.error(e.getMessage());
			throw new MkdirException(TStatusCode.SRM_INVALID_PATH, e.getMessage());
		}	catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw new MkdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}
	}
	
	private boolean isAnonymous(GridUserInterface user) {
		
		return (user == null);
	}
	
	private boolean isUserAuthorized(StoRI stori, GridUserInterface user)
		throws MkdirException {
		
		TSpaceToken token;
		try {
			token = stori.getVirtualFileSystem().getSpaceToken();
		} catch (NamespaceException e) {
			log.error(e.getMessage());
			throw new MkdirException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
		}
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

		boolean isSpaceAuthorized;
		if (isAnonymous(user)) {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.MD);
		} else {
			isSpaceAuthorized = spaceAuth.authorize(user, SRMSpaceRequest.MD);
		}
		if (!isSpaceAuthorized) {
			log.debug("srmMkdir: User not authorized to perform srmMkdir request "
			  + "on the storage area: {}", token);
			return false;
		}

		AuthzDecision decision;
		if (isAnonymous(user)) {
			decision = AuthzDirector.getPathAuthz().authorizeAnonymous(
				SRMFileRequest.MD, stori.getStFN());
		} else {			
			decision = AuthzDirector.getPathAuthz().authorize(user,
				SRMFileRequest.MD, stori);
		}
		if (!decision.equals(AuthzDecision.PERMIT)) {
			log.debug("srmMkdir: User is not authorized to make a new directory");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Method that provide SrmMkdir functionality.
	 * 
	 * @param inputData
	 *          Contains information about input data for Mkdir request.
	 * @return TReturnStatus Contains output data
	 */
	public OutputData execute(InputData data) {

		log.debug("SrmMkdir: Start execution.");
		
		try {
			validate(data);
		} catch (MkdirException e) {
			log.error("srmRmdir: {}", e.getMessage());
			return exitWithStatus(e.getReturnStatus(), (MkdirInputData) data);
		}
		
		MkdirInputData inputData = (MkdirInputData) data;
		TSURL surl = inputData.getSurl();
		GridUserInterface user = (data instanceof IdentityInputData) ? 
			((IdentityInputData) data).getUser() : null;
		StoRI stori = null;

		try {
			stori = resolveStoRI(surl, user);
		} catch (MkdirException e) {
			log.error("Unable to build a stori for surl {} for user {}: {}",
		    surl, DataHelper.getRequestor(inputData), e.getMessage());
			return exitWithStatus(e.getReturnStatus(), inputData);
		}

		boolean isAuthorized = false;
		try {
			isAuthorized = isUserAuthorized(stori, user);
		} catch (MkdirException e) {
			log.error(e.getMessage());
			return exitWithStatus(e.getReturnStatus(), inputData);
		}
		if (!isAuthorized) {
			log.debug("srmMkdir not authorized for {} for directory = {}", 
			  DataHelper.getRequestor(inputData), stori.getPFN());
			return exitWithStatus(CommandHelper.buildStatus(
				TStatusCode.SRM_AUTHORIZATION_FAILURE,
				"User is not authorized to make a new directory"), inputData);
		}
		
		log.debug("srmMkdir authorized for {} for directory = {}",
			DataHelper.getRequestor(inputData), stori.getPFN());

		TReturnStatus returnStatus = manageAuthorizedMKDIR(stori, data);
		log.debug("srmMkdir return status: {}", returnStatus);
		return exitWithStatus(returnStatus, inputData);
	}

	/**
	 * Split PFN , recursive creation is not supported, as reported at page 16 of
	 * Srm v2.1 spec.
	 * 
	 * @param stori
	 * 
	 * @param user
	 *          VomsGridUser
	 * @param stori
	 *          StoRI
	 * @param data
	 * @return TReturnStatus
	 */
	private TReturnStatus manageAuthorizedMKDIR(StoRI stori, InputData data) {

		TReturnStatus returnStatus = createFolder(stori.getLocalFile());
		if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
			updateUsedSpace(stori);
			manageAcl(stori, data, returnStatus);
		}
		return returnStatus;
	}

	private void updateUsedSpace(StoRI stori) {

		SpaceUpdaterHelperInterface sh = SpaceUpdaterHelperFactory
			.getSpaceUpdaterHelper(stori.getVirtualFileSystem());		
		sh.increaseUsedSpace(stori.getVirtualFileSystem(), stori.getLocalFile()
			.getSize());
	}

	private TReturnStatus createFolder(LocalFile file) {

		LocalFile parent = file.getParentFile();
		if (parent != null) {
			log.debug("Mkdir : Parent of '{}' exists.", file);
			if (!file.exists()) {
				if (file.mkdir()) {
					log.debug("SrmMkdir: Request success!");
					return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
						"Directory created with success");
				} else {
					if (file.exists()) { /*
																 * Race condition, the directory has been
																 * created by another mkdir or a PtP running
																 * concurrently.
																 */
						log
							.debug("SrmMkdir: Request fails because it specifies an existent file or directory.");
						return CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR,
							"The given SURL identifies an existing file or directory");
					} else {
						log.debug("SrmMkdir: Request fails because the path is invalid.");
						return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
							"Invalid path");
					}
				}
			} else {
				if (file.isDirectory()) {
					log
						.debug("SrmMkdir: Request fails because it specifies an existent directory.");
					return CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR,
						"Directory specified exists!");
				} else {
					log
						.debug("SrmMkdir: Request fails because the path specified is an existent file.");
					return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
						"Path specified exists as a file");
				}
			}
		} else {
			log
				.debug("SrmMkdir: Request fails because it specifies an invalid parent directory.");
			return CommandHelper
				.buildStatus(TStatusCode.SRM_INVALID_PATH,
					"Parent directory does not exists. Recursive directory creation Not Allowed");
		}
	}

	private void manageAcl(StoRI stori, InputData inputData,
		TReturnStatus returnStatus) {

		FilesystemPermission permission;
		if (Configuration.getInstance().getEnableWritePermOnDirectory()) {
			permission = FilesystemPermission.ListTraverseWrite;
		} else {
			permission = FilesystemPermission.ListTraverse;
		}
		if (inputData instanceof IdentityInputData) {
			setAcl(((IdentityInputData) inputData).getUser(), stori.getLocalFile(),
				stori.hasJustInTimeACLs(), permission, returnStatus);
			manageDefaultACL(stori, permission, returnStatus);
		} else {
			manageDefaultACL(stori, permission, returnStatus);
			setHttpsServiceAcl(stori.getLocalFile(), permission, returnStatus);
		}
	}

	private void setAcl(GridUserInterface user, LocalFile file,
		boolean hasJiTACL, FilesystemPermission permission,
		TReturnStatus returnStatus) {

		/*
		 * Add Acces Control List (ACL) in directory created. ACL allow user to
		 * read-write-list the new directory Call wrapper to set ACL on file
		 * created.
		 */
		log.debug("SrmMkdir: Adding ACL for directory '{}' group:g_name:--x", file);

		/*
		 * Set permission on directory In case of local auth source enable also
		 * write
		 */
		if (hasJiTACL) {
			// Jit Case
			// With JiT Model the ACL for directory is not needed.
		} else {
			try {
				if (user.getLocalUser() == null) {
					log
						.warn("SrmMkdir: Unable to setting up the ACL. LocalUser is null!");
					returnStatus.extendExplaination("ACL setup error. Invalid local user.");
				} else {
					try {
						AclManagerFSAndHTTPS.getInstance().grantGroupPermission(file,
							user.getLocalUser(), permission);
					} catch (IllegalArgumentException e) {
						log.error("Unable to grant user permission on folder. {}", 
						  e.getMessage(),e);
						returnStatus
							.extendExplaination("Unable to grant group permission on the created folder");
					}
				}
			} catch (CannotMapUserException e) {
				log
					.warn("SrmMkdir: ACL setup error. {}", e.getMessage(),e);
				returnStatus.extendExplaination("ACL setup error. Local mapping error.");
			}
		}
	}

	private void manageDefaultACL(StoRI stori, FilesystemPermission permission,
		TReturnStatus returnStatus) {

		VirtualFSInterface vfs = stori.getVirtualFileSystem();
		DefaultACL dacl = vfs.getCapabilities().getDefaultACL();
		if ((dacl != null) && (!dacl.isEmpty())) {
			for (ACLEntry ace : dacl.getACL()) {
				/*
				 * TODO ATTENTION: here we never set the acl contained in the ACE, we
				 * just add xr or xrw in respect to getEnableWritePermOnDirectory
				 */
				log.debug("Adding DefaultACL for the gid: {} with permission: {}",
				  ace.getGroupID(),  ace.getFilePermissionString());

				LocalUser user = new LocalUser(ace.getGroupID(), ace.getGroupID());
				try {
					AclManagerFSAndHTTPS.getInstance().grantGroupPermission(
						stori.getLocalFile(), user, permission);
				} catch (IllegalArgumentException e) {
					log
						.error("Unable to grant group permission on folder to user {}. {}",
						  user, e.getMessage(), e);
					returnStatus.extendExplaination("Default ACL setup error.");
				}
			}
		}
	}

	private void setHttpsServiceAcl(LocalFile file,
		FilesystemPermission permission, TReturnStatus returnStatus) {

		log.debug("SrmMkdir: Adding default ACL for directory {}: {}", 
		  file,
		  permission);

		try {
			AclManagerFSAndHTTPS.getInstance().grantHttpsServiceGroupPermission(file,
				permission);
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to grant user permission on folder. {}",
				  e.getMessage(), e);

			returnStatus
				.extendExplaination("Unable to grant group permission on the created folder");
		}
	}

	private void printRequestOutcome(TReturnStatus status,
		MkdirInputData inputData) {

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
