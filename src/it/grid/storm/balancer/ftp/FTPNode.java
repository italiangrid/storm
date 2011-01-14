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

package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.AbstractNode;
import it.grid.storm.namespace.model.Protocol;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Dec 15, 2008
 *
 */

public class FTPNode extends AbstractNode {

    private String hostname;
    private int port;




    public FTPNode() {

    }

    
    
    public FTPNode(String hostname, int port, int weight) {
        this.hostname = hostname;
        this.port = port;
        this.weight = weight;
    }


    /**
     * @return String hostname
     */
    public String getHostName() {
        return hostname;
    }

    public int getWeight() {
        return weight;
    }


    public void setHostname(String hostname) {
        this.hostname = hostname;
    }



    public int getPort() {
        return port;
    }



    public void setPort(int port) {
        this.port = port;
    }




    public void setWeight(int w) {
        //Weight between 1 and 100
        weight = w%100;
    }


  //TODO
    //Gestire la validita' dell'informazione
    //facendo una cache
    //Vedere se si puo' generalizzare il concetto di metrica
    //con Map<String, Obj metric>

    public int getFTPProc() {
        //cache here
        int nprocs =  0 ;
        nprocs = new FMCTest().getN(this.getHostName());
        nprocs = (nprocs<0) ? -1 : nprocs;
        return nprocs;
    }

    public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(Protocol.GSIFTP.getProtocolPrefix());
      sb.append(this.hostname);
      sb.append(":"+this.port);
      return sb.toString();
    }

}
