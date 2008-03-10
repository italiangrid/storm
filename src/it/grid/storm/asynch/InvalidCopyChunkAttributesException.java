package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.CopyChunkData;
import it.grid.storm.griduser.VomsGridUser;


/**
 * This class represents an Exceptin thrown when a copyChunk is created with
 * any null attribute: GridUser, RequestSummaryData, CopyChunkData,
 * GlobalStatusManager or with a negative counter n.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 2.0
 * @date:    September, 2005
 */
public class InvalidCopyChunkAttributesException extends Exception {

    private boolean nullGu = true; //true if GridUser is null
    private boolean nullRsd = true; //true if RequestSummaryData is null
    private boolean nullChunkData = true; //true if CopyChunkData is null
    private boolean negativeN = true; //true if counter is negative
    private boolean nullGlobalStatusManager = true;

    /**
     * Constructor that requires the GridUser, RequestSummaryData and
     * CopyChunkData, as well as the int counter, that caused the exception
     * to be thrown.
     */
    public InvalidCopyChunkAttributesException(VomsGridUser gu, RequestSummaryData rsd, CopyChunkData chunkData, int n, GlobalStatusManager gsm) {
        nullGu = (gu==null);
        nullRsd = (rsd==null);
        nullChunkData = (chunkData==null);
        negativeN = (n<0);
        nullGlobalStatusManager = (gsm==null);
    }

    public String toString() {
        return "Invalid attributes when creating CopyChunk: nullGridUser="+nullGu+", nullRequestSummaryData="+nullRsd+", nullCopyChunkData="+nullChunkData+", negativeN="+negativeN+", nullGlobalStatusManager="+nullGlobalStatusManager;
    }
}
