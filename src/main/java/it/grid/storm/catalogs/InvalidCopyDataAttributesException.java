/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;

/** @author Michele Dibenedetto */
public class InvalidCopyDataAttributesException extends InvalidSurlRequestDataAttributesException {

  private static final long serialVersionUID = -1217486426437414490L;
  protected boolean nullDestinationSURL;
  protected boolean nullLifetime;
  protected boolean nullFileStorageType;
  protected boolean nullSpaceToken;
  protected boolean nullOverwriteOption;

  public InvalidCopyDataAttributesException(
      TSURL SURL,
      TSURL destinationSURL,
      TLifeTimeInSeconds lifetime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TOverwriteMode overwriteOption,
      TReturnStatus status) {

    super(SURL, status);
    init(destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption);
  }

  public InvalidCopyDataAttributesException(
      TSURL SURL,
      TSURL destinationSURL,
      TLifeTimeInSeconds lifetime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TOverwriteMode overwriteOption,
      TReturnStatus status,
      String message) {

    super(SURL, status, message);
    init(destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption);
  }

  public InvalidCopyDataAttributesException(
      TSURL SURL,
      TSURL destinationSURL,
      TLifeTimeInSeconds lifetime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TOverwriteMode overwriteOption,
      TReturnStatus status,
      Throwable cause) {

    super(SURL, status, cause);
    init(destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption);
  }

  public InvalidCopyDataAttributesException(
      TSURL SURL,
      TSURL destinationSURL,
      TLifeTimeInSeconds lifetime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TOverwriteMode overwriteOption,
      TReturnStatus status,
      String message,
      Throwable cause) {

    super(SURL, status, message, cause);
    init(destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption);
  }

  private void init(
      TSURL destinationSURL,
      TLifeTimeInSeconds lifetime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TOverwriteMode overwriteOption) {

    nullDestinationSURL = destinationSURL == null;
    nullLifetime = lifetime == null;
    nullFileStorageType = fileStorageType == null;
    nullSpaceToken = spaceToken == null;
    nullOverwriteOption = overwriteOption == null;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("InvalidCopyDataAttributesException [nullDestinationSURL=");
    builder.append(nullDestinationSURL);
    builder.append(", nullLifetime=");
    builder.append(nullLifetime);
    builder.append(", nullFileStorageType=");
    builder.append(nullFileStorageType);
    builder.append(", nullSpaceToken=");
    builder.append(nullSpaceToken);
    builder.append(", nullOverwriteOption=");
    builder.append(nullOverwriteOption);
    builder.append(", nullSURL=");
    builder.append(nullSURL);
    builder.append(", nullStatus=");
    builder.append(nullStatus);
    builder.append("]");
    return builder.toString();
  }
}
