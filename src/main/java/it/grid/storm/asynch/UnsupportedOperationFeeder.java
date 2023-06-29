/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.scheduler.Delegable;

public class UnsupportedOperationFeeder implements Delegable {

  @Override
  public void doIt() {

    throw new UnsupportedOperationException();

  }

  @Override
  public String getName() {

    return "Unsupported Operation Feeder";
  }

}
