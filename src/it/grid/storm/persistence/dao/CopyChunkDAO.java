package it.grid.storm.persistence.dao;


import java.util.Collection;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.CopyChunkTO;
import it.grid.storm.srm.types.TRequestToken;


public interface CopyChunkDAO {
  public CopyChunkTO getCopyChunkDataById(Long ssId) throws DataAccessException;

  public void addCopyChunkData(CopyChunkTO copyChunkTO) throws DataAccessException;

  public Collection getCopyChunksDataByToken(TRequestToken token) throws DataAccessException;

  public void removeCopyChunksData(CopyChunkTO copyChunkTO) throws DataAccessException;
}
