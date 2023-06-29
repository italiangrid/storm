/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer;

import it.grid.storm.balancer.exception.BalancingStrategyException;

public interface BalancingStrategy {

  public Node getNextElement() throws BalancingStrategyException;

  public BalancingStrategyType getType();
}
