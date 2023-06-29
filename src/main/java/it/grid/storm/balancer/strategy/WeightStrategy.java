/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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
