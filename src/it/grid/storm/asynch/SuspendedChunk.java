package it.grid.storm.asynch;

import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

public interface SuspendedChunk {
    
    public void completeRequest(RecallTaskStatus recallStatus);
    
    public ChunkData getChunkData();

}
