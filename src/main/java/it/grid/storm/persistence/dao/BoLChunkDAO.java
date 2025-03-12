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

  void update(BoLChunkDataTO to);

  void updateIncomplete(ReducedBoLChunkDataTO to);

  Collection<BoLChunkDataTO> find(TRequestToken requestToken);

  int updateStatus(BoLChunkDataTO to, TStatusCode status, String explanation);

  int releaseExpiredAndSuccessfulRequests();

  int abortInProgressRequestsSince(long expirationTimeInSeconds);

  void updateStatusOnMatchingStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation);

}
