/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.persistence.model.RequestData;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

public interface Suspendedable {

  public Boolean completeRequest(TapeRecallStatus recallStatus);

  public RequestData getRequestData();

}
