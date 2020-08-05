package it.grid.storm.authz.remote.resource;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.authz.remote.Constants;

class RequestParameters {

  private static final Logger log = LoggerFactory.getLogger(RequestParameters.class);

  private final String DNDecoded, FQANSDecoded, filePathDecoded;
  private final boolean hasVomsExtension;

  RequestParameters(String filePath) throws WebApplicationException {

    log.debug("Building RequestParameters from filePath = {}", filePath);

    filePathDecoded = decodeAndCheckFilePath(filePath);
    hasVomsExtension = false;
    FQANSDecoded = null;
    DNDecoded = null;
  }

  RequestParameters(String filePath, String DN) throws WebApplicationException {

    log.debug("Building RequestParameters from filePath={}, DN={}", filePath, DN);

    filePathDecoded = decodeAndCheckFilePath(filePath);
    DNDecoded = decodeAndCheckDN(DN);
    hasVomsExtension = false;
    FQANSDecoded = null;
  }

  RequestParameters(String filePath, String DN, String FQANS) throws WebApplicationException {

    log.debug("Building RequestParameters from filePath={}, DN={}, FQANS={}", filePath, DN, FQANS);

    filePathDecoded = decodeAndCheckFilePath(filePath);
    DNDecoded = decodeAndCheckDN(DN);
    FQANSDecoded = decodeAndCheckFQANS(FQANS);
    hasVomsExtension = true;
  }

  private static String decodeAndCheckFilePath(String filePath) throws WebApplicationException {

    String filePathDecoded;
    try {
      filePathDecoded = URLDecoder.decode(filePath, Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      log.error("Unable to decode filePath parameter. " + "UnsupportedEncodingException: {}",
          e.getMessage(), e);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to decode filePath paramether, unsupported encoding \'"
            + Constants.ENCODING_SCHEME + "\'")
        .build());
    }
    log.debug("Decoded filePath = {}", filePathDecoded);

    if (filePathDecoded == null || filePathDecoded.trim().equals("")) {
      log.error("Unable to evaluate permissions. Some parameters are missing: " + "filePath {}",
          filePathDecoded);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to evaluate permissions. Some parameters are missing")
        .build());
    }
    URI filePathURI;
    try {
      filePathURI = new URI(filePathDecoded);
    } catch (URISyntaxException e) {
      log.error("Unable to evaluate permissions on path {}. " + "URISyntaxException: {}",
          filePathDecoded, e.getMessage(), e);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to evaluate permissions. Invalid file path")
        .build());
    }
    return filePathURI.normalize().toString();
  }

  private static String decodeAndCheckDN(String DN) throws WebApplicationException {

    String DNDecoded;
    try {
      DNDecoded = URLDecoder.decode(DN, Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      log.error("Unable to decode DN parameter. " + "UnsupportedEncodingException: {}",
          e.getMessage(), e);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to decode DN paramether, unsupported encoding \'"
            + Constants.ENCODING_SCHEME + "\'")
        .build());
    }
    log.debug("Decoded DN = {}", DNDecoded);

    if (DNDecoded == null || DNDecoded.trim().equals("")) {
      log.error("Unable to evaluate permissions. Some parameters are missing : DN {}", DNDecoded);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to evaluate permissions. Some parameters are missing")
        .build());
    }
    return DNDecoded;
  }

  private static String decodeAndCheckFQANS(String FQANS) throws WebApplicationException {

    String FQANSDecoded;
    try {
      FQANSDecoded = URLDecoder.decode(FQANS, Constants.ENCODING_SCHEME);
    } catch (UnsupportedEncodingException e) {
      log.error("Unable to decode FQANS parameter. " + "UnsupportedEncodingException: {}",
          e.getMessage(), e);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to decode FQANS paramether, unsupported encoding \'"
            + Constants.ENCODING_SCHEME + "\'")
        .build());
    }
    log.debug("Decoded FQANS = {}", FQANSDecoded);

    if (FQANSDecoded == null || FQANSDecoded.trim().equals("")) {
      log.error("Unable to evaluate permissions. Some parameters are " + "missing : FQANS {}",
          FQANS);
      throw new WebApplicationException(Response.status(BAD_REQUEST)
        .entity("Unable to evaluate permissions. Some parameters are missing")
        .build());
    }
    return FQANSDecoded;
  }

  /**
   * @return the dNDecoded
   */
  public String getDNDecoded() {

    return DNDecoded;
  }

  /**
   * @return the fQANSDecoded
   */
  public String getFQANSDecoded() {

    return FQANSDecoded;
  }

  /**
   * @return the filePathDecoded
   */
  public String getFilePathDecoded() {

    return filePathDecoded;
  }

  /**
   * @return the hasVomsExtension
   */
  public boolean hasVomsExtension() {

    return hasVomsExtension;
  }
}
