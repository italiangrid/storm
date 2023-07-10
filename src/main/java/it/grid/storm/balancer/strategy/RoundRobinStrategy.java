/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import static it.grid.storm.balancer.BalancingStrategyType.ROUNDROBIN;

import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.exception.BalancingStrategyException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundRobinStrategy extends AbstractBalancingStrategy {

  private static final Logger log = LoggerFactory.getLogger(RoundRobinStrategy.class);

  private final CyclicCounter counter;

  public RoundRobinStrategy(List<Node> nodes) {
    super(nodes);
    setType(ROUNDROBIN);
    counter = new CyclicCounter(nodes.size());
  }

  @Override
  public Node getNextElement() throws BalancingStrategyException {

    Node node = getNodePool().get(counter.next());
    log.debug("Found node: {}", node.getHostname());
    return node;
  }

  protected CyclicCounter getCounter() {
    return counter;
  }
}
