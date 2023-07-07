/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class Authority {

  public static final Authority EMPTY = new Authority();

  private String hostname;
  private int port;

  private Authority() {
    hostname = null;
    port = -1;
  }

  public Authority(String hostname, int port) {
    this(hostname);
    Preconditions.checkArgument(port > 0, "Authority builder: invalid port value");
    this.port = port;
  }

  public Authority(String hostname) {
    Preconditions.checkNotNull(hostname, "Authority builder: invalid null hostname");
    Preconditions.checkArgument(!hostname.isEmpty(), "Authority builder: invalid empty hostname");
    setHostname(hostname);
    setPort(-1);
  }

  public String getHostname() {

    return this.hostname;
  }

  public void setHostname(String hostname) {

    this.hostname = hostname;
  }

  public int getPort() {

    return this.port;
  }

  public void setPort(int port) {

    this.port = port;
  }

  public String toString() {

    StringBuilder result = new StringBuilder();
    if (hostname != null) {
      result.append(hostname);
      if (port > 0) {
        result.append(":");
        result.append(port);
      }
    }
    return result.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostname, port);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Authority other = (Authority) obj;
    return Objects.equals(hostname, other.hostname) && port == other.port;
  }

  
}
