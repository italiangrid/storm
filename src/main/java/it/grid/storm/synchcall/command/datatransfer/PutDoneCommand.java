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

import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private static final TReturnStatus anotherPutDoneActiveReturnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
    "There is another PutDone in execution on this SURL.");
    private static final String SRM_COMMAND = "srmPutDone";

    public PutDoneCommand() {
    };

    /**
     * Implements the srmPutDone. Used to notify the SRM that the client completed a file transfer to the TransferURL in
     * the allocated space (by a PrepareToPut).
     */
    public OutputData execute(InputData absData) {

        ManageFileTransferRequestFilesInputData inputData = (ManageFileTransferRequestFilesInputData) absData;
        TReturnStatus globalStatus = null;
        log.debug(funcName + "Started.");

        if (inputData == null || inputData.getRequestToken() == null || inputData.getArrayOfSURLs() == null
                || inputData.getArrayOfSURLs().size() < 1)
        {
            log.error(funcName + "Invalid input parameter specified");
            globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Missing mandatory parameters");
            if (inputData == null)
            {
                log.error("srmPutDone: Requestfailed with [status: " + globalStatus + "]");
            }
            else
            {
                printRequestOutcome(globalStatus, inputData);
            }
            return new ManageFileTransferOutputData(globalStatus);
        }

        /********************************** Start to manage the request ***********************************/
        TRequestToken requestToken = inputData.getRequestToken();
        ArrayList<TSURL> listOfSURLs = inputData.getArrayOfSURLs().getArrayList();
        
        ArrayOfTSURLReturnStatus surlsStatuses;
        try
        {
            surlsStatuses = loadSURLsStatus(requestToken, listOfSURLs);
        } catch(IllegalArgumentException e)
        {
            log.error(funcName + "Unexpected IllegalArgumentException: " + e.getMessage());
            globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, retry.");
            printRequestOutcome(globalStatus, inputData);
            return new ManageFileTransferOutputData(globalStatus);
        } catch(RequestUnknownException e)
        {
            log.info(funcName + "Invalid request token and surl. RequestUnknownException: " + e.getMessage());
            globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token and surls");
            printRequestOutcome(globalStatus, inputData);
            return new ManageFileTransferOutputData(globalStatus);
        } catch(UnknownTokenException e)
        {
            log.info(funcName + "Invalid request token. UnknownTokenException: " + e.getMessage());
            globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            printRequestOutcome(globalStatus, inputData);
            return new ManageFileTransferOutputData(globalStatus);
        } catch(ExpiredTokenException e)
        {
            log.info(funcName + "The request is expired: ExpiredTokenException: " + e.getMessage());
            globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
            printRequestOutcome(globalStatus, inputData);
            return new ManageFileTransferOutputData(globalStatus);
        }
        
        LinkedList<TSURL> spaceAvailableSURLs = new LinkedList<TSURL>();
        boolean atLeastOneSuccess = false;
        boolean atLeastOneFailure = false;
        boolean atLeastOneAborted = false;
        for (TSURLReturnStatus surlStatus : surlsStatuses.getArray())
        {
            TReturnStatus newStatus;
            TReturnStatus currentStatus = surlStatus.getStatus();
            switch (currentStatus.getStatusCode())
            {
                case SRM_SPACE_AVAILABLE:

                    if (lockSurl(surlStatus.getSurl()))
                    {
                        spaceAvailableSURLs.add(surlStatus.getSurl());
                        newStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "");
                        atLeastOneSuccess = true;
                    }
                    else
                    { // there is an active PutDone on this SURL
                        newStatus = anotherPutDoneActiveReturnStatus;
                    }
                    break;
                case SRM_SUCCESS:
                    newStatus = CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR, "");
                    atLeastOneFailure = true;
                    break;
                case SRM_ABORTED:
                    newStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                            "PtP status for this SURL is SRM_ABORTED");
                    atLeastOneAborted = true;
                    break;
                default:
                    newStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
                                            "Check StatusOfPutRequest for more information");
                    atLeastOneFailure = true;
                    break;
            }
            surlsStatuses.updateStatus(surlStatus, newStatus);
        }
        
        GridUserInterface user;
        if(inputData instanceof IdentityInputData)
        {
            user = ((IdentityInputData)inputData).getUser();
        }
        else
        {
            user = null;
        }
        executePutDone(spaceAvailableSURLs, user);
        
        if(!spaceAvailableSURLs.isEmpty())
        {
            try
            {
                SurlStatusManager.checkAndUpdateStatus(requestToken, spaceAvailableSURLs,
                                                       TStatusCode.SRM_SPACE_AVAILABLE, TStatusCode.SRM_SUCCESS);
        
            }catch(IllegalArgumentException e)
            {
                log.error(funcName + "Unexpected IllegalArgumentException: " + e.getMessage());
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, retry.");
                printRequestOutcome(globalStatus, inputData);
                return new ManageFileTransferOutputData(globalStatus);
            } catch(UnknownTokenException e)
            {
                log.error(funcName + "Unexpected UnknownTokenException: " + e.getMessage());
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed,. Unexpected UnknownSurlException in checkAndUpdateStatus");
                printRequestOutcome(globalStatus, inputData);
                return new ManageFileTransferOutputData(globalStatus);
            } catch(ExpiredTokenException e)
            {
                log.info(funcName + "The request is expired: ExpiredTokenException: " + e.getMessage());
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
                printRequestOutcome(globalStatus, inputData);
                return new ManageFileTransferOutputData(globalStatus);
            } catch(UnknownSurlException e)
            {
                log.error(funcName + "Unexpected UnknownSurlException: " + e.getMessage());
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed. Unexpected UnknownSurlException in checkAndUpdateStatus");
                printRequestOutcome(globalStatus, inputData);
                return new ManageFileTransferOutputData(globalStatus);
            }
        }
        
        for (TSURL surl : spaceAvailableSURLs)
        {
            if (surl != null)
            {
                unlockSurl(surl);
            }
        }
        
        log.debug("Number of SURLs locked: " + lockedSurls.size());

        if (atLeastOneSuccess)
        {
            if (!atLeastOneFailure && !atLeastOneAborted)
            {
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
                                           "All file requests are successfully completed");
            }
            else
            {
                globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                           "Details are on the file statuses");
            }
        }
        else
        {
            if (atLeastOneFailure)
            {
                if (!atLeastOneAborted)
                {
                    globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
                }
                else
                {
                    globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
                                               "Some file requests are failed, the others are aborted");
                }
            }
            else
            {
                if(atLeastOneAborted)
                {
                    globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_ABORTED, "Request " + requestToken.toString());    
                }
                else
                {
                    //unexpected
                    log.error("None of the surls is success, failed or aborted, Unexpeced! SurlStatus = " + surlsStatuses.getArray());
                    globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, no surl status recognized, retry.");
                }
            }
        }
        log.debug(funcName + "Finished with status: " + globalStatus.toString());

        printRequestOutcome(globalStatus, inputData);
        return new ManageFileTransferOutputData(globalStatus, surlsStatuses);
    }

    private static void printRequestOutcome(TReturnStatus status, ManageFileTransferRequestFilesInputData inputData)
    {
        if (inputData != null)
        {
            if (inputData.getArrayOfSURLs() != null)
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getRequestToken(),
                                                      inputData.getArrayOfSURLs().asStringList());
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getArrayOfSURLs().asStringList());
                }

            }
            else
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getRequestToken());
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
                }
            }
        }
        else
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
        }
    }

    private ArrayOfTSURLReturnStatus loadSURLsStatus(TRequestToken requestToken,
            List<TSURL> inputSURLs) throws IllegalArgumentException, RequestUnknownException, UnknownTokenException, ExpiredTokenException
    {

        ArrayOfTSURLReturnStatus returnStatuses = new ArrayOfTSURLReturnStatus(inputSURLs.size());
        Map<TSURL, TReturnStatus> surlsStatuses = SurlStatusManager.getSurlsStatus(requestToken, inputSURLs);

        if (surlsStatuses.isEmpty())
        {
            log.info(funcName + "No one of the requested surls found for the provided token");
            throw new RequestUnknownException("No one of the requested surls found for the provided token");
        }

        TReturnStatus status = null;
        for (TSURL surl : inputSURLs)
        {

            log.debug(funcName + "Checking SURL " + surl);

            if (surlsStatuses.containsKey(surl))
            {
                log.debug(funcName + "SURL \'" + surl + "\' found!");
                status = surlsStatuses.get(surl);
            }
            else
            {
                log.debug(funcName + "SURL \'" + surl + "\' NOT found in the DB!");
                try
                {
                    status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                               "SURL does not refer to an existing file for the specified request token");
                } catch(InvalidTReturnStatusAttributeException e)
                {
                    throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                            + "in building TReturnStatus: " + e.getMessage());
                }
            }
            TSURLReturnStatus surlRetStatus;
            try
            {
                surlRetStatus = new TSURLReturnStatus(surl, status);
            } catch(InvalidTSURLReturnStatusAttributeException e)
            {
                // Never thrown
                throw new IllegalStateException("Unexpected InvalidTSURLReturnStatusAttributeException " +
                        "in building TReturnStatus: " + e.getMessage());
            }
            returnStatuses.addTSurlReturnStatus(surlRetStatus);
        }
        return returnStatuses;
    }

    
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
    public static void executePutDone(List<TSURL> spaceAvailableSURLs, GridUserInterface user) {

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
//        for (int i = 0; i < spaceAvailableSURLs.size(); i++) {
        for(TSURL surl: spaceAvailableSURLs) {

//            ReducedPtPChunkData chunkData = spaceAvailableSURLs.get(i);
//            TSURL surl = spaceAvailableSURLs.get(i);
//            if (chunkData == null) {
            if (surl == null) {
                continue;
            }

//            TSURL surl = chunkData.toSURL();

//            if (user == null) {
//
//
//                if (lockSurl(surl)) {
//                } else {
//                    continue;
//                }
//
//            } else {
//                log.info("Executing PutDone for SURL: " + spaceAvailableSURLs.get(i).toSURL().getSURLString());
                log.debug("Executing PutDone for SURL: " + surl.getSURLString());
//            }

            StoRI stori = null;
            // Retrieve the StoRI associate to the SURL
            if (user == null)
            {
                try
                {
                    stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
                } catch(UnapprochableSurlException e)
                {
                    log.info("Unable to build a stori for surl " + surl
                             + " UnapprochableSurlException: " + e.getMessage());
                     continue;
                }
            }
            else
            {
                try
                {
                    stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl, user);
                } catch(IllegalArgumentException e)
                {
                    log.error(funcName + "unable to build STORI from surl=" + surl + " user=" + user);
                    continue;
                } catch(UnapprochableSurlException e)
                {
                    log.info("Unable to build a stori for surl " + surl + " for user " + user
                            + " UnapprochableSurlException: " + e.getMessage());
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
                LocalUser localUser = null;
                try
                {
                    if(user != null)
                    {
                        localUser = user.getLocalUser();                            
                    }
                } catch(CannotMapUserException e)
                {
                    log.warn(funcName + "Unable to get the local user for user " + user
                            + ". CannotMapUserException: " + e.getMessage());
                }
                if (localUser != null) {
                    VolatileAndJiTCatalog.getInstance().expirePutJiTs(stori.getPFN(), localUser);
                } else {
                    VolatileAndJiTCatalog.getInstance().removeAllJiTsOn(stori.getPFN());
                }
            }

            // 3- compute the checksum and store it in an extended attribute
            LocalFile localFile = stori.getLocalFile();

//            if (Configuration.getInstance().getChecksumEnabled()) {
//
//                boolean checksumComputed = localFile.setChecksum();
//                if (!checksumComputed) {
////                        if (arrayOfFileStatus != null) {
////                            arrayOfFileStatus.getTSURLReturnStatus(i)
////                                             .getStatus()
////                                             .setExplanation("Warning: failed to compute the checksum "
////                                                     + "(checksum computation is enabled in the server)");
////                        } else {
//                        log.warn("Error comuting checksum for file: " + localFile.getAbsolutePath());
////                        }
//                } else {
//                    log.debug("Checksum set to SURL:" + surl.toString());
//                }
//
//            }

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
            VirtualFSInterface fs = stori.getVirtualFileSystem();
            if ((fs != null) && (fs.getProperties().isOnlineSpaceLimited())) {
                SpaceHelper sh = new SpaceHelper();
                // Update the used space into Database
                sh.decreaseFreeSpaceForSA(log, funcName, user, surl);
            }
        }
    }

    public static void executeImplicitPutDone(List<TSURL> spaceAvailableSURLs)
    {
        for (TSURL surl : spaceAvailableSURLs)
        {

            if (surl == null)
            {
                continue;
            }

            if (lockSurl(surl))
            {
                ArrayList<TSURL> elementList = new ArrayList<TSURL>(1);
                elementList.add(surl);
                executePutDone(elementList, null);
            }
            else
            {
                continue;
            }
        }
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
}
