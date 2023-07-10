/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TSURLInfo data associated with the SRM request, that is it contains
 * info about: TSURL , StorageSystemInfo * @author Magnoni Luca
 *
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.srm.types;

public class TSURLInfo {

  private TSURL surl = null;
  private TStorageSystemInfo systemInfo = null;

  public TSURLInfo() {}

  public TSURLInfo(TSURL surl, TStorageSystemInfo info) throws InvalidTSURLInfoAttributeException {

    boolean ok = (!(surl == null));
    if (!ok) throw new InvalidTSURLInfoAttributeException(surl);
    this.surl = surl;
    this.systemInfo = info;
  }

  /** Method that return SURL specified in SRM request. */
  public TSURL getSurl() {

    return surl;
  }

  public void setSurl(TSURL surl) {

    this.surl = surl;
  }

  /** Set StorageSystemInfo */
  public void setInfo(TStorageSystemInfo info) {

    this.systemInfo = info;
  }

  /** Get StorageSystemInfo */
  public TStorageSystemInfo getInfo() {

    return this.systemInfo;
  }
}
