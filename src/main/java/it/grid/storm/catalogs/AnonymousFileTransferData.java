/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;

public abstract class AnonymousFileTransferData extends SurlMultyOperationRequestData
    implements FileTransferData {

  protected TURLPrefix transferProtocols;
  protected TTURL transferURL;

  public AnonymousFileTransferData(
      TSURL toSURL, TURLPrefix transferProtocols, TReturnStatus status, TTURL transferURL)
      throws InvalidFileTransferDataAttributesException, InvalidSurlRequestDataAttributesException {

    super(toSURL, status);
    if (transferProtocols == null || transferURL == null) {
      throw new InvalidFileTransferDataAttributesException(
          toSURL, transferProtocols, status, transferURL);
    }
    this.transferProtocols = transferProtocols;
    this.transferURL = transferURL;
  }

  @Override
  public final TURLPrefix getTransferProtocols() {

    return transferProtocols;
  }

  @Override
  public final TTURL getTransferURL() {

    return transferURL;
  }

  @Override
  public final void setTransferURL(final TTURL turl) {

    if (turl != null) {
      transferURL = turl;
    }
  }
}
