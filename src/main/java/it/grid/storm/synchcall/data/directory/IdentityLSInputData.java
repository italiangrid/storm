/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 *
 * <p>This class represents the LS Input Data associated with the SRM request, that is it contains
 * info about: ...,ecc.
 *
 * @author lucamag
 * @date May 28, 2008
 */
public class IdentityLSInputData extends AnonymousLSInputData implements IdentityInputData {

  private final GridUserInterface auth;

  public IdentityLSInputData(
      GridUserInterface auth,
      ArrayOfSURLs surlArray,
      TFileStorageType fileStorageType,
      Boolean fullDetList,
      Boolean allLev,
      Integer numOfLev,
      Integer offset,
      Integer count)
      throws IllegalArgumentException {

    super(surlArray, fileStorageType, fullDetList, allLev, numOfLev, offset, count);
    if (auth == null) {
      throw new IllegalArgumentException(
          "Unable to create the object, invalid arguments: auth=" + auth);
    }
    this.auth = auth;
  }

  /** Get User */
  @Override
  public GridUserInterface getUser() {

    return this.auth;
  }

  @Override
  public String getPrincipal() {

    return this.auth.getDn();
  }
}
