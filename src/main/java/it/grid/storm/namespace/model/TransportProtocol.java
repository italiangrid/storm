/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import java.util.Objects;

public class TransportProtocol {

  private Protocol protocol;
  private Authority authority;

  public TransportProtocol(Protocol protocol, Authority authority) {
    setProtocol(protocol);
    setAuthority(authority);
  }

  public TransportProtocol(Protocol protocol) {
    this(protocol, null);
  }

  public Protocol getProtocol() {

    return protocol;
  }

  public int getProtocolId() {

    return protocol.ordinal();
  }

   public Authority getAuthority() {

     return authority;
   }

//  public void setLocalAuthority() {
//
//    if (!this.protocol.equals(Protocol.FILE)) {
//      this.service = new Authority(NamingConst.getServiceDefaultHost());
//    }
//  }

  public void setAuthority(Authority authority) {

    this.authority = authority;
  }

  public void setProtocol(Protocol protocol) {

    this.protocol = protocol;
  }

  private String getURIRoot() {

    StringBuilder sb = new StringBuilder();
    sb.append("[id:" + protocol.ordinal() + "] ");
    sb.append(protocol.getSchema());
    sb.append("://");
    if (authority != null) {
      sb.append(authority.toString());
    }
    return sb.toString();
  }

  public String toString() {

    return getURIRoot();
  }

  @Override
  public int hashCode() {
    return Objects.hash(authority, protocol);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TransportProtocol other = (TransportProtocol) obj;
    return Objects.equals(authority, other.authority) && protocol == other.protocol;
  }

}
