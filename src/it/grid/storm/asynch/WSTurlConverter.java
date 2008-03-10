package it.grid.storm.asynch;

import org.apache.log4j.Logger;

import org.apache.axis.types.URI;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.*;

/**
 * Class used to convert between TTURL from the WebService and StoRM object model
 * counterpart.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    October 2005
 */
public class WSTurlConverter {

    private static Logger log = Logger.getLogger("srmclient");

    /**
     * Method that accepts an srmClientStubs.TTURL and returns an
     * it.grid.storm.srm.types.TTURL; if the conversion fails, a
     * WSConversionException is thrown with a String reporting the
     * problem encountered.
     *
     * Beware that the representation of an empty srmClientStubs.TTURL
     * is the _null_ handle; so null causes an EmptyTTURL to be returned.
     *
     * However, even if srmClientStubs.TTURL is not null, but its URI then
     * again an EmptyTTURL is returned.
     *
     * Finally, if the URI s String is null or "", then egain an EmptyTTURL
     * is returned.
     *
     * In case for any reason th econversion cannot proceed, a WSConverterException
     * is thrown.
     */
    public it.grid.storm.srm.types.TTURL fromWS(org.apache.axis.types.URI auxURI) throws WSConversionException {
        try {
            if (auxURI==null) return it.grid.storm.srm.types.TTURL.makeEmpty();
            String auxString = auxURI.toString(); if ((auxString==null) || (auxString=="")) return it.grid.storm.srm.types.TTURL.makeEmpty();
            return it.grid.storm.srm.types.TTURL.makeFromString(auxString);
        } catch (InvalidTTURLAttributesException e) {
            log.debug("Unexpected error in WSTurlConverter! This is a programming bug! Could not translate TTURL from WS! "+e);
            throw new WSConversionException(e.toString());
        }
    }
}
