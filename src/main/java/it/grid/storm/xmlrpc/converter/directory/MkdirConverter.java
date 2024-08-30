/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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
import it.grid.storm.synchcall.data.directory.AnonymousMkdirInputData;
import it.grid.storm.synchcall.data.directory.IdentityMkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

public class MkdirConverter implements Converter {

  private static final Logger log = LoggerFactory.getLogger(MkdirConverter.class);

  public MkdirConverter() {

  };

  /**
   * This method return a MkdirInputData created from input Hashtable structure of an xmlrpc Mkdir
   * v2.1 call. Mkdir Input Data can be used to invoke mkdir method of DirectoryFunctionsManager
   */
  public InputData convertToInputData(Map<String, Object> inputParam) {

    log.debug("SrmMkdir: Converter :Call received :Creation of MkdirInputData = {}",
        inputParam.size());
    log.debug("SrmMkdir: Converter: Input Structure toString: {}",
        ParameterDisplayHelper.display(inputParam));

    GridUserInterface guser = GridUserManager.decode(inputParam);

    /* (2) directoryPath */
    TSURL surl = null;
    try {
      surl = TSURL.decode(inputParam, TSURL.PNAME_SURL);
    } catch (InvalidTSURLAttributesException e1) {
      log.debug("SrmMkdir: ErrorCreating surl: {}", e1.toString(), e1);
    }

    MkdirInputData inputData;
    if (guser != null) {
      inputData = new IdentityMkdirInputData(guser, surl);
    } else {
      inputData = new AnonymousMkdirInputData(surl);
    }
    return inputData;
  }

  public Map<String, Object> convertFromOutputData(OutputData outputData) {

    log.debug("SrmMkdir: Converter :Call received :Creation of XMLRPC Output Structure! ");

    Map<String, Object> outputParam = Maps.newHashMap();

    MkdirOutputData odata = (MkdirOutputData) outputData;
    TReturnStatus outputStatus = odata.getStatus();

    outputStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

    // Return Output Structure
    return outputParam;

  }
}
