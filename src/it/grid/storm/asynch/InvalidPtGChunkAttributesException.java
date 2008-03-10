package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.griduser.VomsGridUser;

/**
 * This class represents an Exceptin thrown when a PtGChunk is created with
 * any null attribute: GridUser, RequestSummaryData, PtGChunkData or
 * GlobalStatusManager.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 2.0
 * @date:    May 16th, 2005
 */
public class InvalidPtGChunkAttributesException extends Exception {

    private boolean nullGu = true; //true if GridUser is null
    private boolean nullRsd = true; //true if RequestSummaryData is null
    private boolean nullChunkData = true; //true if PtGChunkData is null
    private boolean nullGlobalStatusManager = true; //true if gsm is null

    /**
     * Constructor that requires the GridUser, RequestSummaryData, 
     * PtGChunkData and GlobalStatusManager that caused the exception to be
     * thrown.
     */
    public InvalidPtGChunkAttributesException(VomsGridUser gu, RequestSummaryData rsd, PtGChunkData chunkData, GlobalStatusManager gsm) {
        nullGu = (gu==null);
        nullRsd = (rsd==null);
        nullChunkData = (chunkData==null);
        nullGlobalStatusManager = (gsm==null);
    }

    public String toString() {
        return "Invalid attributes when creating PtGChunk: nullGridUser="+nullGu+", nullRequestSumamryData="+nullRsd+"nullPtGChunkData="+nullChunkData+"nullGlobalStatusManager="+nullGlobalStatusManager;
    }
}
