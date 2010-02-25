package it.grid.storm.asynch;

import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exception thrown when a BoLChunk is created with any null attribute:
 * GridUser, RequestSummaryData, BoLChunkData or GlobalStatusManager.
 * 
 * @author: CNAF
 * @version: 1.0
 * @date: Aug 2009
 */
public class InvalidBoLChunkAttributesException extends Exception {

    private static final long serialVersionUID = 2320080131526579634L;

    private boolean nullGu = true; // true if GridUser is null
    private boolean nullRsd = true; // true if RequestSummaryData is null
    private boolean nullChunkData = true; // true if BoLChunkData is null
    private boolean nullGlobalStatusManager = true; // true if gsm is null

    /**
     * Constructor that requires the GridUser, RequestSummaryData, BoLChunkData and
     * GlobalStatusManager that caused the exception to be thrown.
     */
    public InvalidBoLChunkAttributesException(GridUserInterface gu, RequestSummaryData rsd,
            BoLChunkData chunkData, GlobalStatusManager gsm) {
        nullGu = (gu == null);
        nullRsd = (rsd == null);
        nullChunkData = (chunkData == null);
        nullGlobalStatusManager = (gsm == null);
    }

    public String toString() {
        return "Invalid attributes when creating BoLChunk: nullGridUser=" + nullGu
                + ", nullRequestSumamryData=" + nullRsd + "nullBoLChunkData=" + nullChunkData
                + "nullGlobalStatusManager=" + nullGlobalStatusManager;
    }
}
