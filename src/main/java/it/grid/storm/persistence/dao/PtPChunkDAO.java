/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
