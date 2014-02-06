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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.SpaceCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensInputData;
import it.grid.storm.synchcall.data.space.IdentityGetSpaceTokensInputData;
import it.grid.storm.synchcall.data.space.GetSpaceTokensOutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project * Execute the GetSpaceTokens
 * request.
 * 
 * @author lucamag
 * @author Alberto Forti
 * 
 * @date May 29, 2008
 * 
 */

public class GetSpaceTokensCommand extends SpaceCommand implements Command {

  public static final Logger log = LoggerFactory
    .getLogger(GetSpaceTokensCommand.class);

  private static final String SRM_COMMAND = "srmGetSpaceTokens";
  private ReservedSpaceCatalog catalog = null;

  public GetSpaceTokensCommand() {

    catalog = new ReservedSpaceCatalog();
  };

  public OutputData execute(InputData data) {

    GetSpaceTokensOutputData outputData;
    IdentityGetSpaceTokensInputData inputData;
    if (data instanceof IdentityInputData) {
      inputData = (IdentityGetSpaceTokensInputData) data;
    } else {
      outputData = new GetSpaceTokensOutputData();
      outputData.setStatus(CommandHelper.buildStatus(
        TStatusCode.SRM_NOT_SUPPORTED, "Anonymous user can not perform"
          + SRM_COMMAND));
      printRequestOutcome(outputData.getStatus(),
        (GetSpaceTokensInputData) data);
      return outputData;
    }

    TReturnStatus status = null;

    log.debug("Started GetSpaceTokens function");

    GridUserInterface user = inputData.getUser();
    if (user == null) {
      log.debug("GetSpaceTokens: the user field is NULL");
      try {
        status = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
          "Unable to get user credential!");
      } catch (InvalidTReturnStatusAttributeException ex1) {
        log.error(ex1.getMessage(), ex1);
      }

      log.error("srmGetSpaceTokens: <{}> "
        + "Request for [spaceTokenDescription:{}] failed with: [status: {}]",
        user,
        inputData.getSpaceTokenAlias(),
        status);

      outputData = new GetSpaceTokensOutputData(status, null);
      return outputData;
    }

    String spaceAlias = inputData.getSpaceTokenAlias();
    log.debug("spaceAlias= {}", spaceAlias);
    
    ArrayOfTSpaceToken arrayOfSpaceTokens = catalog.getSpaceTokens(user,
      spaceAlias);

    if (arrayOfSpaceTokens.size() == 0) {
      arrayOfSpaceTokens = catalog.getSpaceTokensByAlias(spaceAlias);
    }

    try {
      if (arrayOfSpaceTokens.size() == 0) {
        if (spaceAlias != null) {
          status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
            "'userSpaceTokenDescription' does not refer to an existing space");
        } else {
          status = new TReturnStatus(TStatusCode.SRM_FAILURE,
            "No space tokens owned by this user");
        }
        arrayOfSpaceTokens = null;
      } else {
        status = new TReturnStatus(TStatusCode.SRM_SUCCESS, "");
      }
    } catch (InvalidTReturnStatusAttributeException e) {
      log.error(e.getMessage(), e);
    }

    if (status.isSRM_SUCCESS()) {
      log.info("srmGetSpaceTokens: <{}> Request for [spaceTokenDescription: {}] "
        + "succesfully done with: [status: {}]",
        user,
        inputData.getSpaceTokenAlias(),
        status);
    } else {
      log.error("srmGetSpaceTokens: <{}> Request for [spaceTokenDescription: {}] "
        + "failed with: [status: {}]",
        user,
        inputData.getSpaceTokenAlias(),
        status);
    }

    outputData = new GetSpaceTokensOutputData(status, arrayOfSpaceTokens);

    return outputData;

  }

  private void printRequestOutcome(TReturnStatus status,
    GetSpaceTokensInputData data) {

    if (data != null) {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, data);
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }
}
