/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import java.util.Collection;

import it.grid.storm.persistence.model.BoLChunkDataTO;
import it.grid.storm.persistence.model.ReducedBoLChunkDataTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

public interface BoLChunkDAO {

  void addChild(BoLChunkDataTO to);

  void addNew(BoLChunkDataTO to, String clientDn);

  void update(BoLChunkDataTO to);

  void updateIncomplete(ReducedBoLChunkDataTO to);

  Collection<BoLChunkDataTO> find(TRequestToken requestToken);

  Collection<ReducedBoLChunkDataTO> findReduced(TRequestToken requestToken);

  Collection<ReducedBoLChunkDataTO> findReduced(TRequestToken requestToken, int[] surlUniqueIDs,
      String[] surls);

  Collection<ReducedBoLChunkDataTO> findReduced(String griduser, int[] surlUniqueIDs,
      String[] surls);

  int updateStatus(BoLChunkDataTO to, TStatusCode status, String explanation);

  int releaseExpiredAndSuccessfulRequests();

  void updateStatusOnMatchingStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation);

  Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn);

  Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray);
}
