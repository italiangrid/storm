/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

public interface RmdirInputData extends InputData {

  /** @return the surl */
  public TSURL getSurl();

  /** @return the recursive */
  public Boolean getRecursive();
}
