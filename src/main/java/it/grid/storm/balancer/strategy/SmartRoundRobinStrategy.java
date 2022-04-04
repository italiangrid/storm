package it.grid.storm.balancer.strategy;

import static it.grid.storm.balancer.BalancingStrategyType.SMART_RR;
import static it.grid.storm.balancer.cache.Responsiveness.RESPONSIVE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.cache.Responsiveness;
import it.grid.storm.balancer.cache.ResponsivenessCache;
import it.grid.storm.balancer.exception.BalancingStrategyException;

public class SmartRoundRobinStrategy extends RoundRobinStrategy {

  private static final Logger log = LoggerFactory.getLogger(SmartRoundRobinStrategy.class);

  public SmartRoundRobinStrategy(List<Node> nodes) {

    super(nodes);
    setType(SMART_RR);
  }

  @Override
  public Node getNextElement() throws BalancingStrategyException {

    int attempts = 0;
    int maxAttempts = getNodePool().size();

    while (attempts < maxAttempts) {
      attempts++;
      Node node = getNodePool().get(getCounter().next());
      if (RESPONSIVE.equals(getResponsiveness(node))) {
        log.debug("Found responsive node: {}", node.getHostname());
        return node;
      }
    }
    log.warn("No one remote service is responsive!");
    throw new BalancingStrategyException("No remote services are responsive");
  }

  private Responsiveness getResponsiveness(Node node) {
    return ResponsivenessCache.INSTANCE.getResponsiveness(node);
  }
}
