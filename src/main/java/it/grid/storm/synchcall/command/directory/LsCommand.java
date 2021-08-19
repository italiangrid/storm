/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.synchcall.command.directory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.checksum.ChecksumManager;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidDescendantsEmptyRequestException;
import it.grid.storm.namespace.InvalidDescendantsFileRequestException;
import it.grid.storm.namespace.InvalidDescendantsPathRequestException;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
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
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.LSInputData;
import it.grid.storm.synchcall.data.directory.LSOutputData;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * @date = Dec 3, 2008
 */

public class LsCommand extends DirectoryCommand implements Command {

  public static final Logger log = LoggerFactory.getLogger(LsCommand.class);

  private static final String SRM_COMMAND = "srmLs";

  private final NamespaceInterface namespace;

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

    if (inputData == null || inputData.getSurlArray() == null
        || inputData.getSurlArray().size() == 0) {
      log.debug("srmLs: Input parameters for srmLs request NOT found!");
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Invalid input parameters specified");
      printRequestOutcome(globalStatus, inputData);
      outputData.setStatus(globalStatus);
      return outputData;
    }

    ArrayOfSURLs surlArray = inputData.getSurlArray();

    if (inputData.getStorageTypeSpecified()) {
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_NOT_SUPPORTED,
          "Filtering result by fileStorageType not supported.");
      printRequestOutcome(globalStatus, inputData);
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
      allLevelRecursive = DirectoryCommand.config.getLsAllLevelRecursive();
    } else {
      allLevelRecursive = inputData.getAllLevelRecursive().booleanValue();
    }

    int numOfLevels;
    if (inputData.getNumOfLevels() == null) {
      numOfLevels = DirectoryCommand.config.getLsNumOfLevels();
    } else {
      numOfLevels = inputData.getNumOfLevels().intValue();
      if (numOfLevels < 0) {
        globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
            "Parameter 'numOfLevels' is negative");
        printRequestOutcome(globalStatus, inputData);
        outputData.setStatus(globalStatus);
        return outputData;
      }
    }

    boolean coutOrOffsetAreSpecified = false;
    int count;
    if (inputData.getCount() == null) {
      // Set to max entries value. Plus one in order to be able to return
      // TOO_MANY_RESULTS.
      count = DirectoryCommand.config.getLsMaxNumberOfEntry() + 1;
    } else {
      count = inputData.getCount().intValue();
      if (count < 0) {
        globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
            "Parameter 'count' is less or equal zero");
        printRequestOutcome(globalStatus, inputData);
        outputData.setStatus(globalStatus);
        return outputData;
      }
      if (count == 0) {
        count = DirectoryCommand.config.getLsMaxNumberOfEntry() + 1;
      }
      coutOrOffsetAreSpecified = true;
    }

    int offset;
    if (inputData.getOffset() == null) {
      // Set to the default value.
      offset = DirectoryCommand.config.getLsOffset();
    } else {
      offset = inputData.getOffset().intValue();
      if (offset < 0) {
        globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
            "Parameter 'offset' is negative");
        printRequestOutcome(globalStatus, inputData);
        outputData.setStatus(globalStatus);
        return outputData;
      }
      coutOrOffsetAreSpecified = true;
    }

    ArrayOfTMetaDataPathDetail details = new ArrayOfTMetaDataPathDetail();
    TStatusCode fileLevelStatusCode = TStatusCode.EMPTY;
    String fileLevelExplanation = "";
    int errorCount = 0;

    int maxEntries = DirectoryCommand.config.getLsMaxNumberOfEntry();

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

      log.debug("srmLs: surlArray.size={}", surlArray.size());
      TSURL surl = surlArray.getTSURL(j);

      if (!surl.isEmpty()) {
        try {

          if (inputData instanceof IdentityInputData) {
            try {
              stori = namespace.resolveStoRIbySURL(surl, ((IdentityInputData) inputData).getUser());
            } catch (UnapprochableSurlException e) {
              failure = true;

              log.info("Unable to build a stori for surl {} for user {}. {}", surl,
                  DataHelper.getRequestor(inputData), e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);

            } catch (NamespaceException e) {
              failure = true;

              log.info("Unable to build a stori for surl {} for user {}. {}", surl,
                  DataHelper.getRequestor(inputData), e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);

            } catch (InvalidSURLException e) {
              failure = true;

              log.info("Unable to build a stori for surl {} for user {}. {}", surl,
                  DataHelper.getRequestor(inputData), e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);
            }
          } else {
            try {
              stori = namespace.resolveStoRIbySURL(surl);
            } catch (UnapprochableSurlException e) {
              failure = true;
              log.info("Unable to build a stori for surl {}. {}", surl, e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);

            } catch (NamespaceException e) {
              failure = true;

              log.info("Unable to build a stori for surl {}. {}", surl, e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);
            } catch (InvalidSURLException e) {
              failure = true;

              log.info("Unable to build a stori for surl {}. {}", surl, e.getMessage());

              fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
              fileLevelExplanation = e.getMessage();
              printRequestOutcome(
                  CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation), inputData);
            }
          }
        } catch (IllegalArgumentException e) {
          log.error("srmLs: Unable to build StoRI by SURL: {}", e.getMessage(), e);
          failure = true;
          fileLevelStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
          fileLevelExplanation = e.getMessage();
          printRequestOutcome(CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation),
              inputData);
        }
      } else {
        log.debug("srmLs: SURL not specified as input parameter!");
        failure = true;
        fileLevelStatusCode = TStatusCode.SRM_INVALID_PATH;
        fileLevelExplanation = "Invalid path";
        printRequestOutcome(CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation),
            inputData);
      }

      if (!failure) {
        AuthzDecision lsAuthz;
        if (inputData instanceof IdentityInputData) {
          lsAuthz = AuthzDirector.getPathAuthz()
            .authorize(((IdentityInputData) inputData).getUser(), SRMFileRequest.LS, stori);
        } else {
          lsAuthz =
              AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.LS, stori.getStFN());
        }

        if (lsAuthz.equals(AuthzDecision.PERMIT)) {

          log.debug("srmLs: Ls authorized for user [{}] and PFN = [{}]",
              DataHelper.getRequestor(inputData), stori.getPFN());

          errorCount += manageAuthorizedLS(inputData, stori, details, allLevelRecursive,
              numOfLevels, fullDetailedList, errorCount, maxEntries, offset,
              numberOfReturnedEntries, 0, numberOfIterations);

        } else {

          fileLevelStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
          fileLevelExplanation = "User does not have valid permissions";
          printRequestOutcome(CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation),
              inputData);
          failure = true;
        }
      }


      if (failure) {

        errorCount++;
        TReturnStatus status = CommandHelper.buildStatus(fileLevelStatusCode, fileLevelExplanation);
        printRequestOutcome(status, inputData);
        TMetaDataPathDetail elementDetail = new TMetaDataPathDetail();
        elementDetail.setStatus(status);
        elementDetail.setSurl(surl);

        if (stori != null) {
          elementDetail.setStFN(stori.getStFNFromMappingRule());
        } else {
          elementDetail.setStFN(surl.sfn().stfn());
        }

        details.addTMetaDataPathDetail(elementDetail);
      }

    }

    if (details.size() == 0) {
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
          "The offset is grater than the number of results");
      printRequestOutcome(globalStatus, inputData);
      outputData.setStatus(globalStatus);
      return outputData;
    }

    if (numberOfReturnedEntries.intValue() >= maxEntries) {
      if (maxEntries < count) {
        globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_TOO_MANY_RESULTS,
            "Max returned entries is: " + DirectoryCommand.config.getLsMaxNumberOfEntry());
        printRequestOutcome(globalStatus, inputData);
        outputData.setStatus(globalStatus);
        return outputData;
      }
    }

    log.debug("srmLs: Number of details specified in srmLs request: {}", details.size());
    log.debug("srmLs: Creation of srmLs outputdata");

    String warningMessage = "";

    if ((numOfLevels > 0) && atLeastOneInputSURLIsDir && coutOrOffsetAreSpecified) {
      warningMessage =
          "WARNING: specifying \"offset\" and/or \"count\" with \"numOfLevels\" greater than zero "
              + "may result in inconsistent results among different srmLs requests. ";
    }

    if (errorCount == 0) {
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
          warningMessage + "All requests successfully completed");
      printRequestOutcome(globalStatus, inputData);

    } else if (errorCount < surlArray.size()) {
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
          warningMessage + "Check file statuses for details");
      printRequestOutcome(globalStatus, inputData);

    } else {
      globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "All requests failed");
      printRequestOutcome(globalStatus, inputData);
    }

    outputData.setStatus(globalStatus);
    outputData.setDetails(details);
    return outputData;
  }

  private void printRequestOutcome(TReturnStatus status, LSInputData inputData) {

    if (inputData != null) {
      if (inputData.getSurlArray() != null) {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
            inputData.getSurlArray().asStringList());
      } else {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
      }

    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }

  /**
   * Recursive function for visiting Directory an TMetaDataPath Creation. Returns the number of file
   * statuses different than SRM_SUCCESS.
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

  private int manageAuthorizedLS(LSInputData inputData, StoRI stori,
      ArrayOfTMetaDataPathDetail rootArray, boolean allLevelRecursive, int numOfLevels,
      boolean fullDetailedList, int errorCount, int count_maxEntries, int offset,
      MutableInt numberOfResults, int currentLevel, MutableInt numberOfIterations) {

    /**
     * @todo In this version the FileStorageType field is not managed even if it is specified.
     */

    // Check if max number of requests has been reached
    if (numberOfResults.intValue() >= count_maxEntries) {
      return errorCount;
    }

    numberOfIterations.increment();

    // Current metaDataPath
    TMetaDataPathDetail currentElementDetail = new TMetaDataPathDetail();

    /**
     * The recursive idea is: - if the StoRI is a directory, fill up with details, calculate the
     * first level children and for each recurse on. - it the StoRI is a file, fill up with details
     * and return. Please note that for each level the same ArrayOfTMetaData is passed as parameter,
     * in order to collect results. this Array is referenced in the currentTMetaData element.
     */

    LocalFile localElement = stori.getLocalFile();

    // Ls of the current element
    if (localElement.exists()) { // The local element exists in the underlying
                                 // file system

      if (localElement.isDirectory()) {

        atLeastOneInputSURLIsDir = true;

        boolean directoryHasBeenAdedded = false;

        if (numberOfIterations.intValue() >= offset) {
          // Retrieve information of the directory from the underlying file
          // system
          populateDetailFromFS(stori, currentElementDetail);
          if (fullDetailedList) {
            try {
              fullDetail(inputData, stori, currentElementDetail);
            } catch (FSException e) {
              log.error("srmLs: unable to get full details on stori {}. {}",
                  stori.getAbsolutePath(), e.getMessage(), e);
              errorCount++;
              currentElementDetail.setStatus(
                  new TReturnStatus(TStatusCode.SRM_FAILURE, "Unable to get full details"));
            }
          }

          currentElementDetail.setStFN(stori.getStFNFromMappingRule());

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

            manageAuthorizedLS(inputData, item, currentMetaDataArray, allLevelRecursive,
                numOfLevels, fullDetailedList, errorCount, count_maxEntries, offset,
                numberOfResults, currentLevel + 1, numberOfIterations);
          } // for
        }

      } else { // The local element is a file

        // Retrieve information on file from underlying file system
        if (numberOfIterations.intValue() >= offset
            && !namespace.isSpaceFile(stori.getFilename())) {
          populateDetailFromFS(stori, currentElementDetail);
          if (fullDetailedList) {
            try {
              fullDetail(inputData, stori, currentElementDetail);
            } catch (FSException e) {
              log.error("srmLs: unable to get full details on stori {}. {}",
                  stori.getAbsolutePath(), e.getMessage(), e);
              errorCount++;
              currentElementDetail.setStatus(
                  new TReturnStatus(TStatusCode.SRM_FAILURE, "Unable to get full details"));
            }
          }

          currentElementDetail.setStFN(stori.getStFNFromMappingRule());
          numberOfResults.increment();
          rootArray.addTMetaDataPathDetail(currentElementDetail);
        }
      }

    } else { // The local element does not exists in the underlying file system.

      log.debug("srmLs: The file does not exists in underlying file system.");
      if (numberOfIterations.intValue() >= offset) {
        errorCount++;
        // In Any case set SURL value into TMetaDataPathDetail
        currentElementDetail.setStFN(stori.getStFNFromMappingRule());
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
      log.debug(ex.getMessage(), ex);
    }

    try {

      result = element.getChildren(dirOption);

    } catch (InvalidDescendantsFileRequestException ex1) {
      log.error("srmLs: Unable to retrieve StoRI children !", ex1);
    } catch (InvalidDescendantsPathRequestException ex1) {
      log.error("srmLs: Unable to retrieve StoRI children !", ex1);
    } catch (InvalidDescendantsEmptyRequestException ex1) {
      log.debug("srmLs: directory {} is empty", element.getAbsolutePath());
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
      TSizeInBytes size = TSizeInBytes.makeEmpty();
      try {
        if (!(localElement.isDirectory())) {
          size = TSizeInBytes.make(localElement.getExactSize(), SizeUnit.BYTES);
          log.debug("srmLs: Extracting size for {}. Size: {}", localElement.getPath(), size);
        } else {
          size = TSizeInBytes.make(0, SizeUnit.BYTES);
        }
      } catch (InvalidTSizeAttributesException ex) {
        log.error("srmLs: Unable to create the size of file.", ex);
        failure = true;
      }
      elementDetail.setSize(size);

      // Set Status
      if (!failure) {
        explanation = "Successful request completion";
        if (isStoRISURLBusy(element)) {
          statusCode = TStatusCode.SRM_FILE_BUSY;
        } else {
          statusCode = TStatusCode.SRM_SUCCESS;
        }

      } else {
        explanation = "Request failed";
        statusCode = TStatusCode.SRM_FAILURE;
      }
    } else {
      explanation = "No such file or directory";
      statusCode = TStatusCode.SRM_INVALID_PATH;
    }

    returnStatus = new TReturnStatus(statusCode, explanation);

    elementDetail.setStatus(returnStatus);
  }

  /**
   * Returns true if the status of the SURL of the received StoRI is SRM_SPACE_AVAILABLE, false
   * otherwise. This method queries the DB, therefore pay attention to possible performance issues.
   * 
   * @return boolean
   */
  private boolean isStoRISURLBusy(StoRI element) {

    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

    return checker.isSURLBusy(element.getSURL());
  }

  private void fullDetail(LSInputData inputData, StoRI stori,
      TMetaDataPathDetail currentElementDetail) throws FSException {

    if (inputData instanceof IdentityInputData) {
      fullDetail(stori, ((IdentityInputData) inputData).getUser(), currentElementDetail);
    } else {
      fullDetail(stori, currentElementDetail);
    }
  }

  private void fullDetail(StoRI element, GridUserInterface guser, TMetaDataPathDetail elementDetail)
      throws FSException {

    fullDetail(element, elementDetail);

    TUserPermission userPermission = null;
    TGroupPermission groupPermission = null;
    TPermissionMode otherPermission = null;

    try {
      FilesystemPermission permission = null;
      if (element.hasJustInTimeACLs()) {
        permission = element.getLocalFile().getUserPermission(guser.getLocalUser());
      } else {
        permission = element.getLocalFile().getGroupPermission(guser.getLocalUser());
      }
      if (permission != null) {
        userPermission = new TUserPermission(new TUserID(guser.getLocalUser().getLocalUserName()),
            TPermissionMode.getTPermissionMode(permission));
        groupPermission =
            new TGroupPermission(new TGroupID(guser.getLocalUser().getLocalUserName()),
                TPermissionMode.getTPermissionMode(permission));
        otherPermission = TPermissionMode.getTPermissionMode(permission);
      }
    } catch (CannotMapUserException e) {
      log.error("Cannot map user. CannotMapUserException: {}", e.getMessage());
      return;
    } catch (InvalidTUserIDAttributeException e) {
      log.error("Error creating TUserID. InvalidTUserIDAttributeException: {}", e.getMessage());
      return;
    }
    if (element.getLocalFile().isDirectory()) {
      elementDetail.setOwnerPermission(userPermission);
      elementDetail.setGroupPermission(groupPermission);
      elementDetail.setOtherPermission(otherPermission);

    } else {
      if (userPermission == null) {
        userPermission = TUserPermission.makeFileDefault();
      }
      elementDetail.setOwnerPermission(userPermission);

      if (groupPermission == null) {
        groupPermission = TGroupPermission.makeFileDefault();
      }
      elementDetail.setGroupPermission(groupPermission);

      if (otherPermission == null) {
        otherPermission = TPermissionMode.NONE;
      }
      elementDetail.setOtherPermission(otherPermission);
    }
  }

  /**
   * Set full details into "elementDetail". Information details set by the function
   * populateDetailFromFS() are not considered.
   * 
   * @param element StoRI
   * @param localElement LocalFile
   * @param guser GridUserInterface
   * @param elementDetail TMetaDataPathDetail
   */
  private void fullDetail(StoRI element, TMetaDataPathDetail elementDetail) throws FSException {

    LocalFile localElement = element.getLocalFile();

    elementDetail.setModificationTime(new Date(localElement.getLastModifiedTime()));

    if (localElement.isDirectory()) {
      elementDetail.setFileType(TFileType.getTFileType("Directory"));
    } else {
      elementDetail.setFileType(TFileType.getTFileType("File"));

      TRetentionPolicyInfo retentionPolicyInfo;
      boolean isTapeEnabled = element.getVirtualFileSystem().getStorageClassType().isTapeEnabled();
      if (isTapeEnabled) {
        retentionPolicyInfo = TRetentionPolicyInfo.TAPE1_DISK1_RETENTION_POLICY;
      } else {
        retentionPolicyInfo = TRetentionPolicyInfo.TAPE0_DISK1_RETENTION_POLICY;
      }
      elementDetail.setTRetentionPolicyInfo(retentionPolicyInfo);

      boolean isFileOnDisk = false;
      if (isTapeEnabled) {
        isFileOnDisk = localElement.isOnDisk();
        if (isFileOnDisk && localElement.isOnTape()) {
          elementDetail.setTFileLocality(TFileLocality.ONLINE_AND_NEARLINE);
        } else {
          if (isFileOnDisk) {
            elementDetail.setTFileLocality(TFileLocality.ONLINE);
          } else {
            elementDetail.setTFileLocality(TFileLocality.NEARLINE);
          }
        }
      } else {
        elementDetail.setTFileLocality(TFileLocality.ONLINE);
      }

      elementDetail.setLifeTimeAssigned(element.getFileLifeTime());

      if (element.getFileStartTime() != null) {
        elementDetail
          .setLifetimeLeft(element.getFileLifeTime().timeLeft(element.getFileStartTime()));
      } else {
        elementDetail.setLifetimeLeft(TLifeTimeInSeconds.makeInfinite());
      }

      if (localElement.hasDefaultChecksum()) {
        String checksum = localElement.getDefaultChecksum();
        if (checksum != null) {
          String defaultChecksum = ChecksumManager.getInstance().getDefaultAlgorithm().toString();
          TCheckSumType checkSumType = new TCheckSumType(defaultChecksum);
          TCheckSumValue checkSumValue = new TCheckSumValue(checksum);
          elementDetail.setCheckSumType(checkSumType);
          elementDetail.setCheckSumValue(checkSumValue);
        }
      }

      populateFileDetailsFromPersistence(element, elementDetail);
    }
  }

  /**
   * populateDetailFromPersistence
   * 
   * @param element StoRI
   * @param elementDetail TMetaDataPathDetail
   */
  private void populateFileDetailsFromPersistence(StoRI element,
      TMetaDataPathDetail elementDetail) {

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
