package it.grid.storm.catalogs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import it.grid.storm.srm.types.TStatusCode;

/**
 * Package private auxiliary class used to convert between DB raw data and StoRM
 * object model representation of StatusCode.
 *
 * @author:  EGRID ICTP
 * @version: 2.0
 * @date:    June 2005
 */
public class StatusCodeConverter {

    private Map DBtoSTORM = new HashMap();
    private Map STORMtoDB = new HashMap();

    private static StatusCodeConverter c = new StatusCodeConverter();

    private StatusCodeConverter() {
        DBtoSTORM.put(new Integer(0),TStatusCode.SRM_SUCCESS);
        DBtoSTORM.put(new Integer(1),TStatusCode.SRM_FAILURE);
        DBtoSTORM.put(new Integer(2),TStatusCode.SRM_AUTHENTICATION_FAILURE);
        DBtoSTORM.put(new Integer(3),TStatusCode.SRM_AUTHORIZATION_FAILURE);
        DBtoSTORM.put(new Integer(4),TStatusCode.SRM_INVALID_REQUEST);
        DBtoSTORM.put(new Integer(5),TStatusCode.SRM_INVALID_PATH);
        DBtoSTORM.put(new Integer(6),TStatusCode.SRM_FILE_LIFETIME_EXPIRED);
        DBtoSTORM.put(new Integer(7),TStatusCode.SRM_SPACE_LIFETIME_EXPIRED);
        DBtoSTORM.put(new Integer(8),TStatusCode.SRM_EXCEED_ALLOCATION);
        DBtoSTORM.put(new Integer(9),TStatusCode.SRM_NO_USER_SPACE);
        DBtoSTORM.put(new Integer(10),TStatusCode.SRM_NO_FREE_SPACE);
        DBtoSTORM.put(new Integer(11),TStatusCode.SRM_DUPLICATION_ERROR);
        DBtoSTORM.put(new Integer(12),TStatusCode.SRM_NON_EMPTY_DIRECTORY);
        DBtoSTORM.put(new Integer(13),TStatusCode.SRM_TOO_MANY_RESULTS);
        DBtoSTORM.put(new Integer(14),TStatusCode.SRM_INTERNAL_ERROR);
        DBtoSTORM.put(new Integer(15),TStatusCode.SRM_FATAL_INTERNAL_ERROR);
        DBtoSTORM.put(new Integer(16),TStatusCode.SRM_NOT_SUPPORTED);
        DBtoSTORM.put(new Integer(17),TStatusCode.SRM_REQUEST_QUEUED);
        DBtoSTORM.put(new Integer(18),TStatusCode.SRM_REQUEST_INPROGRESS);
        DBtoSTORM.put(new Integer(19),TStatusCode.SRM_REQUEST_SUSPENDED);
        DBtoSTORM.put(new Integer(20),TStatusCode.SRM_ABORTED);
        DBtoSTORM.put(new Integer(21),TStatusCode.SRM_RELEASED);
        DBtoSTORM.put(new Integer(22),TStatusCode.SRM_FILE_PINNED);
        DBtoSTORM.put(new Integer(23),TStatusCode.SRM_FILE_IN_CACHE);
        DBtoSTORM.put(new Integer(24),TStatusCode.SRM_SPACE_AVAILABLE);
        DBtoSTORM.put(new Integer(25),TStatusCode.SRM_LOWER_SPACE_GRANTED);
        DBtoSTORM.put(new Integer(26),TStatusCode.SRM_DONE);
        DBtoSTORM.put(new Integer(27),TStatusCode.SRM_PARTIAL_SUCCESS);
        DBtoSTORM.put(new Integer(28),TStatusCode.SRM_REQUEST_TIMED_OUT);
        DBtoSTORM.put(new Integer(29),TStatusCode.SRM_LAST_COPY);
        DBtoSTORM.put(new Integer(30),TStatusCode.SRM_FILE_BUSY);
        DBtoSTORM.put(new Integer(31),TStatusCode.SRM_FILE_LOST);
        DBtoSTORM.put(new Integer(32),TStatusCode.SRM_FILE_UNAVAILABLE);
        DBtoSTORM.put(new Integer(33),TStatusCode.SRM_CUSTOM_STATUS);
        
        Object aux;
        for (Iterator i = DBtoSTORM.keySet().iterator(); i.hasNext(); ) {
            aux = i.next();
            STORMtoDB.put(DBtoSTORM.get(aux),aux);
        }
    }

    /**
     * Method that returns the only instance of StatusCodeConverter.
     */
    public static StatusCodeConverter getInstance() {
        return c;
    }

    /**
     * Method that returns the int used in the DB to represent the
     * given TStatusCode. -1 is returned if no match is found.
     */
    public int toDB(TStatusCode sc) {
        Integer aux = (Integer) STORMtoDB.get(sc);
        if (aux==null) return -1;
        return aux.intValue();
    }

    /**
     * Method that returns the TStatusCode used by StoRM to represent
     * the supplied int representation of the DB. TStatusCode.EMPTY is
     * returned if no StoRM type is found.
     */
    public TStatusCode toSTORM(int n) {
        TStatusCode aux = (TStatusCode) DBtoSTORM.get(new Integer(n));
        if (aux==null) return TStatusCode.EMPTY;
        return aux;
    }
}
