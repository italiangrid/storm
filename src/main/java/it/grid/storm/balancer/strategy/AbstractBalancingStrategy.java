/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.balancer.Node;

public abstract class AbstractBalancingStrategy implements BalancingStrategy {

  private BalancingStrategyType type;
  private CopyOnWriteArrayList<Node> nodePool;

  public AbstractBalancingStrategy(List<Node> pool) {

    Preconditions.checkNotNull(pool, "Unable to build BalancingStrategy: received null node pool");
    Preconditions.checkArgument(pool.size() > 0,
        "Unable to build BalancingStrategy: received empty node pool");

    this.nodePool = Lists.newCopyOnWriteArrayList(pool);
  }

  public BalancingStrategyType getType() {
    return type;
  }

  protected void setType(BalancingStrategyType type) {
    this.type = type;
  }

  public List<Node> getNodePool() {
    return nodePool;
  }

  public String toString() {
    return this.getClass().getName();
  }
}
