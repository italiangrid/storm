/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityManageFileTransferRequestFilesInputData
    extends AnonymousManageFileTransferRequestFilesInputData implements IdentityInputData {

  private final GridUserInterface auth;

  public IdentityManageFileTransferRequestFilesInputData(
      GridUserInterface auth, TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs)
      throws IllegalArgumentException {

    super(requestToken, arrayOfSURLs);
    if (auth == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: auth=" + auth);
    }
    this.auth = auth;
  }

  @Override
  public GridUserInterface getUser() {

    return this.auth;
  }

  @Override
  public String getPrincipal() {

    return this.auth.getDn();
  }
}
