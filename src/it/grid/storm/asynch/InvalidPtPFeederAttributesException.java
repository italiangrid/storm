package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents an Exception thrown when a PtPFeeder could not be created
 * because the supplied RequestSummayData or GridUser were null.
 *
 * @author  EGRID ICTP
 * @version 3.0
 * @date    June, 2005
 */
public class InvalidPtPFeederAttributesException extends Exception {

    private boolean nullRequestSummaryData = false;
    private boolean nullGridUser = false;
    private boolean nullGlobalStatusManager = false;

    /**
     * Public constructor that requires the RequestSummaryData, the GridUser and
     * the GlobalStatusManager that caused the exception to be thrown.
     */
    public InvalidPtPFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu, GlobalStatusManager gsm) {
        nullRequestSummaryData = (rsd==null);
        nullGridUser = (gu==null);
        nullGlobalStatusManager = (gsm==null);
    }

    public String toString() {
        return "null-RequestSummaryData="+nullRequestSummaryData+"; null-GridUser="+nullGridUser+"; null-GlobalStatusManager="+nullGlobalStatusManager;
    }
}
