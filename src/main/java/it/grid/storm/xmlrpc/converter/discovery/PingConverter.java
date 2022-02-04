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
 * This class represents the Type Converter for the Ping function. This class
 * receives input data from xmlrpc call and fills the PingInputData class.
 * 
 * @author Alberto Forti
 * @author CNAF-INFN Bologna
 * @date Feb 2007
 * @version 1.0
 */

package it.grid.storm.xmlrpc.converter.discovery;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.discovery.AnonymousPingInputData;
import it.grid.storm.synchcall.data.discovery.IdentityPingInputData;
import it.grid.storm.synchcall.data.discovery.PingInputData;
import it.grid.storm.synchcall.data.discovery.PingOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

public class PingConverter implements Converter {

  private static final Logger log = LoggerFactory.getLogger(PingConverter.class);

  public PingConverter() {

  }

  public InputData convertToInputData(Map<String, Object> inputParam) {

    log.debug("Ping: input converter started. InputParam ");

    GridUserInterface requestor = GridUserManager.decode(inputParam);

    String authorizationID = (String) inputParam.get("authorizationID");

    PingInputData inputData;
    if (requestor != null) {
      inputData = new IdentityPingInputData(requestor, authorizationID);
    } else {
      inputData = new AnonymousPingInputData(authorizationID);
    }
    log.debug("Ping: input converter has finished.");
    return inputData;
  }

  public Map<String, Object> convertFromOutputData(OutputData data) {

    log.debug("Ping: output converter started.");
    Map<String, Object> outputParam = Maps.newHashMap();
    PingOutputData outputData = (PingOutputData) data;
    String versionInfo = outputData.getVersionInfo();
    if (versionInfo != null) {
      outputParam.put("versionInfo", versionInfo);
    }

    ArrayOfTExtraInfo extraInfoArray = outputData.getExtraInfoArray();
    if (extraInfoArray != null) {
      extraInfoArray.encode(outputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
    }

    log.debug("Ping: output converter has finished.");
    return outputParam;
  }
}
