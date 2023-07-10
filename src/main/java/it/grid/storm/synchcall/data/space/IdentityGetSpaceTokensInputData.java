/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 *
 * <p>GetSpaceTokens request input data.
 *
 * @author lucamag
 * @author Alberto Forti
 * @date May 29, 2008
 */
public class IdentityGetSpaceTokensInputData extends AnonymousGetSpaceTokensInputData
    implements IdentityInputData {

  private final GridUserInterface auth;

  public IdentityGetSpaceTokensInputData(GridUserInterface auth, String spaceTokenAlias) {

    super(spaceTokenAlias);
    if (auth == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: auth=" + auth);
    }
    this.auth = auth;
  }

  @Override
  public GridUserInterface getUser() {

    return auth;
  }

  @Override
  public String getPrincipal() {

    return this.auth.getDn();
  }
}
