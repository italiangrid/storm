/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.asynch;

import java.util.Map;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.scheduler.Chooser;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.Streets;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a chunk of an srmBringOnLine request: it handles a single file of a
 * multifile/directory-expansion request. StoRM then sends the chunk to a chunk-scheduler. Security checks performed as
 * follows: both in the JiT and AoT approach, policies are checked to see if the Griduser has read rights on the
 * requested SURL. If the AuthorisationCollector replies with an isDeny, then the request fails with
 * SRM_AUTHORIZATION_FAILURE status. If the AuthorisationCollector replies with isIndeterminate, then the request fails
 * with SRM_FAILURE and explanation string "Failure in PolicySource prevented PolicyCollector from establishing access
 * rights! Processing failed!"; a message gets logged as well. If the AuthorisationCollector replies with
 * isNotApplicabale, then the request fails with SRM_FAILURE and error string "No policies found for the requested SURL!
 * Therefore access rights cannot be established! Processing cannot continue!"; a message gets logged as well. If the
 * AuthorisationCollector returns a state for which there is no business logic, then the request fails again with
 * SRM_FAILURE and error string "Unexpected authorization state! Processing failed!"; a message gets logged. If the
 * AuthorisationCollector returns an isPermit, then processing continues as follows: (1) The local file that corresponds
 * to the supplied SURL is determined, together with the local user to whom the grid credentials get mapped; the TURL
 * finally gets constructed. If the local file does not exist the request fails with SRM_INVALID_PATH and corresponding
 * explanation string; if the user cannot be mapped locally, the request fails with SRM_FAILURE and an explanation
 * String which includes the DN used for maping; if there are internal problems constructing the TURL again the request
 * fails with SRM_FAILURE. Appropriate error messages get logged. (2) Traverse permissions get set on all parent
 * directories to allow access to the file. The operation may fail for several reasons: the file or any of the parent
 * directories may have been removed resulting in SRM_INVALID_PATH; StoRM cannot set the requested permissions because a
 * filesystem mask does not allow the permissions to be set up; StoRM may be configured for the wrong filesystem; StoRM
 * has not got the right permissions to manipulate the ACLs on the filesystem; StoRM may have encountered an unexpected
 * error when working with the filesystem. In all these circumstances, the status changes to SRM_FAILURE, together with
 * an appropriate explanation String, and a respective log message. (3) The file size is determined. The operation may
 * fail and hence the request too gets failed, in the following circumstances: the file somehow does not exist, the path
 * to the file is not found, an error while communicating with the underlaying FileSystem, or a JVM SecurityManager
 * forbids such operation. In the first two cases the state changes to SRM_INVALID_PATH, while in the other ones it
 * changes to SRM_FAILURE; proper error strings explain the situation further. Error messages get logged. (3) If AoT
 * acls are in place, then the PinnedFilesCatalog is asked to pinExistingVolatileEntry, that is, it is asked to pin the
 * entry if it is already present thereby extending its lifetime (if it is not present, it just means that the requested
 * file is PERMANENT and there is no need to pin it); status changes to SRM_FILE_PINNED. (4) If it is the JiT that is in
 * place, a ReadACL is first added, the ACL gets tracked by calling the proper method in the PinnedFilesCatalog, and the
 * file gets pinned by invoking pinExistingVolatileEntry; the status changes to SRM_FILE_PINNED. The addition of the ACL
 * could go wrong in several ways: the file may not exist, the ACL could not be set up because there is a mask that does
 * not allow the Read permission to be set, StoRM does not have the permission to manipulate the ACLs, StoRM was not
 * configured for the underlying FileSystem, or there was an unexpected error; in the first case the status changes to
 * SRM_INVALID_PATH, while in all other ones it changes to SRM_FAILURE; corresponding messages get logged.
 * 
 * @author CNAF
 * @date Aug 2009
 * @version 1.0
 */
public class BoL implements Delegable, Chooser, Request, Suspendedable {

    private static Logger log = LoggerFactory.getLogger(BoL.class);

    /** GridUser that made the request */
    protected GridUserInterface gu = null;
    /** BoLChunkData that holds the specific info for this chunk */
    protected BoLPersistentChunkData requestData = null;
    /** boolean that indicates a chunk failure */
    protected boolean failure = false;

    /**
     * variables used to backup values in case the request is suspended waiting for the file to be recalled from the
     * tape
     */
    private LocalFile bupLocalFile;

    /**
     * Constructor requiring the GridUser, the RequestSummaryData and the BoLChunkData about this chunk. If the supplied
     * attributes are null, an InvalidBoLChunkAttributesException is thrown.
     */
    public BoL(GridUserInterface gu, BoLPersistentChunkData chunkData) throws InvalidRequestAttributesException {
        if (gu == null || chunkData == null) {
            throw new InvalidRequestAttributesException(gu, chunkData);
        }
        this.gu = gu;
        this.requestData = chunkData;
    }

    /**
     * Method used in a callback fashion in the scheduler for separately handling PtG, BoL, PtP and Copy chunks.
     */
    public void choose(Streets s) {
        s.bolStreet(this);
    }

    /* (non-Javadoc)
     * @see it.grid.storm.asynch.SuspendedChunk#completeRequest(it.grid.storm.tape.recalltable.model.RecallTaskStatus)
     */
    public Boolean completeRequest(TapeRecallStatus recallStatus){

        boolean requestSuccessfull = false;
        if (recallStatus == TapeRecallStatus.SUCCESS)
        {
            try
            {
                if (bupLocalFile.isOnDisk())
                {
                    requestData.changeStatusSRM_SUCCESS("File recalled from tape");
                    requestSuccessfull = true;
                }
                else
                {
                    log.error("File "
                            + bupLocalFile.getAbsolutePath()
                            + " not found on the disk, but it was reported to be successfully recalled from tape");
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
            if (recallStatus == TapeRecallStatus.ABORTED)
            {
                requestData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
            }
            else
            {
                requestData.changeStatusSRM_FAILURE("Error recalling file from tape");
            }
        }
        return requestSuccessfull;
    }

    /**
     * Method that handles a chunk. It is invoked by the scheduler to carry out the task.
     */
    public void doIt() {

        log.info("Handling BoL chunk for user DN: " + gu.getDn() + "; for SURL: " + requestData.getSURL());

        if(!verifySurlStatusTransition(requestData.getSURL(), requestData.getGeneratedRequestToken()))
        {
            failure = true;
            requestData.changeStatusSRM_FILE_BUSY("Requested file is"
                                                  + " busy (in an incompatible state with BOL)");
            log.info("Unable to perform the BOL request, surl busy");
            printOutcome(gu.getDn(),requestData.getSURL(),requestData.getStatus());
            return;
        }
//        if (PtPChunkCatalog.getInstance().isSRM_SPACE_AVAILABLE(requestData.getSURL())) {
//
//            requestData.changeStatusSRM_FILE_BUSY("Requested file is still in SRM_SPACE_AVAILABLE state!");
//            failure = true;
//            log.debug("ATTENTION in BoLChunk! BoLChunk received request for SURL that is still in SRM_SPACE_AVAILABLE state!");
//
//        }
        else {
            try {

                StoRI fileStoRI = null;
                try
                {
                fileStoRI = NamespaceDirector.getNamespace()
                                                   .resolveStoRIbySURL(requestData.getSURL(), gu);
                } catch(IllegalArgumentException e)
                {
                    failure = true;
                    requestData.changeStatusSRM_INTERNAL_ERROR("Unable to get StoRI for surl "
                            + requestData.getSURL());
                    log.error("Unable to get StoRI for surl "
                              + requestData.getSURL() + " IllegalArgumentException: " + e.getMessage());
                }
                if(!failure)
                {
                    SpaceHelper sp = new SpaceHelper();
                    TSpaceToken token = sp.getTokenFromStoRI(log, fileStoRI);
                    SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);
    
                    if (spaceAuth.authorize(gu, SRMSpaceRequest.BOL))
                    {
                        manageIsPermit(fileStoRI);
                    }
                    else
                    {
                        failure = true;
                        requestData.changeStatusSRM_AUTHORIZATION_FAILURE("Space authoritazion denied "
                                + requestData.getSURL() + " in Storage Area: " + token);
                        log.debug("Read access to " + requestData.getSURL() + " in Storage Area: " + token
                                + " denied!");
                    }
                }
            } catch (NamespaceException e) {
                // The Supplied SURL does not contain a root that could be identified by the StoRI factory
                // as referring to a VO being managed by StoRM... that is SURLs begining with such root
                // are not handled by this SToRM!
                requestData.changeStatusSRM_INVALID_PATH("The path specified in the SURL does not have a local equivalent!");
                failure = true;
                log.debug("ATTENTION in BoLChunk! BoLChunk received request for a SURL whose root is not recognised by StoRI! "
                        + e);
            }
        }
        printOutcome(gu.getDn(),requestData.getSURL(),requestData.getStatus());
    }

    public BoLPersistentChunkData getRequestData() {
        return requestData;
    }

    /**
     * Method that supplies a String describing this BoLChunk - for scheduler Log purposes! It returns the request token
     * and the SURL that was asked for.
     */
    public String getName() {
        return "BoLChunk for SURL " + requestData.getSURL();
    }


    public String getSURL() {
        return requestData.getSURL().toString();
    }

    public String getUserDN() {
        return gu.getDn();
    }

    public boolean isResultSuccess() {
        boolean result = false;
        TStatusCode statusCode = requestData.getStatus().getStatusCode();
        if ((statusCode.getValue().equals(TStatusCode.SRM_FILE_PINNED.getValue()))
                || requestData.getStatus().isSRM_SUCCESS()) {
            result = true;
        }
        return result;
    }

    private void backupData(LocalFile localFile) {
        bupLocalFile = localFile;
    }

    /**
     * Manager of the IsPermit state: the user may indeed read the specified SURL
     */
    private void manageIsPermit(StoRI fileStoRI) {

        LocalFile localFile = fileStoRI.getLocalFile();

        try {
            if ((!localFile.exists()) || (localFile.isDirectory())) {
                // File does not exist, or it is a directory! Fail request with
                // SRM_INVALID_PATH!
                requestData.changeStatusSRM_INVALID_PATH("The requested file either does not exist, or it is a directory!");
                failure = true;
                log.debug("BoLChunk: the requested file either does not exist, or it is a directory!");

            } else { // File exists and it is not a directory

                if (fileStoRI.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {

                    // Compute the Expiration Time in seconds
                    //Add the deferred start time to the expiration date
                    long expDate = (System.currentTimeMillis() / 1000 + (requestData.getLifeTime().value() + requestData.getDeferredStartTime()));
                    StormEA.setPinned(localFile.getAbsolutePath(), expDate);

                    // set group permission for tape quota management
                    fileStoRI.setGroupTapeRead();
                    requestData.setFileSize(TSizeInBytes.make(localFile.length(), SizeUnit.BYTES));

                    if (isStoriOndisk(fileStoRI)) {

                        requestData.changeStatusSRM_SUCCESS("srmBringOnLine successfully handled!");

                    } else {

                        requestData.changeStatusSRM_REQUEST_INPROGRESS("Recalling file from tape");

                        String voName = null;
                        if (gu instanceof AbstractGridUser)
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
                        backupData(localFile);
                    }
                } else {
                    requestData.changeStatusSRM_NOT_SUPPORTED("Tape not supported for this filesystem");
                }

            }
        } catch (SecurityException e) {
            // The check for existence of the File failed because there is a SecurityManager installed that
            // denies read privileges for that File! Perhaps the local system administrator of StoRM set
            // up Java policies that contrast policies described by the PolicyCollector! There is a conflict here!
            requestData.changeStatusSRM_FAILURE("StoRM is not allowed to work on requested file!");
            failure = true;
            log.error("ATTENTION in BoLChunk! BoLChunk received a SecurityException from Java SecurityManager; StoRM cannot check-existence or check-if-directory for: "
                    + localFile.toString() + "; exception: " + e);
        } catch (Exception e) {
            requestData.changeStatusSRM_FAILURE("StoRM encountered an unexpected error!");
            failure = true;
            log.error("ERROR in BoLChunk! StoRM process got an unexpected error! " + e);
        }
    }
    
    
    private boolean isStoriOndisk(StoRI storiFile) throws FSException {
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
    
    private boolean verifySurlStatusTransition(TSURL surl, TRequestToken requestToken)
    {
        Map<TRequestToken, TReturnStatus> statuses = SurlStatusManager.getSurlCurrentStatuses(surl);
        statuses.remove(requestToken);
        return TStatusCode.SRM_FILE_PINNED.isCompatibleWith(statuses.values());
    }
    
    private void printOutcome(String dn, TSURL surl, TReturnStatus status)
    {
        log.info("Finished handling BoL chunk for user DN: " + dn + "; for SURL: "
                     + surl + "; result is: " + status);
    }
}
