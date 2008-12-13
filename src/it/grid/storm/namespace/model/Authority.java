package it.grid.storm.namespace.model;

public class Authority {
    private String hostname;
    private int port = -1;

    public final static Authority EMPTY = new Authority("");

    /**
     * Complete constructor
     *
     * @param serviceHostname String
     * @param servicePort int
     */
    public Authority(String serviceHostname, int servicePort) {
        this.hostname = serviceHostname;
        this.port = servicePort;
    }

    /**
     * Cnstructor with default port
     *
     * @param serviceHostname String
     */
    public Authority(String serviceHostname) {
        this.hostname = serviceHostname;
        this.port = -1;
    }

    public String getServiceHostname() {
        return this.hostname;
    }

    public void setServiceHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getServicePort() {
        return this.port;
    }

    public void setServicePort(int port) {
        this.port = port;
    }

    private String getHostnameAndPort() {
        StringBuffer result = new StringBuffer();
        if (hostname != null) {
            result.append(hostname);
            if (port > 0) {
                result.append(":");
                result.append(port);
            }
        }
        return result.toString();
    }

    public String toString() {
        return getHostnameAndPort();
    }

    public boolean equals(Object other) {
      boolean result = false;
      if (other instanceof Authority) {
        Authority otherA = (Authority)other;
        if (otherA.getServiceHostname().equals(this.getServiceHostname())) { //Hostname is equal
          //Check if the Port is equal.
          if (otherA.getServicePort()==this.getServicePort()) {
            result = true;
          }
        }
      }
      return result;
    }

}
