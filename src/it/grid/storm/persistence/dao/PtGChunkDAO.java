package it.grid.storm.persistence.dao;


import java.util.Collection;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.PtGChunkTO;
import it.grid.storm.srm.types.TRequestToken;


public interface PtGChunkDAO {
  public PtGChunkTO getPtGChunkDataById(Long ssId) throws DataAccessException;

  public void addPtGChunkData(PtGChunkTO ptgChunkTO) throws DataAccessException;

  public Collection getPtGChunksDataByToken(TRequestToken token) throws DataAccessException;

  public void removePtGChunksData(PtGChunkTO ptgChunkTO) throws DataAccessException;
}
