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

package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.common.types.PFN;
import it.grid.storm.ea.StormEA;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
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
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 */

public class PutDoneCommand extends DataTransferCommand implements Command {

  private static final Logger log = LoggerFactory
    .getLogger(PutDoneCommand.class);

  private static final String SRM_COMMAND = "srmPutDone";

	private static final StormEA ea = StormEA.getDefaultStormExtendedAttributes();

  public PutDoneCommand() {

  };
  
  private void inputDataSanityCheck(InputData inputData)
    throws PutDoneCommandException {

    try {
    
    Preconditions.checkNotNull(inputData, "PutDone: Invalid null input data");
    Preconditions.checkArgument(
      inputData instanceof ManageFileTransferRequestFilesInputData,
      "PutDone: Invalid input data class");
    ManageFileTransferRequestFilesInputData data = (ManageFileTransferRequestFilesInputData) inputData;
    Preconditions.checkNotNull(data.getRequestToken(),
      "PutDone: Invalid null request token");
    Preconditions.checkNotNull(data.getArrayOfSURLs(),
      "PutDone: Invalid null array of SURL");
    Preconditions.checkArgument(data.getArrayOfSURLs().size() > 0,
      "PutDone: Invalid empty array of SURL");

    } catch (Throwable e) {
      
      log.error("PutDone: Invalid input parameters specified [{}: {}]",
        e.getClass().getName(), e.getMessage());
      throw new PutDoneCommandException(CommandHelper
        .buildStatus(TStatusCode.SRM_INVALID_REQUEST, e.getMessage()), e);
      
    }
  }
  
  private TReturnStatus buildGlobalStatus(boolean atLeastOneSuccess,
    boolean atLeastOneFailure, boolean atLeastOneAborted) {

    TReturnStatus globalStatus = null;
    
    if (atLeastOneSuccess) {
      if (!atLeastOneFailure && !atLeastOneAborted) {
        globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
          "All file requests are successfully completed");
      } else {
        globalStatus = CommandHelper.buildStatus(
          TStatusCode.SRM_PARTIAL_SUCCESS, "Details are on the file statuses");
      }
    } else {
      if (atLeastOneFailure) {
        if (!atLeastOneAborted) {
          globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
            "All file requests are failed");
        } else {
          globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
            "Some file requests are failed, the others are aborted");
        }
      } else {
        if (atLeastOneAborted) {
          globalStatus = CommandHelper.buildStatus(TStatusCode.SRM_ABORTED,
            "Request aborted");
        } else {
          // unexpected
          log.error(
            "None of the surls is success, failed or aborted, unexpected!");
          globalStatus = CommandHelper.buildStatus(
            TStatusCode.SRM_INTERNAL_ERROR,
            "Request Failed, no surl status recognized, retry.");
        }
      }
    }
    return globalStatus;
  }

  private void markSURLsReadyForRead(TRequestToken requestToken, List<TSURL> spaceAvailableSURLs) throws PutDoneCommandException {
    
    if (spaceAvailableSURLs.isEmpty()) {
      log.debug("markSURLsReadyForRead: empty spaceAvailableSURLs");
      return;
    }
    
    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();
    try {
    
      checker.markSURLsReadyForRead(requestToken, spaceAvailableSURLs);

    } catch (IllegalArgumentException e) {
        
      log.error("PutDone: Unexpected IllegalArgumentException '{}'", e.getMessage());
      throw new PutDoneCommandException(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, retry."), e);
    }
  }
  
  private ArrayOfTSURLReturnStatus loadSURLsStatuses(
    ManageFileTransferRequestFilesInputData inputData)
      throws PutDoneCommandException {
    
    TRequestToken requestToken = inputData.getRequestToken();
    ArrayList<TSURL> listOfSURLs = inputData.getArrayOfSURLs().getArrayList();
    
    ArrayOfTSURLReturnStatus surlsStatuses = null;
    try {

      surlsStatuses = loadSURLsStatus(getUserFromInputData(inputData), requestToken, listOfSURLs);

    } catch (AuthzException e) {

      log.error("PutDone: {}", e.getMessage(), e);
      throw new PutDoneCommandException(CommandHelper
        .buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));

    } catch (IllegalArgumentException e) {

      log.error("PutDone: Unexpected IllegalArgumentException: {}",
        e.getMessage(), e);
      throw new PutDoneCommandException(CommandHelper
        .buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, retry."));

    } catch (RequestUnknownException e) {

      log.info(
        "PutDone: Invalid request token and surl. RequestUnknownException: {}",
        e.getMessage(), e);
      throw new PutDoneCommandException(CommandHelper.buildStatus(
        TStatusCode.SRM_INVALID_REQUEST, "Invalid request token and surls"));

    } catch (UnknownTokenException e) {

      log.info("PutDone: Invalid request token. UnknownTokenException: {}",
        e.getMessage(), e);
      throw new PutDoneCommandException(CommandHelper
        .buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token"));

    } catch (ExpiredTokenException e) {

      log.info("PutDone: The request is expired: ExpiredTokenException: {}",
        e.getMessage());
      throw new PutDoneCommandException(CommandHelper
        .buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired"));
    }

    return surlsStatuses;
  }
  
  
  /**
   * Implements the srmPutDone. Used to notify the SRM that the client completed
   * a file transfer to the TransferURL in the allocated space (by a
   * PrepareToPut).
   */
  public OutputData execute(InputData absData) {

    log.debug("PutDone: Started.");
    
    TReturnStatus globalStatus = null;
    ArrayOfTSURLReturnStatus surlsStatuses = null;
    
    boolean atLeastOneSuccess = false;
    boolean atLeastOneFailure = false;
    boolean atLeastOneAborted = false;
    
    try {
      
      inputDataSanityCheck(absData);
    
    } catch (PutDoneCommandException e) {
      
      printRequestOutcome(e.getReturnStatus());
      return new ManageFileTransferOutputData(e.getReturnStatus());
    }
    
    ManageFileTransferRequestFilesInputData inputData = (ManageFileTransferRequestFilesInputData) absData;
    GridUserInterface user = inputData instanceof IdentityInputData
      ? ((IdentityInputData) inputData).getUser() : null;
    TRequestToken requestToken = inputData.getRequestToken();
    List<TSURL> spaceAvailableSURLs = new ArrayList<TSURL>();
      
    try {
      
      surlsStatuses = loadSURLsStatuses(inputData);
      
    } catch (PutDoneCommandException e) {
      
      printRequestOutcome(e.getReturnStatus(), inputData);
      return new ManageFileTransferOutputData(e.getReturnStatus()); 
    }
      
    
    for (TSURLReturnStatus surlStatus : surlsStatuses.getArray()) {
      
      TReturnStatus newStatus;
      TReturnStatus currentStatus = surlStatus.getStatus();
      
      switch (currentStatus.getStatusCode()) {

        case SRM_SPACE_AVAILABLE:

          spaceAvailableSURLs.add(surlStatus.getSurl());
          // DO PutDone
          try {
            executePutDone(surlStatus.getSurl(), user);
          } catch (PutDoneCommandException e) {
            newStatus = e.getReturnStatus();
            atLeastOneFailure = true;
            break;
          }
          newStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
            "Success");
          atLeastOneSuccess = true;

          break;

        case SRM_SUCCESS:

          newStatus = CommandHelper.buildStatus(
            TStatusCode.SRM_DUPLICATION_ERROR, "Duplication error");
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
    
    try {
	
      markSURLsReadyForRead(requestToken, spaceAvailableSURLs);

    } catch (PutDoneCommandException e) {
      
      printRequestOutcome(e.getReturnStatus(), inputData);
      return new ManageFileTransferOutputData(e.getReturnStatus()); 
    }
      
    log.debug("PutDone: Computing final global status ...");
    globalStatus = buildGlobalStatus(atLeastOneSuccess, atLeastOneFailure,
      atLeastOneAborted);
    
    log.debug("PutDone: Finished with status {}", globalStatus.toString());
    printRequestOutcome(globalStatus, inputData);
    
    return new ManageFileTransferOutputData(globalStatus, surlsStatuses);
  }

  private static void printRequestOutcome(TReturnStatus status) {
    
    Preconditions.checkNotNull(status);
    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
  }
  
  private static void printRequestOutcome(TReturnStatus status, ManageFileTransferRequestFilesInputData inputData) {
    
    Preconditions.checkNotNull(inputData);
    Preconditions.checkNotNull(status);
    
    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
      inputData.getRequestToken(), inputData.getArrayOfSURLs().asStringList());
  }

  private ArrayOfTSURLReturnStatus loadSURLsStatus(GridUserInterface user,
    TRequestToken requestToken, List<TSURL> inputSURLs)
    throws IllegalArgumentException, RequestUnknownException,
    UnknownTokenException, ExpiredTokenException {

    ArrayOfTSURLReturnStatus returnStatuses = new ArrayOfTSURLReturnStatus(
      inputSURLs.size());

    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

    Map<TSURL, TReturnStatus> surlsStatuses = checker.getSURLStatuses(user,
      requestToken, inputSURLs);

    if (surlsStatuses.isEmpty()) {
      log.info("PutDone: No one of the requested surls found for the provided token");
      throw new RequestUnknownException(
        "No one of the requested surls found for the provided token");
    }

    TReturnStatus status = null;
    for (TSURL surl : inputSURLs) {

      log.debug("PutDone: Checking SURL " + surl);

      if (surlsStatuses.containsKey(surl)) {
        log.debug("PutDone: SURL '{}' found!", surl);
        status = surlsStatuses.get(surl);
      } else {
        log.debug("PutDone: SURL '{}' NOT found in the DB!", surl);
        status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
          "SURL does not refer to an existing file for the specified request token");
      }
      TSURLReturnStatus surlRetStatus = new TSURLReturnStatus(surl, status);
      returnStatuses.addTSurlReturnStatus(surlRetStatus);
    }
    return returnStatuses;
  }


	public static boolean executePutDone(TSURL surl, GridUserInterface user)
		throws PutDoneCommandException {

		Preconditions.checkNotNull(surl, "Null SURL received");

		log.debug("Executing PutDone for SURL: {}", surl.getSURLString());

		String userStr = user == null ? "Anonymous" : user.toString();
		StoRI stori = null;

		try {

			stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl, user);

		} catch (IllegalArgumentException e) {

			log.error(
				String.format("User %s is unable to build a stori for surl %s, %s: %s",
					userStr, surl, e.getClass().getName(), e.getMessage()));
			throw new PutDoneCommandException(CommandHelper
				.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()), e);

		} catch (Exception e) {

			log.info(
				String.format("User %s is unable to build a stori for surl %s, %s: %s",
					userStr, surl, e.getClass().getName(), e.getMessage()));
			return false;

		}
		
		// 1- if the SURL is volatile update the entry in the Volatile table
		if (VolatileAndJiTCatalog.getInstance().exists(stori.getPFN())) {
			try {
				VolatileAndJiTCatalog.getInstance().setStartTime(stori.getPFN(),
					Calendar.getInstance());
			} catch (Exception e) {
				// impossible because of the "exists" check
			}
		}

		// 2- JiTs must me removed from the TURL;
		if (stori.hasJustInTimeACLs()) {
			log.debug("PutDone: JiT case, removing ACEs on SURL: " + surl.toString());
			// Retrieve the PFN of the SURL parents
			List<StoRI> storiParentsList = stori.getParents();
			List<PFN> pfnParentsList = new ArrayList<PFN>(storiParentsList.size());

			for (StoRI parentStoRI : storiParentsList) {
				pfnParentsList.add(parentStoRI.getPFN());
			}
			LocalUser localUser = null;
			try {
				if (user != null) {
					localUser = user.getLocalUser();
				}
			} catch (CannotMapUserException e) {
				log.warn(
					"PutDone: Unable to get the local user for user {}. CannotMapUserException: {}",
					user, e.getMessage());
			}
			if (localUser != null) {
				VolatileAndJiTCatalog.getInstance().expirePutJiTs(stori.getPFN(),
					localUser);
			} else {
				VolatileAndJiTCatalog.getInstance().removeAllJiTsOn(stori.getPFN());
			}
		}

		// 3- compute the checksum and store it in an extended attribute
		LocalFile localFile = stori.getLocalFile();

		VirtualFSInterface vfs = null;
		try {
			vfs = NamespaceDirector.getNamespace().resolveVFSbyLocalFile(localFile);
		} catch (NamespaceException e) {
			log.error(e.getMessage());
			return false;
		}

		// 4- Tape stuff management.
		if (vfs.getStorageClassType().isTapeEnabled()) {
			String fileAbosolutePath = localFile.getAbsolutePath();
			ea.removePinned(fileAbosolutePath);
			ea.setPremigrate(fileAbosolutePath);
		}

		// 5- Update UsedSpace into DB
		vfs.increaseUsedSpace(localFile.getSize());

		return true;
	}

}
