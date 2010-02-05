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
import java.util.HashMap;
import java.util.HashSet;
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

    private static final Map<String, Set<String>> lockedSurls = new HashMap<String, Set<String>>();
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
                                                                 "There is another PutDone executing on this SURL.");
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
    public static void executePutDone(List<ReducedPtPChunkData> spaceAvailableSURLs,
            ArrayOfTSURLReturnStatus arrayOfFileStatus, GridUserInterface user, LocalUser localUser) {

        VolatileAndJiTCatalog volatileAndJiTCatalog = VolatileAndJiTCatalog.getInstance();

        // Now spaceAvailableSURLs contains all the SURLs for which to do a PutDone. To execute this
        // operation
        // three steps are needed for each SURL:
        // 1- if the SURL is volatile than an entry in the table Volatile must be created;
        // 2- JiTs must me removed from the TURL;
        // 3- compute the checksum and store it in an extended attribute
        // 4- Tape stuff management.
        // 5- The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        for (int i = 0; i < spaceAvailableSURLs.size(); i++) {

            if (user == null) {
                log.info("Executing implicit PutDone for SURL: "
                        + spaceAvailableSURLs.get(i).toSURL().getSURLString());
            } else {
                log.info("Executing PutDone for SURL: " + spaceAvailableSURLs.get(i).toSURL().getSURLString());
            }

            ReducedPtPChunkData chunkData = spaceAvailableSURLs.get(i);
            TSURL surl = chunkData.toSURL();
            StoRI stori = null;
            // Retrieve the StoRI associate to the SURL
            try {
                NamespaceInterface namespace = NamespaceDirector.getNamespace();
                if (user == null) {
                    stori = namespace.resolveStoRIbySURL(surl);
                } else {
                    stori = namespace.resolveStoRIbySURL(surl, user);
                }

                // 1- if the SURL is volatile create an entry in the Volatile table
                if (chunkData.fileStorageType() == TFileStorageType.VOLATILE) {
                    volatileAndJiTCatalog.trackVolatile(stori.getPFN(),
                                                        Calendar.getInstance(),
                                                        chunkData.fileLifetime());
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
                        volatileAndJiTCatalog.expirePutJiTs(stori.getPFN(), localUser);
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

            } catch (NamespaceException e1) {
                log.debug(funcName + "Unable to build StoRI by SURL: " + surl.toString(), e1);
                continue;
            }
        }

        // 5- The status of the SURL in the DB must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS.
        if (spaceAvailableSURLs.size() > 0) {
            PtPChunkCatalog.getInstance().transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(spaceAvailableSURLs);
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
        } else if (inputData.getRequestToken() == null) {
            requestFailure = true;
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

        /* spaceAvailableSURLs will contain all the SURLs that must transit from SRM_SPACE_AVAILABLE to SRM_SUCCESS. */
        List<ReducedPtPChunkData> spaceAvailableSURLs = new ArrayList<ReducedPtPChunkData>(listOfSURLs.size());

        if (!findCandidateSURLs(requestToken,
                                outputData,
                                user,
                                listOfSURLs,
                                spaceAvailableSURLs,
                                arrayOfFileStatus)) {
            return outputData;
        }

        executePutDone(spaceAvailableSURLs, arrayOfFileStatus, user, lUser);
        
        /* unlock SURLs */
        for (ReducedPtPChunkData chunckData : spaceAvailableSURLs) {
            TSURL surl = chunckData.toSURL();
            unlockSurl(requestToken, surl);
        }

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

    private boolean findCandidateSURLs(TRequestToken requestToken, PutDoneOutputData outputData,
            GridUserInterface user, List<TSURL> inputSURLs, List<ReducedPtPChunkData> spaceAvailableSURLs,
            ArrayOfTSURLReturnStatus arrayOfFileStatus) {

        log.debug(funcName + "requestToken=" + requestToken.toString());

        /* Query the DB to find all the SURLs associated to the request token */
        Collection<ReducedPtPChunkData> chunks = PtPChunkCatalog.getInstance()
                                                                .lookupReducedPtPChunkData(requestToken);

        if (chunks.isEmpty()) { // No SURLs found
            log.debug(funcName + "Invalid request token");

            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            } catch (InvalidTReturnStatusAttributeException ex) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "dataTransferManger: Error creating returnStatus " + ex);
            }

            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);

            StringBuffer sb = new StringBuffer();
            for (TSURL surl : inputSURLs) {
                sb.append(surl.toString() + " ");
            }

            log.error("srmPutDone: <" + user + "> Request for [token:" + requestToken + "] for [SURL:"
                    + sb.toString() + "]failed with [status: " + globalStatus + "]");

            return false;
        }

        /*
         * Select the SURLs to execute a PutDone on (the following for loop). Each requested SURL must be found in the
         * catalog (as a SURL associated with the specified request token), otherwise the return status of the missing
         * SURL indicates the error.
         */

        requestFailure = true;
        requestSuccess = true;
        requestAborted = true;

        /*
         * ArrayOfSURLs is a mandatory parameter, therefore "num_SURLs" is > 0 and inside the for loop the two boolean
         * variables "requestFailure" and "requestSuccess" are correctly set.
         */
        for (int i = 0; i < inputSURLs.size(); i++) {

            TSURL surl = inputSURLs.get(i);
            log.debug(funcName + "Checking SURL[" + i + "]: " + surl.toString());

            // Search SURL[i] (specified in the request) inside the catalog
            ReducedPtPChunkData auxChunkData = null;
            for (ReducedPtPChunkData chunk : chunks) {
                if (surl.equals(chunk.toSURL())) {
                    auxChunkData = chunk;
                    break;
                }
            }

            // Execute the PutDone on SURL[i]
            TReturnStatus surlReturnStatus = null;
            try {
                if (auxChunkData != null) { // SURL found, select it for PutDone
                    log.debug(funcName + "SURL[" + i + "] found!");
                    surlReturnStatus = auxChunkData.status();

                    if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_SPACE_AVAILABLE) {

                        boolean goOnWithPutDone = lockSurl(requestToken, surl);

                        if (goOnWithPutDone) {
                            surlReturnStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
                            // Select the SURL, the DB will be updated at once after
                            spaceAvailableSURLs.add(auxChunkData);
                            requestFailure = false;
                            requestAborted = false;
                        } else { // there is an active PutDone on this SURL
                            arrayOfFileStatus.getTSURLReturnStatus(i).setStatus(anotherPutDoneActiveReturnStatus);
                            requestSuccess = false;
                            requestAborted = false;
                        }

                    } else if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_SUCCESS) {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR, "");
                        requestSuccess = false;
                        requestAborted = false;

                    } else if (surlReturnStatus.getStatusCode() == TStatusCode.SRM_ABORTED) {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                             "PtP status for this SURL is SRM_ABORTED");
                        requestSuccess = false;

                    } else {
                        surlReturnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                             "Check StatusOfPutRequest for more information");
                        requestSuccess = false;
                        requestAborted = false;
                    }
                } else { // SURL not found, set the corresponding return status value
                    log.debug(funcName + "SURL[" + i + "] NOT found in the DB!");
                    surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                         "SURL does not refer to an existing file for the specified request token");
                    requestSuccess = false;
                    requestAborted = false;
                }
            } catch (InvalidTReturnStatusAttributeException e) {
                // Nothing to do, it will never be thrown
                surlReturnStatus = null;
                log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e);
            }

            /* Add the TSURLReturnStatus of SURL[i] to arrayOfFileStatus */
            try {
                if (surlReturnStatus == null) {
                    surlReturnStatus = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                         "Internal error: try again...");
                }
                TSURLReturnStatus surlRetStatus = new TSURLReturnStatus(surl, surlReturnStatus);

                if (surlReturnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                    log.info("srmPutDone: <" + user + "> Request for [token:" + requestToken + "] for [SURL:"
                            + surl + "] successfully done with: [status:" + surlReturnStatus + "]");
                } else {
                    log.warn("srmPutDone: <" + user + "> Request for [token:" + requestToken + "] for [SURL:"
                            + surl + "] failed with: [status:" + surlReturnStatus + "]");
                }
                arrayOfFileStatus.addTSurlReturnStatus(surlRetStatus);
            } catch (InvalidTReturnStatusAttributeException e1) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "Error InvalidTReturnStatusAttributeException" + e1);
            } catch (InvalidTSURLReturnStatusAttributeException e2) {
                // Nothing to do, it will never be thrown
                log.debug(funcName + "Error InvalidTSURLReturnStatusAttributeException" + e2);
            }
        }

        return true;
    }

    private boolean lockSurl(TRequestToken requestToken, TSURL surl) {

        String requestTokenString = requestToken.toString();
        String surlString = surl.getSURLString();

        synchronized (lock) {
            if (lockedSurls.containsKey(requestTokenString)) {
                Set<String> surlSet = lockedSurls.get(requestTokenString);
                return surlSet.add(surlString);
            }
            Set<String> surlSet = new HashSet<String>();
            surlSet.add(surlString);
            lockedSurls.put(requestTokenString, surlSet);
            return true;
        }
    }

    private void unlockSurl(TRequestToken requestToken, TSURL surl) {

        String requestTokenString = requestToken.toString();
        String surlString = surl.getSURLString();

        synchronized (lock) {
            Set<String> surlSet = lockedSurls.get(requestTokenString);
            if (surlSet == null) {
                log.warn("Request token not found inside the map of PutDone locked surls (coudl be a bug). Token: "
                        + requestTokenString);
                return;
            }
            surlSet.remove(surlString);
        }
    }
}
