/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousReleaseRequestInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityReleaseRequestInputData;
import it.grid.storm.xmlrpc.converter.Converter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageFileTransferRequestConverter extends ManageFileTransferConverter
    implements Converter {

  static final Logger log = LoggerFactory.getLogger(ManageFileTransferRequestConverter.class);

  /**
   * This method returns a ReleaseFilesInputData created from the input Hashtable structure of an
   * xmlrpc ReleaseFiles v2.2 call.
   *
   * @param inputParam Hashtable containing the input data
   * @return ReleaseFilesInputData
   */
  public InputData convertToInputData(Map inputParam) {

    GridUserInterface guser = GridUserManager.decode(inputParam);

    /* (2) TRequestToken requestToken */
    TRequestToken requestToken = null;
    try {
      requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
      log.debug("requestToken={}", requestToken.toString());
    } catch (InvalidTRequestTokenAttributesException e) {
      log.debug(
          "No request token provided by user. InvalidTRequestTokenAttributesException: {}",
          e.getMessage(),
          e);
    }

    /* (3) anyURI[] arrayOfSURLs */
    ArrayOfSURLs arrayOfSURLs;
    try {
      arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAY_OF_SURLS);
    } catch (InvalidArrayOfSURLsAttributeException e) {
      log.debug("Empty surlArray!", e);
      arrayOfSURLs = null;
    }

    InputData inputData;
    if (guser != null) {
      if (requestToken != null) {
        if (arrayOfSURLs != null && arrayOfSURLs.size() > 0) {
          inputData =
              new IdentityManageFileTransferRequestFilesInputData(
                  guser, requestToken, arrayOfSURLs);
        } else {
          inputData = new IdentityReleaseRequestInputData(guser, requestToken);
        }
      } else {
        inputData = new IdentityManageFileTransferFilesInputData(guser, arrayOfSURLs);
      }
    } else {
      if (requestToken != null) {
        if (arrayOfSURLs != null && arrayOfSURLs.size() > 0) {
          inputData =
              new AnonymousManageFileTransferRequestFilesInputData(requestToken, arrayOfSURLs);
        } else {
          inputData = new AnonymousReleaseRequestInputData(requestToken);
        }
      } else {
        inputData = new AnonymousManageFileTransferFilesInputData(arrayOfSURLs);
      }
    }
    return inputData;
  }

  @Override
  protected Logger getLogger() {

    return log;
  }
}
