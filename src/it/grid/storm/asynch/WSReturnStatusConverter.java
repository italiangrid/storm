package it.grid.storm.asynch;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.*;

import srmClientStubs.TReturnStatus;
import srmClientStubs.TStatusCode;

/**
 * Class used to convert between TReturnStatus from the WebService and StoRM object model
 * counterpart.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    October 2005
 */
public class WSReturnStatusConverter {
    private static final WSReturnStatusConverter converter = new WSReturnStatusConverter(); //only instance of converter
    private final Map wsTOstorm = new HashMap(); //map that contains the correspondence between WS TStatusCode type and StoRM object model TStatusCode type
    private final Map stormTOws = new HashMap(); //map that contains the previous correspondence, but reversed!

    private WSReturnStatusConverter() {
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_SUCCESS , it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FAILURE , it.grid.storm.srm.types.TStatusCode.SRM_FAILURE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_AUTHENTICATION_FAILURE , it.grid.storm.srm.types.TStatusCode.SRM_AUTHENTICATION_FAILURE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_AUTHORIZATION_FAILURE , it.grid.storm.srm.types.TStatusCode.SRM_AUTHORIZATION_FAILURE); //Bug in wsdl!!! un-AUA-thorised instead of un-AU-thorised!!!
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_INVALID_REQUEST , it.grid.storm.srm.types.TStatusCode.SRM_INVALID_REQUEST);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_INVALID_PATH , it.grid.storm.srm.types.TStatusCode.SRM_INVALID_PATH);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_LIFETIME_EXPIRED , it.grid.storm.srm.types.TStatusCode.SRM_FILE_LIFETIME_EXPIRED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_SPACE_LIFETIME_EXPIRED , it.grid.storm.srm.types.TStatusCode.SRM_SPACE_LIFETIME_EXPIRED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_EXCEED_ALLOCATION , it.grid.storm.srm.types.TStatusCode.SRM_EXCEED_ALLOCATION);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_NO_USER_SPACE , it.grid.storm.srm.types.TStatusCode.SRM_NO_USER_SPACE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_NO_FREE_SPACE , it.grid.storm.srm.types.TStatusCode.SRM_NO_FREE_SPACE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_DUPLICATION_ERROR , it.grid.storm.srm.types.TStatusCode.SRM_DUPLICATION_ERROR);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_NON_EMPTY_DIRECTORY , it.grid.storm.srm.types.TStatusCode.SRM_NON_EMPTY_DIRECTORY);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_TOO_MANY_RESULTS , it.grid.storm.srm.types.TStatusCode.SRM_TOO_MANY_RESULTS);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_INTERNAL_ERROR , it.grid.storm.srm.types.TStatusCode.SRM_INTERNAL_ERROR);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FATAL_INTERNAL_ERROR , it.grid.storm.srm.types.TStatusCode.SRM_FATAL_INTERNAL_ERROR);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_NOT_SUPPORTED , it.grid.storm.srm.types.TStatusCode.SRM_NOT_SUPPORTED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_REQUEST_QUEUED , it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_QUEUED); //hex 0x1000  --- 4096
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_REQUEST_INPROGRESS , it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS); //hex 0x2000 --- 8192
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_REQUEST_SUSPENDED , it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_SUSPENDED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_ABORTED , it.grid.storm.srm.types.TStatusCode.SRM_ABORTED); //hex 0x7000 --- 28672
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_RELEASED , it.grid.storm.srm.types.TStatusCode.SRM_RELEASED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_PINNED , it.grid.storm.srm.types.TStatusCode.SRM_FILE_PINNED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_IN_CACHE , it.grid.storm.srm.types.TStatusCode.SRM_FILE_IN_CACHE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_SPACE_AVAILABLE , it.grid.storm.srm.types.TStatusCode.SRM_SPACE_AVAILABLE);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_LOWER_SPACE_GRANTED , it.grid.storm.srm.types.TStatusCode.SRM_LOWER_SPACE_GRANTED);
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_DONE , it.grid.storm.srm.types.TStatusCode.SRM_DONE); //hex 0x5000
        
        /* New return status codes: SRM v2.2 */
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_PARTIAL_SUCCESS , it.grid.storm.srm.types.TStatusCode.SRM_PARTIAL_SUCCESS);
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_REQUEST_TIMED_OUT , it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_TIMED_OUT);
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_LAST_COPY , it.grid.storm.srm.types.TStatusCode.SRM_LAST_COPY);
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_BUSY , it.grid.storm.srm.types.TStatusCode.SRM_FILE_BUSY);
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_LOST , it.grid.storm.srm.types.TStatusCode.SRM_FILE_LOST);
//        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_FILE_UNAVAILABLE , it.grid.storm.srm.types.TStatusCode.SRM_FILE_UNAVAILABLE);
        
        wsTOstorm.put(srmClientStubs.TStatusCode.SRM_CUSTOM_STATUS , it.grid.storm.srm.types.TStatusCode.SRM_CUSTOM_STATUS);
        Object aux;
        for (Iterator i = wsTOstorm.keySet().iterator(); i.hasNext(); ) {
            aux = i.next();
            stormTOws.put(wsTOstorm.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of WSReturnStatusConverter.
     */
    public static WSReturnStatusConverter getInstance() {
        return converter;
    }



    /**
     * Method that accepts an srmClientStubs.TReturnStatus and returns an
     * it.grid.storm.srm.types.TReturnStatus; if the conversion fails, a
     * WSConversionException is thrown with a String reporting the
     * problem encountered.
     *
     * Beware that it was noticed that sometimes a null stub represents
     * an empty field for the remote ws. Instead of getting an empty String,
     * null may be used instead! This is why null stubs are interpreted as
     * a field that the remote WS decided not to fill in.
     *
     * In particular, since the status cannot be optional, a missing field
     * is just not accepted by StoRM: it is treated as a misbehaving WS! For
     * null, then, an Exception is thrown!
     */
    synchronized public it.grid.storm.srm.types.TReturnStatus fromWS(srmClientStubs.TReturnStatus stub) throws WSConversionException {
        if (stub==null) throw new WSConversionException("Supplied srmClientStubs.TReturnStatus was null!");
        try {
            it.grid.storm.srm.types.TStatusCode code = (it.grid.storm.srm.types.TStatusCode) wsTOstorm.get(stub.getStatusCode());
            String auxExplanation = stub.getExplanation();
            if (auxExplanation==null) auxExplanation="";
            return new it.grid.storm.srm.types.TReturnStatus(code,auxExplanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            throw new WSConversionException("Unable to complete conversion for WS TReturnStatus: " + stub.toString()  +"; "+e);
        }
    }
}
