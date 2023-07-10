/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import it.grid.storm.synchcall.data.InputData;

public interface GetSpaceTokensInputData extends InputData {

  /** @return the spaceTokenAlias */
  public String getSpaceTokenAlias();
}
