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

package it.grid.storm.balancer;

public abstract class AbstractNode implements Node {
    
    protected int nodeId = 0;
	protected final Integer weight;
    
	protected final String hostname;
    protected final int port;
	
    
    protected AbstractNode(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.weight = null;
    }
    
    protected AbstractNode(String hostname, int port, int weight)
    {
        this.hostname = hostname;
        this.port = port;
        // Weight between 1 and 100
        this.weight = weight%100;
    }

    public int getWeight() throws IllegalStateException {
        if(weight != null)
        {
            return weight.intValue();
        }
        throw new IllegalStateException("No weight available for this node: " + toString());
    }
    

    public void setId(int id) {
    	nodeId=id;
    }
    
    public int getId() {
    	return nodeId;
    }
    
    public String getHostName() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AbstractNode [nodeId=");
        builder.append(nodeId);
        builder.append(", weight=");
        builder.append(weight);
        builder.append(", hostname=");
        builder.append(hostname);
        builder.append(", port=");
        builder.append(port);
        builder.append("]");
        return builder.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + nodeId;
        result = prime * result + port;
        result = prime * result + weight;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AbstractNode other = (AbstractNode) obj;
        if (hostname == null)
        {
            if (other.hostname != null)
            {
                return false;
            }
        }
        else
            if (!hostname.equals(other.hostname))
            {
                return false;
            }
        if (nodeId != other.nodeId)
        {
            return false;
        }
        if (port != other.port)
        {
            return false;
        }
        if (weight != other.weight)
        {
            return false;
        }
        return true;
    }
    
    
}
