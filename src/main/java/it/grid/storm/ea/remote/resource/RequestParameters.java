/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.ea.remote.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import it.grid.storm.authz.remote.Constants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RequestParameters {

  private static final Logger log = LoggerFactory.getLogger(RequestParameters.class);

  private final String filePathDecoded;

  private String checksumDecoded;

  RequestParameters(Builder builder) {

    filePathDecoded = decodeAndCheckFilePath(builder.filePath);
    checksumDecoded = decodeAndCheckChecksum(builder.checksum);
  }

  private static String decodeAndCheckFilePath(String filePath)
      throws WebApplicationException, IllegalArgumentException {

    if (filePath == null) {
      throw new IllegalArgumentException(
          "Unable to decode file path. Invalid parameters: filePath=" + filePath);
    }
    String filePathDecoded;
    try {
      filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity(
                  "Unable to decode filePath paramether, unsupported encoding \'"
                      + Constants.ENCODING_SCHEME
                      + "\'")
              .build());
    }

    log.debug("Decoded filePath = {}", filePathDecoded);

    if (filePathDecoded == null || filePathDecoded.trim().equals("")) {
      log.error(
          "Unable to evaluate permissions. Some parameters are missing : filePath {}",
          filePathDecoded);
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity("Unable to manage checksum. Some parameters are missing")
              .build());
    }
    return filePathDecoded;
  }

  private static String decodeAndCheckChecksum(String checksum) throws WebApplicationException {

    if (checksum == null) return null;
    String checksumDecoded;
    try {
      checksumDecoded = URLDecoder.decode(checksum, Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity(
                  "Unable to decode checksum paramether, unsupported encoding \'"
                      + Constants.ENCODING_SCHEME
                      + "\'")
              .build());
    }
    log.debug("Decoded checksum = " + checksumDecoded);

    if (checksum == null || checksum.trim().equals("")) {
      log.error(
          "Unable to evaluate permissions. Some parameters are missing : checksum {}",
          checksumDecoded);
      throw new WebApplicationException(
          Response.status(BAD_REQUEST)
              .entity("Unable to manage checksum. Some parameters are missing")
              .build());
    }
    return checksumDecoded;
  }

  /** @return the filePathDecoded */
  public String getFilePathDecoded() {

    return filePathDecoded;
  }

  /** @return the filePathDecoded */
  public String getChecksumDecoded() {

    return checksumDecoded;
  }

  public static class Builder {

    private final String filePath;
    private String checksum;

    public Builder(String filePath) throws WebApplicationException {

      this.filePath = filePath;
    }

    public Builder checksum(String checksum) {

      this.checksum = checksum;
      return this;
    }

    public RequestParameters build() {

      return new RequestParameters(this);
    }
  }
}
