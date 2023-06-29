/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import java.util.List;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.balancer.Node;

public class BalancingStrategyFactory {

  public static BalancingStrategy getBalancingStrategy(
      BalancingStrategyType type, List<Node> pool) throws IllegalArgumentException {

    switch (type) {
      case RANDOM:
        return new RandomStrategy(pool);
      case ROUNDROBIN:
        return new RoundRobinStrategy(pool);
      case WEIGHT:
        return new WeightStrategy(pool);
      case SMART_RR:
        return new SmartRoundRobinStrategy(pool);
    }
    throw new IllegalArgumentException("StrategyFactory: Unknown BalancingStrategyType: " + type);
  }

}
