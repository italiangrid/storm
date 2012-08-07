package it.grid.storm.scheduler;

import it.grid.storm.asynch.RequestChunk;

public interface PersistentRequestChunk extends RequestChunk
{
    
    public void persistStatus();

}
