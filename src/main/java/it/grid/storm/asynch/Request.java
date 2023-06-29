/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

public interface Request {

  public String getUserDN();

  public String getSURL();

  /**
   * @return boolean
   */
  public boolean isResultSuccess();

}
