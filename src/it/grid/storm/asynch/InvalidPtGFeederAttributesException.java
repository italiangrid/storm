package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.VomsGridUser;

/**
 * Class that represents an Exception thrown when a PtGFeeder could not be created
 * because the supplied RequestSummayData or GridUser or GlobalStatusManager were
 * null.
 *
 * @author  EGRID ICTP
 * @version 3.0
 * @date    July, 2005
 */
public class InvalidPtGFeederAttributesException extends Exception {

    private boolean nullRequestSummaryData = false;
    private boolean nullGridUser = false;
    private boolean nullGlobalStatusManager = false;

    /**
     * Public constructor that requires the RequestSummaryData and the GridUser that caused the
     * exception to be thrown.
     */
    public InvalidPtGFeederAttributesException(RequestSummaryData rsd, VomsGridUser gu, GlobalStatusManager gsm) {
        nullRequestSummaryData = (rsd==null);
        nullGridUser = (gu==null);
        nullGlobalStatusManager = (gsm==null);
    }

    public String toString() {
        return "null-RequestSummaryData="+nullRequestSummaryData+"; null-GridUser="+nullGridUser+"; null-GlobalStatusManagerr="+nullGlobalStatusManager;
    }
}
