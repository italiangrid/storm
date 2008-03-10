package it.grid.storm.namespace.model;

import it.grid.storm.namespace.naming.*;

public class TransportPrefix {

    private Protocol protocol = null;
    private Authority service = null;

    public TransportPrefix(Protocol protocol, Authority service) {
        this.protocol = protocol;
        this.service = service;
    }

    public TransportPrefix(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return this.protocol;
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
