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


import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.PtPData;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.filesystem.ReservationException;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.namespace.ExpiredSpaceTokenException;
import it.grid.storm.namespace.InvalidGetTURLNullPrefixAttributeException;
import it.grid.storm.namespace.InvalidGetTURLProtocolException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.TURLBuildingException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TransferObjectDecodingException;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TOverwriteMode;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a chunk of an srmPrepareToPut request: it handles a single file of a multifile
 * request. StoRM
 * then sends the chunk to a chunk-scheduler. For an existing file: if TOverwriteMode is set to Never, then
 * the chunk
 * fails with SRM_DUPLICATION_ERROR; if TOverwriteMode is Always or WhenFilesAreDifferent, the file gets
 * treated in the
 * same fashion: moreover the behaviour is the same as for the case of a non existing file described later on,
 * except
 * that the only policy check made is about the presence of write rights, instead of create rights, as well as
 * erasing
 * the file before going on with the processing - all previous data gets lost! If the SURL refers to a file
 * that does
 * not exist, the behaviour is identical whatever the TOverwriteMode; in particular: AuthorisationCollector is
 * queried
 * for File Creation policies: if it is set to Deny, then the chunk is failed with SRM_AUTHORIZATION_FAILURE.
 * If it is
 * set to Permit, the situation is decribed later on. For any other decisions, the chunk is failed with
 * SRM_FAILURE: it
 * is caused when the policy is missing so no decision can be made, or if there is a problem querying the
 * Policies, or
 * any new state for the AuthorisationDecision is introduced but the PtP logic is not updated. In case Create
 * rights are
 * granted, the presence of a space token and file size are evaluated. If no size and no token are available,
 * a mock
 * file is created as placeholder; in case the size is available but there is no token, an implicit space
 * reservation
 * takes place so a special mock file is also produced; an implicit space reservation again is carried out if
 * there is
 * no space token and no known size, but this time a default reserve size is used resulting once more in a
 * special mock
 * file; finally if both space token and size are supplied, the space is allocated as requested and again a
 * special mock
 * reserve file gets created. A Write ACL is setup on the file regardless of the Security Model (AoT or JiT);
 * if the
 * file is specified as VOLATILE, it gets pinned in the PinnedFilesCatalog; if JiT is active, the ACL will
 * live only for
 * the given time interval. A TURL gets filled in, the status transits to SRM_SPACE_AVAILABLE, and the
 * PtPCatalog is
 * updated. There are error situations which get handled as follows: If the placeHolder file cannot be
 * created, or the
 * implicit reservation fails, or the supplied space token does not exist, the request fails and chenages
 * state to
 * SRM_FAILURE. If the setting up of the ACL fails, the request fails too and the state changes to
 * SRM_FAILURE.
 * Appropriate messagges get logged.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 3.0
 */
public class PtP implements Delegable, Chooser, Request
{

    protected static final String SRM_COMMAND = "srmPrepareToPut";

    private static Logger log = LoggerFactory.getLogger(PtP.class);

    /**
     * PtPChunkData that holds the specific info for this chunk
     */
    protected final PtPData requestData;

    /**
     * Time that wil be used in all jit and volatile tracking.
     */
    protected final Calendar start;

    /**
     * boolean that indicates the state of the shunk is failure
     */
    protected boolean failure = false;
    
    /**
     * boolean that indicates a failed chunk because of an expired space token
     */
    protected boolean spacefailure = false;

    /**
     * Constructor requiring the VomsGridUser, the RequestSummaryData, the PtPChunkData about this chunk, and
     * the
     * GlobalStatusManager. If the supplied attributes are null, an InvalidPtPChunkAttributesException is
     * thrown.
     */
    public PtP(PtPData chunkData) throws InvalidRequestAttributesException
    {
        if (chunkData == null)
        {
            throw new IllegalArgumentException("Unable to instanciate the object, invalid arguments: chunkData=" + chunkData);
        }
        this.requestData = chunkData;
        start = Calendar.getInstance();
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
    public void doIt()
    {
        PtP.log.info("Handling PtP chunk for user DN: " + DataHelper.getRequestor(requestData) + "; for SURL: " + requestData.getSURL());
        if(!verifySurlStatusTransition(requestData.getSURL(), requestData.getRequestToken()))
        {//should return SRM_DUPLICATION_ERROR if file overwrite disabled instead of SRM_FILE_BUSY
            failure = true;
            requestData.changeStatusSRM_FILE_BUSY("The surl " + requestData.getSURL() + " is currently busy");
            log.info("Unable to perform the PTP request, surl busy");
            printRequestOutcome(requestData);
            return;
        }
        requestData.changeStatusSRM_REQUEST_INPROGRESS("request in progress");
        StoRI fileStoRI = null;
        try
        {
            if (requestData instanceof IdentityInputData)
            {
                fileStoRI = NamespaceDirector.getNamespace()
                                             .resolveStoRIbySURL(requestData.getSURL(),
                                                                 ((IdentityInputData) requestData).getUser());
            }
            else
            {
                fileStoRI = NamespaceDirector.getNamespace().resolveStoRIbySURL(requestData.getSURL());
            }
        } catch(IllegalArgumentException e)
        {
            failure = true;
            requestData.changeStatusSRM_INTERNAL_ERROR("Unable to get StoRI for surl "
                    + requestData.getSURL());
            log.error("Unable to get StoRI for surl " + requestData.getSURL()
                    + " IllegalArgumentException: " + e.getMessage());
            printRequestOutcome(requestData);
            return;
        } catch(NamespaceException e)
        {
            requestData.changeStatusSRM_INVALID_PATH("The path specified in the SURL does not have a local equivalent!");
            failure = true;
            PtP.log.debug("ATTENTION in PtPChunk! PtPChunk received request for a SURL whose root is not recognised by StoRI!");
            printRequestOutcome(requestData);
            return;
        } 
        boolean exists = false;
        try
        {
            exists = fileStoRI.getLocalFile().exists();
        }catch(SecurityException e)
        {
            requestData.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
            failure = true;
            PtP.log.error("ATTENTION in PtPChunk! PtPChunk received a SecurityException from Java SecurityManager: StoRM cannot check for the existence of file: "
                    + fileStoRI.getLocalFile().toString() + "; exception: " + e);
            printRequestOutcome(requestData);
            return;
        } 
        if (!exists)
        {
            manageNotExistentFile(fileStoRI);
        }
        else
        {
            if (requestData.overwriteOption() == TOverwriteMode.ALWAYS
                    || requestData.overwriteOption() == TOverwriteMode.WHENFILESAREDIFFERENT)
            {
                manageOverwriteExistingFile(fileStoRI);
            }
            else
            {
                if (requestData.overwriteOption() == TOverwriteMode.NEVER)
                {
                    requestData.changeStatusSRM_DUPLICATION_ERROR("Cannot srmPut file because it already exists!");
                    failure = true; 
                }
                else
                {
                    // unexpected overwrite option!!!
                    requestData.changeStatusSRM_FAILURE("Unexpected overwrite option! Processing failed!");
                    failure = true; 
                    PtP.log.error("UNEXPECTED ERROR in PtPChunk! The specified overwrite option is unknown!");
                    PtP.log.error("Received Overwrite Option: " + requestData.overwriteOption());
                }
            }
        }
        printRequestOutcome(requestData);
    }

    private boolean verifySurlStatusTransition(TSURL surl, TRequestToken requestToken)
    {
        Map<TRequestToken, TReturnStatus> statuses = SurlStatusManager.getSurlCurrentStatuses(surl);
        statuses.remove(requestToken);
        return TStatusCode.SRM_SPACE_AVAILABLE.isCompatibleWith(statuses.values());
    }

    /**
     * Private method that manages the case of a SURL referring to a file that does not exist: the steps are
     * the same
     * regardless of the TOverwriteMode!
     */
    private void manageNotExistentFile(StoRI fileStoRI)
    {
        AuthzDecision decision;
        if (requestData instanceof IdentityInputData)
        {
            decision = AuthzDirector.getPathAuthz().authorize(((IdentityInputData) requestData).getUser(), SRMFileRequest.PTP, fileStoRI);
        }
        else
        {
            decision = AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.PTP, fileStoRI.getStFN());
        }
        if(decision == null)
        {
            manageAnomaly(decision);
            return;
        }
        switch (decision)
        {
            case PERMIT:
                managePermit(fileStoRI);
                break;
            case DENY:
                manageDeny();
                break;
            default:
                manageAnomaly(decision);
                break;
        }
    }

    /**
     * Private method that handles the case of Permit on Create and Write rights!
     */
    private void managePermit(StoRI fileStoRI)
    {

        TSpaceToken token = new SpaceHelper().getTokenFromStoRI(PtP.log, fileStoRI);
        SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

        boolean isSpaceAuthorized;
        if (requestData instanceof IdentityInputData)
        {
            isSpaceAuthorized = spaceAuth.authorize(((IdentityInputData) requestData).getUser(),
                                                    SRMSpaceRequest.PTP);
        }
        else
        {
            isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.PTP);
        }
        if (!isSpaceAuthorized)
        {
            requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Create/Write access for " + requestData.getSURL()
                    + " in Storage Area: " + token + " denied!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.debug("Create/Write access for " + requestData.getSURL() + " in Storage Area: " + token
                    + " denied!");
            return;
        }
        TTURL auxTURL;
        try
        {
            auxTURL = fileStoRI.getTURL(requestData.getTransferProtocols());
        }
        catch(InvalidGetTURLNullPrefixAttributeException e)
        {
            // Handle null TURL prefix! This is a programming error: it
            // should not occur!
            requestData.changeStatusSRM_FAILURE("Unable to decide TURL!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! Null TURLPrefix in PtPChunkData caused StoRI to be unable to establish TTURL! StoRI object returned the following message: "
                    + e);
            return;
        } catch(InvalidGetTURLProtocolException e)
        {
            // Handle null TURL prefix! This is a programming error: it
            // should not occur!
            requestData.changeStatusSRM_NOT_SUPPORTED("Unable to build TURL with specified transfer protocols!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! No valid transfer protocol found.");
            return;
        } catch(TURLBuildingException e)
        {
            requestData.changeStatusSRM_FAILURE("Unable to build the TURL for the provided transfer protocol");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! There was a failure building the TURL. : TURLBuildingException "
                    + e);
            return;
        }
        // set right permissions to traverse to file
        boolean canTraverse;
        try
        {
            canTraverse = managePermitTraverseStep(fileStoRI);
            log.debug("PtPChunk: finished TraverseStep for " + fileStoRI.getAbsolutePath());
        } catch(CannotMapUserException e)
        {
            requestData.changeStatusSRM_FAILURE("Unable to find local user for " + DataHelper.getRequestor(requestData));
            failure = true;
            log.error("ERROR in PtGChunk! Unable to find LocalUser for " + DataHelper.getRequestor(requestData)
                + "! CannotMapUserException: " + e.getMessage());
            return;
        }
        if(canTraverse)
        {
            // Use any reserved space which implies the existence of a
            // file!
            if (managePermitReserveSpaceStep(fileStoRI))
            {
                // set right permission on file!
                boolean canWrite;
                try
                {
                    canWrite = managePermitSetFileStep(fileStoRI, auxTURL);
                } catch(CannotMapUserException e)
                {
                    requestData.changeStatusSRM_FAILURE("Unable to find local user for " + DataHelper.getRequestor(requestData));
                    failure = true;
                    log.error("ERROR in PtGChunk! Unable to find LocalUser for " + DataHelper.getRequestor(requestData)
                        + "! CannotMapUserException: " + e.getMessage());
                    return;
                }
                if(!canWrite)
                {
                    // URGENT!!!
                    // roll back! ok3, ok2 and ok1
                }
                else
                {
                    PtP.log.debug("PTP CHUNK. Addition of ReadWrite ACL on file successfully completed for "
                            + fileStoRI.getAbsolutePath());
                    requestData.setTransferURL(auxTURL);
                    requestData.changeStatusSRM_SPACE_AVAILABLE("srmPrepareToPut successfully handled!");
                    failure = false; // gsm.successfulChunk(chunkData);
                    if (requestData.fileStorageType() == TFileStorageType.VOLATILE)
                    {
                        VolatileAndJiTCatalog.getInstance().trackVolatile(fileStoRI.getPFN(),
                                                                          Calendar.getInstance(),
                                                                          requestData.fileLifetime());
                    }
                }
            }
            else
            {
                // URGENT!!!
                // roll back! ok2 and ok1
            }
        }
        else
        {
            // URGENT!!!
            // roll back ok1!
        }
    }
    
    private boolean managePermitTraverseStep(StoRI fileStoRI) throws CannotMapUserException
    {
        if (requestData instanceof IdentityInputData)
        {
            return verifyPath(fileStoRI)
                    && setAcl(fileStoRI, ((IdentityInputData) requestData).getUser().getLocalUser());
        }
        else
        {
            return verifyPath(fileStoRI);
        }
    }
    
    private boolean verifyPath(StoRI fileStoRI) {

        boolean exists, isDir;
        for(StoRI parentStoRI : fileStoRI.getParents())
        {
            exists = parentStoRI.getLocalFile().exists();
            isDir = parentStoRI.getLocalFile().isDirectory();
            if(!exists || !isDir)
            {
                String errorString = "The requested SURL is: " + fileStoRI.getSURL().toString()
                                       + ", but its parent " + parentStoRI.getSURL().toString();
                if(!exists)
                {
                    errorString = errorString + "does not exist!";
                }
                else
                {
                    errorString = errorString + "is not a directory!";
                }
                requestData.changeStatusSRM_INVALID_PATH(errorString);
                failure = true;
                log.debug(errorString + " Parent points to " + parentStoRI.getLocalFile().toString()
                    + ".");
                return false;
            }
        }
        return true;
    }
    
    private boolean setAcl(StoRI fileStoRI, LocalUser localUser)
    {
        PtP.log.debug("PtPChunk: entered TraverseStep for " + fileStoRI.getAbsolutePath());
        boolean automaticDirectoryCreation = Configuration.getInstance().getAutomaticDirectoryCreation();
        LocalFile parentFile;
        for (StoRI parentStoRI : fileStoRI.getParents())
        {
            parentFile = parentStoRI.getLocalFile();
            log.debug("PtPChunk TraverseStep - processing parent " + parentFile.toString());
            if (!prepareDirectory(parentFile, automaticDirectoryCreation))
            {
                log.warn("Unable to perform directory preparation step on parent file "
                        + parentFile.getAbsolutePath());
                failure = true; // gsm.failedChunk(chunkData);
                return false;
            }
            else
            {
                FilesystemPermission fp = null;
                boolean allowsTraverse = false;
                if (fileStoRI.hasJustInTimeACLs())
                {
                    // TODO DEFAULT ACL here to make cms happy we have to set also default ACL
                    // JiT
                    // TODO ACL manager
                    try
                    {
                        AclManagerFSAndHTTPS.getInstance().grantUserPermission(parentFile, localUser, FilesystemPermission.Traverse);
                    } catch(IllegalArgumentException e)
                    {
                        log.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: "
                                + e.getMessage());
                        requestData.changeStatusSRM_INTERNAL_ERROR("Unable to grant user traverse permission ACL on parent file");
                        failure = true;
                        return false;
                    }
// parentFile.grantUserPermission(localUser, FilesystemPermission.Traverse);
                    fp = parentFile.getEffectiveUserPermission(localUser);
                    if (fp != null)
                    {
                        allowsTraverse = fp.allows(FilesystemPermission.Traverse);
                        if (allowsTraverse)
                        {
                            VolatileAndJiTCatalog.getInstance().trackJiT(parentStoRI.getPFN(), localUser,
                                                                         FilesystemPermission.Traverse,
                                                                         start, requestData.pinLifetime());
                        }
                        else
                        {
                            log.error("ATTENTION in PtPChunk! The local filesystem has a mask that does not allow Traverse User-ACL to be set up on"
                                    + parentFile.toString() + "!");
                            requestData.changeStatusSRM_INTERNAL_ERROR("Unable to grant user traverse permission ACL on parent file");
                            failure = true;
                            return false;
                        }
                    }
                    else
                    {
                        log.error("ERROR in PTPChunk! A Traverse User-ACL was set on "
                                + fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
                                + " but when subsequently verifying its effectivity, a null ACE was found!");
                        requestData.changeStatusSRM_INTERNAL_ERROR("Unable to grant user traverse permission ACL on parent file");
                        failure = true;
                        return false;
                    }
                }
                else
                {
                    // TODO DEFAULT ACL here to make cms happy we have to set also default ACL
                    try
                    {
                        AclManagerFSAndHTTPS.getInstance().grantGroupPermission(parentFile, localUser, FilesystemPermission.Traverse);
                    } catch(IllegalArgumentException e)
                    {
                        log.error("Unable to grant user traverse permission on parent file. IllegalArgumentException: "
                                + e.getMessage());
                        requestData.changeStatusSRM_INTERNAL_ERROR("Unable to get user ACL for the file");
                        failure = true;
                        return false;
                    }
// parentFile.grantGroupPermission(localUser, FilesystemPermission.Traverse);
                    fp = parentFile.getEffectiveGroupPermission(localUser);
                    if (fp != null)
                    {
                        allowsTraverse = fp.allows(FilesystemPermission.Traverse);
                    }
                    else
                    {
                        log.error("ERROR in PTPChunk! A Traverse Group-ACL was set on "
                                + fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
                                + " but when subsequently verifying its effectivity, a null ACE was found!");
                        requestData.changeStatusSRM_INTERNAL_ERROR("Unable to get group ACL for the file");
                        failure = true;
                        return false;
                    }
                }
                if (!allowsTraverse)
                {
                    /*
                     * mask preset in filesystem does not allow the
                     * setting up of the required permissions!
                     * Request fails!
                     */
                    requestData.changeStatusSRM_FAILURE("Local filesystem mask does not allow setting up correct ACLs for PtP!");
                    failure = true; // gsm.failedChunk(chunkData);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Private method that handles directory creation: it considers whether it already exists, and if
     * automatic creation
     * is enabled or not. BEWARE! It returns a boolean true if any _anomaly_ occurs!
     */
    private boolean prepareDirectory(LocalFile f, boolean canCreate) throws SecurityException
    {
        if ((!f.exists()) && (!canCreate))
        {
            requestData.changeStatusSRM_INVALID_PATH("Directory structure as specified in SURL does not exist!");
            failure = true; // gsm.failedChunk(chunkData);
            log.debug("ATTENTION in PtPChunk! Directory structure as specified in "
                    + f
                    + " does not exist, and automatic directory creation is disbaled! Failing this chunk of request!"); // info
            return false;
        }
        else
        {
            if (!f.exists())
            {
                if (f.mkdirs())
                {
                    requestData.changeStatusSRM_FAILURE("Local filesystem error: could not crete directory!");
                    failure = true; // gsm.failedChunk(chunkData);
                    log.error("ERROR in PtPChunk! Filesystem was unable to successfully create directory: "
                            + f.toString());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Private method used to manage ReserveSpace. Returns false if something went wrong!
     */
    private boolean managePermitReserveSpaceStep(StoRI fileStoRI)
    {
        PtP.log.debug("PtPChunk: entered ReserveSpaceStep for " + fileStoRI.getAbsolutePath());
        TSizeInBytes size = requestData.expectedFileSize();
        TSpaceToken spaceToken = requestData.getSpaceToken();
        LocalFile localFile = fileStoRI.getLocalFile();

        // In case of SRM Storage Area limitation enabled,
        // the Storage Area free size is retrieved from the database
        // and the PtP fails if there is not enougth space.

        VirtualFSInterface fs = fileStoRI.getVirtualFileSystem();

        if (fs != null && fs.getProperties().isOnlineSpaceLimited())
        {
            SpaceHelper sp = new SpaceHelper();
            long freeSpace = sp.getSAFreeSpace(PtP.log, fileStoRI);
            if ((sp.isSAFull(PtP.log, fileStoRI))
                    || ((!size.isEmpty() && ((freeSpace != -1) && (freeSpace <= size.value())))))
            {
                /* Verify if the storage area space has been initialized */
                /*
                 * If is not initialized verify if the SpaceInfoManager is currently initializing this storage
                 * area
                 */
                TSpaceToken SASpaceToken = sp.getTokenFromStoRI(PtP.log, fileStoRI);
                if (SASpaceToken == null || SASpaceToken.isEmpty())
                {
                    log.error("PtPChunk - ReserveSpaceStep: Unable to get a valid TSpaceToken for stori "
                            + fileStoRI + " . Unable to verify storage area space initialization");
                    requestData.changeStatusSRM_FAILURE("No valid space token for the Storage Area");
                    failure = true; // gsm.failedChunk(chunkData);
                    return false;
                }
                else
                {
                    if (!sp.isSAInitialized(PtP.log, fileStoRI)
                            && SpaceInfoManager.isInProgress(SASpaceToken))
                    {
                        /* Trust we got space, let the request pass */
                        PtP.log.debug("PtPChunk: ReserveSpaceStep: the storage area space initialization is in progress, "
                                + "optimistic approach, considering we got enough space");
                    }
                    else
                    {
                        PtP.log.debug("PtPChunk - ReserveSpaceStep: no free space on Storage Area!");
                        requestData.changeStatusSRM_FAILURE("No free space on Storage Area");
                        failure = true; // gsm.failedChunk(chunkData);
                        return false;
                    }
                }
            }
        }

        try
        {
            // set space!
            boolean successful = localFile.createNewFile();
            if ((!successful) && (requestData.overwriteOption() == TOverwriteMode.NEVER))
            {
                // atomic operation of creation of file if non existent, failed:
                // so the file
                // did exist before. The Overwrite option was set to NEVER, so
                // fail request!
                //
                // this test is done here, in case concurrent PtP chunks get
                // executed at the
                // same time: this is the _only_ place where the concurrent
                // operation can be
                // spotted without incurring in exceptionally tough
                // syncronisation issues!
                //
                // File exists but Overwrite set to NEVER!!!
                PtP.log.debug("PtPChunk - ReserveSpaceStep: no overwrite allowed! Failing chunk... ");
                requestData.changeStatusSRM_DUPLICATION_ERROR("Cannot srmPut file because it already exists!");
                failure = true; // gsm.failedChunk(chunkData);
                return false;
            }
            else
                if (!successful && TStatusCode.SRM_SPACE_AVAILABLE.equals(SurlStatusManager.getSurlsStatus(requestData.getSURL())))//PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(requestData.getSURL()))
                {
                    // atomic operation of creation of file if non existent, failed:
                    // so the file
                    // did exist before. A check in the catalogue shows that there
                    // is a past request
                    // which has the SURL in SRM_SPACE_AVAILABLE! So request must be
                    // failed with
                    // SRM_FILE_BUSY
                    //
                    // WARNING! Unfortunately although this test is done here, in
                    // case concurrent PtP
                    // chunks get executed at the same time, it is still NOT
                    // guaranteed that one of the
                    // two PtP will get an SRM_FILE_BUSY.
                    //
                    // The operation isSRM_SPACE_AVAILABLE _is_ atomic because that
                    // method is
                    // synchronised. But the couple createNewFile AND
                    // isSRM_FILE_BUSY
                    // is NOT atomic! Hence the inability to guarantee one of the
                    // two
                    // will get an SRM_FILE_BUSY.
                    //
                    // Putting the check here minimizes the chances, but cannot be
                    // eliminated
                    // altogether.
                    //
                    // fail request with SRM_FILE_BUSY
                    requestData.changeStatusSRM_FILE_BUSY("Requested file is still in SRM_SPACE_AVAILABLE state!");
                    failure = true; // gsm.failedChunk(chunkData);
                    PtP.log.debug("ATTENTION in PtPChunk! PtPChunk received request for SURL that is still in SRM_SPACE_AVAILABLE state!");
                    return false;
                }
                else
                    if ((spaceToken.isEmpty()) && (size.isEmpty()))
                    {
                        PtP.log.debug("PtPChunk - ReserveSpaceStep: no SpaceToken and no FileSize specified; mock file will be created... ");
                    }
                    else
                        if ((spaceToken.isEmpty()) && (!size.isEmpty()))
                        {
                            PtP.log.debug("PtPChunk - ReserveSpaceStep: no SpaceToken available but there is a FileSize specified; implicit space reservation taking place...");
                            fileStoRI.allotSpaceForFile(size);
                        }
                        else
                            if ((!spaceToken.isEmpty()) && (!size.isEmpty()))
                            {
                                PtP.log.debug("PtPChunk - ReserveSpaceStep: SpaceToken available and FileSize specified; reserving space by token...");
                                if (!isExistingSpaceToken(spaceToken))
                                {
                                    requestData.changeStatusSRM_INVALID_REQUEST("The provided Space Token does not exists");
                                    PtP.log.info("PtPChunk execution failed. The space token " + spaceToken
                                            + " provided by user does not exists");
                                    return false;
                                }
                                else
                                {
                                    fileStoRI.allotSpaceByToken(spaceToken, size);
                                }
                            }
                            else
                            {
                                // spaceToken is NOT empty but size IS!
                                // Use of EMPTY Space Size. That means total size of Storage
                                // Space will be used!!
                                PtP.log.debug("PtPChunk - ReserveSpaceStep: SpaceToken available and FileSize specified; reserving space by token...");
                                if (!isExistingSpaceToken(spaceToken))
                                {
                                    requestData.changeStatusSRM_INVALID_REQUEST("The provided Space Token does not exists");
                                    PtP.log.info("PtPChunk execution failed. The space token " + spaceToken
                                            + " provided by user does not exists");
                                    return false;
                                }
                                else
                                {
                                    fileStoRI.allotSpaceByToken(spaceToken);
                                }
                            }
            PtP.log.debug("PtPChunk: finished ReserveSpaceStep for " + fileStoRI.getAbsolutePath());
            return true;
        } catch(SecurityException e)
        {
            // file.createNewFile could not create file because the Java
            // SecurityManager did not grant
            // write premission! This indicates a possible conflict between a
            // local system administrator
            // who applied a strict local policy, and policies as specified by
            // the PolicyCollector!
            requestData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! During space reservation step in PtP, could not create file: "
                    + localFile.toString() + "; Java s SecurityManager does not allow writing the file! " + e);
            return false;
        } catch(IOException e)
        {
            // file.createNewFile could not create file because of a local IO
            // Error!
            requestData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! During space reservation step in PtP, an error occured while trying to create the file:"
                    + localFile.toString() + "; error: " + e);
            return false;
        } catch(it.grid.storm.filesystem.InvalidPermissionOnFileException e)
        {
            // I haven t got the right to create a file as StoRM user!
            // This is thrown when executing createNewFile method!
            requestData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! During space reservation step in PtP, an attempt to create file "
                    + localFile.toString()
                    + " failed because StoRM lacks the privileges to do so! Exception follows: " + e);
            return false;
        } catch(ReservationException e)
        {
            // Something went wrong while using space reservation component!
            requestData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk! Space component failed! Exception follows: " + e);
            return false;
        } catch(ExpiredSpaceTokenException e)
        {
            // The supplied space token is expired
            requestData.changeStatusSRM_SPACE_LIFETIME_EXPIRED("The provided Space Token has expired its lifetime");
            spacefailure = true; // gsm.expiredSpaceLifetimeChunk(chunkData);
            PtP.log.info("PtPChunk execution failed. ExpiredSpaceTokenException : " + e.getMessage());
            return false;
        } catch(Exception e)
        {
            // This could be thrown by Java from Filesystem component given that
            // there is GPFS under the hoods,
            // but I do not know exactly how java.io.File behaves with an ACL
            // capable filesystem!!
            requestData.changeStatusSRM_FAILURE("Space Management step in srmPrepareToPut failed!");
            failure = true; // gsm.failedChunk(chunkData);
            PtP.log.error("ERROR in PtPChunk - space Step! Unexpected error in reserve space step of PtP for file "
                    + localFile.toString() + "! Exception follows: " + e);
            PtP.log.error("Complete error stack trace follows: ");
            StackTraceElement[] ste = e.getStackTrace();
            if (ste != null)
            {
                for (StackTraceElement element : ste)
                {
                    PtP.log.error(element.toString());
                }
            }

            // ROOLBACK??
            // The file already exists!!!
            return false;
        }
    }

    private boolean isExistingSpaceToken(TSpaceToken spaceToken) throws Exception
    {
        StorageSpaceData spaceData = null;
        try
        {
            spaceData = new ReservedSpaceCatalog().getStorageSpace(spaceToken);
        } catch(TransferObjectDecodingException e)
        {
            log.error("Unable to build StorageSpaceData from StorageSpaceTO. TransferObjectDecodingException: "
                    + e.getMessage());
            throw new Exception(
                                "Error retrieving Storage Area information from Token. TransferObjectDecodingException : "
                                        + e.getMessage());
        } catch(DataAccessException e)
        {
            log.error("Unable to build get StorageSpaceTO. DataAccessException: " + e.getMessage());
            throw new Exception(
                                "Error retrieving Storage Area information from Token. DataAccessException : "
                                        + e.getMessage());
        }
        return spaceData != null;
    }

    private boolean managePermitSetFileStep(StoRI fileStoRI, TTURL turl) throws CannotMapUserException
    {
        if (requestData instanceof IdentityInputData)
        {
            if (managePermitSetFileStep(fileStoRI, 
                                         ((IdentityInputData) requestData).getUser().getLocalUser(), turl))
            {
                setDefaultAcl(fileStoRI, turl);
                setTapeManagementAcl(fileStoRI);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            setDefaultAcl(fileStoRI, turl);
            setTapeManagementAcl(fileStoRI);
            return true;
        }
    }
    
    /**
     * Private method used to setup the right file permission Returns false if something goes wrong!
     */
    private boolean managePermitSetFileStep(StoRI fileStoRI, LocalUser localUser, TTURL turl)
    {
        // BEWARE THAT READ PERMISSION IS NEEDED BECAUSE GRID_FTP SERVER _ALSO_
        // REQUIRES READ RIGHTS ELSE IT WON T BE ABLE TO WRITE THE FILE!!!
        PtP.log.debug("PtPChunk: entered SetFileStep for " + fileStoRI.getAbsolutePath());
        LocalFile localFile = fileStoRI.getLocalFile();
        FilesystemPermission fp = null;
        boolean effective = false;
        if (fileStoRI.hasJustInTimeACLs())
        {
            /*
             * here we can suppose (more or less) that the call fileStoRI.getLocalFile() never returns
             * null (code should be revised)
             */
            try
            {
                AclManagerFSAndHTTPS.getInstance().grantUserPermission(localFile, localUser,
                                                                       FilesystemPermission.ReadWrite);
            } catch(IllegalArgumentException e)
            {
                log.error("Unable to grant user write permission on the file. IllegalArgumentException: "
                        + e.getMessage());
            }
// localFile.grantUserPermission(localUser, FilesystemPermission.ReadWrite);
            fp = localFile.getEffectiveUserPermission(localUser);
            if (fp != null)
            {
                effective = fp.allows(FilesystemPermission.ReadWrite);
                if (effective)
                {
                    // ACL was correctly set up! Track JiT!
                    VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(), localUser,
                                                                 FilesystemPermission.Read, start,
                                                                 requestData.pinLifetime());
                    VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(), localUser,
                                                                 FilesystemPermission.Write, start,
                                                                 requestData.pinLifetime());
                }
                else
                {
                    PtP.log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite User-ACL to be set up on"
                            + localFile.toString() + "!");
                }
            }
            else
            {
                PtP.log.error("ERROR in PTP CHUNK! A ReadWrite User-ACL was set on "
                        + fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
                        + " but when subsequently verifying its effectivity, a null ACE was found!");
            }
        }
        else
        {
            try
            {
                AclManagerFSAndHTTPS.getInstance().grantGroupPermission(localFile, localUser,
                                                                        FilesystemPermission.ReadWrite);
            } catch(IllegalArgumentException e)
            {
                log.error("Unable to grant group permission on the file. IllegalArgumentException: "
                        + e.getMessage());
            }
// localFile.grantGroupPermission(localUser, FilesystemPermission.ReadWrite);
            fp = localFile.getEffectiveGroupPermission(localUser);
            if (fp != null)
            {
                effective = fp.allows(FilesystemPermission.ReadWrite);
                if (!effective)
                {
                    PtP.log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite Group-ACL to be set up on"
                            + localFile.toString() + "!");
                }
            }
            else
            {
                PtP.log.error("ERROR in PTP CHUNK! ReadWrite Group-ACL was set on "
                        + fileStoRI.getAbsolutePath() + " for group " + localUser.toString()
                        + " but when subsequently verifying its effectivity, a null ACE was found!");
            }
        }
        if (fp == null)
        {
            /*
             * This is a programming bug and should not occur! An attempt
             * was just made to grant Read
             * permission; so regardless of the umask allowing the
             * permission or not, there must be at least an ACE
             * present! Yet none was found!
             */
            requestData.changeStatusSRM_FAILURE("Local filesystem has problems manipulating ACE!");
            failure = true; // gsm.failedChunk(chunkData);
            return false;
        }
        if (!effective)
        {
            /*
             * mask preset in filesystem does not allow the setting up of
             * the required permissions!
             * Request fails!
             */
            requestData.changeStatusSRM_FAILURE("Local filesystem mask does not allow setting up correct ACLs for PtP!");
            failure = true; // gsm.failedChunk(chunkData);
            return false;
        }
        return true;
    }
    
    private void setDefaultAcl(StoRI fileStoRI, TTURL turl)
    {
        DefaultACL dacl = fileStoRI.getVirtualFileSystem().getCapabilities().getDefaultACL();
        if (dacl != null && !dacl.isEmpty())
        {
            for (ACLEntry ace : dacl.getACL())
            {
                if (ace.isValid())
                {
                    PtP.log.debug("Adding DefaultACL for the gid: " + ace.getGroupID()
                            + " with permission: " + ace.getFilePermissionString());
                    LocalUser u = new LocalUser(ace.getGroupID(), ace.getGroupID());
                    if (ace.getFilesystemPermission() == null)
                    {
                        log.warn("Unable to setting up the ACL. ACl entry permission is null!");
                    }
                    else
                    {
                      //localFile.grantGroupPermission(u, ace.getFilesystemPermission());
                        try
                        {
                            AclManagerFSAndHTTPS.getInstance().grantGroupPermission(fileStoRI.getLocalFile(), u, ace.getFilesystemPermission());
                        } catch(IllegalArgumentException e)
                        {
                            log.error("Unable to grant group permission on the file. IllegalArgumentException: "
                                    + e.getMessage());
                        }
                    }
                }
            }

        }
    }
    
    private void setTapeManagementAcl(StoRI fileStoRI)
    {
        if (fileStoRI.getVirtualFileSystem().getStorageClassType().isTapeEnabled())
        {
            // Compute the Expiration Time in seconds
            long expDate = (System.currentTimeMillis() / 1000 + requestData.pinLifetime().value());
            StormEA.setPinned(fileStoRI.getLocalFile().getAbsolutePath(), expDate);
            // set group permission for tape quota management
            fileStoRI.setGroupTapeWrite();
        }
    }

    /**
     * Method that handles the case of either Create or Write policies are Deny: beware that only one policy
     * needs to be
     * Deny for this method to get invoked!
     */
    private void manageDeny()
    {
        requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Create/Write access to " + requestData.getSURL()
                + " denied!");
        failure = true; // gsm.failedChunk(chunkData);
        PtP.log.debug("Create/Write access to " + requestData.getSURL() + " for user " + DataHelper.getRequestor(requestData)
                + " denied!"); // info
    }

    /**
     * Private method that handles all cases where: new states are added to AuthorizationCollector but this
     * PtP logic
     * does not get updated, or there are missing policies, or there are problems with the PolicyCollector.
     */
    private void manageAnomaly(AuthzDecision decision)
    {

        if(decision == null)
        {
            requestData.changeStatusSRM_FAILURE("Null policy! internal error");
            failure = true; 
            PtP.log.error("PtPChunk: Null policy! internal error!");
            return;
        }
        switch (decision)
        {
            case NOT_APPLICABLE:
                // Missing policy
                requestData.changeStatusSRM_FAILURE("Missing Policy! Access rights cannot be established!");
                failure = true; 
                PtP.log.error("PtPChunk: PolicyCollector warned of missing policies for the supplied SURL!");
                PtP.log.error("Requested SURL: " + requestData.getSURL());
                break;
            case INDETERMINATE:
                // PolicyCollector error
                requestData.changeStatusSRM_FAILURE("PolicyCollector error! Access rights cannot be established!");
                failure = true; 
                PtP.log.error("PtPChunk: PolicyCollector encountered internal problems!");
                PtP.log.error("Requested SURL: " + requestData.getSURL());
                break;
            default:
                // Unexpected policy!
                requestData.changeStatusSRM_FAILURE("Unexpected Policy! StoRM does not know what to do!");
                failure = true; 
                PtP.log.error("PtPChunk: unexpected policy returned by PolicyCollector for the supplied SURL!");
                PtP.log.error("Requested SURL: " + requestData.getSURL());
                break;
        }
    }

    /**
     * Private method that handles the case of overwriting of an existing file: this gets invoked if
     * TOverwriteMode is
     * set to Always or WhenFilesAreDifferet, and a file already exists. Notice that the logic for permit is
     * exactly the
     * same as that for the case of non existent files! This is because the placeHolder/reserveSpace functions
     * of the
     * FileSystem object, in case of existing files, they erase it and create a new mock file - thereby
     * following the
     * SRM specs that no append is possible. So the only difference between the two cases is the kind of
     * policies that
     * are checked before going on and carrying out the processing.
     */
    private void manageOverwriteExistingFile(StoRI fileStoRI)
    {
        AuthzDecision decision;
        if (requestData instanceof IdentityInputData)
        {
            decision = AuthzDirector.getPathAuthz().authorize(((IdentityInputData) requestData).getUser(),
                                                              SRMFileRequest.RM, fileStoRI);
        }
        else
        {
            decision = AuthzDirector.getPathAuthz()
                                    .authorizeAnonymous(SRMFileRequest.RM, fileStoRI.getStFN());
        }
        if (decision.equals(AuthzDecision.PERMIT))
        {
            managePermit(fileStoRI);
        }
        else
        {
            if (decision.equals(AuthzDecision.DENY))
            {
                requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Write access to " + requestData.getSURL()
                        + " denied!");
                failure = true; // gsm.failedChunk(chunkData);
                log.debug("Write access to " + requestData.getSURL() + " for user "
                        + DataHelper.getRequestor(requestData) + " denied!");
            }
            else
            {
                manageAnomaly(decision);
            }
        }
    }

    /**
     * Method that supplies a String describing this PtPChunk - for scheduler Log purposes! It returns the
     * request token
     * and the SURL asked for in This request.
     */
    public String getName()
    {
        return "PtP for SURL " + requestData.getSURL();
    }

    /**
     * Method used in a callback fashion in the scheduler for separately handling PtG, PtP and Copy chunks.
     */
    public void choose(Streets s)
    {
        s.ptpStreet(this);
    }

    /* (non-Javadoc)
     * @see it.grid.storm.asynch.Request#getSURL()
     */
    @Override
    public String getSURL()
    {
        return requestData.getSURL().toString();
    }

    /* (non-Javadoc)
     * @see it.grid.storm.asynch.Request#isResultSuccess()
     */
    @Override
    public boolean isResultSuccess()
    {
        boolean result = false;
        TStatusCode statusCode = requestData.getStatus().getStatusCode();
        if ((statusCode.getValue().equals(TStatusCode.SRM_SPACE_AVAILABLE.getValue()))
                || (requestData.getStatus().isSRM_SUCCESS()))
        {
            result = true;
        }
        return result;
    }

    /**
     * @return the requestData
     */
    public PtPData getRequestData()
    {
        return requestData;
    }
    
    @Override
    public String getUserDN()
    {
        return DataHelper.getRequestor(requestData);
    }
    
    protected void printRequestOutcome(PtPData inputData)
    {
        if(inputData != null)
        {
            if (inputData.getSURL() != null)
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, inputData.getStatus(), inputData,
                                                      inputData.getRequestToken(),
                                                      Arrays.asList(inputData.getSURL().toString()));
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, inputData.getStatus(), inputData,
                                                      Arrays.asList(inputData.getSURL().toString()));
                }

            }
            else
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, inputData.getStatus(), inputData,
                                                      inputData.getRequestToken());
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, inputData.getStatus(), inputData);
                }
            }

        }
        else
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log,
                                              CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                                        "No input available"));
        }
    }
}
