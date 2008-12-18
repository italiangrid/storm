

package it.grid.storm.catalogs;

import org.apache.log4j.Logger;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.config.Configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Timer;
import java.util.TimerTask;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents the RequestSummaryCatalog of StoRM. The rows in the
 * catalog are called RequestSummaryData. Methods are provided to: look up newly
 * added requests as identified by their SRM_REQUEST_QUEUED status, to update
 * the global status of the request, and to fail a request with SRM_FAILURE.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    April 26th, 2005
 */
public class RequestSummaryCatalog {

    private static final Logger log = Logger.getLogger("catalogs");
    private static RequestSummaryCatalog cat = new RequestSummaryCatalog(); //Only instance of RequestSummaryCatalog for StoRM!
    private RequestSummaryDAO dao = RequestSummaryDAO.getInstance(); //WARNING!!! TO BE MODIFIED WITH FACTORY!!!

    private Timer clock = null; //timer thread that will run a task to clean expired requests!
    private TimerTask clockTask = null; //timer task that will remove expired requests and corresponding proxies!

    private RequestSummaryCatalog() {
        if (Configuration.getInstance().getExpiredRequestPurging()) {
            clock = new Timer();
            clockTask = new TimerTask() {
                public void run() {
                    purgeExpiredRequests();
                }
            };
            clock.scheduleAtFixedRate(clockTask,Configuration.getInstance().getRequestPurgerDelay()*1000,Configuration.getInstance().getRequestPurgerPeriod()*1000);
        }
    }

    /**
     * Method that returns the only instance of RequestSummaryCatalog present in
     * StoRM.
     */
    public static RequestSummaryCatalog getInstance() {
        return cat;
    }


    /**
     * Method in charge of retrieving RequestSummaryData associated to new
     * requests, that is those found in SRM_REQUETS_QUEUED global status; such
     * requests then transit into SRM_SUCCESS. The actual number of fetched
     * requests depends on the configured ceiling.
     *
     * If no new request is found, an empty Collection is returned. if a request
     * is malformed, then that request is failed and an attempt is made to signal
     * such occurence in the DB. Only correctly formed requests are returned.
     */
    synchronized public Collection fetchNewRequests(int capacity) {
        log.debug("Retrieving a maximum of "+capacity+" new reuqests. (remaining capacity in Crusher Scheduler) ");
        Collection c = dao.findNew(capacity);
        //
        log.debug("REQUEST SUMMARY CATALOG: retrieved data "+c);
        List list = new ArrayList();
        if (c.isEmpty()) {
            log.debug("REQUEST SUMMARY CATALOG: No new requests found.");
        } else {
            int fetched = c.size();
            log.debug("REQUEST SUMMARY CATALOG: "+fetched+" new requests picked up. "); //info
            RequestSummaryDataTO auxTO;
            RequestSummaryData aux;
            for (Iterator i = c.iterator(); i.hasNext(); ) {
                auxTO = (RequestSummaryDataTO) i.next();
                aux = makeOne(auxTO);
                if (aux!=null) {
                    log.debug("REQUEST SUMMARY CATALOG; "+aux.requestToken()+" associated to "+aux.gridUser().getDn()+" included for processing "); //info
                    list.add(aux);
                }
            }
            int ret = list.size();
            if (ret<fetched) log.warn("REQUEST SUMMARY CATALOG: including "+ret+" requests for processing, since the dropped ones were malformed!");
            else log.debug("REQUEST SUMMARY CATALOG: including for processing all "+ret+" requests.");
        }
        if (!list.isEmpty()) {
            log.debug("REQUEST SUMMARY CATALOG: returning " + list + "\n\n");
        }
        return list;
    }

    /**
     * Private method used to create a RequestSummaryData object, from a
     * RequestSummaryDataTO. If a chunk cannot be created, an error messagge
     * gets logged and an attempt is made to signal in the DB that the request
     * is malformed.
     */
    private RequestSummaryData makeOne(RequestSummaryDataTO auxTO) {
        StringBuffer sb = new StringBuffer();
        //ID
        long auxid = auxTO.primaryKey();
        //TRequestType
        TRequestType auxrtype = RequestTypeConverter.getInstance().toSTORM(auxTO.requestType());
        if (auxrtype==TRequestType.EMPTY) {
            sb.append("TRequestType could not be created from its String representation ");
            sb.append(auxTO.requestType());
            sb.append("\n");
            auxrtype=null; //Fail RequestSummaryData creation!
        }
        //TRequestToken
        TRequestToken auxrtoken = null;
        try {
            auxrtoken = new TRequestToken(auxTO.requestToken());
        } catch (InvalidTRequestTokenAttributesException e) {
            sb.append(e);
            sb.append("\n");
        }
        //VomsGridUser
        GridUserInterface auxgu = null;
        String auxVomsAttributes = auxTO.vomsAttributes();

        String auxClientDN = auxTO.clientDN();
        try {
            //auxgu = loadVomsGridUser(auxClientDN,auxTO.requestToken()); //BEWARE!!! NO VOMS ATTRIBUTES ARE LOADED AS OF TODAY!!!
        	//VOMS attributes workaround
        	auxgu = loadVomsGridUser(auxClientDN,auxVomsAttributes, auxTO.requestToken());
        } catch (MalformedGridUserException e) {
            sb.append("VomsGridUser could not be created from DN String ");
            sb.append(auxClientDN);
            sb.append(" and from request token String ");
            sb.append(auxTO.requestToken());
        }
        //make RequestSummaryData
        RequestSummaryData aux=null;
        try {
            aux = new RequestSummaryData(auxrtype,auxrtoken,auxgu);
            aux.setPrimaryKey(auxTO.primaryKey());
        } catch (InvalidRequestSummaryDataAttributesException e) {
            dao.failRequest(auxTO.primaryKey(),"The request data is malformed!");
            log.warn("REQUEST SUMMARY CATALOG! Catalog retrieved malformed request, and it is being dropped: "+auxTO);
            log.warn(e);
            log.warn(sb.toString());
        }
        //end...
        return aux;
    }

    /**
     * Private method that holds the logic for creating a VomsGridUser from persistence
     * and to load any available Proxy. For the moment the VOMS attributes present in
     * persistence are NOT loaded!
     */
    //private VomsGridUser loadVomsGridUser(String dn, String rtoken) throws MalformedGridUserException {
    private GridUserInterface loadVomsGridUser(String dn, String fqans_string, String rtoken) throws MalformedGridUserException {

        log.debug("REQUEST SUMMARY CATALOG! Received request to create VomsGridUser for "+dn+" "+rtoken);
        //set up proxy from file, if it exists!
        String proxyString = null;
        FQAN[] fqans_vector = null;
        try {
            File proxyFile = new File(Configuration.getInstance().getProxyHome() + File.separator + rtoken);
            if (proxyFile.exists()) {
                ByteArrayOutputStream out;
                InputStream in;
                int c;
                out = new ByteArrayOutputStream(); in = new FileInputStream(proxyFile);
                while ((c = in.read()) != -1) out.write(c);
                in.close(); out.close();
                proxyString = new String(out.toByteArray());
                log.debug("REQUEST SUMMARY CATALOG: Loaded proxy file "+proxyFile.getAbsolutePath()+" for request "+rtoken);
                log.debug("REQUEST SUMMARY CATALOG: proxy content is "+proxyString);
            } else {
                log.debug("REQUEST SUMMARY CATALOG: No proxy file "+proxyFile.getAbsolutePath()+" found for request "+rtoken);
            }
        } catch (FileNotFoundException e) {
            //This should not happen given the existence test just performed!
            log.error("REQUEST SUMMARY CATALOG! The file containing the proxy was deleted just before reading its content! No proxy has been loaded!");
        } catch (IOException e) {
            //Some generic IO error occured!
            log.error("REQUEST SUMMARY CATALOG! The file containing the proxy could not be read! No proxy has been loaded! "+e);
        } catch (Exception e) {
            //An unexpected error occured: I am including this generic catch because the underlaying filesystem has ACLs, and I do not know how exactly Java behaves!
            log.error("REQUEST SUMMARY CATALOG! There was an unexpected error while attempting to read the file containing the proxy! No proxy has been loaded!");
        }

        /**
         * This code is only for the 1.3.18.
         * This is a workaround to get FQANs using the proxy field on request_queue.
         * From the DB is retrieved a single FQAN string containing all
         * FQAN separeted by the "#" char.
         */
        if(fqans_string!="") {
            String[] fqans_string_array = fqans_string.split("#");
            if (fqans_string_array.length!=0) {
                    fqans_vector = new FQAN[fqans_string_array.length];
                for(int i=0;i<fqans_string_array.length;i++)
                    fqans_vector[i] = new FQAN(fqans_string_array[i]);
            }
        }


        log.debug("REQUEST SUMMARY CATALOG! Received request to create VomsGridUser for " +  dn + " " + fqans_string + " " + proxyString);
        if ( (dn != null) && (fqans_string != null) && (proxyString != null)) {
          //all credentials available!
          log.info("REQUEST SUMMARY CATALOG! DN, VOMS Attributes, and Proxy certificate FOUND for request " +rtoken);
          return GridUserManager.makeVOMSGridUser(dn, proxyString, fqans_vector);
        }
        else if ( (dn != null) && (fqans_string != null) && (proxyString == null)) {
          //voms credentials without proxy
          log.info("REQUEST SUMMARY CATALOG! DN and VOMS Attributes FOUND for request " + rtoken);
          return GridUserManager.makeVOMSGridUser(dn, fqans_vector);
        }
        else if ( (dn != null) && (fqans_string == null) && (proxyString != null)) {
          //NON-voms credentials with proxy
          log.info("REQUEST SUMMARY CATALOG! DN and VOMS Attributes FOUND for request " + rtoken);
          return GridUserManager.makeGridUser(dn, proxyString);
        }
        else if ( (dn != null) && (fqans_string == null) && (proxyString == null)) {
          //NON-voms credentials without proxy
          log.info("REQUEST SUMMARY CATALOG! DN and VOMS Attributes FOUND for request " + rtoken);
          return GridUserManager.makeGridUser(dn);
        }
        else {
          //unmanageble combination!
          log.warn("REQUEST SUMMARY CATALOG! Catalog retrieved invalid credentials data for request " + rtoken);
          log.warn("REQUEST SUMMARY CATALOG! proxy=" + fqans_string + "\n dn=" + dn + "\n attributes=" + fqans_string);
          throw new MalformedGridUserException();
        }



    }


    /**
     * Method used to update the global status of a request identified by TRequestToken,
     * to the supplied TReturnStatus.
     *
     * In case of any exception nothing happens.
     */
    synchronized public void updateGlobalStatus(TRequestToken rt, TReturnStatus status) {
        dao.updateGlobalStatus(rt.toString(),StatusCodeConverter.getInstance().toDB(status.getStatusCode()),status.getExplanation());
    }

    /**
     * Method used to change the global status of the supplied request to SRM_FAILURE,
     * as well as that of each single chunk in the request. If the request type is not
     * supported by the logic, only the global status is updated and an error log gets
     * written warning of the unsupported business logic.
     *
     * If the supplied RequestSummaryData is null, nothing gets done; if any DB error
     * occurs, no exception gets thrown but proper messagges get logged.
     */
    synchronized public void failRequest(RequestSummaryData rsd, String explanation) {
        if (rsd!=null) {
            TRequestType rtype = rsd.requestType();
            if (rtype==TRequestType.PREPARE_TO_GET) {
                dao.failPtGRequest(rsd.primaryKey(),explanation);
            } else if (rtype==TRequestType.PREPARE_TO_PUT) {
                dao.failPtPRequest(rsd.primaryKey(),explanation);
            } else if (rtype==TRequestType.COPY) {
                dao.failCopyRequest(rsd.primaryKey(),explanation);
            } else {
                dao.failRequest(rsd.primaryKey(),explanation);
            }
        }
    }

    /**
     * Method used to abort a request that has not yet been fetched for
     * processing; if the status of the request associated to the supplied
     * request token tok is different from SRM_REQUEST_QUEUED, then nothing
     * takes place; likewise if the supplied token does not correspond to any
     * request, or if it is null.
     */
    synchronized public void abortRequest(TRequestToken rt) {
        if (rt!=null) dao.abortRequest(rt.toString());
    }

    /**
     * Method used to abort a request that has not yet been fetched for
     * processing; abort is only applied to those SURLs of the request specified
     * in the Collection; if the status of the request associated to the supplied
     * request token is different from SRM_REQUEST_QUEUED, then nothing
     * takes place; likewise if the supplied token does not correspond to any
     * request, if it is null, if the Collection is null, or the Collection does
     * not contain TSURLs.
     */
    synchronized public void abortChunksOfRequest(TRequestToken rt, Collection c) {
        if ((rt!=null) && (c!=null) && (!c.isEmpty())) {
            try {
                List aux = new ArrayList();
                for (Iterator i = c.iterator(); i.hasNext(); )
                    aux.add( ((TSURL)i.next()).toString() );
                dao.abortChunksOfRequest(rt.toString(),aux);
            } catch (ClassCastException e) {
                log.error("REQUEST SUMMARY CATALOG! Unexpected error in abortChunksOfRequest: the supplied Collection did not contain TSURLs! "+c+e);
            }
        }
    }

    /**
     * Method used to abort a request that HAS been fetched for processing; abort
     * is only applied to those SURLs of the request specified in the Collection;
     * if the status of the request associated to the supplied
     * request token is different from SRM_REQUEST_INPROGRESS, then nothing
     * takes place; likewise if the supplied token does not correspond to any
     * request, if it is null, if the Collection is null, or the Collection does
     * not contain TSURLs.
     */
    synchronized public void abortChunksOfInProgressRequest(TRequestToken rt, Collection c) {
        if ((rt!=null) && (c!=null) && (!c.isEmpty())) {
            try {
                List aux = new ArrayList();
                for (Iterator i = c.iterator(); i.hasNext(); )
                    aux.add( ((TSURL)i.next()).toString() );
                dao.abortChunksOfInProgressRequest(rt.toString(),aux);
            } catch (ClassCastException e) {
                log.error("REQUEST SUMMARY CATALOG! Unexpected error in abortChunksOfInProgressRequest: the supplied Collection did not contain TSURLs! "+c+e);
            }
        }
    }

    /**
     * Method that returns the TRequestType associated to the request with the
     * supplied TRequestToken. If no request exists with that token, or the type
     * cannot be established from the DB, or the suplied token is null, then an
     * EMPTY TRequestType is returned.
     */
    synchronized public TRequestType typeOf(TRequestToken rt) {
        String type = null;
        if (rt!=null) type = dao.typeOf(rt.toString());
        return RequestTypeConverter.getInstance().toSTORM(type);
    }

    /**
     * Method used to abort a request that HAS been fetched for processing; if
     * the status of the request associated to the supplied request token tok is
     * different from SRM_REQUEST_INPROGRESS, then nothing takes place; likewise
     * if the supplied token does not correspond to any request, or if it is null.
     */
    synchronized public void abortInProgressRequest(TRequestToken rt) {
        if (rt!=null) dao.abortInProgressRequest(rt.toString());
    }



    /**
     * Method used to purge the DB of expired requests, and remove the corresponding
     * proxies if available.
     */
    synchronized private void purgeExpiredRequests() {
        List r_tokens = dao.purgeExpiredRequests();
        if (r_tokens.isEmpty()) log.debug("REQUEST SUMMARY CATALOG: No expired requests found.");
        while (!r_tokens.isEmpty()) {
            log.info("REQUEST SUMMARY CATALOG; removed from DB the following expired requests: "+r_tokens);
            for (Iterator i = r_tokens.iterator(); i.hasNext(); ) {
                String rt = (String) i.next();
                String proxyFileName = Configuration.getInstance().getProxyHome() + File.separator + rt;
                File proxyFile = new File(proxyFileName);
                if (proxyFile.exists()) {
                    boolean deleted = proxyFile.delete();
                    if (!deleted) {
                        log.error("ERROR IN REQUEST SUMMARY CATALOG! Removal of proxy file "+proxyFileName+" failed!");
                    } else {
                        log.info("REQUEST SUMMARY CATALOG: removed proxy file "+proxyFileName);
                    }
                }
            }
            r_tokens = dao.purgeExpiredRequests();
        }
    }

}
