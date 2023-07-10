/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.IdentityInputData;
import java.io.Serializable;

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
public class IdentityReserveSpaceInputData extends AnonymousReserveSpaceInputData
    implements Serializable, IdentityInputData {

  private static final long serialVersionUID = 2840674835389671669L;
  private final GridUserInterface auth;

  public IdentityReserveSpaceInputData(
      GridUserInterface auth,
      String spaceTokenAlias,
      TRetentionPolicyInfo retentionPolicyInfo,
      TSizeInBytes spaceDesired,
      TSizeInBytes spaceGuaranteed,
      ArrayOfTExtraInfo storageSystemInfo)
      throws IllegalArgumentException {

    super(spaceTokenAlias, retentionPolicyInfo, spaceDesired, spaceGuaranteed, storageSystemInfo);
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
