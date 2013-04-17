package it.grid.storm.catalogs;

public interface PersistentChunkData extends ChunkData {

	/**
	 * Method that returns the primary key in persistence, associated with This
	 * Chunk.
	 */
	public long getPrimaryKey();

}
