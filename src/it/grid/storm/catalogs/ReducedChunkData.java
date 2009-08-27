package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

public interface ReducedChunkData {

    public long primaryKey();
    public void setPrimaryKey(long l);
    public TSURL fromSURL();
    public TReturnStatus status();
    
}
