package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authorization.AuthorizationCollector;
import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.Checksum;
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
import it.grid.storm.srm.types.TAccessLatency;
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
import it.grid.storm.srm.types.TRetentionPolicy;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;


/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Dec 3, 2008
 *
 */

public class LsCommand extends DirectoryCommand implements Command
{
    private int                maxEntries = -1;
    private final NamespaceInterface namespace;
    private final LinkedList         buffer;
    
    /** In case of ls on more than one file only one checksum computation is admitted */
    private boolean doNotComputeMoreChecksums = false;

    public LsCommand() {
        maxEntries = DirectoryCommand.config.get_LS_MaxNumberOfEntry();
        namespace = NamespaceDirector.getNamespace();
        //buffer = Collections.synchronizedList(new LinkedList());
        buffer = new LinkedList();
    }

    /**
     * Method that provides LS functionality.
     * @param inputData LSInputData
     * @return LSOutputData
     */
    public OutputData execute(InputData data)
    {

        ArrayOfTMetaDataPathDetail details = new ArrayOfTMetaDataPathDetail();
        LSOutputData outputData = new LSOutputData();
        LSInputData inputData =  (LSInputData) data;
        TReturnStatus globalStatus = null;
        TRequestToken requestToken = null; // Not used (now LS is synchronous).

        outputData.setRequestToken(null);
        outputData.setDetails(null);

        /**
         *  Validate LSInputData.
         *  The check is done at this level to separate
         *  internal StoRM logic from xmlrpc specific operation.
         */
        if ((inputData == null) || ((inputData != null) && (inputData.getSurlArray() == null))) {
            log.debug("srmLs: Input parameters for srmLs request NOT found!");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid input parameters specified");
                log.error("srmLs: <> Request for [SURL:] failed with: [status:" + globalStatus.toString()+"]");
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
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                "Unable to get user credential!");
                log.error("srmLs: <> Request for [SURL:] failed with: [status" + globalStatus.toString()+"]");
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
        ArrayOfTExtraInfo storageSystemInfo = inputData.getStorageSystemInfo();
        // Default value for "storageSystemInfo" does not exists.

        TFileStorageType fileStorageType = inputData.getTFileStorageType();
        // Default value for "fileStorageType" does not exists.

        /**
         * Filtering result by storageType is not supported by StoRM.
         * According to SRM specific if fileStorageType is specified
         * return SRM_NOT_SUPPORTED
         */
        if (!(fileStorageType.equals(TFileStorageType.EMPTY))) {
            log.info("srmLs: <"+guser+"> Request for [SURL:] failed since not supported filtering by FileStorageType:"
                    + fileStorageType.toString());
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_NOT_SUPPORTED,
                "Filtering result by fileStorageType not supported.");
                log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed with [status" + globalStatus.toString()+"]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed. Error creating returnStatus " + ex1);
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
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                    "Parameter 'numOfLevels' is negative");
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed with [status" + globalStatus.toString()+"]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    // Nothing to do, it will never be thrown.
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed. Error creating returnStatus " + ex1);
                }
                outputData.setStatus(globalStatus);

                return outputData;
            }
        }

        int count;
        if (inputData.getCount() == null) {
            // Set to the default value.
            count = DirectoryCommand.config.get_LS_count();
        } else {
            count = inputData.getCount().intValue();
            if (count < 0) {
                try {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                    "Parameter 'count' is negative");
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed with [status" + globalStatus.toString()+"]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    // Nothing to do, it will never be thrown.
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed. Error creating returnStatus " + ex1);
                }
                outputData.setStatus(globalStatus);
                return outputData;
            }
            if (count == 0) {
                count = DirectoryCommand.config.get_LS_count();
            }
        }

        int offset;
        if (inputData.getOffset() == null) {
            // Set to the default value.
            offset = DirectoryCommand.config.get_LS_offset();
        } else {
            offset = inputData.getOffset().intValue();
            if (offset < 0) {
                try {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                    "Parameter 'offset' is negative");
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed with [status" + globalStatus.toString()+"]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    // Nothing to do, it will never be thrown.
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed. Error creating returnStatus " + ex1);
                }
                outputData.setStatus(globalStatus);
                return outputData;
            }
        }

        /********************************* Start LS Execution **********************************/
        /*
         *  From this point the log can be more verbose reporting also the SURL involved in the request.
         */

        StoRI stori = null;
        AuthorizationDecision lsAuth = null;
        TStatusCode fileLevelStatusCode = TStatusCode.EMPTY;
        String fileLevelExplanation = "";
        int errorCount = 0;

        // For each path within the request perform a distinct LS.
        for (int j = 0; j < surlArray.size(); j++) {
            boolean failure = false;

            log.debug("srmLs: surlArray.size=" + surlArray.size());
            TSURL surl = surlArray.getTSURL(j);
            if (!surl.isEmpty()) {
                try {
                    stori = namespace.resolveStoRIbySURL(surl, guser);
                } catch (NamespaceException ex) {
                    log.debug("srmLs: Unable to build StoRI by SURL: "+ex);
                    failure = true;
                    fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
                    fileLevelExplanation = "Invalid path";
                    log.info("srmLs: <"+guser+"> Listing on SURL [SURL:"+surl.toString()+"] failed with [status:" + fileLevelStatusCode + " : "+ fileLevelExplanation+" ]");
                }
            } else {
                log.debug("srmLs: SURL not specified as input parameter!");
                failure = true;
                fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
                fileLevelExplanation = "Invalid path";
                log.info("srmLs: <"+guser+"> Listing on SURL [SURL:] failed with [status:" + fileLevelStatusCode + " : "+ fileLevelExplanation+" ]");
            }

            // Check for authorization and execute Ls.
            if (!failure) {

                lsAuth = AuthorizationCollector.getInstance().canListDirectory(guser, stori);

                if (lsAuth.isPermit()) {
                    log.debug("srmLs: Ls authorized for user [" + guser + "] and PFN = [" + stori.getPFN() + "]");
                    int error=0;
                    MutableInt numberOfReturnedEntries = new MutableInt(0);
                    MutableInt numberOfIterations = new MutableInt(0);
                    //At this point starts the recursive call
                    error = manageAuthorizedLS(guser, stori, details, fileStorageType, allLevelRecursive,
                            numOfLevels, fullDetailedList, errorCount, count, offset, numberOfReturnedEntries , 0, numberOfIterations );

                    if (error==0)  {
                        log.info("srmLs: <"+guser+"> Listing on SURL "+(j+1)+" of "+surlArray.size()+" [SURL:"+surl.toString()+"] successfully done with [status:"+details.getTMetaDataPathDetail(j).getStatus()+"].");
                    }  else {
                        log.info("srmLs: <"+guser+"> Listing on SURL "+(j+1)+" of "+surlArray.size()+" [SURL:"+surl.toString()+"] failed with [status:"+details.getTMetaDataPathDetail(j).getStatus()+"]");
                    }

                    errorCount+=error;


                } else {
                    fileLevelStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
                    fileLevelExplanation = "User does not have valid permissions";
                    log.info("srmLs: <"+guser+"> Listing on SURL [SURL:"+surl.toString()+"] failed with: [status:"+fileLevelStatusCode+" : "+fileLevelExplanation+"]");
                    failure = true;
                }
            }
            if (failure) {
                errorCount++;
                TReturnStatus status = null;
                try {
                    status = new TReturnStatus(fileLevelStatusCode, fileLevelExplanation);
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+surl.toString()+"] failed with [status" + status.toString()+"]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    log.error("srmLs: <"+guser+"> Request for [SURL:"+surl.toString()+"] failed. Error creating returnStatus " + ex1);
                }
                TMetaDataPathDetail elementDetail = new TMetaDataPathDetail();
                elementDetail.setStatus(status);
                elementDetail.setSurl(surl);
                if (stori != null) {
                    elementDetail.setStFN(stori.getStFN());
                } else {
                    elementDetail.setStFN(surl.sfn().stfn());
                    //TOREMOVE
                    //log.debug("StFN : "+surl.sfn().stfn());
                }

                details.addTMetaDataPathDetail(elementDetail);
            }

        } // for

        log.debug("srmLs: Number of details specified in srmLs request:" + details.size());
        log.debug("srmLs: Creation of srmLs outputdata");

        // Set the Global return status.
        try {
            if (errorCount == 0) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "All requests successfully completed");
                log.info("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] successfully done with [status:" + globalStatus.toString()+"]");
            } else if (errorCount < surlArray.size()) {
                globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                "Check file statuses for details");
                log.info("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] partially done with [status:" + globalStatus.toString()+"]");
            } else {
                globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All requests failed");
                log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed with [status:" + globalStatus.toString()+"]");
            }
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown.
            log.error("srmLs: <"+guser+"> Request for [SURL:"+inputData.getSurlArray()+"] failed.Error creating returnStatus " + e);
        }
        outputData.setStatus(globalStatus);
        outputData.setDetails(details);
        return outputData;
    }



    /**
     *
     * Recursive function for visiting Directory an TMetaDataPath Creation. Returns the number of
     * file statuses different than SRM_SUCCESS.
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
     * @param numberOfResult
     * @param currentLevel
     * @param numberOfIterations
     * @return number of errors
     *
     */

    private int manageAuthorizedLS(GridUserInterface guser, StoRI stori, ArrayOfTMetaDataPathDetail rootArray,
            TFileStorageType type, boolean allLevelRecursive, int numOfLevels, boolean fullDetailedList,
            int errorCount, int count_maxEntries, int offset, MutableInt numberOfResult, int currentLevel, MutableInt numberOfIterations)
    {

        /** @todo In this version the FileStorageType field is not managed even if it is specified. */

        //log.debug("srmLs: BFS visit of " + stori.getPFN()+"\n Number of entries: "+numberOfResult+"\n MaxEntriesToReturn: "+
        //          count_maxEntries+"\n NumOfLevel: "+numOfLevels+"\n CurrentLevel: "+currentLevel+"\n Offset: "+offset);


        // Insert the StoRI into the buffer
        //buffer.addLast(new BufferElement(stori, 0));

        LocalFile localElement = null;
        TMetaDataPathDetail currentElementDetail = null;
        boolean anotherLevel = false;
        //Current metaDataPath
        currentElementDetail = new TMetaDataPathDetail();
        ArrayOfTMetaDataPathDetail currentMetaDataArray  = null;
        //Create the annidate array of TMetaDataPathDetails
        currentMetaDataArray =  new ArrayOfTMetaDataPathDetail();
        //Set the hierarchial array of metaData
        currentElementDetail.setArrayOfSubPaths(currentMetaDataArray);
        //iter++
        numberOfIterations.add(1);
        //Check if max number of requests has been reached
        if(numberOfResult.intValue()<count_maxEntries) {
            /**
             *
             *  The recursive idea is:
             *
             *  - if the StoRI is a directory, fill up with details, calculate the first level children and for each recurse on.
             *  - it the StoRI is a file, fill up with details and return.
             *
             * Please note that for each level the same ArrayOfTMetaData is passed as parameter, in order to collect results.
             * this Array is referenced in the currentTMetaData element.
             *
             */

            localElement = stori.getLocalFile();

            // Ls of the current element
            if (localElement.exists()) { // The local element exists in the underlying file system
                if (localElement.isDirectory()) {
                    if (numberOfIterations.intValue() >= offset) {
                        // Retrieve information of the directory from the underlying file system
                        populateDetailFromFS(stori, currentElementDetail);
                        if (fullDetailedList) {
                            fullDetail(stori, guser, currentElementDetail);
                        }
                        // In Any case set SURL value into TMetaDataPathDetail
                        if (stori  != null) {
                            currentElementDetail.setStFN(stori.getStFN());
                        }
                        // Add the information into the details structure

                        numberOfResult.add(1);
                        rootArray.addTMetaDataPathDetail(currentElementDetail);
                    }
                    anotherLevel = checkAnotherLevel(allLevelRecursive, numOfLevels, currentLevel);
                    currentLevel = currentLevel+1;
                    if (anotherLevel) {
                        // Retrieve directory element
                        ArrayList childrenArray = (ArrayList) getFirstLevel(stori);
                        if (childrenArray != null) { //Populate the buffer
                            for (Iterator<StoRI> iter = childrenArray.iterator(); iter.hasNext()&&(numberOfResult.intValue()<count_maxEntries);) {
                                StoRI item = iter.next();
                                if(numberOfResult.intValue() >= offset) {
                                    manageAuthorizedLS(guser, item, currentMetaDataArray, type, allLevelRecursive,
                                            numOfLevels, fullDetailedList, errorCount, count_maxEntries, offset, numberOfResult, currentLevel, numberOfIterations);
                                } else {
                                    manageAuthorizedLS(guser, item, rootArray, type, allLevelRecursive,
                                            numOfLevels, fullDetailedList, errorCount, count_maxEntries, offset, numberOfResult, currentLevel, numberOfIterations);
                                }
                            }
                        } // no valid children
                    } //No More element
                } else { //The local element is a file
                    // Retrieve information on file from underlying file system
                    if (numberOfIterations.intValue() >= offset) {
                        populateDetailFromFS(stori, currentElementDetail);
                        if (fullDetailedList) {
                            fullDetail(stori, guser, currentElementDetail);
                        }
                        // In Any case set SURL value into TMetaDataPathDetail
                        if (stori != null) {
                            //elementDetail.setSurl(stori_tmp.getSURL());
                            currentElementDetail.setStFN(stori.getStFN());
                        }
                        numberOfResult.add(1);
                        rootArray.addTMetaDataPathDetail(currentElementDetail);
                    } //Just do nothing. Skip this element.
                }
            } else { // The local element does not exists in the underlying file system.
                log.debug("srmLs: The file does not exists in underlying file system.");
                if (numberOfIterations.intValue() >= offset) {
                    errorCount++;
                    // In Any case set SURL value into TMetaDataPathDetail
                    if (stori != null) {
                        //elementDetail.setSurl(stori_tmp.getSURL());
                        currentElementDetail.setStFN(stori.getStFN());
                    }
                    // Set Error Status Code and Explanation
                    populateDetailFromFS(stori, currentElementDetail);
                    // Add the information into details structure
                    numberOfResult.add(1);
                    rootArray.addTMetaDataPathDetail(currentElementDetail);
                }
            }
        }
        return errorCount;
    }






    /**
     * getChildren
     *
     * @param element StoRI
     * @return StoRI[]
     */
    private List getChildren(StoRI element)
    {
        ArrayList result = null;
        TDirOption dirOption = null;
        try {
            dirOption = new TDirOption(true, false, 1);
        } catch (InvalidTDirOptionAttributesException ex) {
            log.debug("srmLs: Unable to create DIR OPTION. WOW!");
        }
        try {
            result = element.getChildren(dirOption);

        } catch (InvalidDescendantsFileRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsPathRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsAuthRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsEmptyRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        }
        return result;
    }


    private List getFirstLevel(StoRI element)
    {
        ArrayList result = null;
        TDirOption dirOption = null;
        try {
            dirOption = new TDirOption(true, false, 1);
        } catch (InvalidTDirOptionAttributesException ex) {
            log.debug("srmLs: Unable to create DIR OPTION. WOW!");
        }
        try {
            result = element.getFirstLevelChildren(dirOption);

        } catch (InvalidDescendantsFileRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsPathRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsAuthRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        } catch (InvalidDescendantsEmptyRequestException ex1) {
            log.debug("srmLs: Unable to retrieve StoRI children !" +ex1);
        }
        return result;
    }




    /**
     * Set size and status of "localElement" into "elementDetail".
     *
     * @param localElement LocalFile
     * @param elementDetail TMetaDataPathDetail
     */
    private void populateDetailFromFS(StoRI element, TMetaDataPathDetail elementDetail)
    {

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
                    //Patch. getExactSize now works with Java and not with the use of FS Driver (native code)
                    size = TSizeInBytes.make(localElement.getExactSize(), SizeUnit.BYTES);
                    log.debug("srmLs: Extracting size: "+localElement.getPath()+" SIZE: "+size);
                }
                else{
                    size = TSizeInBytes.make( 0, SizeUnit.BYTES );
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

                //log.debug("srmLs: Listing on SURL [" + element.getSURL() + "] sucessfully done with:["+statusCode+" : "+explanation+"]");

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
     * Set full details into "elementDetail". Information details set by the function populateDetailFromFS()
     * are not considered.
     *
     * @param element StoRI
     * @param localElement LocalFile
     * @param guser GridUserInterface
     * @param elementDetail TMetaDataPathDetail
     */
    private void fullDetail(StoRI element, GridUserInterface guser, TMetaDataPathDetail elementDetail)
    {
        LocalFile localElement = element.getLocalFile();

        /** Retrive permissions information (used in both file or directory cases) */
        TUserPermission userPermission = null;
        TGroupPermission groupPermission = null;
        TPermissionMode otherPermission = null;

        /**
         * Comment added to prevent BUG in FS Driver
         *
         */

        try {
            FilesystemPermission permission = null;
            if (element.hasJustInTimeACLs()) {
                permission = localElement.getUserPermission(guser.getLocalUser());
            } else {
                permission = localElement.getGroupPermission(guser.getLocalUser());
            }
            if (permission != null) {
                userPermission = new TUserPermission(new TUserID(guser.getLocalUser().getLocalUserName()), TPermissionMode
                        .getTPermissionMode(permission));
                groupPermission = new TGroupPermission(new TGroupID(guser.getLocalUser().getLocalUserName()), TPermissionMode.getTPermissionMode(permission));
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
            //elementDetail.setOwnerPermission( TUserPermission.makeDirectoryDefault() );
            elementDetail.setOwnerPermission(userPermission);

            // Set GroupPermission
            //elementDetail.setGroupPermission( TGroupPermission.makeDirectoryDefault() );
            elementDetail.setGroupPermission(groupPermission);

            // Set otherPermission
            //elementDetail.setOtherPermission( TPermissionMode.NONE );
            elementDetail.setOtherPermission(otherPermission);



        } else { // localElement is a file

            /**
             * DEFAULT PERMISSION VALUES FOR DIRECTORY
             */


            /** Set common information (for files and directories) */
            // Set UserPermission
            if(userPermission==null) {
                userPermission= TUserPermission.makeFileDefault();
            }
            elementDetail.setOwnerPermission(userPermission);

            // Set GroupPermission
            if(groupPermission==null) {
                groupPermission = TGroupPermission.makeFileDefault();
            }
            elementDetail.setGroupPermission(groupPermission);

            // Set otherPermission

            if(otherPermission==null) {
                otherPermission=TPermissionMode.NONE;
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
            boolean getChecksum = false;
            
            if (localElement.hasChecksum()) {
                getChecksum = true;
            } else if (isFileOnDisk) {
                
                // Only one checksum computation is admitted
                if (doNotComputeMoreChecksums) {
                    getChecksum = false;
                } else {
                    getChecksum = true;
                    doNotComputeMoreChecksums = true;
                }
            }
            
            if (getChecksum) {
                
                String checksum = localElement.getChecksum();
                
                TCheckSumValue checkSumValue = new TCheckSumValue(checksum);
                TCheckSumType checkSumType = new TCheckSumType(localElement.getChecksumAlgorithm());
                
                elementDetail.setCheckSumType(checkSumType);
                elementDetail.setCheckSumValue(checkSumValue);
            }
        }

        //Retrieve information on directory from PERSISTENCE
        populateDetailFromPersistence(element, elementDetail);
        /**
         * @todo IMPLEMENT THIS
         */
    }

    /**
     * populateDetailFromPersistence
     *
     * @param element StoRI
     * @param elementDetail TMetaDataPathDetail
     */
    private void populateDetailFromPersistence(StoRI element, TMetaDataPathDetail elementDetail)
    {
        /**
         * @todo IMPLEMENT THIS
         */
    }

    /**
     * checkAnotherLevel
     *
     * @param allLevelRecursive boolean
     * @param numOfLevels int
     * @param currentLevel int
     * @return boolean
     */
    private boolean checkAnotherLevel(boolean allLevelRecursive, int numOfLevels, int currentLevel)
    {
        boolean result = false;
        if (allLevelRecursive) {
            result = true;
        } else if (currentLevel < numOfLevels) {
            result = true;
        }
        return result;
    }




}
