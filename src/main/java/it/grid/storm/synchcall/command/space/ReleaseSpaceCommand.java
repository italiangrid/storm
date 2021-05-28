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

package it.grid.storm.synchcall.command.space;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.common.types.PFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.space.StorageSpaceData;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.IdentityReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceOutputData;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the ReleaseSpaceManager Class. This class hava a reseveSpace method that
 * perform all operation nedded to satisfy a SRM space release request.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

public class ReleaseSpaceCommand extends SpaceCommand implements Command {

  private final ReservedSpaceCatalog catalog = ReservedSpaceCatalog.getInstance();

  private static final Logger log = LoggerFactory.getLogger(ReleaseSpaceCommand.class);

  private static final String SRM_COMMAND = "srmReleaseSpace";

  public OutputData execute(InputData indata) {

    ReleaseSpaceOutputData outputData = new ReleaseSpaceOutputData();
    IdentityReleaseSpaceInputData inputData;
    if (indata instanceof IdentityInputData) {
      inputData = (IdentityReleaseSpaceInputData) indata;
    } else {
      outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_NOT_SUPPORTED,
          "Anonymous user can not perform" + SRM_COMMAND));
      printRequestOutcome(outputData.getStatus(), (ReleaseSpaceInputData) indata);
      return outputData;
    }
    TReturnStatus returnStatus = null;

    if ((inputData == null) || ((inputData != null) && (inputData.getSpaceToken() == null))) {
      log.error("Empty space token.");
      returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "SpaceToken is empty.");
      outputData.setStatus(returnStatus);
      return outputData;
    }

    GridUserInterface user = inputData.getUser();
    if (user == null) {
      log.debug("Null user credentials.");
      returnStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
          "Unable to get user credential");
      outputData.setStatus(returnStatus);

      log.error("srmReleaseSpace: <{}> Request for [spacetoken: {}] failed " + "with: [status: {}]",
          user, inputData.getSpaceToken(), returnStatus);

      return outputData;
    }

    boolean forceFileRelease = inputData.isForceFileRelease();
    boolean nopinned = true;
    String explanation = "";
    TStatusCode statusCode = null;

    StorageSpaceData data;
    try {
      data = catalog.getStorageSpace(inputData.getSpaceToken());
    } catch (Throwable e) {
      log.error("Error fetching data for space token {}. {}", inputData.getSpaceToken(),
          e.getMessage(), e);

      explanation = "Error building space data from row DB data.";
      statusCode = TStatusCode.SRM_INTERNAL_ERROR;
      returnStatus = new TReturnStatus(statusCode, explanation);
      outputData.setStatus(returnStatus);

      log.error("srmReleaseSpace: <{}> Request for [spacetoken: {}] failed " + "with: [status: {}]",
          user, inputData.getSpaceToken(), returnStatus);

      return outputData;
    }

    if (data == null) {
      explanation = "SpaceToken does not refers to an existing space.";
      statusCode = TStatusCode.SRM_INVALID_REQUEST;
      returnStatus = new TReturnStatus(statusCode, explanation);
      outputData.setStatus(returnStatus);

      log.error("srmReleaseSpace: <{}> Request for [spacetoken: {}] failed " + "with: [status: {}]",
          user, inputData.getSpaceToken(), returnStatus);

      return outputData;
    }

    if (!forceFileRelease) {
      nopinned = true;
    }

    if ((forceFileRelease) || (nopinned)) {

      if (data.getOwner().getDn().equals(user.getDn())) {

        log.debug("Authorized Release Space for user: {}", data.getOwner());
        returnStatus = manageAuthorizedReleaseSpace(data, user);

      } else {

        log.debug("User {} not authorized to release space.", data.getOwner());
        explanation = "User is not authorized to release this token";
        statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
        returnStatus = new TReturnStatus(statusCode, explanation);
      }

    } else {
      log.debug("Space contains pinned files.");
      explanation = "Space still contains pinned files. ";
      statusCode = TStatusCode.SRM_FAILURE;
      returnStatus = new TReturnStatus(statusCode, explanation);
    }

    outputData.setStatus(returnStatus);

    if (returnStatus.isSRM_SUCCESS()) {

      log.error("srmReleaseSpace: <{}> Request for [spacetoken: {}] succesfully done "
          + "with: [status: {}]", user, inputData.getSpaceToken(), returnStatus);

    } else {

      log.error("srmReleaseSpace: <" + user + "> Request for [spacetoken:"
          + inputData.getSpaceToken() + "] for failed with: [status:" + returnStatus + "]");

    }

    return outputData;
  }

  /**
   * 
   * @param user GridUserInterface
   * @param data StorageSpaceData
   * @return TReturnStatus
   */
  private TReturnStatus manageAuthorizedReleaseSpace(StorageSpaceData data,
      GridUserInterface user) {

    String spaceFileName;
    PFN pfn = data.getSpaceFileName();

    if (pfn != null) {
      spaceFileName = pfn.getValue();
      log.debug("spaceFileName: {}", spaceFileName);
      File spaceFile = new File(spaceFileName);
      if (spaceFile.delete()) {
        if (catalog.release(user, data.getSpaceToken())) {
          return new TReturnStatus(TStatusCode.SRM_SUCCESS, "Space Released.");
        } else {
          return new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
              "Space removed, but spaceToken was not found in the DB");
        }
      } else {
        return new TReturnStatus(TStatusCode.SRM_FAILURE, "Space can not be removed by StoRM!");
      }
    } else {
      return new TReturnStatus(TStatusCode.SRM_FAILURE, "SRM Internal failure.");
    }
  }

  private void printRequestOutcome(TReturnStatus status, ReleaseSpaceInputData indata) {

    if (indata != null) {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, indata);
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }

}
