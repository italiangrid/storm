package it.grid.storm.namespace.model;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Protocol {

    private int protocolIndex = -1;
    private String protocolServiceName;
    private String protocolName;
    private String scheme;
    private int defaultPort = -1;

    public final static Protocol FILE = new Protocol(1, "FILE", "file", -1);
    public final static Protocol GSIFTP = new Protocol(2, "GSIFTP", "gsiftp", 2811);
    public final static Protocol RFIO = new Protocol(3, "RFIO", "rfio", 5001);
    public final static Protocol SRM = new Protocol(4, "SRM", "srm", 8444);
    public final static Protocol ROOT = new Protocol(5, "ROOT", "root", 1094);
    public final static Protocol EMPTY = new Protocol(0, "EMPTY", "", -1);
    public final static Protocol UNKNOWN = new Protocol(-1, "UNKNOWN", "", -1);

    /**
     * Constructor
     *
     * @param protocolName String
     * @param protocolSchema String
     */
    public Protocol(int protocolIndex, String protocolName, String protocolScheme, int defaultPort) {
        this.protocolIndex = protocolIndex;
        this.protocolName = protocolName;
        this.scheme = protocolScheme;
        this.defaultPort = defaultPort;
    }

    //Return internal index for equals method and to use in a switch statement
    public int getProtocolIndex(){
      return protocolIndex;
    }

    //Only get method for Name
    public String getProtocolName() {
        return protocolName;
    }

    //Only get method for Schema
    public String getSchema() {
        return scheme;
    }

    //Only get method for Schema
    public String getProtocolPrefix() {
      return this.scheme + "://";
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
        if (scheme.toLowerCase().replaceAll(" ", "").equals(EMPTY.getSchema().toLowerCase())) {
            return EMPTY;
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
        if (other.getProtocolIndex()==this.getProtocolIndex()) {
          result = true;
        }
      }
      return result;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.protocolName + " = " + this.getSchema() + "://");
        return buf.toString();
    }

}
