package it.grid.storm.namespace.model;

import it.grid.storm.namespace.naming.*;

public class TransportProtocol {

    private int protocolID = -1;
    private Protocol protocol = null;
    private Authority service = null;

    public TransportProtocol(Protocol protocol, Authority service) {
        this.protocol = protocol;
        this.service = service;
    }

    public TransportProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    //Used in Protocol Pool definition
    public void setProtocolID(int id) {
      this.protocolID = id;
    }

    //Used in Protocol Pool definition
    public int getProtocolID() {
      return this.protocolID;
    }


    public Authority getAuthority() {
        if (this.protocol.equals(Protocol.FILE)) {
            return Authority.EMPTY;
        }
        else {
            return this.service;
        }
    }

    public void setLocalAuthority() {
        if (!this.protocol.equals(Protocol.FILE)) {
            this.service = new Authority(NamingConst.getServiceDefaultHost());
        }
    }

    public void setAuthority(Authority service) {
        this.service = service;
    }

    private String getURIRoot() {
      StringBuffer sb = new StringBuffer();
      if (protocolID != -1)
        sb.append("[id:" + this.protocolID + "] ");
      sb.append(protocol.getSchema());
      sb.append("://");
      if (service != null) {
        sb.append(service);
      }
      return sb.toString();
    }

    public String toString() {
        return getURIRoot();
    }

}
