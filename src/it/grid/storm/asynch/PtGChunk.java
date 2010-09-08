package it.grid.storm.asynch;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.InvalidPathException;
import it.grid.storm.filesystem.InvalidPermissionOnFileException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.WrongFilesystemType;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.namespace.InvalidGetTURLNullPrefixAttributeException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a chunk of an srmPrepareToGet request: it handles a single file of a
 * multifile/directory-expansion request. StoRM then sends the chunk to a chunk-scheduler. Security checks performed as
 * follows: both in the JiT and AoT approach, policies are checked to see if the Griduser has read rights on the
 * requested SURL. If the AuthorisationCollector replies with an isDeny, then the request fails with
 * SRM_AUTHORIZATION_FAILURE status. If the AuthorisationCollector replies with isIndeterminate, then the request fails
 * with SRM_FAILURE and explanation string "Failure in PolicySource prevented PolicyCollector from establishing access
 * rights! Processing failed!"; a messagge gets logged as well. If the AuthorisationCollector replies with
 * isNotApplicabale, then the request fails with SRM_FAILURE and error string "No policies found for the requested SURL!
 * Therefore access rights cannot be established! Processing cannot continue!"; a messagge gets logged as well. If the
 * AuthorisationCollector returns a state for which there is no business logic, then the request fails again with
 * SRM_FAILURE and error string "Unexpected authorization state! Processing failed!"; a messagge gets logged. If the
 * AuthorisationCollector returns an isPermit, then processing continues as follows: (1) The local file that corresponds
 * to the supplied SURL is determined, together with the local user to whom the grid credentials get mapped; the TURL
 * finally gets constructed. If the local file does not exist the request fails with SRM_INVALID_PATH and corresponding
 * explanation string; if the user cannot be mapped locally, the request fails with SRM_FAILURE and an explanation
 * String which includes the DN used for maping; if there are internal problems constructing the TURL again the request
 * fails with SRM_FAILURE. Appropriate error messagges get logged. (2) Traverse permissions get set on all parent
 * directories to allow access to the file. The operation may fail for several reasons: the file or any of the parent
 * directories may have been removed resulting in SRM_INVALID_PATH; StoRM cannot set the requested permissions because a
 * filesystem mask does not allow the permissions to be set up; StoRM may be configured for the wrong filesystem; StoRM
 * has not got the right permissions to manipulate the ACLs on the filesystem; StoRM may have encountered an unexpected
 * error when working with the filesystem. In all these circumstances, the status changes to SRM_FAILURE, together with
 * an appropriate explanation String, and a respective log messagge. (3) The file size is determined. The operation may
 * fail and hence the request too gets failed, in the following circumstances: the file somehow does not exist, the path
 * to the file is not found, an error while communicating with the underlaying FileSystem, or a JVM SecurityManager
 * forbids such operation. In the first two cases the state changes to SRM_INVALID_PATH, while in the other ones it
 * changes to SRM_FAILURE; proper error strings explain the situation further. Error messagges get logged. (3) If AoT
 * acls are in place, then the PinnedFilesCatalog is asked to pinExistingVolatileEntry, that is, it is asked to pin the
 * entry if it is already present thereby extending its lifetime (if it is not present, it just means that the requested
 * file is PERMANENT and there is no need to pin it); status changes to SRM_FILE_PINNED. (4) If it is the JiT that is in
 * place, a ReadACL is first added, the ACL gets tracked by calling the proper method in the PinnedFilesCatalog, and the
 * file gets pinned by invoking pinExistingVolatileEntry; the status changes to SRM_FILE_PINNED. The addition of the ACL
 * could go wrong in several ways: the file may not exist, the ACL could not be set up because there is a mask that does
 * not allow the Read permission to be set, StoRM does not have the permission to manipulate the ACLs, StoRM was not
 * configured for the underlying FileSystem, or there was an unexpected error; in the first case the status changes to
 * SRM_INVALID_PATH, while in all other ones it changes to SRM_FAILURE; corresponding messagges get logged.
 * 
 * @author EGRID - ICTP Trieste
 * @date May 3rd, 2005
 * @version 4.0
 */
public class PtGChunk implements Delegable, Chooser, SuspendedChunk {

    private static Logger log = LoggerFactory.getLogger(PtGChunk.class);

    /** GridUser that made the request */
    private GridUserInterface gu = null;
    /** RequestSummaryData containing all the statistics for the originating srmPrepareToGetRequest */
    private RequestSummaryData rsd = null;
    /** PtGChunkData that holds the specific info for this chunk */
    private PtGChunkData chunkData = null;
    /** Calendar used for jit tracking */
    private Calendar start = null;
    /** manager for global status computation */
    private GlobalStatusManager gsm = null;
    /** boolean that indicates a chunk failure */
    private boolean failure = false;

    /**
     * variables used to backup values in case the request is suspended waiting for the file to be recalled from the
     * tape
     */
    private StoRI bupFileStori;
    private LocalFile bupLocalFile;
    private LocalUser bupLocalUser;
    private TTURL bupTURL;

    /**
     * Constructor requiring the GridUser, the RequestSummaryData and the PtGChunkData about this chunk. If the supplied
     * attributes are null, an InvalidPtGChunkAttributesException is thrown.
     */
	public PtGChunk(GridUserInterface gu, RequestSummaryData rsd, PtGChunkData chunkData,
			GlobalStatusManager gsm) throws InvalidPtGChunkAttributesException {

		if(gu == null || rsd == null || chunkData == null || gsm == null)
		{
			throw new InvalidPtGChunkAttributesException(gu, rsd, chunkData, gsm);
		}
		this.gu = gu;
		this.rsd = rsd;
		this.chunkData = chunkData;
		this.gsm = gsm;
		start = Calendar.getInstance();
	}

	  /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
	public void doIt() {

		log.info("Handling PtG chunk for user DN: " + gu.getDn() + "; for SURL: "
			+ chunkData.fromSURL() + "; for requestToken: " + rsd.requestToken());
		if(PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(chunkData.fromSURL()))
		{
			/* fail request with SRM_FILE_BUSY */
			chunkData.changeStatusSRM_FILE_BUSY("Requested file is"
				+ " still in SRM_SPACE_AVAILABLE state!");
			failure = true;
			log.debug("ATTENTION in PtGChunk! PtGChunk received r"
				+ "equest for SURL that is still in SRM_SPACE_AVAILABLE state!");
		}
		else
		{
			/* proceed normally! */
			try
			{
				StoRI fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(
									  chunkData.fromSURL(), gu);
				AuthzDecision ptgAuthz = AuthzDirector.getPathAuthz().authorize(gu,
											 SRMFileRequest.PTG, fileStoRI);

				if(ptgAuthz.equals(AuthzDecision.PERMIT))
				{
					manageIsPermit(fileStoRI);
				}
				else
				{
					if(ptgAuthz.equals(AuthzDecision.DENY))
					{
						manageIsDeny();
					}
					else
					{
						if(ptgAuthz.equals(AuthzDecision.INDETERMINATE))
						{
							manageIsIndeterminate(ptgAuthz);
						}
						else
						{
							manageIsNotApplicabale(ptgAuthz);
						}
					}
				}
			} catch(NamespaceException e)
			{
				/*
				 * The Supplied SURL does not contain a root that could be
				 * identified by the StoRI factory as referring to a VO being
				 * managed by StoRM... that is SURLs begining with such root are
				 * not handled by this SToRM!
				 */
				chunkData.changeStatusSRM_INVALID_PATH("The path specified in the"
					+ "SURL does not have a local equivalent!");
				failure = true;
				log.debug("ATTENTION in PtGChunk! PtGChunk received"
					+ " request for a SURL whose root is not recognised by StoRI! " + e);
			}
		}
		
		/* update status in persistence!!! */
		PtGChunkCatalog.getInstance().update(chunkData);

		/*
		 * If the status is still SRM_REQUEST_INPROGRESS means that the file is
		 * on the tape and it's being recalled. The status will be changed to
		 * SRM_FILE_PINNED by the function that updates the TapeRecall table
		 * putting a SUCCESS in the corresponding row (and the file will have
		 * been recalled).
		 */
		if(chunkData.status().getStatusCode() != TStatusCode.SRM_REQUEST_INPROGRESS)
		{
			if(failure)
			{
				gsm.failedChunk(chunkData);
			}
			else
			{
				/* update global status!!! */
				gsm.successfulChunk(chunkData);
			}
		}
		log.info("Finished handling PtG chunk for user DN: " + gu.getDn() + "; for SURL: "
			+ chunkData.fromSURL() + "; for requestToken: " + rsd.requestToken() + "; result is: "
			+ chunkData.status());
	}
	
	 /**
     * Manager of the IsPermit state: the user may indeed read the specified SURL
     * 
     * @param fileStoRI
     */
	private void manageIsPermit(StoRI fileStoRI) {

		SpaceHelper sp = new SpaceHelper();
		TSpaceToken token = sp.getTokenFromStoRI(log, fileStoRI);
		SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

		if(spaceAuth.authorize(gu, SRMSpaceRequest.PTG))
		{
			LocalFile localFile = fileStoRI.getLocalFile();
			try
			{
				LocalUser localUser = gu.getLocalUser();
				TTURL turl = fileStoRI.getTURL(chunkData.desiredProtocols());
				if((!localFile.exists()) || (localFile.isDirectory()))
				{
					/*
					 * File does not exist, or it is a directory! Fail request
					 * with SRM_INVALID_PATH!
					 */
					chunkData.changeStatusSRM_INVALID_PATH("The requested file either"
						+ " does not exist, or it is a directory!");
					failure = true;
					log.debug("ANOMALY in PtGChunk! PolicyCollector confirms read rights on"
						+ " file, yet file does not exist physically! Or, an srmPrepareToGet"
						+ " was attempted on a directory!");
				}
				else
				{
					/* File exists and it is not a directory */
					/* Sets traverse permissions on file parent folders */
					boolean canTraverse = managePermitTraverseStep(fileStoRI, localUser);
					if(canTraverse)
					{
						if(fileStoRI.getVirtualFileSystem().getStorageClassType().isTapeEnabled())
						{
							/* Compute the Expiration Time in seconds */
							long expDate = (System.currentTimeMillis() / 1000 + chunkData
											   .getPinLifeTime().value());
							StormEA.setPinned(localFile.getAbsolutePath(), expDate);

							/* set group permission for tape quota management */
							fileStoRI.setGroupTapeRead();
							chunkData.setFileSize(TSizeInBytes.make(localFile.length(),
								SizeUnit.BYTES));

							if(isStoriOndisk(fileStoRI))
							{
								/* Set the read permission for the user on the localfile and any default ace specified in the story files*/
								boolean canRead = managePermitReadFileStep(fileStoRI, localFile,
													  localUser, turl);
								if(!canRead)
								{
									// FIXME roll back Read, and Traverse URGENT!
								}
							}
							else
							{
								chunkData.changeStatusSRM_REQUEST_INPROGRESS("Recalling"
									+ " file from tape");
								
								String voName = null;
								if(gu instanceof VomsGridUser)
								{
									voName = ((VomsGridUser) gu).getVO().getValue();
								}

								PersistenceDirector.getDAOFactory().getTapeRecallDAO().insertTask(
									this, voName, localFile.getAbsolutePath());

								/* Stores the parameters in this object */
								backupData(fileStoRI, localFile, localUser, turl);

								/*
								 * The request now ends by saving in the DB the
								 * IN_PROGRESS status information. The effective
								 * PtG will be accomplished when the
								 * setTaskStatus() method of the tapeRecallDAO
								 * calls the completeRequest() method.
								 */
							}
						}
						else
						{
							log.debug("File is on SA without Tape, so no EA will be setted on it.");
							/* Set the read permission for the user on the localfile and any default ace specified in the story files*/
							boolean canRead = managePermitReadFileStep(fileStoRI, localFile,
												  localUser, turl);
							if(!canRead)
							{
								// FIXME roll back Read, and Traverse URGENT!
							}
						}
					}
					else
					{
						// FIXME roll back Read, and Traverse URGENT!
					}
				}
			} catch(SecurityException e)
			{
				/*
				 * The check for existence of the File failed because there is a
				 * SecurityManager installed that denies read privileges for
				 * that File! Perhaps the local system administrator of StoRM
				 * set up Java policies that contrast policies described by the
				 * PolicyCollector! There is a conflict here!
				 */
				chunkData
					.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
				failure = true;
				log
					.error("ATTENTION in PtGChunk! PtGChunk received a SecurityException from Java SecurityManager; StoRM cannot check-existence or check-if-directory for: "
						+ localFile.toString() + "; exception: " + e);
			} catch(CannotMapUserException e)
			{
				/*
				 * StoRM could not get LocalUser corresponding to GridUser! So
				 * ACL cannot be tracked!
				 */
				chunkData.changeStatusSRM_FAILURE("Unable to find local user for " + gu.getDn());
				failure = true;
				log.error("ERROR in PtGChunk! Unable to find LocalUser for " + gu.getDn()
					+ "! GridUser object returned: " + e);
			} catch(InvalidGetTURLNullPrefixAttributeException e)
			{
				/*
				 * Handle null TURL prefix! This is a programming error: it
				 * should not occur!
				 */
				chunkData.changeStatusSRM_FAILURE("Unable to decide TURL!");
				failure = true;
				log
					.error("ERROR in PtGChunk! Null TURLPrefix in PtGChunkData caused StoRI to be unable to establish TTURL! StoRI object returned: "
						+ e);
			} catch(Exception e)
			{
				/*
				 * There could be unexpected runtime errors given the fact that
				 * we have an ACL enabled filesystem! I do not know the
				 * behaviour of Java File class!!!
				 */
				chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
				failure = true;
				log.error("ERROR in PtGChunk! StoRM process got an unexpected error! ", e);
			} catch(Error e)
			{
				/*
				 * This is a temporary measure to catch an arror occurring
				 * because of the use of deprecated method in VomsGridUser! It
				 * happens in exceptional conditions: when a user is mapped to a
				 * specific account instead of a pool account, and the
				 * VomsGridUser class handles the situation through a deprecated
				 * getEnv method!
				 */
				chunkData.changeStatusSRM_FAILURE("Unable to map grid credentials to local user!");
				failure = true;
				log.error("ERROR in PtGChunk! There was a failure in mapping " + gu.getDn()
					+ " to a local user! Error returned: " + e);
			}

		}
		else
		{
			chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to "
				+ chunkData.fromSURL() + " in Storage Area: " + token + " denied!");
			
			failure = true;
			log.debug("Read access to " + chunkData.fromSURL() + " in Storage Area: " + token
				+ " denied!");
		}
	}
	
	/**
     * Private method used to setup the right traverse permissions. Returns false if something goes wrong!
     * 
	 * @param fileStoRI
	 * @param localUser
	 * @return
	 */
	private boolean managePermitTraverseStep(StoRI fileStoRI, LocalUser localUser) {

		/* Set Traverse ACL on parent directories */
		/* ATTENTION!!! For AoT this turns out to be a PERMANENT ACL!!! */

		/* StoRI representing a parent directory */
		StoRI parentStoRI = null;
		/* File representing a parent direCtory */
		LocalFile parentFile = null;
		/* boolean that is true if parentFile exists */
		boolean exists = false;
		/* boolean true if parentFile is a directory */
		boolean dir = false;
		/*
		 * boolean _true_ if the parent just treated, exists and is not a
		 * directory
		 */
		List<StoRI> parentList = fileStoRI.getParents();
		Iterator<StoRI> i = parentList.iterator();
		boolean anomaly = false;
		while((!anomaly) && i.hasNext())
		{
			parentStoRI = i.next();
			parentFile = parentStoRI.getLocalFile();
			exists = parentFile.exists();
			dir = parentFile.isDirectory();
			anomaly = ((!exists) || (!dir));
			if(anomaly)
			{
				/*
				 * error situation! The parent directory either does not exist
				 * or is not a directory! The request should fail!
				 */
				String errorString = "The requested SURL is: " + fileStoRI.getSURL().toString()
									   + ", but its parent " + parentStoRI.getSURL().toString();
				if(!exists)
				{
					errorString = errorString + " does not exist!";
				}
				else
				{
					errorString = errorString + "is not a directory!";
				}
				chunkData.changeStatusSRM_INVALID_PATH(errorString);
				failure = true;
				log.error(errorString + " Parent points to " + parentStoRI.getLocalFile().toString()
					+ ".");
				/* anomaly is already true: no need to restate it. */
			}
			else
			{
				/* true if parent exists and is not a directory! */
				/* process existing parent directory */
				try
				{
					FilesystemPermission fp = null;
					boolean allowsTraverse = false;
					if(fileStoRI.hasJustInTimeACLs())
					{
						// JiT
						parentFile.grantUserPermission(localUser, FilesystemPermission.Traverse);
						fp = parentFile.getEffectiveUserPermission(localUser);
						if(fp != null)
						{
							allowsTraverse = fp.allows(FilesystemPermission.Traverse);
							if(allowsTraverse)
							{
								VolatileAndJiTCatalog.getInstance().trackJiT(parentStoRI.getPFN(),
									localUser, FilesystemPermission.Traverse, start,
									chunkData.getPinLifeTime());
							}
							else
							{
								log.error("ATTENTION in PtGChunk! The local filesystem has"
									+ " a mask that does not allow Traverse User-ACL to "
									+ "be set up on" + parentFile.toString() + "!");
							}
						}
						else
						{
							log.error("ERROR in PTGChunk! A Traverse User-ACL was set on "
								+ fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
								+ " but when subsequently verifying its effectivity,"
								+ " a null ACE was found!");
						}
					}
					else
					{
						// AoT
						parentFile.grantGroupPermission(localUser, FilesystemPermission.Traverse);
						fp = parentFile.getEffectiveGroupPermission(localUser);
						if(fp != null)
						{
							allowsTraverse = fp.allows(FilesystemPermission.Traverse);
							if(!allowsTraverse)
							{
								log.error("ATTENTION in PtGChunk! The local filesystem has a mask"
									+ " that does not allow Traverse Group-ACL to be set up on"
									+ parentFile.toString() + "!");
							}
						}
						else
						{
							log.error("ERROR in PTPChunk! A Traverse Group-ACL was set on "
								+ fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
								+ " but when subsequently verifying its effectivity, "
								+ "a null ACE was found!");
						}
					}
					if(fp == null)
					{
						/*
						 * Problems when manipulating ACEs! Added a traverse
						 * permission but no corresponding ACE was found!
						 * Request fails!
						 */
						chunkData.changeStatusSRM_FAILURE("Local filesystem has"
							+ " problems manipulating ACE!");
						failure = true;
						anomaly = true;
					}
					else
					{
						if(!allowsTraverse)
						{
							/*
							 * mask preset in file system does not allow the
							 * setting up of the required permissions! Request
							 * fails!
							 */
							chunkData.changeStatusSRM_FAILURE("Local filesystem mask does "
								+ "not allow setting up correct ACLs for PtG!");
							failure = true;
							anomaly = true;
						}
						else
						{
							/* Permissions set correctly */
							/* anomaly is already false: no need to restate it! */
						}
					}
				} catch(WrongFilesystemType e)
				{
					/* StoRM configured with wrong Filesystem type! */
					chunkData.changeStatusSRM_FAILURE("StoRM configured for wrong filesystem!");
					failure = true;
					log.error("ERROR in PtGChunk! StoRM is not configured"
						+ " for underlying fileystem! " + e);
					anomaly = true;
				} catch(InvalidPermissionOnFileException e)
				{
					/*
					 * StoRM cannot carry out file manipulation because it lacks
					 * enough privileges!
					 */
					chunkData.changeStatusSRM_FAILURE("StoRM cannot manipulate directory!");
					failure = true;
					log.error("ERROR in PtGChunk! StoRM process has"
						+ " not got enough privileges to work on: " + parentFile.toString()
						+ "; exception: " + e);
					anomaly = true;
				} catch(InvalidPathException e)
				{
					/* Could not set ACL because file does not exist! */
					chunkData.changeStatusSRM_FAILURE("Unable to setup ACL!");
					failure = true;
					log.error("ERROR in PtGChunk! The directory on which to "
						+ "set up the Trasverse ACL does not exist!");
					log.error("ERROR in PtGChunk! This should not have happenned "
						+ "because previous existence tests were successful!");
					anomaly = true;
				} catch(Exception e)
				{
					/*
					 * Catch any other Runtime exception: Filesystem component
					 * may throw other Runtime Exceptions!
					 */
					chunkData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
					failure = true;
					log.error("ERROR in PtGChunk! StoRM process got an unexpected error! " + e);
					anomaly = true;
				}
			}
		}
		return !anomaly;
	}
	
	/**
     * Private method used to set Read Permission on existing file. Returns false if something goes wrong
     *
     * @param fileStoRI
     * @param localFile
     * @param localUser
     * @param turl
     * @return
     */
	private boolean managePermitReadFileStep(StoRI fileStoRI, LocalFile localFile,
			LocalUser localUser, TTURL turl) {

		try
		{
			/* Set the read acl to the localfile*/
			FilesystemPermission fp = null;
			boolean effective = false;
			if(fileStoRI.hasJustInTimeACLs())
			{
				// JiT
				localFile.grantUserPermission(localUser, FilesystemPermission.Read);
				fp = localFile.getEffectiveUserPermission(localUser);
				if(fp != null)
				{
					effective = fp.allows(FilesystemPermission.Read);
					if(effective)
					{
						/* ACL was correctly set up! Track JiT ACL! */
						VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(), localUser,
							FilesystemPermission.Read, start, chunkData.getPinLifeTime());
					}
					else
					{
						log.error("ATTENTION in PtGChunk! The local filesystem has a mask"
							+ " that does not allow Read User-ACL to be set up on"
							+ localFile.toString() + "!");
					}
				}
				else
				{
					log.error("ERROR in PTPChunk! A Read FilesystemPermission was set on "
						+ fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
						+ " but when subsequently verifying its effectivity,"
						+ " a null ACE was found!");
				}
			}
			else
			{
				// AoT
				localFile.grantGroupPermission(localUser, FilesystemPermission.Read);
				fp = localFile.getEffectiveGroupPermission(localUser);
				if(fp != null)
				{
					effective = fp.allows(FilesystemPermission.Read);
					if(!effective)
					{
						log.error("ATTENTION in PtGChunk! The local filesystem has a mask"
							+ " that does not allow Read Group-ACL to be set up on"
							+ localFile.toString() + "!");
					}
				}
				else
				{
					log.error("ERROR in PTPChunk! Read FilesystemPermission was set on "
						+ fileStoRI.getAbsolutePath() + " for group " + localUser.toString()
						+ " but when subsequently verifying its effectivity, "
						+ "a null ACE was found!");
				}
			}

			/* Manage DefaultACL */
			VirtualFSInterface vfs = fileStoRI.getVirtualFileSystem();
			DefaultACL acl = vfs.getCapabilities().getDefaultACL();
			if((acl != null) && (!acl.isEmpty()))
			{
				/* There are ACLs to set n file */
				List<ACLEntry> aclList = acl.getACL();
				for(ACLEntry ace : aclList)
				{
					/* Re-Check if the ACE is yet valid */
					if(ace.isValid())
					{
						PtGChunk.log.debug("Adding DefaultACL for the gid: " + ace.getGroupID()
							+ " with permission: " + ace.getFilePermissionString());
						LocalUser u = new LocalUser(ace.getGroupID(), ace.getGroupID());
						localFile.grantGroupPermission(u, ace.getFilesystemPermission());
					}
				}
			}

			if(fp == null)
			{
				/*
				 * This is a programming bug and should not occur! An attempt
				 * was just made to grant Read permission; so regardless of the
				 * umask allowing the permission or not, there must be at least
				 * an ACE present! Yet none was found!
				 */
				chunkData.changeStatusSRM_FAILURE("Local filesystem has problems"
					+ " manipulating ACE!");
				failure = true;
				return false;
			}
			if(!effective)
			{
				/*
				 * mask preset in filesystem does not allow the setting up of
				 * the required permissions! Request fails!
				 */
				chunkData.changeStatusSRM_FAILURE("Local filesystem mask does not allow"
					+	" setting up correct ACLs for PtG!");
				failure = true;
				return false;
			}
			/* ACL was correctly set up! */
			log.debug("PTG CHUNK DAO. Addition of Read ACL on file successfully completed.");
			
			chunkData.setTransferURL(turl);
			chunkData.changeStatusSRM_FILE_PINNED("srmPrepareToGet successfully handled!");
			
			failure = false;
			return true;
		} catch(WrongFilesystemType e)
		{
			/* StoRM configured for a different filesystem! */
			chunkData.changeStatusSRM_FAILURE("StoRM not configured for underlying filesystem!");
			failure = true;
			log.error("ERROR in PtGChunk! StoRM not configured for underlying filesystem! " + e);
			return false;
		} catch(InvalidPathException e)
		{
			/*
			 * The file does not exist! But it should not happen at this time,
			 * since an explicit previous test on the existence of the file was
			 * successful, else this code portion would have never been
			 * reached!!!
			 */
			chunkData.changeStatusSRM_INVALID_PATH("The requested file does not exist!");
			failure = true;
			log.error("ANOMALY in PtGChunk! File " + localFile.toString()
				+ " does not exist! This shouldnt happen since"
				+ " an explicit previous test did pass!");
			return false;
		} catch(InvalidPermissionOnFileException e)
		{
			/* FileSystem could not execute command to set up ACL! */
			chunkData.changeStatusSRM_FAILURE("Unable to setup ACL!");
			failure = true;
			log.error("ERROR in PtGChunk! StoRM has not got enough privileges"
				+ " to set up READ ACL on " + localFile.toString() + "! Exception returned: " + e);
			return false;
		} catch(SecurityException e)
		{
			/*
			 * Could not establish size of File because Java SecurityManager
			 * threw a SecurityException!
			 */
			chunkData.changeStatusSRM_FAILURE("StoRM cannot interact with the "
				+ "filesystem to establish the file size!");
			failure = true;
			log.error("ERROR in PtGChunk! Java SecurityManager "
				+ "did not allow establishing file size for " + localFile + "; exception: " + e);
			return false;
		} catch(Exception e)
		{
			/*
			 * We are using a filesystem with ACLs in place. I do not know
			 * exactly how Java s File object behaves in such situation! Better
			 * to catch any unexpected RuntimeExceptions!
			 */
			chunkData.changeStatusSRM_FAILURE("Unexpected error when operating on filesystem!");
			failure = true;
			log.error("ERROR in PtGChunk! StoRM has got unexpected problems"
				+ " with underlying filesystem! Exception returned: " + e);
			return false;
		}
	}
	
    /**
     * @param fileStoRI
     * @param localFile
     * @param localUser
     * @param turl
     */
    private void backupData(StoRI fileStoRI, LocalFile localFile, LocalUser localUser, TTURL turl) {
        bupFileStori = fileStoRI;
        bupLocalFile = localFile;
        bupLocalUser = localUser;
        bupTURL = turl;
    }
	
	/**
     * @param storiFile
     * @return
     */
	private boolean isStoriOndisk(StoRI storiFile) {

		boolean result = true;
		/* Check if Tape is Enabled */
		boolean isTapeEnabled = false;
		try
		{
			isTapeEnabled = storiFile.getVirtualFileSystem().getStorageClassType().isTapeEnabled();
		} catch(NamespaceException e)
		{
			log.error("Cannot retrieve storage class type information", e);
			result = true;
		}

		if(!(isTapeEnabled))
		{
			result = true;
		}
		else
		{
			LocalFile localFile = storiFile.getLocalFile();
			result = localFile.isOnDisk();
		}

		return result;
	}
	
    /**
     * Method that supplies a String describing this PtGChunk - for scheduler Log purposes! It returns the request token
     * and the SURL that was asked for.
     */
    public String getName() {
        return "PtGChunk of request " + rsd.requestToken() + " for SURL " + chunkData.fromSURL();
    }
    
    /**
     * Method used in a callback fashion in the scheduler for separately handling PtG, PtP and Copy chunks.
     */
	public void choose(Streets s) {

		s.ptgStreet(this);
	}

	public void completeRequest(RecallTaskStatus recallStatus) {

		/* Updates request status on the DB and on the statusManager accordingly with the received recallStatus */
		boolean success;
		if(recallStatus == RecallTaskStatus.SUCCESS)
		{
			if(bupLocalFile.isOnDisk())
			{
				success = managePermitReadFileStep(bupFileStori, bupLocalFile, bupLocalUser,
							  bupTURL);
			}
			else
			{
				log.error("File " + bupLocalFile.getAbsolutePath()
					+ " not found on the disk, but it was reported to"
					+ " be successfully recalled from tape");
				chunkData.changeStatusSRM_FAILURE("Error recalling file from tape");
				success = false;
			}
		}
		else
		{
			if(recallStatus == RecallTaskStatus.ABORTED)
			{
				chunkData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
				success = false;
			}
			else
			{
				chunkData.changeStatusSRM_FAILURE("Error recalling file from tape");
				success = false;
			}
		}
		PtGChunkCatalog.getInstance().update(chunkData);
		if(success)
		{

			gsm.successfulChunk(chunkData);
			log.info("Completed PtG request (" + rsd.requestToken()
				+ "), file successfully recalled from tape: " + chunkData.fromSURL().toString());
		}
		else
		{
			gsm.failedChunk(chunkData);
			log.error("BoL request (" + rsd.requestToken() + "), file not recalled from tape: "
				+ chunkData.fromSURL().toString());
		}
	}

  

    public ChunkData getChunkData() {
        return chunkData;
    }

    public String getRequestToken() {
        return rsd.requestToken().toString();
    }

    public String getSURL() {
        return chunkData.fromSURL().toString();
    }

    public String getUserDN() {
        return gu.getDn();
    }

    /**
     * @return
     */
	public boolean isResultSuccess() {

		boolean result = false;
		TStatusCode statusCode = chunkData.status().getStatusCode();
		if((statusCode.getValue().equals(TStatusCode.SRM_FILE_PINNED.getValue()))
			|| chunkData.status().isSRM_SUCCESS())
		{
			result = true;
		}
		return result;
	}

    /**
     * Manager of the IsDeny state: it indicates that Permission is not granted.
     */
	private void manageIsDeny() {

		chunkData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to " + chunkData.fromSURL()
			+ " denied!");
		failure = true;
		log.debug("Read access to " + chunkData.fromSURL() + " denied!");
	}

    /**
     * Manager of the IsIndeterminate state: this state indicates that an error in the PolicySource occured and so the
     * policy Collector does not know what do to!
     */
	private void manageIsIndeterminate(AuthzDecision ad) {

		chunkData.changeStatusSRM_FAILURE("Failure in PolicySource prevented"
			+ " PolicyCollector from establishing access rights! Processing failed!");
		failure = true;
		log.error("ERROR in PtGChunk! PolicyCollector received an error from PolicySource!");
		log.error("Received state: " + ad);
		log.error("Request: " + rsd.requestToken());
		log.error("Requested SURL: " + chunkData.fromSURL());
	}

    /**
     * Manager of the IsNotApplicable state: this state indicates that the PolicyCollector has not got any info on the
     * requested file, so it does not know what to answer.
     */
	private void manageIsNotApplicabale(AuthzDecision ad) {

		chunkData.changeStatusSRM_FAILURE("No policies found for the requested SURL! "
			+ "Therefore access rights cannot be established! Processing cannot continue!");
		failure = true;
		log.warn("PtGChunk: PolicyCollector found no policy for the supplied SURL!");
		log.warn("Received state: " + ad);
		log.warn("Request: " + rsd.requestToken());
		log.warn("Requested SURL: " + chunkData.fromSURL());
	}
    
    private boolean isStoriOndisk(StoRI storiFile) {
        boolean result = true;
       
        //Check if Tape is Enabled
        boolean isTapeEnabled = false;
        try {
            isTapeEnabled = storiFile.getVirtualFileSystem().getStorageClassType().isTapeEnabled();
        } catch (NamespaceException e) {
            log.error("Cannot retrieve storage class type information", e);
            result = true;
        }
        
        if (!(isTapeEnabled)) {
            result = true;
        } else {
            LocalFile localFile = storiFile.getLocalFile();
           result =  localFile.isOnDisk();  
        }
        
        return result;
    }
    
}
