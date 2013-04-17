package it.grid.storm.scheduler;

import it.grid.storm.asynch.RequestChunk;

public interface PersistentRequestChunk extends RequestChunk {

	public String getRequestToken();

	public void persistStatus();

}
