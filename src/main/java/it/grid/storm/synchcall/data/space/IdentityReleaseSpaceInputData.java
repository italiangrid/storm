/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class represents the SpaceReservationData associated with the SRM request, that is it
 * contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc. Number of files
 * progressing, Number of files finished, and whether the request is currently suspended.
 *
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
public class IdentityReleaseSpaceInputData extends AnonymousReleaseSpaceInputData
    implements IdentityInputData {

  private final GridUserInterface gUser;

  public IdentityReleaseSpaceInputData(
      GridUserInterface auth, TSpaceToken spaceToken, Boolean forceFileRelease)
      throws IllegalArgumentException {

    super(spaceToken, forceFileRelease);
    if (auth == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: auth=" + auth);
    }
    this.gUser = auth;
  }

  @Override
  public GridUserInterface getUser() {

    return this.gUser;
  }

  @Override
  public String getPrincipal() {

    return this.gUser.getDn();
  }
}
