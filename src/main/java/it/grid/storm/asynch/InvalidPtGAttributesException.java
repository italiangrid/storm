package it.grid.storm.asynch;

import it.grid.storm.catalogs.PtGData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidPtGAttributesException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 6957632945020144458L;
	protected boolean nullGu = true; // true if GridUser is null
	protected boolean nullChunkData = true; // true if PtGChunkData is null

	/**
	 * Constructor that requires the GridUser, RequestSummaryData, PtGChunkData
	 * and GlobalStatusManager that caused the exception to be thrown.
	 */
	public InvalidPtGAttributesException(GridUserInterface gu, PtGData chunkData) {

		nullGu = (gu == null);
		nullChunkData = (chunkData == null);
	}

	public String toString() {

		return String.format("Invalid attributes when creating PtG: "
			+ "null-GridUser=%b, null-PtGChunkData=%b", nullGu, nullChunkData);
	}
}
