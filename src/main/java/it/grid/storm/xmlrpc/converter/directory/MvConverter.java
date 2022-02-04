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

package it.grid.storm.xmlrpc.converter.directory;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.AnonymousMvInputData;
import it.grid.storm.synchcall.data.directory.IdentityMvInputData;
import it.grid.storm.synchcall.data.directory.MvInputData;
import it.grid.storm.synchcall.data.directory.MvOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 * 
 * This class represents the Type Converter for SrmMv function . This class have get an input data
 * from xmlrpc call anc convert it into a StoRM Type that can be used to invoke the MvExecutor.
 * 
 * @author lucamag
 * @date May 28, 2008
 * 
 */

public class MvConverter implements Converter {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(MvConverter.class);

  public MvConverter() {

  };

  /**
   * This method return a MvInputData created from input Map structure of an xmlrpc SrmMv v2.2 call.
   */
  public InputData convertToInputData(Map<String, Object> inputParam) {

    log.debug("SrmMv: Converter :Call received :Creation of MvInputData = {}", inputParam.size());
    log.debug("SrmMv: Converter: Input Structure toString: {}",
        ParameterDisplayHelper.display(inputParam));

    GridUserInterface guser = GridUserManager.decode(inputParam);

    /* (2) fromSURL */
    TSURL fromSURL = null;
    try {
      fromSURL = TSURL.decode(inputParam, TSURL.PNAME_FROMSURL);
    } catch (InvalidTSURLAttributesException e1) {
      log.debug("SrmMv: ErrorCreating surl: {}", e1.toString(), e1);
    }

    /* (3) toSURL */
    TSURL toSURL = null;
    try {
      toSURL = TSURL.decode(inputParam, TSURL.PNAME_TOSURL);
    } catch (InvalidTSURLAttributesException e1) {
      log.debug("SrmMv: ErrorCreating surl: {}", e1.toString(), e1);
    }

    MvInputData inputData;
    if (guser != null) {
      inputData = new IdentityMvInputData(guser, fromSURL, toSURL);
    } else {
      inputData = new AnonymousMvInputData(fromSURL, toSURL);
    }
    return inputData;

  }

  public Map<String, Object> convertFromOutputData(OutputData data) {

    log.debug("SrmMv: Converter :Call received :Creation of XMLRPC Output Structure! ");
    // Output structure to return to xmlrpc client
    Map<String, Object> outputParam = Maps.newHashMap();
    MvOutputData outputData = (MvOutputData) data;
    TReturnStatus status = outputData.getStatus();
    status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

    // Return Output Structure
    return outputParam;
  }
}
