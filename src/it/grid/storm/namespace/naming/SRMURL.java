package it.grid.storm.namespace.naming;

import it.grid.storm.namespace.model.*;

public abstract class SRMURL {

    protected TransportPrefix transfProtocol;
    protected SRMURLType surlType = null;
    protected String queryString;
    protected String path;

    protected int local = -1; //undef
    protected boolean localSURL = false;

    protected String hostName;

    public SRMURL(final TransportPrefix protocol,
                  final String path,
                  final String queryString) {
        if (protocol.getAuthority() == null) {
            protocol.setLocalAuthority();
        }
        this.hostName = protocol.getAuthority().getServiceHostname();
        //The path and the query string must to be expressed in absolute form!
        if (path != null) {
            this.path = makeInAbsoluteForm(path);
        }
        else {
            this.path = "";
        }
        if (queryString != null) {
            this.queryString = makeInAbsoluteForm(queryString);
        }

    }

    public SRMURL(Protocol protocol, String hostname, int port, String servicePath, String queryString) {
        this.hostName = hostname;
        Authority service = new Authority(hostname, port);
        this.transfProtocol = new TransportPrefix(protocol, service);
        //The path and the query string must to be expressed in absolute form!
        if (servicePath != null) {
            this.path = makeInAbsoluteForm(servicePath);
        }
        else {
            this.path = "";
        }
        if (queryString != null) {
            this.queryString = makeInAbsoluteForm(queryString);
        }

    }

    public SRMURL(Protocol protocol, String hostname, int port, String stfn) {
        this.hostName = hostname;
        Authority service = new Authority(hostname, port);
        this.transfProtocol = new TransportPrefix(protocol, service);
        //The path and the query string must to be expressed in absolute form!
        if (stfn != null) {
            this.path = makeInAbsoluteForm(stfn);
        }
        else {
            this.path = "";
        }
        this.queryString = null;
    }

    private String makeInAbsoluteForm(String path) {
        StringBuffer absolutePath = new StringBuffer();

        absolutePath.append(path);
        if (absolutePath.charAt(0) != NamingConst.SEPARATOR_CHAR) {
            absolutePath.insert(0, NamingConst.ROOT_PATH);
        }
        return absolutePath.toString();
    }

    public void setServiceHostName(String hostname) {
        this.transfProtocol.setAuthority(new Authority(hostname));
    }

    public void setServiceHostPort(int port) {
        this.transfProtocol.getAuthority().setServicePort(port);
    }

    public int getServiceHostPort() {
        return this.transfProtocol.getAuthority().getServicePort();
    }

    public String getSURLType() {
        if (surlType == null) {
            surlType = computeType();
        }
        return surlType.toString();
    }

    public boolean isLocal() {
        if (local == -1) {
            localSURL = getServiceHost().equals(NamingConst.getServiceDefaultHost());
            local = 1;
        }
        return localSURL;
    }

    public String getPath() {
        return path;
    }

    public String getServiceHost() {
        return hostName;
    }

    public String getTransportPrefix() {
        return transfProtocol.toString();
    }

    public boolean isQueriedFormSURL() {
        if (surlType == null) {
            surlType = computeType();
        }
        return (surlType.equals(SRMURLType.QUERIED));
    }

    public boolean isNormalFormSURL() {
        return (! (isQueriedFormSURL()));
    }

    public String getQueryString() {
        return queryString;
    }

    private SRMURLType computeType() {
        if (this.getQueryString() != null) {
            return SRMURLType.QUERIED;
        }
        else {
            return SRMURLType.SIMPLE;
        }
    }

    public String getServiceEndPoint() {
        if (isQueriedFormSURL()) {
            return getPath();
        }
        else {
            return "";
        }
    }

    public String getStFN() {
        StringBuffer stFN = new StringBuffer();
        if (isQueriedFormSURL()) {
            stFN.append(getQueryString());
            if (stFN.charAt(0) != NamingConst.SEPARATOR_CHAR) {
                stFN.insert(0, NamingConst.ROOT_PATH);
            }
            return stFN.toString();
        }
        else { //In this case the path represents the StFN
            return getPath();
        }
    }

    /**
     *
     * <p>Title: </p>
     *
     * <p>Description: </p>
     *
     * <p>Copyright: Copyright (c) 2005</p>
     *
     * <p>Company: </p>
     *
     * @author not attributable
     * @version 1.0
     */
    protected static class SRMURLType {
        private String type;
        public final static SRMURLType QUERIED = new SRMURLType("query_form");
        public final static SRMURLType SIMPLE = new SRMURLType("simple_form");

        private SRMURLType(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }

    }

}
