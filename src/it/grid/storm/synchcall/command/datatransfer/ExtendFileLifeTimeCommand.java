package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.CopyChunkData;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.ReducedPtGChunkData;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLLifetimeReturnStatusAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeOutputData;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.LinkedList;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * @date = Oct 10, 2008
 */
public class ExtendFileLifeTimeCommand extends DataTransferCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(ExtendFileLifeTimeCommand.class);

    public ExtendFileLifeTimeCommand() {
    };

    /**
     * Executes an srmExtendFileLifeTime().
     * 
     * @param inputData ExtendFileLifeTimeInputData
     * @return ExtendFileLifeTimeOutputData
     */

    public OutputData execute(InputData data) {
        final String funcName = "ExtendFileLifeTime: ";
        ExtendFileLifeTimeOutputData outputData = new ExtendFileLifeTimeOutputData();
        ExtendFileLifeTimeInputData inputData = (ExtendFileLifeTimeInputData) data;
        TReturnStatus globalStatus = null;

        ExtendFileLifeTimeCommand.log.debug(funcName + "Started.");

        /****************************** Check for malformed request ******************************/
        try {
            if (inputData == null) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Missing mandatory parameters");
            } else if (inputData.getArrayOfSURLs() == null) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Missing mandatory parameter 'arrayOfSURLs'");
            } else if (inputData.getArrayOfSURLs().size() < 1) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Parameter 'arrayOfSURLs': invalid size");
            } else if (!(inputData.getNewPinLifetime().isEmpty())
                    && !(inputData.getNewFileLifetime().isEmpty()) && (inputData.getRequestToken() != null)) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Cannot update both FileLifetime and PinLifetime");
            } else if (inputData.getNewPinLifetime().isEmpty() && !(inputData.getNewFileLifetime().isEmpty())
                    && (inputData.getRequestToken() != null)) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Do not specify the request token to update the FileLifetime");
            } else if (!(inputData.getNewPinLifetime().isEmpty())
                    && !(inputData.getNewFileLifetime().isEmpty()) && (inputData.getRequestToken() == null)) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Attempt to extend PinLifetime without request token");
            } else if (!(inputData.getNewPinLifetime().isEmpty()) && inputData.getNewFileLifetime().isEmpty()
                    && (inputData.getRequestToken() == null)) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Attempt to extend PinLifetime without request token");
            }

        } catch (InvalidTReturnStatusAttributeException e) {
            ExtendFileLifeTimeCommand.log.warn("dataTransferManger: Error creating returnStatus " + e);
        }
        if (globalStatus != null) {
            ExtendFileLifeTimeCommand.log.debug(funcName + globalStatus.getExplanation());
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            if (inputData == null) {
                ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <> Request for [token:] [SURL:] failed with [status: "
                        + globalStatus + "]");
            } else {
                ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <" + inputData.getUser()
                        + "> Request for [token:" + inputData.getRequestToken() + "] for [SURL: "
                        + inputData.getArrayOfSURLs() + "] failed with [status: " + globalStatus + "]");
            }

            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            ExtendFileLifeTimeCommand.log.debug(funcName + "The user field is NULL");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                 "Unable to get user credential!");
                ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <> Request for [token:] [SURL:] failed with [status: "
                        + globalStatus + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <> Request for [token:] [SURL:] failed. Error creating status: [status: ]");

            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            return outputData;
        }

        /**
         * !!! LocalUser is unnecessary !!! // Maps the VOMS Grid user into Local User LocalUser lUser = null; try {
         * lUser = user.getLocalUser(); } catch (CannotMapUserException e) { log.error(funcName +
         * "Unable to map the user '" + user + "' in a local user.", e); try { globalStatus = new
         * TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, "Unable to map the user"); } catch
         * (InvalidTReturnStatusAttributeException ex1) { // Nothing to do, it will never be thrown
         * log.warn("dataTransferManger: Error creating returnStatus "); } outputData.setReturnStatus(globalStatus);
         * outputData.setArrayOfFileStatuses(null); return outputData; }
         **/

        /********************************** Start to manage the request ***********************************/
        ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatus = new ArrayOfTSURLLifetimeReturnStatus();

        if ((inputData.getRequestToken() == null) && (inputData.getNewPinLifetime().isEmpty())) {
            log.debug(funcName + "Extending SURL lifetime...");
            globalStatus = manageExtendSURLLifetime(inputData.getNewFileLifetime(),
                                                    inputData.getArrayOfSURLs(),
                                                    user,
                                                    arrayOfFileStatus,
                                                    inputData.getRequestToken());
        } else {
            log.debug(funcName + "Extending PIN lifetime...");
            globalStatus = manageExtendPinLifetime(inputData.getRequestToken(),
                                                   inputData.getNewPinLifetime(),
                                                   inputData.getArrayOfSURLs(),
                                                   user,
                                                   arrayOfFileStatus);
        }

        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(arrayOfFileStatus);

        if (globalStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
            log.info("srmExtendFileLifeTime: <" + user + "> Request for [token:"
                    + inputData.getRequestToken() + "] [SURL:" + inputData.getArrayOfSURLs()
                    + "] successfully done with: [status:" + globalStatus.toString() + "]");
        } else if (globalStatus.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
            log.info("srmExtendFileLifeTime: <" + user + "> Request for [token:"
                    + inputData.getRequestToken() + "] [SURL:" + inputData.getArrayOfSURLs()
                    + "] partially done with: [status:" + globalStatus.toString() + "]");
        } else {
            log.error("srmExtendFileLifeTime: <" + user + "> Request for [token:"
                    + inputData.getRequestToken() + "] [SURL:" + inputData.getArrayOfSURLs()
                    + "] failed with: [status:" + globalStatus.toString() + "]");
        }

        log.debug(funcName + "Finished.");

        return outputData;
    }

    /**
     * Extend the lifetime of a SURL. The parameter details is filled by this method and contains file level information
     * on the execution of the request.
     * 
     * @param newLifetime TLifeTimeInSeconds.
     * @param arrayOfSURLS ArrayOfSURLs.
     * @param guser VomsGridUser.
     * @param arrayOfFileLifetimeStatus. ArrayOfTSURLLifetimeReturnStatus The returned file level information.
     * @return TReturnStatus. The request status.
     */
    private TReturnStatus manageExtendSURLLifetime(TLifeTimeInSeconds newLifetime, ArrayOfSURLs arrayOfSURLS,
            GridUserInterface guser, ArrayOfTSURLLifetimeReturnStatus details, TRequestToken requestToken) {
        if (details == null) {
            ExtendFileLifeTimeCommand.log.debug("Function manageExtendSURLLifetime, class ExtendFileLifeTimeExecutor: parameter details is NULL");
        }
        NamespaceInterface namespace = NamespaceDirector.getNamespace();
        VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
        boolean requestSuccess = true;
        boolean requestFailure = true;

        // For each requested SURL, try to extend its lifetime.
        for (int i = 0; i < arrayOfSURLS.size(); i++) {
            TSURL surl = arrayOfSURLS.getTSURL(i);
            StoRI stori = null;
            TStatusCode fileStatusCode;
            String fileStatusExplanation;
            try {
                stori = namespace.resolveStoRIbySURL(surl, guser);
                LocalFile localFile = stori.getLocalFile();
                if (localFile.exists()) {
                    ExtendFileLifeTimeCommand.log.debug(stori.getPFN().toString());
                    List volatileInfo = catalog.volatileInfoOn(stori.getPFN());
                    if (volatileInfo.isEmpty()) {
                        fileStatusCode = TStatusCode.SRM_SUCCESS;
                        fileStatusExplanation = "Nothing to do, SURL is permanent";
                        newLifetime = TLifeTimeInSeconds.makeInfinite();
                        requestFailure = false;
                    } else if (volatileInfo.size() > 2) {
                        fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
                        fileStatusExplanation = "Found more than one entry.... that's a BUG.";
                        // For lifetimes infinite means also unknown
                        newLifetime = TLifeTimeInSeconds.makeInfinite();
                        requestSuccess = false;
                    } else if (stori.isSURLBusy()) {
                        fileStatusCode = TStatusCode.SRM_FILE_BUSY;
                        fileStatusExplanation = "File status is SRM_SPACE_AVAILABLE. SURL lifetime cannot be extend (try with PIN lifetime)";
                        // For lifetimes infinite means also unknown
                        newLifetime = TLifeTimeInSeconds.makeInfinite();
                        requestSuccess = false;
                    } else { // Ok, extend the lifetime of the SURL
                        // Update the DB with the new lifetime
                        catalog.trackVolatile(stori.getPFN(), (Calendar) volatileInfo.get(0), newLifetime);
                        // TODO: return the correct lifetime, i.e. the one which is written to the DB.
                        // TLifeTimeInSeconds writtenLifetime = (TLifeTimeInSeconds) volatileInfo.get(1);

                        fileStatusCode = TStatusCode.SRM_SUCCESS;
                        fileStatusExplanation = "Lifetime extended";
                        requestFailure = false;
                    }
                } else { // Requested SURL does not exists in the filesystem
                    fileStatusCode = TStatusCode.SRM_INVALID_PATH;
                    fileStatusExplanation = "File does not exists";
                    requestSuccess = false;
                }

                // Set the file level information to be returned.
                TReturnStatus fileStatus = new TReturnStatus(fileStatusCode, fileStatusExplanation);
                if (fileStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                    ExtendFileLifeTimeCommand.log.info("srmExtendFileLifeTime: <" + guser
                            + "> Request for [token:" + requestToken + "] for [SURL:" + surl
                            + "] with [lifetime:" + newLifetime + " ] successfully done with: [status:"
                            + fileStatus + "]");
                } else {
                    ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <" + guser
                            + "> Request for [token:" + requestToken + "] for [SURL:" + surl
                            + "] with [lifetime:" + newLifetime + "] failed with: [status:" + fileStatus
                            + "]");
                }
                TSURLLifetimeReturnStatus lifetimeReturnStatus = new TSURLLifetimeReturnStatus(surl,
                                                                                               fileStatus,
                                                                                               newLifetime,
                                                                                               null);
                details.addTSurlReturnStatus(lifetimeReturnStatus);
            } catch (NamespaceException e1) {
                ExtendFileLifeTimeCommand.log.debug("Unable to build StoRI by SURL", e1);
                fileStatusCode = TStatusCode.SRM_INVALID_PATH;
                fileStatusExplanation = "Invalid path";
            } catch (InvalidTReturnStatusAttributeException e2) {
                // Nothing to do, it will never be thrown
                ExtendFileLifeTimeCommand.log.debug("Thrown InvalidTReturnStatusAttributeException");
            } catch (InvalidTSURLLifetimeReturnStatusAttributeException e3) {
                ExtendFileLifeTimeCommand.log.debug("Thrown InvalidTSURLLifetimeReturnStatusAttributeException");
            }
        }
        TReturnStatus globalStatus = null;
        // Set global status
        try {
            if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
            } else if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
                                                 "All file requests are successfully completed");
            } else {
                globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                 "Details are on the file statuses");
            }
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("Unable to build Return Status in Extend Lifetime."+e);
        }
        return globalStatus;
    }

    /**
     * Extend the PIN lifetime of a SURL. The parameter details is filled by this method and contains file level
     * information on the execution of the request.
     * 
     * @param requestToken TRequestToken.
     * @param newPINLifetime TLifeTimeInSeconds.
     * @param arrayOfSURLS ArrayOfSURLs.
     * @param guser VomsGridUser.
     * @param details ArrayOfTSURLLifetimeReturnStatus.
     * @return TReturnStatus. The request status.
     */
    private TReturnStatus manageExtendPinLifetime(TRequestToken requestToken,
            TLifeTimeInSeconds newPINLifetime, ArrayOfSURLs arrayOfSURLS, GridUserInterface guser,
            ArrayOfTSURLLifetimeReturnStatus details) {
        if (details == null) {
            ExtendFileLifeTimeCommand.log.debug("Function manageExtendSURLLifetime, class ExtendFileLifeTimeExecutor: parameter details is NULL");
        }
        TReturnStatus globalStatus = null;
        List requestSURLsList = getListOfSURLsInTheRequest(requestToken);
        if (requestSURLsList.isEmpty()) {
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            } catch (InvalidTReturnStatusAttributeException e) {
                // Nothing to do, it will never be thrown
                ExtendFileLifeTimeCommand.log.debug("BUG: Unexpected TReturnStatus exception" + e);
            }
            return globalStatus;
        }
        // Once we have the list of SURLs belonging to the request, we must check that the SURLs
        // given by the user are consistent, that the resulting lifetime could be lower than
        // the one requested (and for this we must read the Volatile table of the DB), that the SURLs
        // are not released, aborted, expired or suspended and so on...
        // therefore the purpose of all that stuff is to return the right information. I mean, no PIN lifetime
        // is effectively extend, in StoRM the TURL corresponds to the SURL.
        boolean requestSuccess = true;
        boolean requestFailure = true;
        TLifeTimeInSeconds PINLifetime;
        TLifeTimeInSeconds dbLifetime = null;
        for (int i = 0; i < arrayOfSURLS.size(); i++) {
            TSURL surl = arrayOfSURLS.getTSURL(i);
            TStatusCode statusOfTheSURL = null;
            TStatusCode fileStatusCode;
            String fileStatusExplanation;
            boolean surlFound = false;
            // Check if the current SURL belongs to the request token
            for (int j = 0; j < requestSURLsList.size(); j++) {
                SURLData surlData = (SURLData) requestSURLsList.get(j);
                if (surl.equals(surlData.surl)) {
                    statusOfTheSURL = surlData.statusCode;
                    requestSURLsList.remove(j);
                    surlFound = true;
                    break;
                }
            }
            try {

                if (surlFound) {
                    ExtendFileLifeTimeCommand.log.debug("Found SURL: " + surl.getSURLString() + " (status: "
                            + statusOfTheSURL.toString() + ")");
                    NamespaceInterface namespace = NamespaceDirector.getNamespace();
                    StoRI stori = namespace.resolveStoRIbySURL(surl, guser);
                    LocalFile localFile = stori.getLocalFile();
                    if (localFile.exists()) {
                        VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
                        List volatileInfo = catalog.volatileInfoOn(stori.getPFN());

                        if ((statusOfTheSURL != TStatusCode.SRM_FILE_PINNED)
                                && (statusOfTheSURL != TStatusCode.SRM_SPACE_AVAILABLE)
                                && (statusOfTheSURL != TStatusCode.SRM_SUCCESS)) // ULTIMO CASE e da rimuovere. questo
                                                                                 // e' il caso di REQUEST_TOKEN senza
                                                                                 // niente, che deve ritornare valori di
                                                                                 // default.
                        {
                            fileStatusCode = TStatusCode.SRM_INVALID_REQUEST;
                            fileStatusExplanation = "No TURL available";
                            PINLifetime = null;
                            requestSuccess = false;
                        } else if (volatileInfo.size() > 2) {
                            fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
                            fileStatusExplanation = "Found more than one entry.... that's a BUG.";
                            // For lifetimes infinite means also unknown
                            PINLifetime = TLifeTimeInSeconds.makeInfinite();
                            requestSuccess = false;
                        } else { // OK, extend the PIN lifetime.
                            // If the status is success the extension will not take place, only in case of empty
                            // parametetr
                            // the current value are returned, otherwaise the request must fail!

                            if ((statusOfTheSURL == TStatusCode.SRM_SUCCESS) && (!newPINLifetime.isEmpty())) {

                                fileStatusCode = TStatusCode.SRM_INVALID_REQUEST;
                                fileStatusExplanation = "No TURL available";
                                PINLifetime = null;
                                requestSuccess = false;

                            } else {

                                fileStatusCode = TStatusCode.SRM_SUCCESS;

                                if (volatileInfo.isEmpty()) { // SURL is permanent
                                    dbLifetime = TLifeTimeInSeconds.makeInfinite();
                                } else {
                                    dbLifetime = (TLifeTimeInSeconds) volatileInfo.get(1);
                                }
                                if ((!dbLifetime.isInfinite())
                                        && (newPINLifetime.value() > dbLifetime.value())) {
                                    PINLifetime = dbLifetime;
                                    fileStatusExplanation = "The requested PIN lifetime is greater than the lifetime of the SURL."
                                            + " PIN lifetime is now equal to the lifetime of the SURL.";
                                } else {
                                    PINLifetime = newPINLifetime;
                                    fileStatusExplanation = "Lifetime extended";
                                }
                                ExtendFileLifeTimeCommand.log.debug("New PIN lifetime is: "
                                        + PINLifetime.value() + "(SURL: " + surl.getSURLString() + ")");
                                // TODO: update the RequestSummaryCatalog with the new pinLifetime
                                // it is better to do it only once after the for loop
                                requestFailure = false;
                            }
                        }
                    } else { // file does not exist in the file system
                        fileStatusCode = TStatusCode.SRM_INVALID_PATH;
                        fileStatusExplanation = "Invalid path";
                        PINLifetime = null;
                        requestSuccess = false;
                    }
                } else { // SURL not found in the DB
                    ExtendFileLifeTimeCommand.log.debug("SURL: " + surl.getSURLString() + " NOT FOUND!");
                    fileStatusCode = TStatusCode.SRM_INVALID_PATH;
                    fileStatusExplanation = "SURL not found in the request";
                    PINLifetime = null;
                    requestSuccess = false;
                }
                // Set the file level information to be returned.
                TReturnStatus fileStatus = new TReturnStatus(fileStatusCode, fileStatusExplanation);
                // TSURLLifetimeReturnStatus lifetimeReturnStatus = new TSURLLifetimeReturnStatus(surl, fileStatus,
                // null, PINLifetime);
                if (fileStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                    ExtendFileLifeTimeCommand.log.info("srmExtendFileLifeTime: <" + guser
                            + "> Request for [token:" + requestToken + "] for [SURL:" + surl
                            + "] with [pinlifetime: " + newPINLifetime + "] successfully done with: [status:"
                            + fileStatus.toString() + "]");
                } else {
                    ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <" + guser
                            + "> Request for [token:" + requestToken + "] for [SURL:" + surl
                            + "] with [pinlifetime: " + newPINLifetime + "] failed with: [status:"
                            + fileStatus.toString() + "]");
                }

                TSURLLifetimeReturnStatus lifetimeReturnStatus = new TSURLLifetimeReturnStatus(surl,
                                                                                               fileStatus,
                                                                                               dbLifetime,
                                                                                               PINLifetime);
                // log.warn("pinlifetime"+PINLifetime.value());
                details.addTSurlReturnStatus(lifetimeReturnStatus);
            } catch (NamespaceException e1) {
                ExtendFileLifeTimeCommand.log.debug("Unable to build StoRI by SURL", e1);
                fileStatusCode = TStatusCode.SRM_INVALID_PATH;
                fileStatusExplanation = "Invalid path";
            } catch (InvalidTReturnStatusAttributeException e2) {
                // Nothing to do, it will never be thrown
                ExtendFileLifeTimeCommand.log.debug("Thrown InvalidTReturnStatusAttributeException");
            } catch (InvalidTSURLLifetimeReturnStatusAttributeException e3) {
                ExtendFileLifeTimeCommand.log.debug("Thrown InvalidTSURLLifetimeReturnStatusAttributeException");
            }
        } // for (int i = 0; i < arrayOfSURLS.size(); i++)

        // Set global status
        try {
            if (requestFailure) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
            } else if (requestSuccess) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
                                                 "All file requests are successfully completed");
            } else {
                globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                 "Details are on the file statuses");
            }
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            ExtendFileLifeTimeCommand.log.debug("BUG: Unexpected TReturnStatus exception" + e);
        }
        return globalStatus;
    }

    /**
     * Returns the list of SURLs and statuses (a List of SURLData) belonging to the request identified by the
     * requestToken.
     * 
     * @param requestToken TRequestToken
     * @return List<SURLData>
     */
    private List getListOfSURLsInTheRequest(TRequestToken requestToken) {
        List listOfSURLsInfo = new LinkedList();
        RequestSummaryCatalog rsCatalog = RequestSummaryCatalog.getInstance();
        TRequestType requestType = rsCatalog.typeOf(requestToken);

        if (requestType == TRequestType.PREPARE_TO_GET) {
            PtGChunkCatalog ptgCatalog = PtGChunkCatalog.getInstance();
            Collection chunkList = ptgCatalog.lookupReducedPtGChunkData(requestToken);
            Iterator chunk = chunkList.iterator();
            while (chunk.hasNext()) {
                ReducedPtGChunkData aux = (ReducedPtGChunkData) chunk.next();
                SURLData surlData = new SURLData(aux.fromSURL(), aux.status().getStatusCode());
                listOfSURLsInfo.add(surlData);
            }
        } else if (requestType == TRequestType.PREPARE_TO_PUT) {
            PtPChunkCatalog ptpCatalog = PtPChunkCatalog.getInstance();
            Collection chunkList = ptpCatalog.lookupReducedPtPChunkData(requestToken);
            Iterator chunk = chunkList.iterator();
            while (chunk.hasNext()) {
                ReducedPtPChunkData aux = (ReducedPtPChunkData) chunk.next();
                SURLData surlData = new SURLData(aux.toSURL(), aux.status().getStatusCode());
                listOfSURLsInfo.add(surlData);
            }
        } else if (requestType == TRequestType.COPY) {
            CopyChunkCatalog copyCatalog = CopyChunkCatalog.getInstance();
            Collection chunkList = copyCatalog.lookup(requestToken);
            Iterator chunk = chunkList.iterator();
            while (chunk.hasNext()) {
                CopyChunkData aux = (CopyChunkData) chunk.next();
                SURLData surlData = new SURLData(aux.toSURL(), aux.status().getStatusCode());
                listOfSURLsInfo.add(surlData);
            }
        }

        return listOfSURLsInfo;
    }

    private class SURLData {
        public TSURL surl;
        public TStatusCode statusCode;

        public SURLData(TSURL surl, TStatusCode statusCode) {
            this.surl = surl;
            this.statusCode = statusCode;
        }
    }
}
