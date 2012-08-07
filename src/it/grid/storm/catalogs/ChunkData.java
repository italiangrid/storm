package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

public interface ChunkData extends RequestData
{
    /**
     * Method that returns the primary key in persistence, associated with This
     * Chunk.
     */
    public long getIdentifier();
    
    public TRequestToken getRequestToken();
}
