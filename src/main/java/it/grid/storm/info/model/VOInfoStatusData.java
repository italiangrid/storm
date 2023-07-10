/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.model;

import it.grid.storm.srm.types.TSizeInBytes;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2006
 *
 * <p>Company: INFN-CNAF
 *
 * @author R.Zappi
 * @version 1.0
 */
public class VOInfoStatusData {

  private String voInfoLocalIdentifier = null;
  private TSizeInBytes usedSpaceNearLine = TSizeInBytes.makeEmpty();
  private TSizeInBytes availableSpaceNearLine = TSizeInBytes.makeEmpty();
  private TSizeInBytes ReservedSpaceNearLine = TSizeInBytes.makeEmpty();
  private TSizeInBytes usedSpaceOnLine = TSizeInBytes.makeEmpty();
  private TSizeInBytes availableSpaceOnLine = TSizeInBytes.makeEmpty();
  private TSizeInBytes ReservedSpaceOnLine = TSizeInBytes.makeEmpty();

  public VOInfoStatusData() {}

  public void setVOInfoLocalID(String voInfoLocalID) {

    this.voInfoLocalIdentifier = voInfoLocalID;
  }

  public String getVOInfoLocalID() {

    return this.voInfoLocalIdentifier;
  }
}
