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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.balancer.Node;

public class WeightStrategy extends RoundRobinStrategy {

  private static final Logger log = LoggerFactory.getLogger(WeightStrategy.class);

  private List<Integer> weights;
  private int current;

  public WeightStrategy(List<Node> pool) {

    super(pool);
    current = -1;
    initializeWeights();
  }

  private void initializeWeights() {

    weights = Lists.newArrayList();

    for (Node node : getNodePool()) {
      int weight = node.getWeight();
      int nodeIndex = getNodePool().indexOf(node);
      for (int i = 0; i < weight; i++) {
        weights.add(nodeIndex);
      }
    }
  }

  public Node getNextElement() {

    current = (current + 1) % weights.size();
    Node node = getNodePool().get(weights.get(current));
    log.debug("Found node: {}", node.getHostname());
    return node;
  }
}
