package it.grid.storm.catalogs;

/**
 * Class that represents a generic chunk. It provides only one method which is the
 * primary key associated ot the chunk in persistence.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2006
 */
public interface ChunkData {

    /**
     * Method that returns the primary key in persistence, associated with This
     * Chunk.
     */
    public long primaryKey();

}
