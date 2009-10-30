package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidDescendantsAuthRequestException;
import it.grid.storm.namespace.InvalidDescendantsEmptyRequestException;
import it.grid.storm.namespace.InvalidDescendantsFileRequestException;
import it.grid.storm.namespace.InvalidDescendantsPathRequestException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTUserIDAttributeException;
import it.grid.storm.srm.types.TCheckSumType;
import it.grid.storm.srm.types.TCheckSumValue;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TFileLocality;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TFileType;
import it.grid.storm.srm.types.TGroupID;
import it.grid.storm.srm.types.TGroupPermission;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TMetaDataPathDetail;
import it.grid.storm.srm.types.TPermissionMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TUserID;
import it.grid.storm.srm.types.TUserPermission;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.LSInputData;
import it.grid.storm.synchcall.data.directory.LSOutputData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * @date = Dec 3, 2008
 */

public class LsCommand extends DirectoryCommand implements Command {
    private final NamespaceInterface namespace;

    /** In case of ls on more than one file only one checksum computation is admitted */
    private boolean doNotComputeMoreChecksums = false;
    private boolean atLeastOneInputSURLIsDir;

    public LsCommand() {
        namespace = NamespaceDirector.getNamespace();
    }

    /**
     * Method that provides LS functionality.
     * 
     * @param inputData LSInputData
     * @return LSOutputData
     */
    public OutputData execute(InputData data) {

        LSOutputData outputData = new LSOutputData();
        LSInputData inputData = (LSInputData) data;
        TReturnStatus globalStatus = null;
        @SuppressWarnings("unused")
        TRequestToken requestToken = null; // Not used (now LS is synchronous).

        outputData.setRequestToken(null);
        outputData.setDetails(null);

        /**
         * Validate LSInputData. The check is done at this level to separate internal StoRM logic from xmlrpc specific
         * operation.
         */
        if ((inputData == null) || ((inputData != null) && (inputData.getSurlArray() == null))) {
            log.debug("srmLs: Input parameters for srmLs request NOT found!");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid input parameters specified");
                log.error("srmLs: <> Request for [SURL:] failed with: [status:" + globalStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                // Nothing to do, it will never be thrown.
                log.error("srmLs: <> Request for [SURL:] failed. Error creating returnStatus " + ex1);
            }
            outputData.setStatus(globalStatus);
            return outputData;
        }

        ArrayOfSURLs surlArray = inputData.getSurlArray();

        // Check if GridUser in LSInputData is not null
        GridUserInterface guser = inputData.getUser();
        if (guser == null) {
            log.debug("srmLs: Unable to get user credential. ");
            try {
                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
                log.error("srmLs: <> Request for [SURL:] failed with: [status" + globalStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                // Nothing to do, it will never be thrown.
                log.error("srmLs: <> Request for [SURL:] failed. Error creating returnStatus " + ex1);
            }
            outputData.setStatus(globalStatus);
            outputData.setRequestToken(null);
            outputData.setDetails(null);

            return outputData;
        }

        /***************** Check for DEFAULT parameters not specified in input Data **************/
        @SuppressWarnings("unused")
        ArrayOfTExtraInfo storageSystemInfo = inputData.getStorageSystemInfo();
        // Default value for "storageSystemInfo" does not exists.

        TFileStorageType fileStorageType = inputData.getTFileStorageType();
        // Default value for "fileStorageType" does not exists.

        /**
         * Filtering result by storageType is not supported by StoRM. According to SRM specific if fileStorageType is
         * specified return SRM_NOT_SUPPORTED
         */
        if (!(fileStorageType.equals(TFileStorageType.EMPTY))) {
            log.info("srmLs: <" + guser
                    + "> Request for [SURL:] failed since not supported filtering by FileStorageType:"
                    + fileStorageType.toString());
            try {
                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_NOT_SUPPORTED,
                                          "Filtering result by fileStorageType not supported.");
                log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                        + "] failed with [status" + globalStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                        + "] failed. Error creating returnStatus " + ex1);
            }
            outputData.setStatus(globalStatus);
            outputData.setRequestToken(null);
            outputData.setDetails(null);
            return outputData;
        }

        boolean fullDetailedList;
        if (inputData.getFullDetailedList() == null) {
            fullDetailedList = SRMConstants.fullDetailedList;
        } else {
            fullDetailedList = inputData.getFullDetailedList().booleanValue();
        }

        boolean allLevelRecursive;
        if (inputData.getAllLevelRecursive() == null) {
            // Set to the default value.
            allLevelRecursive = DirectoryCommand.config.get_LS_allLevelRecursive();
        } else {
            allLevelRecursive = inputData.getAllLevelRecursive().booleanValue();
        }

        int numOfLevels;
        if (inputData.getNumOfLevels() == null) {
            // Set to the default value.
            numOfLevels = DirectoryCommand.config.get_LS_numOfLevels();
        } else {
            numOfLevels = inputData.getNumOfLevels().intValue();
            if (numOfLevels < 0) {
                try {
                    globalStatus =
                            new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Parameter 'numOfLevels' is negative");
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                            + "] failed with [status" + globalStatus.toString() + "]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    // Nothing to do, it will never be thrown.
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                            + "] failed. Error creating returnStatus " + ex1);
                }
                outputData.setStatus(globalStatus);

                return outputData;
            }
        }

        boolean coutOrOffsetAreSpecified = false;
        int count;
        if (inputData.getCount() == null) {
            // Set to max entries value. Plus one in order to be able to return TOO_MANY_RESULTS.
            count = DirectoryCommand.config.get_LS_MaxNumberOfEntry() + 1;
        } else {
            count = inputData.getCount().intValue();
            if (count < 0) {
                try {
                    globalStatus =
                            new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                              "Parameter 'count' is less or equal zero");
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                            + "] failed with [status" + globalStatus.toString() + "]");
                } catch (InvalidTReturnStatusAttributeException e) {
                    // Nothing to do, it will never be thrown.
                    log.error("Programming BUG", e);
                }
                outputData.setStatus(globalStatus);
                return outputData;
            }
            if (count == 0) {
                count = DirectoryCommand.config.get_LS_MaxNumberOfEntry() + 1;
            }
            coutOrOffsetAreSpecified = true;
        }

        int offset;
        if (inputData.getOffset() == null) {
            // Set to the default value.
            offset = DirectoryCommand.config.get_LS_offset();
        } else {
            offset = inputData.getOffset().intValue();
            if (offset < 0) {
                try {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Parameter 'offset' is negative");
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                            + "] failed with [status" + globalStatus.toString() + "]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    // Nothing to do, it will never be thrown.
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                            + "] failed. Error creating returnStatus " + ex1);
                }
                outputData.setStatus(globalStatus);
                return outputData;
            }
            coutOrOffsetAreSpecified = true;
        }

        /********************************* Start LS Execution **********************************/
        /*
         * From this point the log can be more verbose reporting also the SURL involved in the request.
         */

        ArrayOfTMetaDataPathDetail details = new ArrayOfTMetaDataPathDetail();
        TStatusCode fileLevelStatusCode = TStatusCode.EMPTY;
        String fileLevelExplanation = "";
        int errorCount = 0;

        int maxEntries = DirectoryCommand.config.get_LS_MaxNumberOfEntry();
        if (count < maxEntries) {
            maxEntries = count;
        }

        MutableInt numberOfReturnedEntries = new MutableInt(0);
        MutableInt numberOfIterations = new MutableInt(-1);

        atLeastOneInputSURLIsDir = false;

        // For each path within the request perform a distinct LS.
        for (int j = 0; j < surlArray.size(); j++) {
            StoRI stori = null;
            boolean failure = false;

            log.debug("srmLs: surlArray.size=" + surlArray.size());
            TSURL surl = surlArray.getTSURL(j);
            if (!surl.isEmpty()) {
                try {
                    stori = namespace.resolveStoRIbySURL(surl, guser);
                } catch (NamespaceException ex) {
                    log.debug("srmLs: Unable to build StoRI by SURL: " + ex);
                    failure = true;
                    fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
                    fileLevelExplanation = "Invalid path";
                    log.info("srmLs: <" + guser + "> Listing on SURL [SURL:" + surl.toString()
                            + "] failed with [status:" + fileLevelStatusCode + " : " + fileLevelExplanation + " ]");
                }
            } else {
                log.debug("srmLs: SURL not specified as input parameter!");
                failure = true;
                fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
                fileLevelExplanation = "Invalid path";
                log.info("srmLs: <" + guser + "> Listing on SURL [SURL:] failed with [status:" + fileLevelStatusCode
                        + " : " + fileLevelExplanation + " ]");
            }

            // Check for authorization and execute Ls.
            if (!failure) {

                // AuthorizationDecision lsAuth = AuthorizationCollector.getInstance().canListDirectory(guser, stori);
                /**
                 * 1.5.0 Path Authorization
                 */
                AuthzDecision lsAuthz = AuthzDirector.getPathAuthz().authorize(guser, SRMFileRequest.LS, stori);

                if (lsAuthz.equals(AuthzDecision.PERMIT)) {
                    log.debug("srmLs: Ls authorized for user [" + guser + "] and PFN = [" + stori.getPFN() + "]");

                    // At this point starts the recursive call
                    errorCount +=
                            manageAuthorizedLS(guser,
                                               stori,
                                               details,
                                               fileStorageType,
                                               allLevelRecursive,
                                               numOfLevels,
                                               fullDetailedList,
                                               errorCount,
                                               maxEntries,
                                               offset,
                                               numberOfReturnedEntries,
                                               0,
                                               numberOfIterations);

                } else {
                    fileLevelStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
                    fileLevelExplanation = "User does not have valid permissions";
                    log.info("srmLs: <" + guser + "> Listing on SURL [SURL:" + surl.toString()
                            + "] failed with: [status:" + fileLevelStatusCode + " : " + fileLevelExplanation + "]");
                    failure = true;
                }
            }
            if (failure) {
                errorCount++;
                TReturnStatus status = null;
                try {
                    status = new TReturnStatus(fileLevelStatusCode, fileLevelExplanation);
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + surl.toString() + "] failed with [status"
                            + status.toString() + "]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    log.error("srmLs: <" + guser + "> Request for [SURL:" + surl.toString()
                            + "] failed. Error creating returnStatus " + ex1);
                }
                TMetaDataPathDetail elementDetail = new TMetaDataPathDetail();
                elementDetail.setStatus(status);
                elementDetail.setSurl(surl);
                if (stori != null) {
                    elementDetail.setStFN(stori.getStFN());
                } else {
                    elementDetail.setStFN(surl.sfn().stfn());
                }

                details.addTMetaDataPathDetail(elementDetail);
            }

        } // for

        if (details.size() == 0) {
            try {
                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                          "The offset is grater than the number of results");
            } catch (InvalidTReturnStatusAttributeException e) {
                // Never thrown
            }

            log.info("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray() + "] status:"
                    + globalStatus.toString());
            outputData.setStatus(globalStatus);
            return outputData;
        }

        if (numberOfReturnedEntries.intValue() >= maxEntries) {
            if (maxEntries < count) {
                try {
                    globalStatus =
                            new TReturnStatus(TStatusCode.SRM_TOO_MANY_RESULTS, "Max returned entries is: "
                                    + DirectoryCommand.config.get_LS_MaxNumberOfEntry());
                } catch (InvalidTReturnStatusAttributeException e) {
                    // Never thrown
                }

                log.info("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray() + "] status:"
                        + globalStatus.toString());
                outputData.setStatus(globalStatus);
                return outputData;
            }
        }

        log.debug("srmLs: Number of details specified in srmLs request:" + details.size());
        log.debug("srmLs: Creation of srmLs outputdata");

        // Set the Global return status.
        try {
            String warningMessage = "";

            if ((numOfLevels > 0) && atLeastOneInputSURLIsDir && coutOrOffsetAreSpecified) {
                warningMessage =
                        "WARNING: specifying \"offset\" and/or \"count\" with \"numOfLevels\" greater than zero "
                                + "may result in inconsistent results among different srmLs requests. ";
            }

            if (errorCount == 0) {

                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_SUCCESS, warningMessage
                                + "All requests successfully completed");
                log.info("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                        + "] successfully done with [status:" + globalStatus.toString() + "]");

            } else if (errorCount < surlArray.size()) {

                globalStatus =
                        new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, warningMessage
                                + "Check file statuses for details");
                log.info("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                        + "] partially done with [status:" + globalStatus.toString() + "]");

            } else {

                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All requests failed");
                log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                        + "] failed with [status:" + globalStatus.toString() + "]");
            }

        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown.
            log.error("srmLs: <" + guser + "> Request for [SURL:" + inputData.getSurlArray()
                    + "] failed.Error creating returnStatus " + e);
        }
        outputData.setStatus(globalStatus);
        outputData.setDetails(details);
        return outputData;
    }

    /**
     * Recursive function for visiting Directory an TMetaDataPath Creation. Returns the number of file statuses
     * different than SRM_SUCCESS.
     * 
     * @param guser
     * @param stori
     * @param rootArray
     * @param type
     * @param allLevelRecursive
     * @param numOfLevels
     * @param fullDetailedList
     * @param errorCount
     * @param count_maxEntries
     * @param offset
     * @param numberOfResults
     * @param currentLevel
     * @param numberOfIterations
     * @return number of errors
     */

    private int manageAuthorizedLS(GridUserInterface guser, StoRI stori, ArrayOfTMetaDataPathDetail rootArray,
            TFileStorageType type, boolean allLevelRecursive, int numOfLevels, boolean fullDetailedList,
            int errorCount, int count_maxEntries, int offset, MutableInt numberOfResults, int currentLevel,
            MutableInt numberOfIterations) {

        /** @todo In this version the FileStorageType field is not managed even if it is specified. */

        // Check if max number of requests has been reached
        if (numberOfResults.intValue() >= count_maxEntries) {
            return errorCount;
        }

        numberOfIterations.increment();

        // Current metaDataPath
        TMetaDataPathDetail currentElementDetail = new TMetaDataPathDetail();

        /**
         * The recursive idea is: - if the StoRI is a directory, fill up with details, calculate the first level
         * children and for each recurse on. - it the StoRI is a file, fill up with details and return. Please note that
         * for each level the same ArrayOfTMetaData is passed as parameter, in order to collect results. this Array is
         * referenced in the currentTMetaData element.
         */

        LocalFile localElement = stori.getLocalFile();

        // Ls of the current element
        if (localElement.exists()) { // The local element exists in the underlying file system

            if (localElement.isDirectory()) {

                atLeastOneInputSURLIsDir = true;

                boolean directoryHasBeenAdedded = false;

                if (numberOfIterations.intValue() >= offset) {
                    // Retrieve information of the directory from the underlying file system
                    populateDetailFromFS(stori, currentElementDetail);
                    if (fullDetailedList) {
                        fullDetail(stori, guser, currentElementDetail);
                    }
                    // In Any case set SURL value into TMetaDataPathDetail
                    currentElementDetail.setStFN(stori.getStFN());

                    numberOfResults.increment();
                    rootArray.addTMetaDataPathDetail(currentElementDetail);
                    directoryHasBeenAdedded = true;
                }

                if (checkAnotherLevel(allLevelRecursive, numOfLevels, currentLevel)) {

                    // Create the nested array of TMetaDataPathDetails
                    ArrayOfTMetaDataPathDetail currentMetaDataArray;
                    if (directoryHasBeenAdedded) {
                        currentMetaDataArray = new ArrayOfTMetaDataPathDetail();
                        currentElementDetail.setArrayOfSubPaths(currentMetaDataArray);
                    } else {
                        currentMetaDataArray = rootArray;
                    }

                    // Retrieve directory element
                    List<StoRI> childrenArray = getFirstLevel(stori);

                    for (StoRI item : childrenArray) {

                        if (numberOfResults.intValue() >= count_maxEntries) {
                            break;
                        }

                        manageAuthorizedLS(guser,
                                           item,
                                           currentMetaDataArray,
                                           type,
                                           allLevelRecursive,
                                           numOfLevels,
                                           fullDetailedList,
                                           errorCount,
                                           count_maxEntries,
                                           offset,
                                           numberOfResults,
                                           currentLevel + 1,
                                           numberOfIterations);
                    } // for
                }

            } else { // The local element is a file

                // Retrieve information on file from underlying file system
                if (numberOfIterations.intValue() >= offset) {
                    populateDetailFromFS(stori, currentElementDetail);
                    if (fullDetailedList) {
                        fullDetail(stori, guser, currentElementDetail);
                    }

                    // In Any case set SURL value into TMetaDataPathDetail
                    currentElementDetail.setStFN(stori.getStFN());
                    numberOfResults.increment();
                    rootArray.addTMetaDataPathDetail(currentElementDetail);
                }
            }

        } else { // The local element does not exists in the underlying file system.

            log.debug("srmLs: The file does not exists in underlying file system.");
            if (numberOfIterations.intValue() >= offset) {
                errorCount++;
                // In Any case set SURL value into TMetaDataPathDetail
                currentElementDetail.setStFN(stori.getStFN());
                // Set Error Status Code and Explanation
                populateDetailFromFS(stori, currentElementDetail);
                // Add the information into details structure
                numberOfResults.increment();
                rootArray.addTMetaDataPathDetail(currentElementDetail);
            }
        }
        return errorCount;
    }

    private List<StoRI> getFirstLevel(StoRI element) {

        List<StoRI> result = null;
        TDirOption dirOption = null;

        try {
            dirOption = new TDirOption(true, false, 1);
        } catch (InvalidTDirOptionAttributesException ex) {
            // Never thrown
            log.debug("srmLs: Unable to create DIR OPTION. WOW!");
        }

        try {

            result = element.getFirstLevelChildren(dirOption);

        } catch (InvalidDescendantsFileRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" + ex1);
        } catch (InvalidDescendantsPathRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" + ex1);
        } catch (InvalidDescendantsAuthRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" + ex1);
        } catch (InvalidDescendantsEmptyRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" + ex1);
        }

        if (result == null) {
            result = new ArrayList<StoRI>(0);
        }

        return result;
    }

    /**
     * Set size and status of "localElement" into "elementDetail".
     * 
     * @param localElement LocalFile
     * @param elementDetail TMetaDataPathDetail
     */
    private void populateDetailFromFS(StoRI element, TMetaDataPathDetail elementDetail) {

        boolean failure = false;
        TReturnStatus returnStatus = null;
        String explanation;
        TStatusCode statusCode;
        LocalFile localElement = element.getLocalFile();

        if (localElement.exists()) {
            // Set Size
            TSizeInBytes size = TSizeInBytes.makeEmpty();
            try {
                if (!(localElement.isDirectory())) {
                    // Patch. getExactSize now works with Java and not with the use of FS Driver (native code)
                    size = TSizeInBytes.make(localElement.getExactSize(), SizeUnit.BYTES);
                    log.debug("srmLs: Extracting size: " + localElement.getPath() + " SIZE: " + size);
                } else {
                    size = TSizeInBytes.make(0, SizeUnit.BYTES);
                }
            } catch (InvalidTSizeAttributesException ex) {
                log.debug("srmLs: Unable to create the size of file.", ex);
                failure = true;
            }
            elementDetail.setSize(size);

            // Set Status
            if (!failure) {
                explanation = "Successful request completion";
                if (element.isSURLBusy()) {
                    statusCode = TStatusCode.SRM_FILE_BUSY;
                } else {
                    statusCode = TStatusCode.SRM_SUCCESS;
                }

                // log.debug("srmLs: Listing on SURL [" + element.getSURL() +
                // "] sucessfully done with:["+statusCode+" : "+explanation+"]");

            } else {
                explanation = "Request failed";
                statusCode = TStatusCode.SRM_FAILURE;
            }
        } else { // localElement does not exist
            explanation = "No such file or directory";
            statusCode = TStatusCode.SRM_INVALID_PATH;
        }

        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            log.error("srmLs: Error creating returnStatus " + ex1);
        }
        // Set Status into elementDetail.
        elementDetail.setStatus(returnStatus);
    }

    /**
     * Set full details into "elementDetail". Information details set by the function populateDetailFromFS() are not
     * considered.
     * 
     * @param element StoRI
     * @param localElement LocalFile
     * @param guser GridUserInterface
     * @param elementDetail TMetaDataPathDetail
     */
    private void fullDetail(StoRI element, GridUserInterface guser, TMetaDataPathDetail elementDetail) {
        LocalFile localElement = element.getLocalFile();

        /** Retrieve permissions information (used in both file or directory cases) */
        TUserPermission userPermission = null;
        TGroupPermission groupPermission = null;
        TPermissionMode otherPermission = null;

        try {
            FilesystemPermission permission = null;
            if (element.hasJustInTimeACLs()) {
                permission = localElement.getUserPermission(guser.getLocalUser());
            } else {
                permission = localElement.getGroupPermission(guser.getLocalUser());
            }
            if (permission != null) {
                userPermission =
                        new TUserPermission(new TUserID(guser.getLocalUser().getLocalUserName()),
                                            TPermissionMode.getTPermissionMode(permission));
                groupPermission =
                        new TGroupPermission(new TGroupID(guser.getLocalUser().getLocalUserName()),
                                             TPermissionMode.getTPermissionMode(permission));
                otherPermission = TPermissionMode.getTPermissionMode(permission);
            }
        } catch (CannotMapUserException e1) {
            log.error("Cannot map user.");
        } catch (InvalidTUserIDAttributeException e) {
            log.error("InvalidTUserIDAttributeException...");
        }

        // Set lastModificationAtTime

        Date lastModificationTime = new Date(localElement.getLastModifiedTime());
        elementDetail.setModificationTime(lastModificationTime);

        /** Set specific information of files and directories */
        if (localElement.isDirectory()) {
            // Set fileType
            elementDetail.setFileType(TFileType.getTFileType("Directory"));

            /**
             * DEFAULT PERMISSION VALUES FOR DIRECTORY
             */

            /** Set common information (for files and directories) */
            // Set UserPermission
            elementDetail.setOwnerPermission(userPermission);

            // Set GroupPermission
            elementDetail.setGroupPermission(groupPermission);

            // Set otherPermission
            elementDetail.setOtherPermission(otherPermission);

        } else { // localElement is a file

            /**
             * DEFAULT PERMISSION VALUES FOR DIRECTORY
             */

            /** Set common information (for files and directories) */
            // Set UserPermission
            if (userPermission == null) {
                userPermission = TUserPermission.makeFileDefault();
            }
            elementDetail.setOwnerPermission(userPermission);

            // Set GroupPermission
            if (groupPermission == null) {
                groupPermission = TGroupPermission.makeFileDefault();
            }
            elementDetail.setGroupPermission(groupPermission);

            // Set otherPermission

            if (otherPermission == null) {
                otherPermission = TPermissionMode.NONE;
            }

            elementDetail.setOtherPermission(otherPermission);

            // fileType
            elementDetail.setFileType(TFileType.getTFileType("File"));

            // retentionPolicyInfo
            TRetentionPolicyInfo retentionPolicyInfo;

            boolean isTapeEnabled = false;

            try {
                isTapeEnabled = element.getVirtualFileSystem().getStorageClassType().isTapeEnabled();
            } catch (NamespaceException e) {
                log.error("Cannot retrieve storage class type information", e);
            }

            if (isTapeEnabled) {
                retentionPolicyInfo = TRetentionPolicyInfo.TAPE1_DISK1_RETENTION_POLICY;
            } else {
                retentionPolicyInfo = TRetentionPolicyInfo.TAPE0_DISK1_RETENTION_POLICY;
            }
            elementDetail.setTRetentionPolicyInfo(retentionPolicyInfo);

            // fileLocality
            boolean isFileOnDisk = localElement.isOnDisk();
            if (isTapeEnabled) {
                boolean isFileOnTape = localElement.isOnTape();

                if (isFileOnTape && isFileOnDisk) {
                    elementDetail.setTFileLocality(TFileLocality.ONLINE_AND_NEARLINE);
                } else if (isFileOnDisk) {
                    elementDetail.setTFileLocality(TFileLocality.ONLINE);
                } else {
                    elementDetail.setTFileLocality(TFileLocality.NEARLINE);
                }
            } else {
                elementDetail.setTFileLocality(TFileLocality.ONLINE);
            }

            // lifetimeAssigned
            TLifeTimeInSeconds lifetimeAssigned = element.getFileLifeTime();
            elementDetail.setLifeTimeAssigned(lifetimeAssigned);

            // lifetimeLeft
            Date startTime = element.getFileStartTime();
            if (startTime != null) {
                elementDetail.setLifetimeLeft(lifetimeAssigned.timeLeft(startTime));
            } else {
                elementDetail.setLifetimeLeft(TLifeTimeInSeconds.makeInfinite());
            }

            // checksum
            if (checksumHasToBeRetrieved(localElement)) {

                String checksum = localElement.getChecksum();

                if (checksum != null) {
                    TCheckSumValue checkSumValue = new TCheckSumValue(checksum);
                    TCheckSumType checkSumType = new TCheckSumType(localElement.getChecksumAlgorithm());
                    elementDetail.setCheckSumType(checkSumType);
                    elementDetail.setCheckSumValue(checkSumValue);
                } else {
                    // Checksum is not available
                    // so StoRM doesn't set the attributes checkSumType and checkSumValue
                    log.warn("Checksum value is not available for file :'" + localElement.getAbsolutePath() + "'");
                }
            }
            // Retrieve information on directory from PERSISTENCE
            populateFileDetailsFromPersistence(element, elementDetail);
        }
    }

    private boolean checksumHasToBeRetrieved(LocalFile localFile) {

        boolean retrieveChecksum;

        if (localFile.hasChecksum()) {

            // Computation of checksum is not needed
            retrieveChecksum = true;

        } else {
            // Computation of checksum could be needed

            if (Configuration.getInstance().getChecksumEnabled()) {

                if (localFile.isOnDisk()) {
                    // Only one checksum computation is admitted
                    if (doNotComputeMoreChecksums) {

                        retrieveChecksum = false;

                    } else {
                        retrieveChecksum = true;
                        doNotComputeMoreChecksums = true;
                        log.debug("Checksum Computation is needed for file :'" + localFile.getAbsolutePath() + "'");
                    }
                } else {

                    retrieveChecksum = false;

                }

            } else {

                // Computation is needed but it is disabled
                retrieveChecksum = false;
                log.debug("Checksum computation is disabled.");

            }
        }
        return retrieveChecksum;
    }

    /**
     * populateDetailFromPersistence
     * 
     * @param element StoRI
     * @param elementDetail TMetaDataPathDetail
     */
    private void populateFileDetailsFromPersistence(StoRI element, TMetaDataPathDetail elementDetail) {
        // TFileStorageType
        boolean isVolatile = VolatileAndJiTCatalog.getInstance().exists(element.getPFN());
        if (isVolatile) {
            elementDetail.setTFileStorageType(TFileStorageType.VOLATILE);
        } else {
            elementDetail.setTFileStorageType(TFileStorageType.PERMANENT);
        }

    }

    /**
     * checkAnotherLevel
     * 
     * @param allLevelRecursive boolean
     * @param numOfLevels int
     * @param currentLevel int
     * @return boolean
     */
    private boolean checkAnotherLevel(boolean allLevelRecursive, int numOfLevels, int currentLevel) {
        boolean result = false;
        if (allLevelRecursive) {
            result = true;
        } else if (currentLevel < numOfLevels) {
            result = true;
        }
        return result;
    }

}
