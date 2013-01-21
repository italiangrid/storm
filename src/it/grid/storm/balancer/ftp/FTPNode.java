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

import java.net.InetSocketAddress;
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

    private static final String prefix = Protocol.GSIFTP.getProtocolPrefix(); 
    
    public FTPNode(String hostname, int port)
    {
        super(hostname, port);
    }
    
    public FTPNode(String hostname, int port, int weight)
    {
        super(hostname, port, weight);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(hostname);
        sb.append(":" + port);
        return sb.toString();
    }

    @Override
    public boolean checkServer() throws Exception
    {
        return CheckControlChannel.checkGFtpServer(new InetSocketAddress(hostname, port));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        return result;
    }

}
