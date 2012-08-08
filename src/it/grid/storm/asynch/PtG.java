/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.asynch;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.acl.AclManager;
import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.InvalidPathException;
import it.grid.storm.filesystem.InvalidPermissionOnFileException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.WrongFilesystemType;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.InvalidGetTURLNullPrefixAttributeException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.TURLBuildingException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

public class PtG implements Delegable, Chooser, Request, Suspendedable
{

    private static Logger log = LoggerFactory.getLogger(PtG.class);
    
    /**
     * GridUser that made the request
     */
    protected final GridUserInterface gu;

    /**
     * PtGChunkData that holds the specific info for this chunk
     */
    protected final PtGData requestData;
    
    /**
     * Time that wil be used in all jit and volatile tracking.
     */
    protected final Calendar start;
    
    /**
     * boolean that indicates the state of the shunk is failure
     */
    protected boolean failure = false;

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
    public PtG(GridUserInterface gu, PtGData requestData) throws InvalidRequestAttributesException
    {
        if (gu == null || requestData == null)
        {
            throw new InvalidRequestAttributesException(gu, requestData);
        }
        this.gu = gu;
        this.requestData = requestData;
        start = Calendar.getInstance();
    }

      /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
    public void doIt() {

        log.info("Handling PtG chunk for user DN: " + gu.getDn() + "; for SURL: "
            + requestData.getSURL());
        if(PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(requestData.getSURL()))
        {
            /* fail request with SRM_FILE_BUSY */
            requestData.changeStatusSRM_FILE_BUSY("Requested file is"
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
                StoRI fileStoRI = null;
                try
                {
                    fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(requestData.getSURL(), gu);
                }
                catch (IllegalArgumentException e)
                {
                    failure = true;
                    requestData.changeStatusSRM_INTERNAL_ERROR("Unable to get StoRI for surl " + requestData.getSURL());
                    log.error("Unable to get StoRY for surl " + requestData.getSURL() + " IllegalArgumentException: " + e.getMessage());
                }
                if (!failure)
                {
                    AuthzDecision ptgAuthz = AuthzDirector.getPathAuthz().authorize(gu, SRMFileRequest.PTG, fileStoRI);
                    if (ptgAuthz.equals(AuthzDecision.PERMIT))
                    {
                        manageIsPermit(fileStoRI);
                    }
                    else
                    {
                        if (ptgAuthz.equals(AuthzDecision.DENY))
                        {
                            manageIsDeny();
                        }
                        else
                        {
                            if (ptgAuthz.equals(AuthzDecision.INDETERMINATE))
                            {
                                manageIsIndeterminate(ptgAuthz);
                            }
                            else
                            {
                                manageIsNotApplicabale(ptgAuthz);
                            }
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
                requestData.changeStatusSRM_INVALID_PATH("The path specified in the"
                    + "SURL does not have a local equivalent!");
                failure = true;
                log.debug("ATTENTION in PtGChunk! PtGChunk received"
                    + " request for a SURL whose root is not recognised by StoRI! " + e);
            }
        }
        
        log.info("Finished handling PtG chunk for user DN: " + gu.getDn() + "; for SURL: "
            + requestData.getSURL() + "; result is: "
            + requestData.getStatus());
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
                TTURL turl = fileStoRI.getTURL(requestData.getTransferProtocols());
                if((!localFile.exists()) || (localFile.isDirectory()))
                {
                    /*
                     * File does not exist, or it is a directory! Fail request
                     * with SRM_INVALID_PATH!
                     */
                    requestData.changeStatusSRM_INVALID_PATH("The requested file either"
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
                            long expDate = (System.currentTimeMillis() / 1000 + requestData
                                               .getPinLifeTime().value());
                            StormEA.setPinned(localFile.getAbsolutePath(), expDate);

                            /* set group permission for tape quota management */
                            fileStoRI.setGroupTapeRead();
                            requestData.setFileSize(TSizeInBytes.make(localFile.length(),
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
                                requestData.changeStatusSRM_REQUEST_INPROGRESS("Recalling"
                                    + " file from tape");
                                
                                String voName = null;
                                if(gu instanceof AbstractGridUser)
                                {
                                    voName = ((AbstractGridUser) gu).getVO().getValue();
                                }

                                TapeRecallCatalog rtCat = null;

                                try {
                                    rtCat = new TapeRecallCatalog();
                                } catch (DataAccessException e) {
                                    log.error("Unable to use RecallTable DB.");
                                    throw new TapeRecallException("Unable to use RecallTable DB.");
                                }
                                rtCat.insertTask(this, voName, localFile.getAbsolutePath());
                                
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
                requestData
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
                requestData.changeStatusSRM_FAILURE("Unable to find local user for " + gu.getDn());
                failure = true;
                log.error("ERROR in PtGChunk! Unable to find LocalUser for " + gu.getDn()
                    + "! GridUser object returned: " + e);
            } catch(InvalidGetTURLNullPrefixAttributeException e)
            {
                /*
                 * Handle null TURL prefix! This is a programming error: it
                 * should not occur!
                 */
                requestData.changeStatusSRM_FAILURE("Unable to decide TURL!");
                failure = true;
                log
                    .error("ERROR in PtGChunk! Null TURLPrefix in PtGChunkData caused StoRI to be unable to establish TTURL! StoRI object returned: "
                        + e);
            } catch(TURLBuildingException e)
            {
                requestData.changeStatusSRM_FAILURE("Unable to build the TURL for the provided transfer protocol");
                failure = true; 
                log.error("ERROR in PtGChunk! There was a failure building the TURL. : TURLBuildingException " + e);
            } 
            catch(Exception e)
            {
                /*
                 * There could be unexpected runtime errors given the fact that
                 * we have an ACL enabled filesystem! I do not know the
                 * behaviour of Java File class!!!
                 */
                requestData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
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
                requestData.changeStatusSRM_FAILURE("Unable to map grid credentials to local user!");
                failure = true;
                log.error("ERROR in PtGChunk! There was a failure in mapping " + gu.getDn()
                    + " to a local user! Error returned: " + e);
            }

        }
        else
        {
            requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to "
                + requestData.getSURL() + " in Storage Area: " + token + " denied!");
            
            failure = true;
            log.debug("Read access to " + requestData.getSURL() + " in Storage Area: " + token
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
                requestData.changeStatusSRM_INVALID_PATH(errorString);
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
                        AclManager manager = AclManagerFSAndHTTPS.getInstance();
                        //TODO ACL manager
                        try
                        {
                            manager.grantUserPermission(parentFile,localUser, FilesystemPermission.Traverse);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: " + e.getMessage());
                        }
//                      parentFile.grantUserPermission(localUser, FilesystemPermission.Traverse);
                        fp = parentFile.getEffectiveUserPermission(localUser);
                        if(fp != null)
                        {
                            allowsTraverse = fp.allows(FilesystemPermission.Traverse);
                            if(allowsTraverse)
                            {
                                VolatileAndJiTCatalog.getInstance().trackJiT(parentStoRI.getPFN(),
                                    localUser, FilesystemPermission.Traverse, start,
                                    requestData.getPinLifeTime());
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
                        AclManager manager = AclManagerFSAndHTTPS.getInstance();
                        //TODO ACL manager
                        try
                        {
                            manager.grantGroupPermission(parentFile,localUser, FilesystemPermission.Traverse);
                        }
                        catch (IllegalArgumentException e)
                        {
                            log.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: " + e.getMessage());
                        }
//                      parentFile.grantGroupPermission(localUser, FilesystemPermission.Traverse);
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
                        requestData.changeStatusSRM_FAILURE("Local filesystem has"
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
                            requestData.changeStatusSRM_FAILURE("Local filesystem mask does "
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
                    requestData.changeStatusSRM_FAILURE("StoRM configured for wrong filesystem!");
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
                    requestData.changeStatusSRM_FAILURE("StoRM cannot manipulate directory!");
                    failure = true;
                    log.error("ERROR in PtGChunk! StoRM process has"
                        + " not got enough privileges to work on: " + parentFile.toString()
                        + "; exception: " + e);
                    anomaly = true;
                } catch(InvalidPathException e)
                {
                    /* Could not set ACL because file does not exist! */
                    requestData.changeStatusSRM_FAILURE("Unable to setup ACL!");
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
                    requestData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
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
                AclManager manager = AclManagerFSAndHTTPS.getInstance();
                //TODO ACL manager
                try
                {
                    manager.grantUserPermission(localFile,localUser, FilesystemPermission.Read);
                }
                catch (IllegalArgumentException e)
                {
                    log.error("Unable to grant user read permission on the file. IllegalArgumentException: " + e.getMessage());
                }
//              localFile.grantUserPermission(localUser, FilesystemPermission.Read);
                fp = localFile.getEffectiveUserPermission(localUser);
                if(fp != null)
                {
                    effective = fp.allows(FilesystemPermission.Read);
                    if(effective)
                    {
                        /* ACL was correctly set up! Track JiT ACL! */
                        VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(), localUser,
                            FilesystemPermission.Read, start, requestData.getPinLifeTime());
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
                AclManager manager = AclManagerFSAndHTTPS.getInstance();
                //TODO ACL manager
                try
                {
                    manager.grantGroupPermission(localFile,localUser, FilesystemPermission.Read);
                }
                catch (IllegalArgumentException e)
                {
                    log.error("Unable to grant user read permission on the file. IllegalArgumentException: " + e.getMessage());
                }
//              localFile.grantGroupPermission(localUser, FilesystemPermission.Read);
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
                        log.debug("Adding DefaultACL for the gid: " + ace.getGroupID()
                            + " with permission: " + ace.getFilePermissionString());
                        LocalUser u = new LocalUser(ace.getGroupID(), ace.getGroupID());
                         AclManager manager = AclManagerFSAndHTTPS.getInstance();
                        //TODO ACL manager
                        if(ace.getFilesystemPermission() == null)
                        {
                            log.warn("Unable to setting up the ACL. ACl entry permission is null!");
                        }
                        else
                        {
                            try
                            {
                                manager.grantGroupPermission(localFile,u, ace.getFilesystemPermission());
                                
                            }
                            catch (IllegalArgumentException e)
                            {
                                log.error("Unable to grant group permissions on the file. IllegalArgumentException: " + e.getMessage());
                            }
                        }
//                      localFile.grantGroupPermission(u, ace.getFilesystemPermission());
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
                requestData.changeStatusSRM_FAILURE("Local filesystem has problems"
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
                requestData.changeStatusSRM_FAILURE("Local filesystem mask does not allow"
                    +   " setting up correct ACLs for PtG!");
                failure = true;
                return false;
            }
            /* ACL was correctly set up! */
            log.debug("PTG CHUNK DAO. Addition of Read ACL on file successfully completed.");
            
            requestData.setTransferURL(turl);
            requestData.changeStatusSRM_FILE_PINNED("srmPrepareToGet successfully handled!");
            
            failure = false;
            return true;
        } catch(WrongFilesystemType e)
        {
            /* StoRM configured for a different filesystem! */
            requestData.changeStatusSRM_FAILURE("StoRM not configured for underlying filesystem!");
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
            requestData.changeStatusSRM_INVALID_PATH("The requested file does not exist!");
            failure = true;
            log.error("ANOMALY in PtGChunk! File " + localFile.toString()
                + " does not exist! This shouldnt happen since"
                + " an explicit previous test did pass!");
            return false;
        } catch(InvalidPermissionOnFileException e)
        {
            /* FileSystem could not execute command to set up ACL! */
            requestData.changeStatusSRM_FAILURE("Unable to setup ACL!");
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
            requestData.changeStatusSRM_FAILURE("StoRM cannot interact with the "
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
            requestData.changeStatusSRM_FAILURE("Unexpected error when operating on filesystem!");
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
     * @throws FSException 
     */
    private boolean isStoriOndisk(StoRI storiFile) throws FSException {

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
        return "PtGChunk for SURL " + requestData.getSURL();
    }
    
    /**
     * Method used in a callback fashion in the scheduler for separately handling PtG, PtP and Copy chunks.
     */
    public void choose(Streets s) {

        s.ptgStreet(this);
    }

    @Override
    public Boolean completeRequest(TapeRecallStatus recallStatus) {

       Boolean success = false;
        if(recallStatus == TapeRecallStatus.SUCCESS)
        {
            try
            {
                if (bupLocalFile.isOnDisk())
                {
                    success = managePermitReadFileStep(bupFileStori, bupLocalFile, bupLocalUser, bupTURL);
                }
                else
                {
                    log.error("File " + bupLocalFile.getAbsolutePath()
                            + " not found on the disk, but it was reported to"
                            + " be successfully recalled from tape");
                    requestData.changeStatusSRM_FAILURE("Error recalling file from tape");
                }
            }
            catch (FSException e)
            {
                log.error("Unable to determine if file " + bupLocalFile.getAbsolutePath()
                        + " is on disk . FSException : " + e.getMessage());
                requestData.changeStatusSRM_FAILURE("Internal error: unable to determine if the file is on disk");
            }
        }
        else
        {
            if(recallStatus == TapeRecallStatus.ABORTED)
            {
                requestData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
            }
            else
            {
                requestData.changeStatusSRM_FAILURE("Error recalling file from tape");
            }
        }
        return success;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.asynch.Suspendedable#getRequestData()
     */
    public PtGData getRequestData()
    {
        return requestData;
    }

    public String getSURL() {
        return requestData.getSURL().toString();
    }

    public String getUserDN() {
        return gu.getDn();
    }

    /**
     * @return
     */
    public boolean isResultSuccess() {

        boolean result = false;
        TStatusCode statusCode = requestData.getStatus().getStatusCode();
        if((statusCode.getValue().equals(TStatusCode.SRM_FILE_PINNED.getValue()))
            || requestData.getStatus().isSRM_SUCCESS())
        {
            result = true;
        }
        return result;
    }
    
    /**
     * Manager of the IsDeny state: it indicates that Permission is not granted.
     */
    private void manageIsDeny() {

        requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Read access to " + requestData.getSURL()
            + " denied!");
        failure = true;
        log.debug("Read access to " + requestData.getSURL() + " denied!");
    }

    /**
     * Manager of the IsIndeterminate state: this state indicates that an error in the PolicySource occured and so the
     * policy Collector does not know what do to!
     */
    private void manageIsIndeterminate(AuthzDecision ad) {

        requestData.changeStatusSRM_FAILURE("Failure in PolicySource prevented"
            + " PolicyCollector from establishing access rights! Processing failed!");
        failure = true;
        log.error("ERROR in PtGChunk! PolicyCollector received an error from PolicySource!");
        log.error("Received state: " + ad);
        log.error("Requested SURL: " + requestData.getSURL());
    }

    /**
     * Manager of the IsNotApplicable state: this state indicates that the PolicyCollector has not got any info on the
     * requested file, so it does not know what to answer.
     */
    private void manageIsNotApplicabale(AuthzDecision ad) {

        requestData.changeStatusSRM_FAILURE("No policies found for the requested SURL! "
            + "Therefore access rights cannot be established! Processing cannot continue!");
        failure = true;
        log.warn("PtGChunk: PolicyCollector found no policy for the supplied SURL!");
        log.warn("Received state: " + ad);
        log.warn("Requested SURL: " + requestData.getSURL());
    }


}
