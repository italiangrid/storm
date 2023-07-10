/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.PtGChunkTO;
import it.grid.storm.srm.types.TRequestToken;
import java.util.Collection;

public interface PtGChunkDAO {

  public PtGChunkTO getPtGChunkDataById(Long ssId) throws DataAccessException;

  public void addPtGChunkData(PtGChunkTO ptgChunkTO) throws DataAccessException;

  public Collection getPtGChunksDataByToken(TRequestToken token) throws DataAccessException;

  public void removePtGChunksData(PtGChunkTO ptgChunkTO) throws DataAccessException;
}
