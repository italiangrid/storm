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

  public void addNew(PtGChunkDataTO to, String clientDn);

  public void update(PtGChunkDataTO to);

  public void updateIncomplete(ReducedPtGChunkDataTO chunkTO);

  public PtGChunkDataTO refresh(long primaryKey);

  public Collection<PtGChunkDataTO> find(TRequestToken requestToken);

  public Collection<ReducedPtGChunkDataTO> findReduced(TRequestToken requestToken);

  public Collection<ReducedPtGChunkDataTO> findReduced(TRequestToken requestToken,
      int[] surlsUniqueIDs, String[] surlsArray);

  public Collection<ReducedPtGChunkDataTO> findReduced(String griduser, int[] surlUniqueIDs,
      String[] surls);

  public void fail(PtGChunkDataTO auxTO);

  public int numberInSRM_FILE_PINNED(int surlUniqueID);

  public int count(int surlUniqueID, TStatusCode status);

  public Collection<TSURL> transitExpiredSRM_FILE_PINNED();

  public void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids);

  public void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids, TRequestToken token);

  public void updateStatus(TRequestToken requestToken, int[] surlUniqueIDs,
      String[] surls, TStatusCode statusCode, String explanation);

  public void updateStatusOnMatchingStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation);
}
