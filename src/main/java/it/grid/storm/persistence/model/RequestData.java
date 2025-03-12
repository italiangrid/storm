/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

/**
 * Class that represents a generic chunk. It provides only one method which is
 * the primary key associated ot the chunk in persistence.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2006
 */
public interface RequestData extends InputData {

  public TRequestToken getRequestToken();

  public TReturnStatus getStatus();

  public TSURL getSURL();

  public void changeStatusSRM_ABORTED(String explanation);

  public void changeStatusSRM_FILE_BUSY(String explanation);

  public void changeStatusSRM_INVALID_PATH(String explanation);

  public void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation);

  public void changeStatusSRM_INVALID_REQUEST(String explanation);

  public void changeStatusSRM_INTERNAL_ERROR(String explanation);

  public void changeStatusSRM_NOT_SUPPORTED(String explanation);

  public void changeStatusSRM_FAILURE(String explanation);

  public void changeStatusSRM_SUCCESS(String explanation);

  public void changeStatusSRM_SPACE_LIFETIME_EXPIRED(String explanation);

  public void changeStatusSRM_REQUEST_INPROGRESS(String explanation);

  public void changeStatusSRM_REQUEST_QUEUED(String explanation);
}
