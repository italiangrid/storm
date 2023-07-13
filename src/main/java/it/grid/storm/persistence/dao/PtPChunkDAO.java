/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import java.util.Collection;
import java.util.Map;

import it.grid.storm.persistence.model.PtPChunkDataTO;
import it.grid.storm.persistence.model.ReducedPtPChunkDataTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

public interface PtPChunkDAO {

  public void update(PtPChunkDataTO to);

  public void updateIncomplete(ReducedPtPChunkDataTO chunkTO);

  public Collection<PtPChunkDataTO> find(TRequestToken requestToken);

  public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn);

  public int fail(PtPChunkDataTO auxTO);

  public Map<Long, String> getExpiredSRM_SPACE_AVAILABLE();

  public Map<Long, String> getExpired(TStatusCode status);

  public int transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(Collection<Long> ids);

  public int transitLongTimeInProgressRequestsToStatus(long expirationTime, TStatusCode status,
      String explanation);

  public int updateStatus(Collection<Long> ids, TStatusCode fromStatus, TStatusCode toStatus,
      String explanation);

  public int updateStatus(TRequestToken requestToken, int[] surlsUniqueIDs, String[] surls,
      TStatusCode statusCode, String explanation);

  public int updateStatusOnMatchingStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation);

  public int updateStatusOnMatchingStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
      String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode);
}
