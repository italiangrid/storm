package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents an Exception thrown by the ReservedSpaceCatalog when it
 * finds no data for the specified request.
 *
 * @author:  EGRID ICTP
 * @version: 1.0
 * @date:    June 2005
 */
public class NoDataFoundException extends Exception {

    private TRequestToken requestToken;

    /**
     * Constructor tha trequires the attributes that caused the exception to be
     * thrown.
     */
    public NoDataFoundException(TRequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public String toString() {
        return "NoDataFoundException: requestToken="+requestToken;
    }

}
