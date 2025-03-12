/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import java.util.Collection;

import it.grid.storm.persistence.model.PtGChunkDataTO;
import it.grid.storm.persistence.model.ReducedPtGChunkDataTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public interface PtGChunkDAO {

  public void addChild(PtGChunkDataTO to);

  public void update(PtGChunkDataTO to);

  public void updateIncomplete(ReducedPtGChunkDataTO chunkTO);

  public Collection<PtGChunkDataTO> find(TRequestToken requestToken);

  public void fail(PtGChunkDataTO auxTO);

  public Collection<TSURL> transitExpiredSRM_FILE_PINNED();

  public void updateStatus(TRequestToken requestToken, int[] surlUniqueIDs,
      String[] surls, TStatusCode statusCode, String explanation);

  public void updateStatusOnMatchingStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation);
}
