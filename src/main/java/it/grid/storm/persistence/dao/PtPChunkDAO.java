/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.PtPChunkTO;
import it.grid.storm.srm.types.TRequestToken;
import java.util.Collection;

public interface PtPChunkDAO {

  public PtPChunkTO getPtGChunkDataById(Long ssId) throws DataAccessException;

  public void addPtGChunkData(PtPChunkTO ptpChunkData) throws DataAccessException;

  public Collection getPtPChunksDataByToken(TRequestToken token) throws DataAccessException;

  public void removePtGChunksData(PtPChunkTO ptpChunkData) throws DataAccessException;
}
