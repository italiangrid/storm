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

package it.grid.storm.namespace.naming;

import it.grid.storm.namespace.model.*;

public abstract class SRMURL {

    protected TransportProtocol transfProtocol;
    protected SRMURLType surlType = null;
    /**
     * If this is a normal form SRMURL path contains the file path,
     * it instead is a query form SRMURL path contains the service endpoint 
     */
    protected String path;
    protected String queryString = null;

    protected int local = -1; //undef
    protected boolean localSURL = false;

	public SRMURL(Protocol protocol, String hostname, int port, String servicePath, String queryString) {

		Authority authority = new Authority(hostname, port);
		this.transfProtocol = new TransportProtocol(protocol, authority);
		// The path and the query string must to be expressed in absolute form!
		this.path = makeInAbsoluteForm(servicePath);
		if(queryString != null)
		{
			this.queryString = makeInAbsoluteForm(queryString);
		}

	}

    public SRMURL(Protocol protocol, String hostname, int port, String stfn) {

		Authority autority = new Authority(hostname, port);
		this.transfProtocol = new TransportProtocol(protocol, autority);

		// The path and the query string must to be expressed in absolute form!
		this.path = makeInAbsoluteForm(stfn);
	}

    /**
     * Provides from the received path string a string that starts with NamingConst.ROOT_PATH ("/")
     * @param path
     * @return
     */
    private String makeInAbsoluteForm(String path) {
        StringBuffer absolutePath = new StringBuffer();
   
		if((path == null) || (path.length() == 0))
		{
			absolutePath.append(NamingConst.ROOT_PATH);
		}
		else
		{
			if(path.charAt(0) != NamingConst.SEPARATOR_CHAR)
			{
				absolutePath.insert(0, NamingConst.ROOT_PATH);
			}
			absolutePath.append(path);
		}
        return absolutePath.toString();
    }

    /**
     * @param hostname
     */
    public void setServiceHostName(String hostname) {
        this.transfProtocol.setAuthority(new Authority(hostname));
    }

    public String getServiceHostname() {
        return this.transfProtocol.getAuthority().getServiceHostname();
    }
    
    public void setServiceHostPort(int port) {
        this.transfProtocol.getAuthority().setServicePort(port);
    }

    public int getServiceHostPort() {
        return this.transfProtocol.getAuthority().getServicePort();
    }
    
    public String getPath() {
        return path;
    }
    
    public String getQueryString() {
        return queryString;
    }
    
    public String getTransportPrefix() {
        return transfProtocol.toString();
    }

    public String getSURLType() {
        if (surlType == null) {
            surlType = computeType();
        }
        return surlType.toString();
    }
    
    /**
     * Returns true if the hostname of this srmurl is the one specified in configuration file field "storm.service.FE-public.hostname"
     * @return
     */
    public boolean isLocal() {
        if (local == -1) {
            localSURL = getServiceHostname().equals(NamingConst.getServiceDefaultHost());
            local = 1;
        }
        return localSURL;
    }

    public boolean isQueriedFormSURL() {
        if (surlType == null) {
            surlType = computeType();
        }
        return (surlType.equals(SRMURLType.QUERIED));
    }

	public boolean isNormalFormSURL() {

		return (!(isQueriedFormSURL()));
	}

    private SRMURLType computeType() {
        if (this.getQueryString() != null) {
            return SRMURLType.QUERIED;
        }
        else {
            return SRMURLType.SIMPLE;
        }
    }

    /**
     * If this is a queri form SRMURL returns the service endpoint, an empty string otherwise
     * @return
     */
	public String getServiceEndPoint() {

		if(isQueriedFormSURL())
		{
			return getPath();
		}
		else
		{
			return "";
		}
	}

	public String getStFN() {

		if(isQueriedFormSURL())
		{
			return this.getQueryString();
		}
		else
		{ // In this case the path represents the StFN
			return this.getPath();
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

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if(obj == null)
			{
				return false;
			}
			if(!(obj instanceof SRMURLType))
			{
				return false;
			}
			SRMURLType other = (SRMURLType) obj;
			if(type == null)
			{
				if(other.type != null)
				{
					return false;
				}
			}
			else
				if(!type.equals(other.type))
				{
					return false;
				}
			return true;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 17;
			result = prime * result + type.hashCode();
			return result;
		}
		
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 17;
		result = prime * result + local;
		result = prime * result + (localSURL ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((queryString == null) ? 0 : queryString.hashCode());
		result = prime * result + ((surlType == null) ? 0 : surlType.hashCode());
		result = prime * result + ((transfProtocol == null) ? 0 : transfProtocol.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof SRMURL))
			return false;
		SRMURL other = (SRMURL) obj;
		if(local != other.local)
			return false;
		if(localSURL != other.localSURL)
			return false;
		if(path == null)
		{
			if(other.path != null)
				return false;
		}
		else
			if(!path.equals(other.path))
				return false;
		if(queryString == null)
		{
			if(other.queryString != null)
				return false;
		}
		else
			if(!queryString.equals(other.queryString))
				return false;
		if(surlType == null)
		{
			if(other.surlType != null)
				return false;
		}
		else
			if(!surlType.equals(other.surlType))
				return false;
		if(transfProtocol == null)
		{
			if(other.transfProtocol != null)
				return false;
		}
		else
			if(!transfProtocol.equals(other.transfProtocol))
				return false;
		return true;
	}

	

}
