/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.InvalidGetTURLProtocolException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.TURLBuildingException;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtG implements Delegable, Chooser, Request, Suspendedable {

	protected static final String SRM_COMMAND = "srmPrepareToGet";

	private static Logger log = LoggerFactory.getLogger(PtG.class);

	/**
	 * PtGChunkData that holds the specific info for this chunk
	 */
	protected PtGData requestData;

	/**
	 * Time that wil be used in all jit and volatile tracking.
	 */
	protected final Calendar start;

	/**
	 * boolean that indicates the state of the shunk is failure
	 */
	protected boolean failure = false;

	/**
	 * variables used to backup values in case the request is suspended waiting
	 * for the file to be recalled from the tape
	 */
	private StoRI bupFileStori;
	private LocalFile bupLocalFile;
	private LocalUser bupLocalUser;
	private TTURL bupTURL;

	private boolean downgradedToAnonymous = false;

	/**
	 * Constructor requiring the GridUser, the RequestSummaryData and the
	 * PtGChunkData about this chunk. If the supplied attributes are null, an
	 * InvalidPtGChunkAttributesException is thrown.
	 */
	public PtG(PtGData requestData) throws IllegalArgumentException {

		if (requestData == null) {
			throw new IllegalArgumentException(
				"Unable to build the object, invalid arguments: requestData="
					+ requestData);
		}
		this.requestData = requestData;
		start = Calendar.getInstance();
	}

	/**
	 * Method that handles a chunk. It is invoked by the scheduler to carry out
	 * the task.
	 */
	@Override
	public void doIt() {

		log.debug("Handling PtG chunk for user DN: "
			+ DataHelper.getRequestor(requestData) + "; for SURL: "
			+ requestData.getSURL());
		if (!verifySurlStatusTransition(requestData.getSURL(),
			requestData.getRequestToken())) {
			failure = true;
			requestData.changeStatusSRM_FILE_BUSY("Requested file is"
				+ " busy (in an incompatible state with PTG)");
			log.info("Unable to perform the PTG request, surl busy");
			printRequestOutcome(requestData);
			return;
		}

		else {
			/* proceed normally! */
			StoRI fileStoRI = null;
			boolean unapprochableSurl = false;
			try {
				if (!downgradedToAnonymous && requestData instanceof IdentityInputData) {
					try {
						fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(
							requestData.getSURL(),
							((IdentityInputData) requestData).getUser());
					} catch (UnapprochableSurlException e) {
						unapprochableSurl = true;
						log.info("Unable to build a stori for surl "
							+ requestData.getSURL() + " for user "
							+ DataHelper.getRequestor(requestData)
							+ " UnapprochableSurlException: " + e.getMessage());
					}
				} else {
					try {
						fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(
							requestData.getSURL());
					} catch (UnapprochableSurlException e) {
						log.info("Unable to build a stori for surl "
							+ requestData.getSURL() + " UnapprochableSurlException: "
							+ e.getMessage());
						failure = true;
						requestData
							.changeStatusSRM_INVALID_PATH("This surl is not managed by this StoRM instance");
					}
				}
			} catch (IllegalArgumentException e) {
				failure = true;
				requestData
					.changeStatusSRM_INTERNAL_ERROR("Unable to get StoRI for surl "
						+ requestData.getSURL());
				log.error("Unable to get StoRY for surl " + requestData.getSURL()
					+ " IllegalArgumentException: " + e.getMessage());
			}
			if (!failure) {
				AuthzDecision ptgAuthz;
				if (!unapprochableSurl) {
					if (!downgradedToAnonymous
						&& requestData instanceof IdentityInputData) {
						ptgAuthz = AuthzDirector.getPathAuthz().authorize(
							((IdentityInputData) requestData).getUser(), SRMFileRequest.PTG,
							fileStoRI);
					} else {
						ptgAuthz = AuthzDirector.getPathAuthz().authorizeAnonymous(
							SRMFileRequest.PTG, fileStoRI.getStFN());
					}
				} else {
					if (requestData.getTransferProtocols().allows(Protocol.HTTP)) {
						try {
							fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(
								requestData.getSURL());
						} catch (UnapprochableSurlException e) {
							log.info("Unable to build a stori for surl "
								+ requestData.getSURL() + " UnapprochableSurlException: "
								+ e.getMessage());
							failure = true;
							requestData
								.changeStatusSRM_INVALID_PATH("This surl is not managed by this StoRM instance");
						}
						if (!failure) {
							if (fileStoRI.getVirtualFileSystem().isHttpWorldReadable()) {
								this.downgradeToAnonymousHttpRequest();
								ptgAuthz = AuthzDecision.PERMIT;
							} else {
								ptgAuthz = AuthzDecision.DENY;
							}
						} else {
							ptgAuthz = AuthzDecision.INDETERMINATE;
						}
					} else {
						ptgAuthz = AuthzDecision.DENY;
					}
				}
				if (!failure) {
					if (ptgAuthz.equals(AuthzDecision.PERMIT)) {
						manageIsPermit(fileStoRI);
					} else {
						if (ptgAuthz.equals(AuthzDecision.DENY)) {
							manageIsDeny();
						} else {
							if (ptgAuthz.equals(AuthzDecision.INDETERMINATE)) {
								manageIsIndeterminate(ptgAuthz);
							} else {
								manageIsNotApplicabale(ptgAuthz);
							}
						}
					}
				}
			}
		}
		printRequestOutcome(requestData);
	}

	private void downgradeToAnonymousHttpRequest() {

		this.downgradedToAnonymous = true;
		this.requestData.getTransferProtocols().getDesiredProtocols().clear();
		this.requestData.getTransferProtocols().getDesiredProtocols()
			.add(Protocol.HTTP);
	}

	private boolean verifySurlStatusTransition(TSURL surl,
		TRequestToken requestToken) {

		Map<TRequestToken, TReturnStatus> statuses = SurlStatusManager
			.getSurlCurrentStatuses(surl);
		statuses.remove(requestToken);
		return TStatusCode.SRM_FILE_PINNED.isCompatibleWith(statuses.values());
	}

	/**
	 * Manager of the IsPermit state: the user may indeed read the specified SURL
	 * 
	 * @param fileStoRI
	 */
	private void manageIsPermit(StoRI fileStoRI) {

		TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, fileStoRI);
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

		boolean isSpaceAuthorized;
		if (!downgradedToAnonymous && requestData instanceof IdentityInputData) {
			isSpaceAuthorized = spaceAuth.authorize(
				((IdentityInputData) requestData).getUser(), SRMSpaceRequest.PTG);
		} else {
			isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.PTG);
		}
		if (isSpaceAuthorized) {
			try {
				if ((!fileStoRI.getLocalFile().exists())
					|| (fileStoRI.getLocalFile().isDirectory())) {
					/*
					 * File does not exist, or it is a directory! Fail request with
					 * SRM_INVALID_PATH!
					 */
					requestData.changeStatusSRM_INVALID_PATH("The requested file either"
						+ " does not exist, or it is a directory!");
					failure = true;
					log
						.debug("ANOMALY in PtGChunk! PolicyCollector confirms read rights on"
							+ " file, yet file does not exist physically! Or, an srmPrepareToGet"
							+ " was attempted on a directory!");
				} else {
					/* File exists and it is not a directory */
					/* Sets traverse permissions on file parent folders */
					boolean canTraverse;
					try {
						canTraverse = managePermitTraverseStep(fileStoRI);
					} catch (CannotMapUserException e) {
						requestData
							.changeStatusSRM_FAILURE("Unable to find local user for "
								+ DataHelper.getRequestor(requestData));
						failure = true;
						log.error("ERROR in PtGChunk! Unable to find LocalUser for "
							+ DataHelper.getRequestor(requestData)
							+ "! CannotMapUserException: " + e.getMessage());
						return;
					}
					if (canTraverse) {
						TTURL turl;
						try {
							turl = fileStoRI.getTURL(requestData.getTransferProtocols());
						} catch (TURLBuildingException e) {
							requestData
								.changeStatusSRM_FAILURE("Unable to build the TURL for the provided transfer protocol");
							failure = true;
							log
								.error("ERROR in PtGChunk! There was a failure building the TURL. : TURLBuildingException "
									+ e.getMessage());
							return;
						} catch (IllegalArgumentException e) {
							/*
							 * Handle null TURL prefix! This is a programming error: it should
							 * not occur!
							 */
							requestData.changeStatusSRM_FAILURE("Unable to decide TURL!");
							failure = true;
							log
								.error("ERROR in PtGChunk! invalid TURLPrefix in PtGChunkData caused StoRI to be unable to establish TTURL! IllegalArgumentException: "
									+ e.getMessage());
							return;
						} catch (InvalidGetTURLProtocolException e) {
							requestData.changeStatusSRM_FAILURE("Unable to decide TURL!");
							failure = true;
							log
								.error("ERROR in PtGChunk! invalid TURL Protocol in PtGChunkData caused StoRI to be unable to establish TTURL! InvalidGetTURLProtocolException: "
									+ e.getMessage());
							return;
						}
						if (fileStoRI.getVirtualFileSystem().getStorageClassType()
							.isTapeEnabled()) {
							/* Compute the Expiration Time in seconds */
							long expDate = (System.currentTimeMillis() / 1000 + requestData
								.getPinLifeTime().value());
							StormEA.setPinned(fileStoRI.getLocalFile().getAbsolutePath(),
								expDate);

							/* set group permission for tape quota management */
							fileStoRI.setGroupTapeRead();
							try {
								TSizeInBytes fileSize = TSizeInBytes.make(fileStoRI
									.getLocalFile().length(), SizeUnit.BYTES);
								
								requestData.setFileSize(fileSize);
								log.debug("File size: {}", fileSize);
								
							} catch (InvalidTSizeAttributesException e) {
								requestData
									.changeStatusSRM_FAILURE("Unable to determine file size");
								failure = true;
								log
									.error("ERROR in PtGChunk! error in file size computation! InvalidTSizeAttributesException: "
										+ e.getMessage());
								return;
							}
						}
						boolean isOnDisk;
						try {
							isOnDisk = isStoriOndisk(fileStoRI);
						} catch (FSException e) {
							requestData
								.changeStatusSRM_FAILURE("Unable to verify file disk status");
							failure = true;
							log
								.error("ERROR in PtGChunk! error in file on disk check! FSException: "
									+ e.getMessage());
							return;
						}
						if (!isOnDisk
							&& fileStoRI.getVirtualFileSystem().getStorageClassType()
								.isTapeEnabled()) {
							requestData.changeStatusSRM_REQUEST_INPROGRESS("Recalling"
								+ " file from tape");
							String voName = null;
							if (!downgradedToAnonymous
								&& requestData instanceof IdentityInputData) {
								if (((IdentityInputData) requestData).getUser() instanceof AbstractGridUser) {
									voName = ((AbstractGridUser) ((IdentityInputData) requestData)
										.getUser()).getVO().getValue();
								}
							}
							try {
								new TapeRecallCatalog().insertTask(this, voName, fileStoRI
									.getLocalFile().getAbsolutePath());
							} catch (DataAccessException e) {
								requestData
									.changeStatusSRM_FAILURE("Unable to request file recall from tape");
								failure = true;
								log
									.error("ERROR in PtGChunk! error in tape recall task insertion! DataAccessException: "
										+ e.getMessage());
								return;
							}
							/* Stores the parameters in this object */
							if (!downgradedToAnonymous
								&& requestData instanceof IdentityInputData) {
								try {
									backupData(fileStoRI, fileStoRI.getLocalFile(),
										((IdentityInputData) requestData).getUser().getLocalUser(),
										turl);
								} catch (CannotMapUserException e) {
									requestData
										.changeStatusSRM_FAILURE("Unable to find local user for "
											+ DataHelper.getRequestor(requestData));
									failure = true;
									log.error("ERROR in PtGChunk! Unable to find LocalUser for "
										+ DataHelper.getRequestor(requestData)
										+ "! CannotMapUserException: " + e.getMessage());
									return;
								}
							} else {
								backupData(fileStoRI, fileStoRI.getLocalFile(), null, turl);
							}

							/*
							 * The request now ends by saving in the DB the IN_PROGRESS status
							 * information. The effective PtG will be accomplished when the
							 * setTaskStatus() method of the tapeRecallDAO calls the
							 * completeRequest() method.
							 */
						} else {
							/*
							 * Set the read permission for the user on the localfile and any
							 * default ace specified in the story files
							 */
							boolean canRead;
							try {
								canRead = managePermitReadFileStep(fileStoRI, turl);
							} catch (CannotMapUserException e) {
								requestData
									.changeStatusSRM_FAILURE("Unable to find local user for "
										+ DataHelper.getRequestor(requestData));
								failure = true;
								log.error("ERROR in PtGChunk! Unable to find LocalUser for "
									+ DataHelper.getRequestor(requestData)
									+ "! CannotMapUserException: " + e.getMessage());
								return;
							}
							if (canRead) {
								
								try {
									TSizeInBytes fileSize = TSizeInBytes.make(fileStoRI
										.getLocalFile().length(), SizeUnit.BYTES);
									
									requestData.setFileSize(fileSize);
									log.debug("File size: {}", fileSize);
									
								} catch (InvalidTSizeAttributesException e) {
									requestData
										.changeStatusSRM_FAILURE("Unable to determine file size");
									failure = true;
									log
										.error("ERROR in PtGChunk! error in file size computation! InvalidTSizeAttributesException: "
											+ e.getMessage());
									return;
								}
								
								requestData.setTransferURL(turl);
								requestData
									.changeStatusSRM_FILE_PINNED("srmPrepareToGet successfully handled!");
							} else {
								requestData
									.changeStatusSRM_FAILURE("Local filesystem mask does not allow"
										+ " setting up correct ACLs for PtG!");
							}
						}
					} else {
						// FIXME roll back Read, and Traverse URGENT!
					}
				}
			} catch (SecurityException e) {
				/*
				 * The check for existence of the File failed because there is a
				 * SecurityManager installed that denies read privileges for that File!
				 * Perhaps the local system administrator of StoRM set up Java policies
				 * that contrast policies described by the PolicyCollector! There is a
				 * conflict here!
				 */
				requestData
					.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
				failure = true;
				log
					.error("ATTENTION in PtGChunk! PtGChunk received a SecurityException from Java SecurityManager; StoRM cannot check-existence or check-if-directory for: "
						+ fileStoRI.getLocalFile().toString() + "; exception: " + e);
			}
		} else {
			requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to "
				+ requestData.getSURL() + " in Storage Area: " + token + " denied!");

			failure = true;
			log.debug("Read access to " + requestData.getSURL()
				+ " in Storage Area: " + token + " denied!");
		}
	}

	/**
	 * Private method used to setup the right traverse permissions. Returns false
	 * if something goes wrong!
	 * 
	 * @param fileStoRI
	 * 
	 * @param fileStoRI
	 * @param localUser
	 * @return
	 * @throws CannotMapUserException
	 */

	private boolean managePermitTraverseStep(StoRI fileStoRI)
		throws CannotMapUserException {

		if (!downgradedToAnonymous && requestData instanceof IdentityInputData) {
			return verifyPath(fileStoRI)
				&& setParentsAcl(fileStoRI, ((IdentityInputData) requestData).getUser()
					.getLocalUser());
		} else {
			if (verifyPath(fileStoRI)) {
				setHttpsServiceParentAcl(fileStoRI);
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean verifyPath(StoRI fileStoRI) {

		boolean exists, isDir;
		for (StoRI parentStoRI : fileStoRI.getParents()) {
			exists = parentStoRI.getLocalFile().exists();
			isDir = parentStoRI.getLocalFile().isDirectory();
			if (!exists || !isDir) {
				String errorString = "The requested SURL is: "
					+ fileStoRI.getSURL().toString() + ", but its parent "
					+ parentStoRI.getSURL().toString();
				if (!exists) {
					errorString = errorString + " does not exist!";
				} else {
					errorString = errorString + "is not a directory!";
				}
				requestData.changeStatusSRM_INVALID_PATH(errorString);
				failure = true;
				log.error(errorString + " Parent points to "
					+ parentStoRI.getLocalFile().toString() + ".");
				return false;
			}
		}
		return true;
	}

	private boolean setParentsAcl(StoRI fileStoRI, LocalUser localUser) {

		log.debug("Adding parent user ACL for directory : '"
			+ fileStoRI.getAbsolutePath() + "' parents");
		for (StoRI parentStoRI : fileStoRI.getParents()) {
			try {
				if (!setAcl(parentStoRI, localUser, FilesystemPermission.Traverse,
					fileStoRI.hasJustInTimeACLs())) {
					requestData.changeStatusSRM_FAILURE("Local filesystem mask does "
						+ "not allow setting up correct ACLs for PtG!");
					failure = true;
					return false;
				}
			} catch (Exception e) {
				requestData.changeStatusSRM_FAILURE("Local filesystem has"
					+ " problems manipulating ACE!");
				failure = true;
				return false;
			}
		}
		return true;
	}

	private boolean managePermitReadFileStep(StoRI fileStoRI, TTURL turl)
		throws CannotMapUserException {

		if (!downgradedToAnonymous && requestData instanceof IdentityInputData) {
			if (managePermitReadFileStep(fileStoRI, fileStoRI.getLocalFile(),
				((IdentityInputData) requestData).getUser().getLocalUser(), turl)) {
				setDefaultAcl(fileStoRI, fileStoRI.getLocalFile());
				return true;
			} else {
				return false;
			}
		} else {
			setDefaultAcl(fileStoRI, fileStoRI.getLocalFile());
			setHttpsServiceAcl(fileStoRI.getLocalFile(), FilesystemPermission.Read);
			return true;
		}
	}

	/**
	 * Private method used to set Read Permission on existing file. Returns false
	 * if something goes wrong
	 * 
	 * @param fileStoRI
	 * @param localFile
	 * @param localUser
	 * @param turl
	 * @return
	 */
	private boolean managePermitReadFileStep(StoRI fileStoRI,
		LocalFile localFile, LocalUser localUser, TTURL turl) {

		try {
			if (!setAcl(fileStoRI, localUser, FilesystemPermission.Read,
				fileStoRI.hasJustInTimeACLs())) {
				requestData.changeStatusSRM_FAILURE("Local filesystem mask does "
					+ "not allow setting up correct ACLs for PtG!");
				failure = true;
				return false;
			}
		} catch (Exception e) {
			requestData.changeStatusSRM_INTERNAL_ERROR("Local filesystem has"
				+ " problems manipulating ACE!");
			failure = true;
			return false;
		}

		log
			.debug("PTG CHUNK DAO. Addition of Read ACL on file successfully completed.");
		return true;
	}

	private boolean setAcl(StoRI parentStoRI, LocalUser localUser,
		FilesystemPermission permission, boolean hasJustInTimeACLs)
		throws Exception {

		if (hasJustInTimeACLs) {
			return setJiTAcl(parentStoRI, localUser, permission);
		} else {
			return setAoTAcl(parentStoRI, localUser, permission);
		}
	}

	private boolean setJiTAcl(StoRI fileStori, LocalUser localUser,
		FilesystemPermission permission) throws Exception {

		log.debug("Adding JiT ACL " + permission + " to user "
			+ localUser + " for directory : '" + fileStori.getAbsolutePath() + "'");
		try {
			AclManagerFSAndHTTPS.getInstance().grantUserPermission(
				fileStori.getLocalFile(), localUser, permission);
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: "
					+ e.getMessage());
			return false;
		}
		boolean response;

		FilesystemPermission fp = fileStori.getLocalFile()
			.getEffectiveUserPermission(localUser);
		if (fp != null) {
			if (fp.allows(permission)) {
				VolatileAndJiTCatalog.getInstance().trackJiT(fileStori.getPFN(),
					localUser, permission, start, requestData.getPinLifeTime());
				response = true;
			} else {
				log.error("ATTENTION in PtGChunk! The local filesystem has"
					+ " a mask that does not allow " + permission + " User-ACL to "
					+ "be set up on" + fileStori.getLocalFile().toString() + "!");
				response = false;
			}
		} else {
			log.error("ERROR in PTGChunk! A " + permission + " User-ACL was set on "
				+ fileStori.getAbsolutePath() + " for user " + localUser.toString()
				+ " but when subsequently verifying its effectivity,"
				+ " a null ACE was found!");
			throw new Exception("Unable to verify user ACL");
		}
		return response;
	}

	private boolean setAoTAcl(StoRI fileStori, LocalUser localUser,
		FilesystemPermission permission) throws Exception {

		log.debug("Adding AoT ACL " + permission + " to user "
			+ localUser + " for directory : '" + fileStori.getAbsolutePath() + "'");
		try {
			AclManagerFSAndHTTPS.getInstance().grantGroupPermission(
				fileStori.getLocalFile(), localUser, permission);
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: "
					+ e.getMessage());
			return false;
		}
		boolean response;

		FilesystemPermission fp = fileStori.getLocalFile()
			.getEffectiveGroupPermission(localUser);
		if (fp != null) {
			if (fp.allows(permission)) {
				response = true;
			} else {
				log.error("ATTENTION in PtGChunk! The local filesystem has a mask"
					+ " that does not allow Traverse Group-ACL to be set up on"
					+ fileStori.getLocalFile().toString() + "!");
				response = false;
			}
		} else {
			log.error("ERROR in PtGChunk! A Traverse Group-ACL was set on "
				+ fileStori.getAbsolutePath() + " for user " + localUser.toString()
				+ " but when subsequently verifying its effectivity, "
				+ "a null ACE was found!");
			response = false;
		}
		return response;
	}

	private void setHttpsServiceParentAcl(StoRI fileStoRI) {

		log.debug("Adding parent https ACL for directory : '"
			+ fileStoRI.getAbsolutePath() + "' parents");
		for (StoRI parentStoRI : fileStoRI.getParents()) {
			setHttpsServiceAcl(parentStoRI.getLocalFile(),
				FilesystemPermission.Traverse);
		}
	}

	private void setHttpsServiceAcl(LocalFile file,
		FilesystemPermission permission) {

		log.debug("Adding https ACL " + permission + "for directory : '"
			+ file + "'");
		try {
			AclManagerFSAndHTTPS.getInstance().grantHttpsServiceGroupPermission(file,
				permission);
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to grant user permission on the created folder. IllegalArgumentException: "
					+ e.getMessage());
			requestData.getStatus().extendExplaination(
				"Unable to grant group permission on the created folder");
		}
	}

	private void setDefaultAcl(StoRI fileStoRI, LocalFile localFile) {

		/* Manage DefaultACL */
		VirtualFSInterface vfs = fileStoRI.getVirtualFileSystem();
		DefaultACL acl = vfs.getCapabilities().getDefaultACL();
		if ((acl != null) && (!acl.isEmpty())) {
			/* There are ACLs to set n file */
			List<ACLEntry> aclList = acl.getACL();
			for (ACLEntry ace : aclList) {
				/* Re-Check if the ACE is yet valid */
				if (ace.isValid()) {
					log.debug("Adding DefaultACL for the gid: " + ace.getGroupID()
						+ " with permission: " + ace.getFilePermissionString());
					LocalUser u = new LocalUser(ace.getGroupID(), ace.getGroupID());
					if (ace.getFilesystemPermission() == null) {
						log
							.warn("Unable to setting up the ACL. ACl entry permission is null!");
					} else {
						try {
							AclManagerFSAndHTTPS.getInstance().grantGroupPermission(
								localFile, u, ace.getFilesystemPermission());

						} catch (IllegalArgumentException e) {
							log
								.error("Unable to grant group permissions on the file. IllegalArgumentException: "
									+ e.getMessage());
						}
					}
				}
			}
		}

	}

	/**
	 * @param fileStoRI
	 * @param localFile
	 * @param localUser
	 * @param turl
	 */
	private void backupData(StoRI fileStoRI, LocalFile localFile,
		LocalUser localUser, TTURL turl) {

		bupFileStori = fileStoRI;
		bupLocalFile = localFile;
		bupLocalUser = localUser;
		bupTURL = turl;
	}

	/**
	 * @param storiFile
	 * @return
	 * @throws FSException
	 */
	private boolean isStoriOndisk(StoRI storiFile) throws FSException {

		if (!storiFile.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {
			return true;
		} else {
			return storiFile.getLocalFile().isOnDisk();
		}
	}

	/**
	 * Method that supplies a String describing this PtGChunk - for scheduler Log
	 * purposes! It returns the request token and the SURL that was asked for.
	 */
	@Override
	public String getName() {

		return "PtGChunk for SURL " + requestData.getSURL();
	}

	/**
	 * Method used in a callback fashion in the scheduler for separately handling
	 * PtG, PtP and Copy chunks.
	 */
	@Override
	public void choose(Streets s) {

		s.ptgStreet(this);
	}

	@Override
	public Boolean completeRequest(TapeRecallStatus recallStatus) {

		Boolean success = false;
		if (recallStatus == TapeRecallStatus.SUCCESS) {
			try {
				if (bupLocalFile.isOnDisk()) {
					success = managePermitReadFileStep(bupFileStori, bupLocalFile,
						bupLocalUser, bupTURL);
					if (bupLocalUser != null) {
						success = managePermitReadFileStep(bupFileStori, bupLocalFile,
							bupLocalUser, bupTURL);
					} else {
						success = true;
						setDefaultAcl(bupFileStori, bupLocalFile);
					}
					if (success) {
						requestData.setTransferURL(bupTURL);
						requestData
							.changeStatusSRM_FILE_PINNED("srmPrepareToGet successfully handled!");
					} else {
						requestData
							.changeStatusSRM_FAILURE("Local filesystem mask does not allow"
								+ " setting up correct ACLs for PtG!");
					}
				} else {
					log.error("File " + bupLocalFile.getAbsolutePath()
						+ " not found on the disk, but it was reported to"
						+ " be successfully recalled from tape");
					requestData.changeStatusSRM_FAILURE("Error recalling file from tape");
				}
			} catch (FSException e) {
				log.error("Unable to determine if file "
					+ bupLocalFile.getAbsolutePath() + " is on disk . FSException : "
					+ e.getMessage());
				requestData
					.changeStatusSRM_FAILURE("Internal error: unable to determine if the file is on disk");
			}
		} else {
			if (recallStatus == TapeRecallStatus.ABORTED) {
				requestData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
			} else {
				requestData.changeStatusSRM_FAILURE("Error recalling file from tape");
			}
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.asynch.Suspendedable#getRequestData()
	 */
	@Override
	public PtGData getRequestData() {

		return requestData;
	}

	@Override
	public String getSURL() {

		return requestData.getSURL().toString();
	}

	@Override
	public String getUserDN() {

		return DataHelper.getRequestor(requestData);
	}

	/**
	 * @return
	 */
	@Override
	public boolean isResultSuccess() {

		boolean result = false;
		TStatusCode statusCode = requestData.getStatus().getStatusCode();
		if ((statusCode.getValue().equals(TStatusCode.SRM_FILE_PINNED.getValue()))
			|| requestData.getStatus().isSRM_SUCCESS()) {
			result = true;
		}
		return result;
	}

	/**
	 * Manager of the IsDeny state: it indicates that Permission is not granted.
	 */
	private void manageIsDeny() {

		requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to "
			+ requestData.getSURL() + " denied!");
		failure = true;
		log.debug("Read access to " + requestData.getSURL() + " denied!");
	}

	/**
	 * Manager of the IsIndeterminate state: this state indicates that an error in
	 * the PolicySource occured and so the policy Collector does not know what do
	 * to!
	 */
	private void manageIsIndeterminate(AuthzDecision ad) {

		requestData.changeStatusSRM_FAILURE("Failure in PolicySource prevented"
			+ " PolicyCollector from establishing access rights! Processing failed!");
		failure = true;
		log
			.error("ERROR in PtGChunk! PolicyCollector received an error from PolicySource!");
		log.error("Received state: " + ad);
		log.error("Requested SURL: " + requestData.getSURL());
	}

	/**
	 * Manager of the IsNotApplicable state: this state indicates that the
	 * PolicyCollector has not got any info on the requested file, so it does not
	 * know what to answer.
	 */
	private void manageIsNotApplicabale(AuthzDecision ad) {

		requestData
			.changeStatusSRM_FAILURE("No policies found for the requested SURL! "
				+ "Therefore access rights cannot be established! Processing cannot continue!");
		failure = true;
		log
			.warn("PtGChunk: PolicyCollector found no policy for the supplied SURL!");
		log.warn("Received state: " + ad);
		log.warn("Requested SURL: " + requestData.getSURL());
	}

	protected void printRequestOutcome(PtGData inputData) {

		if (inputData != null) {
			if (inputData.getSURL() != null) {
				if (inputData.getRequestToken() != null) {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData, inputData.getRequestToken(),
						Arrays.asList(inputData.getSURL().toString()));
				} else {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData,
						Arrays.asList(inputData.getSURL().toString()));
				}

			} else {
				if (inputData.getRequestToken() != null) {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData, inputData.getRequestToken());
				} else {
					CommandHelper.printRequestOutcome(SRM_COMMAND, log,
						inputData.getStatus(), inputData);
				}
			}

		} else {
			CommandHelper.printRequestOutcome(SRM_COMMAND, log, CommandHelper
				.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "No input available"));
		}
	}

}
