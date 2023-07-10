/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

/** @author Enrico Vianello */
public class PrepareToPutOutputData extends FileTransferOutputData {

  public PrepareToPutOutputData(
      TSURL surl, TTURL turl, TReturnStatus status, TRequestToken requestToken)
      throws IllegalArgumentException {

    super(surl, turl, status, requestToken);
  }

  @Override
  public boolean isSuccess() {

    return this.getStatus().getStatusCode().equals(TStatusCode.SRM_SPACE_AVAILABLE);
  }
}
