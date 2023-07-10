/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;

public interface FileTransferInputData extends InputData {

  /** @return the surl */
  public TSURL getSurl();

  /** @return the transferProtocols */
  public TURLPrefix getTransferProtocols();

  /** @return the targetSpaceToken */
  public TSpaceToken getTargetSpaceToken();

  /** @param targetSpaceToken */
  public void setTargetSpaceToken(TSpaceToken targetSpaceToken);

  /** @return */
  public TLifeTimeInSeconds getDesiredPinLifetime();

  /** @param desiredPinLifetime */
  public void setDesiredPinLifetime(TLifeTimeInSeconds desiredPinLifetime);
}
