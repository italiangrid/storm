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

package it.grid.storm.catalogs;

import java.sql.Timestamp;

/**
 * Class that represents data of an asynchrnous Request, regardless of whether
 * it is a Put, Get or Copy, in the Persistence Layer: this is all raw data
 * referring to the request proper, that is, String and primitive types.
 *
 * @author  EGRID ICTP
 * @version 2.0
 * @date    June 2005
 */
public class RequestSummaryDataTO {

    private long id = -1; //id of request in persistence
    private String requestType = "";    //request type
    private String requestToken = "";   //request token
    private String clientDN = "";       //DN that issued request
    private String vomsAttributes = ""; //String containing all VOMS attributes
    private Timestamp timestamp = null;
    
    private boolean empty=true;

    public boolean isEmpty() {
        return empty;
    }

    public long primaryKey() {
        return id;
    }

    public void setPrimaryKey(long l) {
        empty = false;
        id = l;
    }

    public String requestType() {
        return requestType;
    }

    public void setRequestType(String s) {
        empty=false;
        requestType = s;
    }

    public String requestToken() {
        return requestToken;
    }

    public void setRequestToken(String s) {
        empty=false;
        requestToken = s;
    }

    public String clientDN() {
        return clientDN;
    }

    public void setClientDN(String s) {
        empty = false;
        clientDN = s;
    }

    public String vomsAttributes() {
        return vomsAttributes;
    }

    public void setVomsAttributes(String s) {
        empty = false;
        vomsAttributes = s;
    }

    public Timestamp timestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp)
    {
        empty = false;
        this.timestamp = timestamp;
    }


    public String toString() {
        return id + " " + requestType + " " + requestToken + " " + " " + clientDN + " " + vomsAttributes;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientDN == null) ? 0 : clientDN.hashCode());
        result = prime * result + (empty ? 1231 : 1237);
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((requestToken == null) ? 0 : requestToken.hashCode());
        result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((vomsAttributes == null) ? 0 : vomsAttributes.hashCode());
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
        RequestSummaryDataTO other = (RequestSummaryDataTO) obj;
        if (clientDN == null)
        {
            if (other.clientDN != null)
            {
                return false;
            }
        }
        else
            if (!clientDN.equals(other.clientDN))
            {
                return false;
            }
        if (empty != other.empty)
        {
            return false;
        }
        if (id != other.id)
        {
            return false;
        }
        if (requestToken == null)
        {
            if (other.requestToken != null)
            {
                return false;
            }
        }
        else
            if (!requestToken.equals(other.requestToken))
            {
                return false;
            }
        if (requestType == null)
        {
            if (other.requestType != null)
            {
                return false;
            }
        }
        else
            if (!requestType.equals(other.requestType))
            {
                return false;
            }
        if (timestamp == null)
        {
            if (other.timestamp != null)
            {
                return false;
            }
        }
        else
            if (!timestamp.equals(other.timestamp))
            {
                return false;
            }
        if (vomsAttributes == null)
        {
            if (other.vomsAttributes != null)
            {
                return false;
            }
        }
        else
            if (!vomsAttributes.equals(other.vomsAttributes))
            {
                return false;
            }
        return true;
    }

     
}
