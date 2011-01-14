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

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Protocol;

import java.net.URI;
import java.util.ArrayList;

import org.slf4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class SURL extends SRMURL {

    private static Logger log = NamespaceDirector.getLogger();
    private static ArrayList<String> schemes = new ArrayList<String>();

    static {
        schemes.add("srm");
    }
    
    public final boolean directory;

    private SURL(final String hostName,
            final int port,
            final String serviceEndpoint,
            final String queryString) {
        super(Protocol.SRM, hostName, port, serviceEndpoint, queryString);
        directory = checkDirectory(queryString);
    }

    private SURL(final String hostName,
            final int port,
            final String stfn) {
        super(Protocol.SRM, hostName, port, stfn);
        directory = checkDirectory(stfn);
    }

    //TODO MICHELE USER_SURL debug
    public SURL(final String stfn) {
        super(Protocol.SRM, NamingConst.getServiceDefaultHost(), NamingConst.getServicePort(), stfn);
        directory = checkDirectory(stfn);
    }

    /**
     * Build SURL from the string format.
     * Many control will be executed in the string format
     * No other way to create a SURL, if u got a SURL for sure it's a valid URI normalized 
     *
     * @param surlString String
     * @return SURL
     */
    public static SURL makeSURLfromString(String surlString) throws NamespaceException {
        SURL result = null;
        
        //checks if is a valid uri and normalize 
		URI uri = null;
		try
		{
			uri = URI.create(surlString);
			uri = uri.normalize();
		} catch(IllegalArgumentException uriEx)
		{
			throw new NamespaceException("SURL_String :'" + surlString + "' is INVALID. Reason: URI Except: " + uriEx.getMessage());
		} catch(NullPointerException npe)
		{
			throw new NamespaceException("SURL_String :'" + surlString + "' is INVALID. Reason: URI Except (null SURL): "
				+ npe.getMessage());
		}

		// Check the scheme
		// uri should be not null
		String scheme = uri.getScheme();
		if(!(schemes.contains(scheme)))
		{
			throw new NamespaceException("SURL_String :'" + surlString + "' is INVALID. Reason: unknown scheme '" + scheme + "'");
		}

		// Check the query
		String host = uri.getHost();
		if(host == null)
		{
			throw new NamespaceException("SURL_String :'" + surlString + "' is INVALID. Reason: malformed host!");
		}
		int port = uri.getPort();
		String query = uri.getQuery();
		if(query == null || query.trim().equals(""))
		{
			String stfn = uri.getPath();
			result = new SURL(host, port, stfn);
		}
		else
		{
			// The SURL_Str is in a Query FORM.
			log.debug(" !! SURL ('" + surlString + "') in a query form (query:'" + query + "') !!");
			String service = uri.getPath();
			log.debug(" Service endpoint : " + service);
			if(checkQuery(query))
			{
				log.debug(" Query is in a valid form.");
				// Extract the StFN from query:
				String stfn = extractStFNfromQuery(query);
				result = new SURL(host, port, service, stfn);
			}
			else
			{
				log.warn("SURL_String :'" + surlString + "' is not VALID! (query is in invalid form)");
				throw new NamespaceException("SURL_String :'" + surlString + "' is not VALID within the Query!");
			}
		}
		return result;
    }

    public boolean isDirectory() {
        return directory;
    }

    private boolean checkDirectory(String path) {

		if(path != null && path.endsWith(NamingConst.SEPARATOR))
		{
			return true;
		}
		else
		{
			return false;
		}
    }

    /**
     * 
     * Checks if the quesry string begins with the correct prefix ("SFN=")
     * @param query
     * @return
     */
    private static boolean checkQuery(String query) {
    	if(query == null)
    	{
    		log.error("Received a null query to check!");
    		return false;
    	}
        return query.startsWith(NamingConst.getServiceSFNQueryPrefix() + "=");
    }

	private static String extractStFNfromQuery(String query) {

		String stfn = "";
		if(query == null)
		{
			return stfn;
		}
		else
		{
			int len = query.length();
			if(len < 4)
			{
				return stfn;
			}
			else
			{
				stfn = query.substring(4);
			}
		}
		return stfn;
	}

    /**
     * get the path and query string
     * e.g. /path/service?SFN=pippo.txt  if query form
     * e.g  /path/pippo.txt              if simple form
     * @return the path and its query string
     */
    public String getPathQuery() {
        StringBuffer sb = new StringBuffer(250);
        sb.append(getPath());
        if (this.isQueriedFormSURL()) {
            sb.append("?");
            sb.append(NamingConst.getServiceSFNQueryPrefix());
            sb.append("=");
            sb.append(getQueryString());
        }
        return sb.toString();
    }

	public String getSURLAsURIString() {

		String uriString =
						   transfProtocol.getProtocol().getSchema() + "://"
							   + this.transfProtocol.getAuthority().getServiceHostname();
		if(this.transfProtocol.getAuthority().getServicePort() >= 0)
		{
			uriString += ":" + this.transfProtocol.getAuthority().getServicePort();
		}
		if(this.isNormalFormSURL())
		{
			uriString += this.path;
		}
		else
		{
			uriString += this.path + this.getPathQuery() + this.queryString;
		}
		return uriString;
	}

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.transfProtocol.toString());
        //buffer.append(getServiceEndPoint());
        buffer.append(this.getPathQuery());
        return buffer.toString();
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(!(obj instanceof SURL))
			return false;
		SURL other = (SURL) obj;
		if(directory != other.directory)
			return false;
		return true;
	}
}
