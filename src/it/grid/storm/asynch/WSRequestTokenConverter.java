package it.grid.storm.asynch;

import it.grid.storm.srm.types.*;

import org.apache.log4j.Logger;


/**
 * Class used to convert between TRequestToken from the WebService and StoRM object model
 * counterpart.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    October 2005
 */
public class WSRequestTokenConverter {

    private static Logger log = Logger.getLogger("srmclient");

    /**
     * Method that accepts an srmClientStubs.TRequestToken and returns an
     * it.grid.storm.srm.types.TRequestToken; if the conversion fails, a
     * WSConversionException is thrown with a String reporting the
     * problem encountered.
     *
     * Beware that in case the String representation of srmClientStubs.TRequestToken
     * is null then a TRequestToken with the "" String is returned. Likewise if the
     * _stub_ itself is null!
     *
     * Beware that it was noticed that sometimes a null stub represents
     * an empty field for the remote ws. Instead of getting an empty String,
     * null may be used instead! This is why null stubs are interpreted as
     * a field that the remote WS decided not to fill in.
     *
     * In case for any reason th econversion cannot proceed, a WSConverterException
     * is thrown.
     */
    public it.grid.storm.srm.types.TRequestToken fromWS(String stub) throws WSConversionException {
        try {
            String auxString = null;
            if (stub==null) auxString = ""; else auxString = stub;
            return new it.grid.storm.srm.types.TRequestToken(auxString);
        } catch (InvalidTRequestTokenAttributesException e) {
            log.debug("Unexpected error in WSRequestTokenConverter! This is a programming bug! Could not translate TRequestToken from WS! "+e);
            throw new WSConversionException(e.toString());
        }
    }
}
