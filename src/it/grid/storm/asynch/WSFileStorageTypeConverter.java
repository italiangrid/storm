package it.grid.storm.asynch;

import it.grid.storm.srm.types.*;

import srmClientStubs.TFileStorageType;

/**
 * Class used to convert between TFileStorageType from the WebService and StoRM object model
 * counterpart.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    October 2005
 */
public class WSFileStorageTypeConverter {

    /**
     * Method that accepts an srmClientStubs.TFileStorageType and returns an
     * it.grid.storm.srm.types.TFileStorageType; if the conversion fails, a
     * WSConversionException is thrown with a String reporting the
     * problem encountered.
     *
     * If the supplied srmClientStubs.TFileStorageType is null, a WSConversionException
     * is thrown.
     *
     * Beware that it was noticed that sometimes a null stub represents
     * an empty field for the remote WS. Instead of getting an empty String,
     * null may be used instead! This is why null stubs are interpreted as
     * a field that the remote WS decided not to fill in.
     *
     * In particular, since the FileStorageType cannot be optional, a missing field
     * is just not accepted by StoRM: it is treated as a misbehaving WS! For
     * null, then, an Exception is thrown!
     */
    public it.grid.storm.srm.types.TFileStorageType fromWS(srmClientStubs.TFileStorageType stub) throws WSConversionException {
        if (stub==null) throw new WSConversionException("Supplied srmClientStubs.TFileStorageType was null!");
        if (stub==srmClientStubs.TFileStorageType.VOLATILE) return it.grid.storm.srm.types.TFileStorageType.VOLATILE;
        else if (stub==srmClientStubs.TFileStorageType.PERMANENT) return it.grid.storm.srm.types.TFileStorageType.PERMANENT;
        else if (stub==srmClientStubs.TFileStorageType.DURABLE) return it.grid.storm.srm.types.TFileStorageType.DURABLE;
        else throw new WSConversionException("Unable to convert srmClientStubs.TFileStorageType "+stub.toString());
    }

    /**
     * Method that accepts an it.grid.storm.srm.types.TFileStorageType and returns an
     * srmClientStubs.TFileStorageType; if the conversion fails, a WSConversionException
     * is thrown with a String reporting the problem encountered.
     *
     * If the supplied it.grid.storm.srm.types.TFileStorageType is null, a WSConversionException
     * is thrown: StoRM makes no use of null in its object model, so it must be interpreted as a
     * programming bug!
     */
    public srmClientStubs.TFileStorageType fromStoRM(it.grid.storm.srm.types.TFileStorageType storm) throws WSConversionException {
        if (storm==null) throw new WSConversionException("Supplied it.grid.storm.srm.types.TFileStorageType was null!");
        if (storm==it.grid.storm.srm.types.TFileStorageType.VOLATILE) return srmClientStubs.TFileStorageType.VOLATILE;
        else if (storm==it.grid.storm.srm.types.TFileStorageType.PERMANENT) return srmClientStubs.TFileStorageType.PERMANENT;
        else if (storm==it.grid.storm.srm.types.TFileStorageType.DURABLE) return srmClientStubs.TFileStorageType.DURABLE;
        else throw new WSConversionException("Unable to convert it.grid.storm.srm.types.TFileStorageType "+storm.toString());
    }
}
