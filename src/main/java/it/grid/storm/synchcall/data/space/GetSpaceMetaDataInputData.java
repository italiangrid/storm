/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.synchcall.data.InputData;

public interface GetSpaceMetaDataInputData extends InputData {

  /** @return the tokenArray */
  public ArrayOfTSpaceToken getSpaceTokenArray();
}
