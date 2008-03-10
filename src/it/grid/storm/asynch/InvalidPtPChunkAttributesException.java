package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtPChunkData;
import it.grid.storm.griduser.VomsGridUser;

/**
 * This class represents an Exceptin thrown when a PtPChunk is created with
 * any null attribute: GridUser, RequestSummaryData, PtPChunkData or OverallRequest.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 2.0
 * @date:    June, 2005
 */
public class InvalidPtPChunkAttributesException extends Exception {

    private boolean nullGu = true; //true if GridUser is null
    private boolean nullRsd = true; //true if RequestSummaryData is null
    private boolean nullChunkData = true; //true if PtPChunkData is null
    private boolean nullOverallRequest = true; //true if OverallRequest is null

    /**
     * Constructor that requires the GridUser, RequestSummaryData,
     * PtPChunkData and OverallRequest, that caused the exception to be thrown.
     */
    public InvalidPtPChunkAttributesException(VomsGridUser gu, RequestSummaryData rsd, PtPChunkData chunkData, GlobalStatusManager or) {
        nullGu = (gu==null);
        nullRsd = (rsd==null);
        nullChunkData = (chunkData==null);
        nullOverallRequest = (or==null);
    }

    public String toString() {
        return "Invalid attributes when creating PtPChunk: nullGridUser="+nullGu+", nullRequestSumamryData="+nullRsd+"nullPtPChunkData="+nullChunkData+"nullOverallRequest="+nullOverallRequest;
    }
}
