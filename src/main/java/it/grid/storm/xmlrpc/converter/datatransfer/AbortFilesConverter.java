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

/**
 * This class represents the Type Converter for AbortFiles function. This class
 * receives input datas from xmlrpc call and converts these datas into a StoRM
 * Type that can be used to invoke the AbortManager.
 * 
 * @author Magnoni Luca
 * @author CNAF-INFN Bologna
 * @date Jan 2007
 * @version 1.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousAbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityAbortFilesInputData;
import it.grid.storm.xmlrpc.converter.Converter;

public class AbortFilesConverter implements Converter {

  private static final Logger log = LoggerFactory.getLogger(AbortFilesConverter.class);

  public AbortFilesConverter() {

  }

  /**
   * This method returns a AbortFilesInputData created from the input Hashtable structure of a
   * xmlrpc srmAbortFiles() v2.2 call.
   * 
   * @param inputParam Hashtable containing the input data
   * @return AbortFilesInputData
   */
  public InputData convertToInputData(Map<String, Object> inputParam) {

    GridUserInterface guser = GridUserManager.decode(inputParam);

    TRequestToken requestToken;
    try {
      requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
      log.debug("requestToken={}", requestToken.toString());
    } catch (InvalidTRequestTokenAttributesException e) {
      requestToken = null;
      log.debug("requestToken=NULL", e);
    }

    ArrayOfSURLs arrayOfSURLs;
    try {
      arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAY_OF_SURLS);
    } catch (InvalidArrayOfSURLsAttributeException e) {
      log.debug("Empty surlArray!");
      arrayOfSURLs = null;
    }

    AbortFilesInputData inputData;
    if (guser != null) {
      inputData = new IdentityAbortFilesInputData(guser, requestToken, arrayOfSURLs);
    } else {
      inputData = new AnonymousAbortFilesInputData(requestToken, arrayOfSURLs);
    }
    return inputData;
  }

  public Map<String, Object> convertFromOutputData(OutputData data) {

    log.debug("AbortFilesOutputData - Creation of XMLRPC Output Structure!");

    Map<String, Object> outputParam = Maps.newHashMap();
    AbortFilesOutputData outputData = AbortFilesOutputData.make((AbortGeneralOutputData) data);

    // (1) returnStatus
    TReturnStatus returnStatus = outputData.getReturnStatus();
    if (returnStatus != null) {
      returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
    }

    // (2) arrayOfFileStatuses
    ArrayOfTSURLReturnStatus arrayOfFileStatuses = outputData.getArrayOfFileStatuses();
    if (arrayOfFileStatuses != null) {
      arrayOfFileStatuses.encode(outputParam, ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
    }

    log.debug("AbortFilesConverter - Sending: {}", outputParam.toString());

    // Return global structure.
    return outputParam;
  }
}
