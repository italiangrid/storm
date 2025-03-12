/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeOutputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityExtendFileLifeTimeInputData;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * This class represents the Type Converter for ExtendFileLifeTime function. This class receives
 * input datas from xmlrpc call and converts these datas into a StoRM Type that can be used to
 * invoke the ExtendFileLifeTimeManager.
 * 
 */
public class ExtendFileLifeTimeConverter implements Converter {

  private static final Logger log = LoggerFactory.getLogger(ExtendFileLifeTimeConverter.class);

  public ExtendFileLifeTimeConverter() {

  }

  public InputData convertToInputData(Map<String, Object> inputParam) {

    GridUserInterface guser = GridUserManager.decode(inputParam);

    TRequestToken requestToken;
    try {
      requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
      log.debug("requestToken={}", requestToken.toString());
    } catch (InvalidTRequestTokenAttributesException e) {
      requestToken = null;
      log.error("requestToken=NULL", e);
    }

    ArrayOfSURLs arrayOfSURLs;
    try {
      arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAY_OF_SURLS);
    } catch (InvalidArrayOfSURLsAttributeException e) {
      log.error("Empty surlArray!", e);
      arrayOfSURLs = null;
    }

    TLifeTimeInSeconds newFileLifetime =
        TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_FILELIFETIME);

    TLifeTimeInSeconds newPinLifetime =
        TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_PINLIFETIME);

    ExtendFileLifeTimeInputData inputData;
    if (guser != null) {
      inputData = new IdentityExtendFileLifeTimeInputData(guser, requestToken, arrayOfSURLs,
          newFileLifetime, newPinLifetime);
    } else {
      inputData = new AnonymousExtendFileLifeTimeInputData(requestToken, arrayOfSURLs,
          newFileLifetime, newPinLifetime);
    }
    return inputData;
  }

  public Map<String, Object> convertFromOutputData(OutputData data) {

    log.debug("ExtendFileLifeTimeOutputData - Creation of XMLRPC Output Structure!");

    Map<String, Object> outputParam = Maps.newHashMap();
    ExtendFileLifeTimeOutputData outputData = (ExtendFileLifeTimeOutputData) data;

    // (1) returnStatus
    TReturnStatus returnStatus = outputData.getReturnStatus();
    if (returnStatus != null) {
      returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
    }

    // (2) arrayOfFileStatuses
    ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses = outputData.getArrayOfFileStatuses();
    if (arrayOfFileStatuses != null) {
      arrayOfFileStatuses.encode(outputParam,
          ArrayOfTSURLLifetimeReturnStatus.PNAME_ARRAYOFFILESTATUSES);
    }

    log.debug("ExtendFileLifeTimeConverter - Sending: {}", outputParam.toString());

    // Return global structure.
    return outputParam;
  }
}
