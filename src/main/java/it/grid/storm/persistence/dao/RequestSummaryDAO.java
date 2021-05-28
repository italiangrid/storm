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

import it.grid.storm.persistence.model.RequestSummaryDataTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TStatusCode;

public interface RequestSummaryDAO {

  Collection<RequestSummaryDataTO> fetchNewRequests(int limit);

  void failRequest(long requestId, String explanation);

  void failPtGRequest(long requestId, String explanation);

  void failPtPRequest(long requestId, String explanation);

  void updateGlobalStatus(TRequestToken requestToken, TStatusCode status, String explanation);

  void updateGlobalStatusOnMatchingGlobalStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation);

  void updateGlobalStatusPinFileLifetime(TRequestToken requestToken, TStatusCode status,
      String explanation);

  void abortRequest(TRequestToken requestToken);

  void abortInProgressRequest(TRequestToken requestToken);

  void abortChunksOfInProgressRequest(TRequestToken requestToken, Collection<String> surls);

  TRequestType getRequestType(TRequestToken requestToken);

  RequestSummaryDataTO find(TRequestToken requestToken);

  Collection<String> purgeExpiredRequests(long expiredRequestTime, int purgeSize);

  int getNumberExpired();

}
