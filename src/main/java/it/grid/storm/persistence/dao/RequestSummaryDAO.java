/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.dao;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RequestSummaryTO;

public interface RequestSummaryDAO {

  public RequestSummaryTO getRequestSummaryById(Long ssId) throws DataAccessException;

  public void addRequestSummary(RequestSummaryTO rsd) throws DataAccessException;

  public void removeRequestSummary(RequestSummaryTO rsd) throws DataAccessException;
}
