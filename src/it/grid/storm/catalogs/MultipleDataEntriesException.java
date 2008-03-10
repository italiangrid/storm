package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents an Exception thrown by the ReservedSpaceCatalog when it
 * finds more than one row of data for the specified request.
 *
 * @author:  EGRID ICTP
 * @version: 1.0
 * @date:    June 2005
 */
public class MultipleDataEntriesException extends Exception {

    private TRequestToken requestToken;

    /**
     * Constructor tha trequires the attributes that caused the exception to be
     * thrown.
     */
    public MultipleDataEntriesException(TRequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public String toString() {
        return "MultipleDataEntriesException: requestToken="+requestToken;
    }

}
