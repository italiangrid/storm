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

import com.google.common.collect.Lists;

import it.grid.storm.balancer.Node;

public class WeightStrategy<E extends Node> extends RoundRobinStrategy<E> {

  // Contains a list of index/key with replica depending on weight
  private List<Integer> weights;
  private int current;

  public WeightStrategy(List<E> pool) {

    super(pool);
    current = -1;
    initializeWeights();
  }

  private void initializeWeights() {

    weights = Lists.newArrayList();

    for (Node node : getNodePool()) {
      int weight = node.getWeight();
      for (int i = 0; i <= weight; i++) {
        weights.add(getNodePool().indexOf(node));
      }
    }
  }

  public E getNextElement() {

    current = (current + 1) % weights.size();
    return getNodePool().get(weights.get(current));
  }
}
