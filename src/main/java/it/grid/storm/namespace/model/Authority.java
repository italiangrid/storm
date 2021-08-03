/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
   * Constructor with default port
   * 
   * @param serviceHostname String
   */
  public Authority(String serviceHostname) {

    this.hostname = serviceHostname;
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

  public String toString() {

    return getHostnameAndPort();
  }

  public boolean equals(Object other) {

    boolean result = false;
    if (other instanceof Authority) {
      Authority otherA = (Authority) other;
      if (otherA.getServiceHostname().equals(this.getServiceHostname())) {
        // Check if the Port is equal.
        if (otherA.getServicePort() == this.getServicePort()) {
          result = true;
        }
      }
    }
    return result;
  }

  public static Authority fromString(String endpoint) {
    String host = endpoint.split(":")[0];
    int port = Integer.valueOf(endpoint.split(":")[1]);
    return new Authority(host, port);
  }

  @Override
  public int hashCode() {

    int result = 17;
    result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
    result = 31 * result + port;
    return result;
  }

}
