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

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestOutputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * 
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 10, 2008
 * 
 */

public class AbortRequestCommand extends DataTransferCommand implements Command {

  private static final Logger log = LoggerFactory
    .getLogger(AbortRequestCommand.class);

  private AdvancedPicker advancedPicker = null;
  private AbortExecutorInterface executor = null;

  public AbortRequestCommand() {

  };

  public OutputData execute(InputData data) {

    advancedPicker = new AdvancedPicker();
    AbortRequestOutputData outputData = new AbortRequestOutputData();
    AbortInputData inputData = (AbortInputData) data;

    boolean res = false;
    TReturnStatus globalStatus = null;

    AbortRequestCommand.log.debug("Started AbortRequest function.");

    if (inputData == null || inputData.getRequestToken() == null
      || (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES))) {
      log.debug("SrmAbortRequest: Invalid input parameter specified");

      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Missing mandatory parameters");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      log
        .error(
          "srmAbortRequest: <> Request for [token:] [SURL:] failed with [status: {}]",
          globalStatus);
      return outputData;
    }

    TRequestToken requestToken = inputData.getRequestToken();
    log.debug("srmAbortRequest: requestToken={}", requestToken);

    if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {

      AbortRequestCommand.log
        .debug("Phase (1.A) AbortRequest: SurlArray Not specified.");

      GridUserInterface user = getUserFromInputData(inputData);
      SURLStatusManager manager = SURLStatusManagerFactory
        .newSURLStatusManager();

      boolean hasErrors = false;
      try {

        manager.abortRequest(user, requestToken, "Request aborted by user");

      } catch (UnknownTokenException e) {

        hasErrors = true;
        log.info("Unknown token: {}", e.getMessage());
        globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Invalid request token");

      } catch (ExpiredTokenException e) {

        hasErrors = true;
        log.info("Expired token: {}. {}", requestToken, e.getMessage());
        globalStatus = new TReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
          "Request expired"); 

      } catch (AuthzException e) {

        hasErrors = true;
        log.info("Authorization error: {}", e.getMessage());
        if (log.isDebugEnabled()) {
          log.debug(e.getMessage(), e);
        }
        globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
          e.getMessage());

      } catch (IllegalArgumentException e) {
        
        hasErrors = true;
        log.info("Invalid request error: {}", e);
        globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          e.getMessage());
        
      } finally {
        
        if (hasErrors) {
          
          outputData.setArrayOfFileStatuses(null);
          outputData.setReturnStatus(globalStatus);
          CommandHelper.printRequestOutcome("srmAbortRequest", log, globalStatus,
            inputData, inputData.getRequestToken());
          return outputData;
          
        }
      }

      RequestSummaryCatalog.getInstance().updateFromPreviousGlobalStatus(
        inputData.getRequestToken(), TStatusCode.SRM_REQUEST_QUEUED,
        TStatusCode.SRM_ABORTED, "User aborted request!");

      res = false;

    } else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {

      log.debug("Phase (1.A) AbortRequest: SurlArray Specified.");
      res = false;
    }

    if (res == false) {
      log.debug("Phase (1.A) AbortRequest: Token not found.");
    }

    if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {
      log.debug("Phase (1.B) AbortRequest: SurlArray Not specified.");

      advancedPicker.abortRequest(inputData.getRequestToken());
      res = false;

    } else if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
      log.debug("Phase (1.B) AbortRequest: SurlArray Specified.");
      res = false;
    }

    if (res == false) {
      log.debug("Phase (1.B) AbortRequest: Token not found.");
    }


    TRequestType rtype = RequestSummaryCatalog.getInstance().typeOf(
      requestToken);

    if (rtype == TRequestType.EMPTY) {

      globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
        "Request aborted succesfully");

      CommandHelper.printRequestOutcome("srmAbortRequest", log, globalStatus,
        inputData, inputData.getRequestToken());

      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      return outputData;

    } else if (rtype == TRequestType.PREPARE_TO_GET) {
      executor = new PtGAbortExecutor();
      return executor.doIt(inputData);
    } else if (rtype == TRequestType.PREPARE_TO_PUT) {
      executor = new PtPAbortExecutor();
      return executor.doIt(inputData);
    } else if (rtype == TRequestType.COPY) {
      executor = new CopyAbortExecutor();
      return executor.doIt(inputData);
    } else {
      log.debug("SrmAbortRequest : Invalid input parameter specified");

      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Invalid request token. Abort only works for PtG, PtP and Copy.");

      CommandHelper.printRequestOutcome("srmAbortRequest", log, globalStatus,
        inputData, inputData.getRequestToken());

      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      return outputData;
    }
  }

}
