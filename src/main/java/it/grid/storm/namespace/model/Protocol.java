/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class Protocol {

  private int protocolIndex = -1;

  private String protocolServiceName;
  private String protocolName;
  private String schema;
  private int defaultPort = -1;

  public static final Protocol FILE = new Protocol(1, "FILE", "file", -1);
  public static final Protocol GSIFTP = new Protocol(2, "GSIFTP", "gsiftp", 2811);
  public static final Protocol RFIO = new Protocol(3, "RFIO", "rfio", 5001);
  public static final Protocol SRM = new Protocol(4, "SRM", "srm", 8444);
  public static final Protocol ROOT = new Protocol(5, "ROOT", "root", 1094);

  public static final Protocol HTTP = new Protocol(6, "HTTP", "http", 8080);
  public static final Protocol HTTPS = new Protocol(7, "HTTPS", "https", 443);

  public static final Protocol XROOT = new Protocol(8, "XROOT", "xroot", 1094);

  public static final Protocol DAV = new Protocol(9, "DAV", "dav", 8080);
  public static final Protocol DAVS = new Protocol(10, "DAVS", "davs", 443);

  public static final Protocol EMPTY = new Protocol(0, "EMPTY", "", -1);
  public static final Protocol UNKNOWN = new Protocol(-1, "UNKNOWN", "", -1);

  private Protocol(int protocolIndex, String protocolName, String protocolScheme, int defaultPort) {

    this.protocolIndex = protocolIndex;
    this.protocolName = protocolName;
    this.schema = protocolScheme;
    this.defaultPort = defaultPort;
  }

  public int getProtocolIndex() {

    return protocolIndex;
  }

  public String getProtocolName() {

    return protocolName;
  }

  public String getSchema() {

    return schema;
  }

  public String getProtocolPrefix() {

    return this.schema + "://";
  }

  public void setProtocolServiceName(String serviceName) {

    this.protocolServiceName = serviceName;
  }

  public String getProtocolServiceName() {

    return this.protocolServiceName;
  }

  public int getDefaultPort() {

    return this.defaultPort;
  }

  public static Protocol getProtocol(String scheme) {

    if (scheme.toLowerCase().replaceAll(" ", "").equals(FILE.getSchema().toLowerCase())) {
      return FILE;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(GSIFTP.getSchema().toLowerCase())) {
      return GSIFTP;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(RFIO.getSchema().toLowerCase())) {
      return RFIO;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(ROOT.getSchema().toLowerCase())) {
      return ROOT;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(SRM.getSchema().toLowerCase())) {
      return SRM;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(HTTP.getSchema().toLowerCase())) {
      return HTTP;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(HTTPS.getSchema().toLowerCase())) {
      return HTTPS;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(DAV.getSchema().toLowerCase())) {
      return DAV;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(DAVS.getSchema().toLowerCase())) {
      return DAVS;
    }

    if (scheme.toLowerCase().replaceAll(" ", "").equals(EMPTY.getSchema().toLowerCase())) {
      return EMPTY;
    }
    if (scheme.toLowerCase().replaceAll(" ", "").equals(XROOT.getSchema().toLowerCase())) {
      return XROOT;
    }
    return UNKNOWN;
  }

  public int hashCode() {

    return protocolIndex;
  }

  public boolean equals(Object o) {

    boolean result = false;
    if (o instanceof Protocol) {
      Protocol other = (Protocol) o;
      if (other.getProtocolIndex() == this.getProtocolIndex()) {
        result = true;
      }
    }
    return result;
  }

  public String toString() {

    StringBuilder buf = new StringBuilder();
    buf.append(this.protocolName + " = " + this.getSchema() + "://");
    return buf.toString();
  }
}
