package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents an Exception thrown when a BoLFeeder could not be created because the
 * supplied RequestSummayData or GridUser or GlobalStatusManager were null.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug, 2009
 */
public class InvalidBoLFeederAttributesException extends Exception {

    private static final long serialVersionUID = -5043656524831277137L;

    private boolean nullRequestSummaryData = false;
    private boolean nullGridUser = false;
    private boolean nullGlobalStatusManager = false;

    /**
     * Public constructor that requires the RequestSummaryData and the GridUser that caused the
     * exception to be thrown.
     */
    public InvalidBoLFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu,
            GlobalStatusManager gsm) {
        
        nullRequestSummaryData = (rsd == null);
        nullGridUser = (gu == null);
        nullGlobalStatusManager = (gsm == null);
    }

    public String toString() {
        return "null-RequestSummaryData=" + nullRequestSummaryData + "; null-GridUser=" + nullGridUser
                + "; null-GlobalStatusManagerr=" + nullGlobalStatusManager;
    }
}
