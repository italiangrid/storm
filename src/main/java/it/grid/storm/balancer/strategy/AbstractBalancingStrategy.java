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

package it.grid.storm.balancer.strategy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.BalancingStrategyType;
import it.grid.storm.balancer.Node;

public abstract class AbstractBalancingStrategy<E extends Node> implements BalancingStrategy<E> {

  private BalancingStrategyType type;
  private CopyOnWriteArrayList<E> nodePool;

  public AbstractBalancingStrategy(BalancingStrategyType type, List<E> pool) {

    Preconditions.checkNotNull(pool,
        "Unable to build BalancingStrategy of type " + type + ": received null node pool");
    Preconditions.checkArgument(pool.size() > 0,
        "Unable to build BalancingStrategy of type " + type + ": received empty node pool");

    this.type = type;
    this.nodePool = Lists.newCopyOnWriteArrayList(pool);
  }

  public BalancingStrategyType getType() {
    return type;
  }

  public List<E> getNodePool() {
    return nodePool;
  }

  public String toString() {
    return this.getClass().getName();
  }
}
