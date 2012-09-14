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

package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.SurlStatusStore;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneOutputData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * @date = Oct 10, 2008
 */

public class PutDoneCommand extends DataTransferCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(PutDoneCommand.class);
    private static final String funcName = "PutDone: ";

    private static final Set<String> lockedSurls = new HashSet<String>();
    private static final Object lock = new Object();

    private static TReturnStatus anotherPutDoneActiveReturnStatus;

    private final PutDoneOutputData outputData = new PutDoneOutputData();
    private TReturnStatus globalStatus = null;
    private boolean requestFailure;
    private boolean requestSuccess = true;
    private boolean requestAborted = true;

    static {
        try {
            anotherPutDoneActiveReturnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                                 "There is another PutDone in execution on this SURL.");
        } catch (InvalidTReturnStatusAttributeException e) {
            // Never thrown
            anotherPutDoneActiveReturnStatus = null;
        }
    }

    public PutDoneCommand() {
    };

    /**
     * Executes the PutDone for the given SURLs. Function called also from the purge thread of PtPChunkCatalog.
     * 
     * @param spaceAvailableSURLs list of SURLs for which to execute the PutDone.
     * @param arrayOfFileStatus array of file status for each of the given SURL (<code>null</code> when called from the
     *            purge thread of PtPChunkCatalog)
     * @param user the user requesting the PutDone (<code>null</code> when called from the purge thread of
     *            PtPChunkCatalog)
     * @param localUser the local user associate to the Grid user (<code>null</code> when called from the purge thread
     *            of PtPChunkCatalog)
     */
    public static void executePutDone(List<TSURL> spaceAvailableSURLs,
            ArrayOfTSURLReturnStatus arrayOfFileStatus, GridUserInterface user, LocalUser localUser) {

//        VolatileAndJiTCatalog volatileAndJiTCatalog = VolatileAndJiTCatalog.getInstance();

        // Now spaceAvailableSURLs contains all the SURLs for which to do a PutDone. To execute this
        // operation
        // three steps are needed for each SURL:
        // 1- if the SURL is volatile than an entry in the table Volatile must be created;
        // 2- JiTs must me removed from the TURL;
        // 3- compute the checksum and store it in an extended attribute
        // 4- Tape stuff management.
        // 5- Update FreeSpace into DB
        // 6- The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        for (int i = 0; i < spaceAvailableSURLs.size(); i++) {

//            ReducedPtPChunkData chunkData = spaceAvailableSURLs.get(i);
            TSURL surl = spaceAvailableSURLs.get(i);
//            if (chunkData == null) {
            if (surl == null) {
                continue;
            }

//            TSURL surl = chunkData.toSURL();

            if (user == null) {

                boolean goOnWithPutDone = lockSurl(surl);

                if (goOnWithPutDone) {
                    log.info("Executing implicit PutDone for SURL: "
//                            + spaceAvailableSURLs.get(i).toSURL().getSURLString());
                             + surl.getSURLString());
                } else {
                    continue;
                }

            } else {
//                log.info("Executing PutDone for SURL: " + spaceAvailableSURLs.get(i).toSURL().getSURLString());
                log.info("Executing PutDone for SURL: " + surl.getSURLString());
            }

            StoRI stori = null;
            // Retrieve the StoRI associate to the SURL
            try {
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                if (user == null) {
                    stori = namespace.resolveStoRIbySURL(surl);
                } else {
                    try
                    {
                        stori = namespace.resolveStoRIbySURL(surl, user);
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.error(funcName + "unable to build STORI from surl=" + surl + " user=" + user);
                        continue;
                    }
                }

                // 1- if the SURL is volatile update the entry in the Volatile table
                if (VolatileAndJiTCatalog.getInstance().exists(stori.getPFN())) {
                    try
                    {
                        VolatileAndJiTCatalog.getInstance().setStartTime(stori.getPFN(),
                                                            Calendar.getInstance());
                    } catch(Exception e)
                    {
                        //impossible because of the "exists" check
                    }
                }

                // 2- JiTs must me removed from the TURL;
                if (stori.hasJustInTimeACLs()) {
                    log.debug(funcName + "JiT case, removing ACEs on SURL: " + surl.toString());
                    // Retrieve the PFN of the SURL parents
                    List<StoRI> storiParentsList = stori.getParents();
                    List<PFN> pfnParentsList = new ArrayList<PFN>(storiParentsList.size());

                    for (StoRI parentStoRI : storiParentsList) {
                        pfnParentsList.add(parentStoRI.getPFN());
                    }

                    if (localUser == null) {
                        VolatileAndJiTCatalog.getInstance().expirePutJiTs(stori.getPFN(), localUser);
                    } else {
                        VolatileAndJiTCatalog.getInstance().removeAllJiTsOn(stori.getPFN());
                    }
                }

                // 3- compute the checksum and store it in an extended attribute
                LocalFile localFile = stori.getLocalFile();

                if (Configuration.getInstance().getChecksumEnabled()) {

                    boolean checksumComputed = localFile.setChecksum();
                    if (!checksumComputed) {
                        if (arrayOfFileStatus != null) {
                            arrayOfFileStatus.getTSURLReturnStatus(i)
                                             .getStatus()
                                             .setExplanation("Warning: failed to compute the checksum "
                                                     + "(checksum computation is enabled in the server)");
                        } else {
                            log.warn("Error comuting checksum for file: " + localFile.getAbsolutePath());
                        }
                    } else {
                        log.debug("Checksum set to SURL:" + surl.toString());
                    }

                }

                // 4- Tape stuff management.
                if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {
                    String fileAbosolutePath = localFile.getAbsolutePath();
                    StormEA.removePinned(fileAbosolutePath);
                    StormEA.setPremigrate(fileAbosolutePath);
                }

                // 5- Update FreeSpace into DB
                /**
                 * If Storage Area hard limit is enabled, update space on DB
                 */
                try {
                    VirtualFSInterface fs = stori.getVirtualFileSystem();
                    if ((fs != null) && (fs.getProperties().isOnlineSpaceLimited())) {
                        SpaceHelper sh = new SpaceHelper();
                        // Update the used space into Database
                        sh.decreaseFreeSpaceForSA(log, funcName, user, surl);
                    }
                } catch (NamespaceException e) {
                    log.warn(funcName
                            + " Not able to build the virtual fs properties for checking Storage Area size enforcement!");
                }
                
            } catch (NamespaceException e1) {
                log.debug(funcName + "Unable to build StoRI by SURL: " + surl.toString(), e1);
                continue;
            }
        }
    }

    /**
     * Implements the srmPutDone. Used to notify the SRM that the client completed a file transfer to the TransferURL in
     * the allocated space (by a PrepareToPut).
     */
    public OutputData execute(InputData absData) {

        PutDoneInputData inputData = (PutDoneInputData) absData;

        log.debug(funcName + "Started.");

        /******************** Check for malformed request: missing mandatory input parameters ****************/
        requestFailure = false;
        if (inputData == null) {
            requestFailure = true;
            //a request without token is assumed to be a synchronous one
//        } else if (inputData.getRequestToken() == null) {
//            requestFailure = true;
        } else if (inputData.getArrayOfSURLs() == null) {
            requestFailure = true;
        } else if (inputData.getArrayOfSURLs().size() < 1) {
            requestFailure = true;
        }
        if (requestFailure) {
            log.debug(funcName + "Invalid input parameter specified");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Missing mandatory parameters");
            } catch (InvalidTReturnStatusAttributeException e) {
                log.warn(funcName + "dataTransferManger: Error creating returnStatus " + e);
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);

            if (inputData == null) {
                log.error("srmPutDone: <> Request for [token:] [SURL:] failed with [status: " + globalStatus
                        + "]");
            } else {
                log.error("srmPutDone: <" + inputData.getUser() + "> Request for [token:"
                        + inputData.getRequestToken() + "] for [SURL:" + inputData.getArrayOfSURLs()
                        + "]failed with [status: " + globalStatus + "]");
            }

            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            log.error(funcName + "the user field is NULL");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                 "Unable to get user credential!");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.debug(funcName + "dataTransferManger: Error creating returnStatus " + ex1);
            }

            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmPutDone: <" + inputData.getUser() + "> Request for [token:"
                    + inputData.getRequestToken() + "] for [SURL:" + inputData.getArrayOfSURLs()
                    + "]failed with [status: " + globalStatus + "]");
            return outputData;
        }

        /* Maps the VOMS Grid user into Local User */
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        } catch (CannotMapUserException e) {
            log.debug(funcName + "Unable to map the user '" + user + "' in a local user.", e);
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                                 "Unable to map the user");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                // Nothing to do, it will never be thrown
                log.error(funcName + "dataTransferManger: Error creating returnStatus ");
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmPutDone: <" + inputData.getUser() + "> Request for [token:"
                    + inputData.getRequestToken() + "] for [SURL:" + inputData.getArrayOfSURLs()
                    + "]failed with [status: " + globalStatus + "]");
            return outputData;
        }

        /********************************** Start to manage the request ***********************************/
        TRequestToken requestToken = inputData.getRequestToken();
        List<TSURL> listOfSURLs = inputData.getArrayOfSURLs().getArrayList();
        ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();

        /*
         * spaceAvailableSURLs will contain all the SURLs that must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS,
         * i.e. the not-null elements.
         */
        List<TSURL> spaceAvailableSURLs = new ArrayList<TSURL>(listOfSURLs.size());
        if (!findCandidateSURLs(requestToken,
                                outputData,
                                user,
                                listOfSURLs,
                                spaceAvailableSURLs,
                                arrayOfFileStatus)) {
            return outputData;
        }

        executePutDone(spaceAvailableSURLs, arrayOfFileStatus, user, lUser);

     // The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        if(requestToken != null)
        {
            PtPChunkCatalog.getInstance().transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(requestToken, spaceAvailableSURLs);    
        }
        else
        {
            for(TSURL surl : spaceAvailableSURLs)
            {
                if(surl != null)
                {
                    SurlStatusStore.getInstance().storeSurlStatus(surl, TStatusCode.SRM_SUCCESS);
                }
            }
        }
        

        /* Unlock the SURLs */
        for (TSURL surl : spaceAvailableSURLs) {
            if (surl != null) {
                unlockSurl(surl);
            }
        }
        
        log.trace("Number of SURLs locked: " + lockedSurls.size());
        
        /* Set the request status */
        try {
            if (requestAborted) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_ABORTED, "Request "
                        + requestToken.toString());
            } else if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
            } else if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
                                                 "All file requests are successfully completed");
            } else {
                globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                 "Details are on the file statuses");
            }

            log.debug(funcName + "Finished with status: " + globalStatus.toString());
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
        }

        if (globalStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
            log.info("srmPutDone: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] for [SURL:" + inputData.getArrayOfSURLs() + "] successfully done with: [status:"
                    + globalStatus + "]");
        } else if (globalStatus.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
            log.info("srmPutDone: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] for [SURL:" + inputData.getArrayOfSURLs() + "] partially done with: [status:"
                    + globalStatus + "]");
        } else {
            log.error("srmPutDone: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] for [SURL:" + inputData.getArrayOfSURLs() + "] failed with: [status:" + globalStatus
                    + "]");

        }

        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(arrayOfFileStatus);

        return outputData;
    }

    /**
     * A 1:1 match is guaranteed among the Lists: inputSURLs, spaceAvailableSURLs and arrayOfFileStatus. That means the
     * list spaceAvailableSURLs may contain null elements corresponding to the SURLs not in the SPACE_AVAILABLE state
     * (for these entries the corresponding element in arrayOfFileStatus is properly set).
     * 
     * @param requestToken
     * @param outputData
     * @param user
     * @param inputSURLs
     * @param spaceAvailableSURLs
     * @param arrayOfFileStatus
     * @return
     */
    private boolean findCandidateSURLs(TRequestToken requestToken, PutDoneOutputData outputData,
            GridUserInterface user, List<TSURL> inputSURLs, List<TSURL> spaceAvailableSURLs,
            ArrayOfTSURLReturnStatus arrayOfFileStatus) {

        if(requestToken != null)
        {
            log.debug(funcName + " requestToken=" + requestToken);    
        }
        else
        {
            log.debug(funcName + " for a synchronous PTP");
        }
        
        if (requestToken != null)
        {
            /* Query the DB to find all the SURLs associated to the request token */
            Collection<ReducedPtPChunkData> chunks = PtPChunkCatalog.getInstance()
                                                                    .lookupReducedPtPChunkData(requestToken);
            if (chunks.isEmpty())
            { // No SURLs found
                log.debug(funcName + " Invalid request token");
                try
                {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
                } catch(InvalidTReturnStatusAttributeException ex)
                {
                    // Nothing to do, it will never be thrown
                    log.debug(funcName + "dataTransferManger: Error creating returnStatus " + ex);
                }

                outputData.setReturnStatus(globalStatus);
                outputData.setArrayOfFileStatuses(null);

                StringBuffer sb = new StringBuffer();
                for (TSURL surl : inputSURLs)
                {
                    sb.append(surl.toString() + " ");
                }

                log.error("srmPutDone: <" + user + "> Request for [token:" + requestToken + "] for [SURL:"
                        + sb.toString() + "]failed with [status: " + globalStatus + "]");

                return false;
            }

            /*
             * Select the SURLs to execute a PutDone on (the following for loop). Each requested SURL must be
             * found in the
             * catalog (as a SURL associated with the specified request token), otherwise the return status of
             * the missing
             * SURL indicates the error.
             */

            requestFailure = true;
            requestSuccess = true;
            requestAborted = true;

            /*
             * ArrayOfSURLs is a mandatory parameter, therefore "num_SURLs" is > 0 and inside the for loop the
             * two boolean
             * variables "requestFailure" and "requestSuccess" are correctly set.
             */
            TReturnStatus status = null;
            boolean found;
            for(TSURL surl : inputSURLs)
            {

                log.debug(funcName + "Checking SURL " + surl);

                // Search SURL[i] (specified in the request) inside the catalog
//                ReducedPtPChunkData auxChunkData = null;
                found = false;
                for (ReducedPtPChunkData chunk : chunks)
                {
                    if (surl.equals(chunk.toSURL()))
                    {
//                        auxChunkData = chunk;
                        status = chunk.status();
                        found = true;
                        break;
                    }
                }

//                TReturnStatus surlReturnStatus = null;
                boolean surlIsSpaceAvailable = false;
//                try
//                {
//                    if (auxChunkData != null)
                    if(found)
                    { // SURL found, select it for PutDone
                        log.debug(funcName + "SURL \'" + surl + "\' found!");
//                        surlReturnStatus = auxChunkData.status();

                        if (status.getStatusCode() == TStatusCode.SRM_SPACE_AVAILABLE)
                        {

                            boolean goOnWithPutDone = lockSurl(surl);

                            if (goOnWithPutDone)
                            {
                                try
                                {
                                    status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                                } catch(InvalidTReturnStatusAttributeException e)
                                {
                                    //never thrown
                                }
                                // Select the SURL, the DB will be updated at once after
                                spaceAvailableSURLs.add(surl);
                                surlIsSpaceAvailable = true;
                                requestFailure = false;
                                requestAborted = false;
                            }
                            else
                            { // there is an active PutDone on this SURL
                                status = anotherPutDoneActiveReturnStatus;
                                requestSuccess = false;
                                requestAborted = false;
                            }

                        }
                        else
                        {
                            if (status.getStatusCode() == TStatusCode.SRM_SUCCESS)
                            {
                                try
                                {
                                    status = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR, "");
                                } catch(InvalidTReturnStatusAttributeException e)
                                {
                                    //never thrown
                                }
                                requestSuccess = false;
                                requestAborted = false;

                            }
                            else
                            {
                                if (status.getStatusCode() == TStatusCode.SRM_ABORTED)
                                {
                                    try
                                    {
                                        status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                                             "PtP status for this SURL is SRM_ABORTED");
                                    } catch(InvalidTReturnStatusAttributeException e)
                                    {
                                        //never thrown
                                    }
                                    requestSuccess = false;

                                }
                                else
                                {
                                    try
                                    {
                                        status = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                                             "Check StatusOfPutRequest for more information");
                                    } catch(InvalidTReturnStatusAttributeException e)
                                    {
                                        //never thrown
                                    }
                                    requestSuccess = false;
                                    requestAborted = false;
                                }
                            }
                        }
                    }
                    else
                    { // SURL not found, set the corresponding return status value
                        log.debug(funcName + "SURL \'" + surl + "\' NOT found in the DB!");
                        try
                        {
                            status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                                 "SURL does not refer to an existing file for the specified request token");
                        } catch(InvalidTReturnStatusAttributeException e)
                        {
                            //never thrown
                        }
                        requestSuccess = false;
                        requestAborted = false;
                    }
                    
//                }
//                catch(InvalidTReturnStatusAttributeException e)
//                {
//                    // Nothing to do, it will never be thrown
//                    surlReturnStatus = null;
//                    log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
//                }
                if (!surlIsSpaceAvailable)
                { // Keep 1:1 match with arrayOfFileStatus
                    spaceAvailableSURLs.add(null);
                }

                /* Add the TSURLReturnStatus of SURL[i] to arrayOfFileStatus */
                try
                {
                    if (status == null)
                    {
                        status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                             "Internal error: try again...");
                    }
                    TSURLReturnStatus surlRetStatus = new TSURLReturnStatus(surl, status);

                    if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
                    {
                        log.info("srmPutDone: <" + user + "> Request for [token:" + requestToken
                                + "] for [SURL:" + surl + "] successfully done with: [status:"
                                + status + "]");
                    }
                    else
                    {
                        log.warn("srmPutDone: <" + user + "> Request for [token:" + requestToken
                                + "] for [SURL:" + surl + "] failed with: [status:" + status + "]");
                    }
                    arrayOfFileStatus.addTSurlReturnStatus(surlRetStatus);
                } catch(InvalidTReturnStatusAttributeException e1)
                {
                    // Nothing to do, it will never be thrown
                    log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e1);
                } catch(InvalidTSURLReturnStatusAttributeException e2)
                {
                    // Nothing to do, it will never be thrown
                    log.debug(funcName + "Error InvalidTSURLReturnStatusAttributeException" + e2);
                }
            }
        }
        else
        {
            

            requestFailure = true;
            requestSuccess = true;
            requestAborted = true;

            /*
             * ArrayOfSURLs is a mandatory parameter, therefore "num_SURLs" is > 0 and inside the for loop the
             * two boolean
             * variables "requestFailure" and "requestSuccess" are correctly set.
             */
            TReturnStatus status = null;
            for(TSURL surl : inputSURLs)
            {
                TStatusCode statusCode = SurlStatusStore.getInstance().getSurlStatus(surl);

                log.debug(funcName + "Checking SURL " + surl);

                boolean surlIsSpaceAvailable = false;
                if (statusCode != null)
                { 
//                    status = new TReturnStatus(statusCode, "");
                    if (statusCode == TStatusCode.SRM_SPACE_AVAILABLE)
                    {

                            boolean goOnWithPutDone = lockSurl(surl);

                            if (goOnWithPutDone)
                            {
                                try
                                {
                                    status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                                } catch(InvalidTReturnStatusAttributeException e)
                                {
                                    //never thrown
                                };
                                // Select the SURL, the DB will be updated at once after
                                spaceAvailableSURLs.add(surl);
                                surlIsSpaceAvailable = true;
                                requestFailure = false;
                                requestAborted = false;
                            }
                            else
                            { // there is an active PutDone on this SURL
                                status = anotherPutDoneActiveReturnStatus;
                                requestSuccess = false;
                                requestAborted = false;
                            }

                        }
                        else
                        {
                            if (statusCode == TStatusCode.SRM_SUCCESS)
                            {
                                try
                                {
                                    status = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR, "");
                                } catch(InvalidTReturnStatusAttributeException e)
                                {
                                    //never thrown
                                }
                                requestSuccess = false;
                                requestAborted = false;

                            }
                            else
                            {
                                if (statusCode == TStatusCode.SRM_ABORTED)
                                {
                                    try
                                    {
                                        status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                                             "PtP status for this SURL is SRM_ABORTED");
                                    } catch(InvalidTReturnStatusAttributeException e)
                                    {
                                        //never thrown
                                    }
                                    requestSuccess = false;

                                }
                                else
                                {
                                    try
                                    {
                                        status = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                                             "Check StatusOfPutRequest for more information");
                                    } catch(InvalidTReturnStatusAttributeException e)
                                    {
                                        //never thrown
                                    }
                                    requestSuccess = false;
                                    requestAborted = false;
                                }
                            }
                        }
                    }
                    else
                    { // SURL not found, set the corresponding return status value
                        log.debug(funcName + "SURL \'" + surl + "\' NOT found in the DB!");
                        try
                        {
                            status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                                 "SURL does not refer to an existing file for the specified request token");
                        } catch(InvalidTReturnStatusAttributeException e)
                        {
                            //never thrown
                        }
                        requestSuccess = false;
                        requestAborted = false;
                    }
                    
//                }
//                catch(InvalidTReturnStatusAttributeException e)
//                {
//                    // Nothing to do, it will never be thrown
//                    surlReturnStatus = null;
//                    log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
//                }
                if (!surlIsSpaceAvailable)
                { // Keep 1:1 match with arrayOfFileStatus
                    spaceAvailableSURLs.add(null);
                }

                /* Add the TSURLReturnStatus of SURL[i] to arrayOfFileStatus */
                try
                {
                    if (status == null)
                    {
                        status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                             "Internal error: try again...");
                    }
                    TSURLReturnStatus surlRetStatus = new TSURLReturnStatus(surl, status);

                    if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
                    {
                        log.info("srmPutDone: <" + user + "> Request for [token:" + requestToken
                                + "] for [SURL:" + surl + "] successfully done with: [status:"
                                + status + "]");
                    }
                    else
                    {
                        log.warn("srmPutDone: <" + user + "> Request for [token:" + requestToken
                                + "] for [SURL:" + surl + "] failed with: [status:" + status + "]");
                    }
                    arrayOfFileStatus.addTSurlReturnStatus(surlRetStatus);
                } catch(InvalidTReturnStatusAttributeException e1)
                {
                    // Nothing to do, it will never be thrown
                    log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e1);
                } catch(InvalidTSURLReturnStatusAttributeException e2)
                {
                    // Nothing to do, it will never be thrown
                    log.debug(funcName + "Error InvalidTSURLReturnStatusAttributeException" + e2);
                }
            }
        }
        return true;
    }

    private static boolean lockSurl(TSURL surl) {
        synchronized (lock) {
            return lockedSurls.add(surl.getSURLString());
        }
    }

    private static void unlockSurl(TSURL surl) {
        synchronized (lock) {
            lockedSurls.remove(surl.getSURLString());
        }
    }

    // private boolean lockSurl(TRequestToken requestToken, TSURL surl) {
    //
    // String requestTokenString = requestToken.toString();
    // String surlString = surl.getSURLString();
    //        
    // log.info("##################################### SURL: " + surlString);
    // log.info("##################################### SFN : " + surl.sfn());
    //
    // synchronized (lock) {
    // if (lockedSurls.containsKey(requestTokenString)) {
    // Set<String> surlSet = lockedSurls.get(requestTokenString);
    // return surlSet.add(surlString);
    // }
    // Set<String> surlSet = new HashSet<String>();
    // surlSet.add(surlString);
    // lockedSurls.put(requestTokenString, surlSet);
    // return true;
    // }
    // }

    // private void unlockSurl(TRequestToken requestToken, TSURL surl) {
    //
    // String requestTokenString = requestToken.toString();
    // String surlString = surl.getSURLString();
    //
    // synchronized (lock) {
    // Set<String> surlSet = lockedSurls.get(requestTokenString);
    // if (surlSet == null) {
    // log.warn("Request token not found inside the map of PutDone locked surls (coudl be a bug). Token: "
    // + requestTokenString);
    // return;
    // }
    // surlSet.remove(surlString);
    // }
    // }
}
