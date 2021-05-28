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

package it.grid.storm.xmlrpc.converter.space;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.InvalidArrayOfTSpaceTokenAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.AnonymousGetSpaceMetaDataInputData;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataInputData;
import it.grid.storm.synchcall.data.space.GetSpaceMetaDataOutputData;
import it.grid.storm.synchcall.data.space.IdentityGetSpaceMetaDataInputData;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 * 
 * This class represents the Type Converter for GetSpaceMetaData function . This class have get an
 * input data from xmlrpc call anc convert it into a StoRM Type that can be used to invoke the
 * GetSpaceMetaDataManager
 * 
 * @author lucamag
 * @date May 29, 2008
 * 
 */

public class GetSpaceMetaDataConverter implements Converter {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(GetSpaceMetaDataConverter.class);

  public GetSpaceMetaDataConverter() {

  };

  /**
   * This method return a SpaceResData created from input Hashtable structure of an xmlrpc
   * spaceReservation v2.2 call. SpaceResData can be used to invoke SpaceResevation Manager
   */
  public InputData convertToInputData(Map<String, Object> inputParam) {

    /* Creation of VomsGridUser */
    GridUserInterface guser = GridUserManager.decode(inputParam);

    ArrayOfTSpaceToken arrayOfSpaceTokens;
    try {
      arrayOfSpaceTokens =
          ArrayOfTSpaceToken.decode(inputParam, ArrayOfTSpaceToken.PNAME_ARRAYOFSPACETOKENS);
    } catch (InvalidArrayOfTSpaceTokenAttributeException e) {
      arrayOfSpaceTokens = null;
    }

    GetSpaceMetaDataInputData inputData;
    if (guser != null) {
      inputData = new IdentityGetSpaceMetaDataInputData(guser, arrayOfSpaceTokens);
    } else {
      inputData = new AnonymousGetSpaceMetaDataInputData(arrayOfSpaceTokens);
    }
    return inputData;
  }

  public Map<String, Object> convertFromOutputData(OutputData data) {

    log.debug("GetSpaceMetaDataConverter: Creation of XMLRPC Output Structure! ");

    // Creation of new Hashtable to return
    Map<String, Object> outputParam = Maps.newHashMap();

    // outputData
    GetSpaceMetaDataOutputData outputData = (GetSpaceMetaDataOutputData) data;

    /* (1) returnStatus */
    TReturnStatus returnStatus = outputData.getStatus();
    if (returnStatus != null) {
      returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
    }

    /* (2) arrayOfSpaceDetails */
    ArrayOfTMetaDataSpace arrayOfSpaceDetails = outputData.getMetaDataSpaceArray();
    if (arrayOfSpaceDetails != null) {
      arrayOfSpaceDetails.encode(outputParam, ArrayOfTMetaDataSpace.PNAME_ARRAYOFSPACEDETAILS);
    }

    log.debug(outputParam.toString());

    // Return output Parameter structure
    return outputParam;
  }
}
