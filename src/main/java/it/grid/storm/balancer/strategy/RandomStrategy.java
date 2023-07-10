/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import static it.grid.storm.balancer.BalancingStrategyType.RANDOM;

import it.grid.storm.balancer.Node;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomStrategy extends AbstractBalancingStrategy {

  private static final Logger log = LoggerFactory.getLogger(RandomStrategy.class);

  private final Random random;

  public RandomStrategy(List<Node> nodes) {
    super(nodes);
    setType(RANDOM);
    random = new Random((new Date()).getTime());
  }

  public Node getNextElement() {
    // Return index from 0 to size-1
    int index = random.nextInt(getNodePool().size());
    Node node = getNodePool().get(index);
    log.debug("Found node: {}", node.getHostname());
    return node;
  }
}
