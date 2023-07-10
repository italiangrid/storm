/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the Rmdir Input Data associated with the SRM request, that is it contains
 * info about: ...,ecc. * @author Magnoni Luca
 *
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityRmdirInputData extends AnonymousRmdirInputData implements IdentityInputData {

  private final GridUserInterface auth;

  public IdentityRmdirInputData(GridUserInterface auth, TSURL surl, Boolean recursive)
      throws IllegalArgumentException {

    super(surl, recursive);
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
