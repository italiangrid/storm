package it.grid.storm.catalogs;

public interface ChunkData extends RequestData
{
    /**
     * Method that returns the primary key in persistence, associated with This
     * Chunk.
     */
    public long getIdentifier();
    
    
}
