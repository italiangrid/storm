package it.grid.storm.namespace.naming;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.validator.UrlValidator;
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

    public SURL(final String hostName,
            final int port,
            final String serviceEndpoint,
            final String queryString) {
        super(Protocol.SRM, hostName, port, serviceEndpoint, queryString);
        directory = checkDirectory(queryString);
    }

    public SURL(final String hostName,
            final int port,
            final String stfn) {
        super(Protocol.SRM, hostName, port, stfn);
        directory = checkDirectory(stfn);
    }

    public SURL(final String stfn) {
        super(Protocol.SRM, NamingConst.getServiceDefaultHost(), NamingConst.getServicePort(), stfn);
        directory = checkDirectory(stfn);
    }

    /**
     * Build SURL from the string format.
     * Many control will be executed in the string format
     *
     * @param surlString String
     * @return SURL
     */
    public static SURL makeSURLfromString(String surlString) throws NamespaceException {
        SURL result = null;
        // boolean valid = surlValidator.isValid(surlString);
        // Create a URI try { uri = new URI("file://D:/almanac1.4/Ex1.java"); } catch (URISyntaxException e) { }
        boolean valid = true;
        String explanation = "";
        URI uri = null;
        try {
            uri = URI.create(surlString);
        } catch (IllegalArgumentException uriEx) {
            valid = false;
            explanation = "URI Except: " + uriEx.getMessage();
        } catch (NullPointerException npe) {
            valid = false;
            explanation = "URI Except (null SURL): " + npe.getMessage();
        }
        

        //Check the scheme
        if (valid) {
            // uri should be not null
            uri = uri.normalize();
            String scheme = uri.getScheme();
            if (!(schemes.contains(scheme))) {
              valid = false;
              explanation = "unknown scheme '"+scheme+"'";
            }     
        }  
        
        //Check the query
        if (valid) {            
            String host = uri.getHost();
            int port = uri.getPort();
            String query = uri.getQuery();
            if (query == null) {
                String stfn = uri.getPath();
                result = new SURL(host, port, stfn);
            } else {
                if (query.equals("")) {
                    String stfn = uri.getPath();
                    result = new SURL(host, port, stfn);
                } else { // The SURL_Str is in a Query FORM.
                    log.debug(" !! SURL ('" + surlString + "') in a query form (query:'" + query + "') !!");
                    String service = uri.getPath();
                    log.debug(" Service endpoint : " + service);
                    boolean queryValidForm = checkQuery(query);
                    if (queryValidForm) {
                        log.debug(" Query is in a valid form.");
                        // Extract the StFN from query:
                        String stfn = extractStFNfromQuery(query);
                        result = new SURL(host, port, service, stfn);
                    } else {
                        log.warn("SURL_String :'" + surlString + "' is not VALID! (query is in invalid form)");
                        throw new NamespaceException("SURL_String :'" + surlString
                                + "' is not VALID within the Query!");
                    }
                }
            }
        } else {
            throw new NamespaceException("SURL_String :'" + surlString + "' is INVALID. Reason: "+explanation);
        }
        return result;
    }

    public boolean isDirectory() {
        return directory;
    }

    private boolean checkDirectory(String path) {
        if (path != null) {
            if (path.endsWith(NamingConst.SEPARATOR)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private static boolean checkQuery(String query) {
        boolean valid = false;
        if (query.startsWith(NamingConst.getServiceSFNQueryPrefix() + "=")) {
            valid = true;
        }
        return valid;
    }

    private static String extractStFNfromQuery(String query) {
        String stfn = "";
        if (query == null) {
            return stfn;
        }
        else {
            int len = query.length();
            if (len < 4) {
                return stfn;
            }
            else {
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

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.transfProtocol.toString());
        //buffer.append(getServiceEndPoint());
        buffer.append(this.getPathQuery());
        return buffer.toString();
    }

}
