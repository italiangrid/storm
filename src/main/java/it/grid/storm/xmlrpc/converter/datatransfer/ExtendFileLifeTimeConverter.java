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
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class represents the Type Converter for ExtendFileLifeTime function. This class receives
 * input datas from xmlrpc call and converts these datas into a StoRM Type that can be used to
 * invoke the ExtendFileLifeTimeManager.
 * 
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * 
 * @date = Oct 10, 2008
 * 
 */

public class ExtendFileLifeTimeConverter implements Converter {

  private static final Logger log = LoggerFactory.getLogger(ExtendFileLifeTimeConverter.class);

  public ExtendFileLifeTimeConverter() {

  }

  /**
   * This method returns a ExtendFileLifeTimeInputData created from the input Hashtable structure of
   * a xmlrpc srmExtendFileLifeTime() v2.2 call.
   * 
   * @param inputParam Hashtable containing the input data
   * @return ExtendFileLifeTimeInputData
   */
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
