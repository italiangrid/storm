/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the PutDone Input Data associated with the SRM request PutDone
 *
 * @author Alberto Forti
 * @author CNAF -INFN Bologna
 * @date Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityPutDoneInputData extends AnonymousPutDoneInputData
    implements IdentityInputData {

  private final GridUserInterface auth;

  public IdentityPutDoneInputData(
      GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray)
      throws IllegalArgumentException {

    super(reqToken, surlArray);
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
