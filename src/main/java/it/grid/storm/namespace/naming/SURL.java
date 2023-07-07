/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.naming;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Protocol;

public class SURL extends SRMURL {

  private static Logger log = NamespaceDirector.getLogger();
  private static List<String> schemes = Lists.newArrayList();

  static {
    schemes.add("srm");
  }

  public final boolean directory;

  private SURL(final String hostName, final int port, final String serviceEndpoint,
      final String queryString) {

    super(Protocol.SRM, hostName, port, serviceEndpoint, queryString);
    directory = checkDirectory(queryString);
  }

  private SURL(final String hostName, final int port, final String stfn) {

    super(Protocol.SRM, hostName, port, stfn);
    directory = checkDirectory(stfn);
  }

  public SURL(final String stfn) {

    super(Protocol.SRM, NamingConst.getServiceDefaultHost(), NamingConst.getServicePort(),
        stfn);
    directory = checkDirectory(stfn);
  }

  /**
   * Build SURL from the string format. Many control will be executed in the string format No other
   * way to create a SURL, if u got a SURL for sure it's a valid URI normalized
   * 
   * @param surlString String
   * @return SURL
   */
  public static SURL makeSURLfromString(String surlString) throws NamespaceException {

    SURL result = null;

    // checks if is a valid uri and normalize
    URI uri = null;
    try {
      uri = URI.create(surlString);
    } catch (IllegalArgumentException uriEx) {
      throw new NamespaceException("SURL_String :'" + surlString
          + "' is INVALID. Reason: URI Except: " + uriEx.getMessage());
    } catch (NullPointerException npe) {
      throw new NamespaceException("SURL_String :'" + surlString
          + "' is INVALID. Reason: URI Except (null SURL): " + npe.getMessage());
    }

    // Check the scheme
    // URI should be not null
    String scheme = uri.getScheme();
    if (!(schemes.contains(scheme))) {
      throw new NamespaceException(
          "SURL_String :'" + surlString + "' is INVALID. Reason: unknown scheme '" + scheme + "'");
    }

    // Check the query
    String host = uri.getHost();
    if (host == null) {
      throw new NamespaceException(
          "SURL_String :'" + surlString + "' is INVALID. Reason: malformed host!");
    }
    int port = uri.getPort();
    String query = uri.getQuery();
    if (query == null || query.trim().equals("")) {
      String stfn = uri.getPath();
      result = new SURL(host, port, stfn);
    } else {
      // The SURL_Str is in a Query FORM.
      log.debug(" !! SURL ('" + surlString + "') in a query form (query:'" + query + "') !!");
      String service = uri.getPath();
      log.debug(" Service endpoint : " + service);
      if (checkQuery(query)) {
        log.debug(" Query is in a valid form.");
        // Extract the StFN from query:
        String stfn = extractStFNfromQuery(query);
        result = new SURL(host, port, service, stfn);
      } else {
        log.warn("SURL_String :'" + surlString + "' is not VALID! (query is in invalid form)");
        throw new NamespaceException(
            "SURL_String :'" + surlString + "' is not VALID within the Query!");
      }
    }
    return result;
  }

  public String getQueryFormAsString() {
    if (this.isNormalFormSURL()) {
      String uriString = transfProtocol.getProtocol().getSchema() + "://"
          + this.transfProtocol.getAuthority().getHostname();
      if (this.transfProtocol.getAuthority().getPort() >= 0) {
        uriString += ":" + this.transfProtocol.getAuthority().getPort();
      }
      uriString += "/srm/managerv2?SFN=" + this.path;
      return uriString;
    }
    return this.getSURLAsURIString();
  }

  public String getNormalFormAsString() {
    if (this.isQueriedFormSURL()) {
      String uriString = transfProtocol.getProtocol().getSchema() + "://"
          + this.transfProtocol.getAuthority().getHostname();
      if (this.transfProtocol.getAuthority().getPort() >= 0) {
        uriString += ":" + this.transfProtocol.getAuthority().getPort();
      }
      uriString += this.getStFN();
      return uriString;
    }
    return this.getSURLAsURIString();
  }

  public boolean isDirectory() {

    return directory;
  }

  private boolean checkDirectory(String path) {

    if (path != null && path.endsWith(NamingConst.SEPARATOR)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 
   * Checks if the query string begins with the correct prefix ("SFN=")
   * 
   * @param query
   * @return
   */
  private static boolean checkQuery(String query) {

    if (query == null) {
      log.error("Received a null query to check!");
      return false;
    }
    return query.startsWith(NamingConst.getServiceSFNQueryPrefix() + "=");
  }

  private static String extractStFNfromQuery(String query) {

    String stfn = "";
    if (query == null) {
      return stfn;
    } else {
      int len = query.length();
      if (len < 4) {
        return stfn;
      } else {
        stfn = query.substring(4);
      }
    }
    return stfn;
  }

  /**
   * get the path and query string e.g. /path/service?SFN=pippo.txt if query form e.g
   * /path/pippo.txt if simple form
   * 
   * @return the path and its query string
   */
  public String getPathQuery() {

    StringBuilder sb = new StringBuilder(250);
    sb.append(getPath());
    if (this.isQueriedFormSURL()) {
      sb.append("?");
      sb.append(NamingConst.getServiceSFNQueryPrefix());
      sb.append("=");
      sb.append(getQueryString());
    }
    return sb.toString();
  }

  public String getSURLAsURIString() {

    String uriString = transfProtocol.getProtocol().getSchema() + "://"
        + this.transfProtocol.getAuthority().getHostname();
    if (this.transfProtocol.getAuthority().getPort() >= 0) {
      uriString += ":" + this.transfProtocol.getAuthority().getPort();
    }
    if (this.isNormalFormSURL()) {
      uriString += this.path;
    } else {
      uriString += this.getPathQuery();
    }
    return uriString;
  }

  @Override
  public String toString() {

    StringBuilder buffer = new StringBuilder();
    buffer.append(this.transfProtocol.toString());
    buffer.append(this.getPathQuery());
    return buffer.toString();
  }

  @Override
  public int hashCode() {

    int result = super.hashCode();
    result += 37 * schemes.hashCode() + 63 * (directory ? 1 : 0);
    return result;
  }

  /* 
   * 
   */
  @Override
  public boolean equals(Object obj) {

    if (!super.equals(obj))
      return false;
    if (!(obj instanceof SURL))
      return false;
    SURL other = (SURL) obj;
    if (directory != other.directory)
      return false;
    return true;
  }
}
