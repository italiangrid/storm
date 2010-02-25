package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

public interface ReducedChunkData {

    public TSURL fromSURL();
    public boolean isPinned();
    public long primaryKey();
    public void setPrimaryKey(long l);
    public TReturnStatus status();
    
}
