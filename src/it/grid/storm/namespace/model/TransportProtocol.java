/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    public boolean equals(Object other) {
      boolean result = false;
      if (other instanceof TransportProtocol) {
        TransportProtocol otherTP = (TransportProtocol)other;
        if (otherTP.getProtocol().equals(this.getProtocol())) { //Protocol is equal
          //Check if the Authority is equal.
          if (otherTP.getAuthority().equals(this.getAuthority())) {
            result = true;
          }
        }
      }
      return result;
    }

}
