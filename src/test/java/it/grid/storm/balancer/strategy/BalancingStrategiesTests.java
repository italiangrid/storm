/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import static it.grid.storm.config.Configuration.CONFIG_FILE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import it.grid.storm.balancer.BalancerUtils;
import it.grid.storm.balancer.BalancingStrategy;
import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.cache.ResponsivenessCache;
import it.grid.storm.balancer.exception.BalancingStrategyException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BalancingStrategiesTests extends BalancerUtils {

  static {
    System.setProperty(CONFIG_FILE_PATH, "storm.properties");
  }

  private final ResponsivenessCache CACHE = ResponsivenessCache.INSTANCE;

  @Before
  public void initCache() {
    CACHE.invalidate();
  }

  @Test
  public void smartRoundRobinTest() throws BalancingStrategyException {

    Node https1 = getResponsiveHttpsNode(1, "dav01.example.org", 8443);
    Node https2 = getUnresponsiveHttpsNode(2, "dav02.example.org", 8443);
    Node https3 = getResponsiveHttpsNode(3, "dav03.example.org", 8443);

    BalancingStrategy srr = new SmartRoundRobinStrategy(Lists.newArrayList(https1, https2, https3));

    assertEquals(https1, srr.getNextElement());
    assertEquals(https3, srr.getNextElement());
    assertEquals(https1, srr.getNextElement());
  }

  @Test
  public void roundRobinTest() throws BalancingStrategyException {

    Node https1 = getResponsiveHttpsNode(1, "dav01.example.org", 8443);
    Node https2 = getUnresponsiveHttpsNode(2, "dav02.example.org", 8443);
    Node https3 = getResponsiveHttpsNode(3, "dav03.example.org", 8443);

    BalancingStrategy rr = new RoundRobinStrategy(Lists.newArrayList(https1, https2, https3));

    assertEquals(https1, rr.getNextElement());
    assertEquals(https2, rr.getNextElement());
    assertEquals(https3, rr.getNextElement());
    assertEquals(https1, rr.getNextElement());
  }

  @Test
  public void randomTest() throws BalancingStrategyException {

    Node https1 = getResponsiveHttpsNode(1, "dav01.example.org", 8443);
    Node https2 = getUnresponsiveHttpsNode(2, "dav02.example.org", 8443);
    Node https3 = getResponsiveHttpsNode(3, "dav03.example.org", 8443);
    List<Node> nodes = Lists.newArrayList(https1, https2, https3);

    BalancingStrategy rs = new RandomStrategy(nodes);

    assertTrue(nodes.indexOf(rs.getNextElement()) != -1);
    assertTrue(nodes.indexOf(rs.getNextElement()) != -1);
    assertTrue(nodes.indexOf(rs.getNextElement()) != -1);
    assertTrue(nodes.indexOf(rs.getNextElement()) != -1);
    assertTrue(nodes.indexOf(rs.getNextElement()) != -1);
  }

  @Test
  public void weightTest() throws BalancingStrategyException {

    Node https1 = getResponsiveHttpsNode(1, "dav01.example.org", 8443, 5);
    Node https2 = getUnresponsiveHttpsNode(2, "dav02.example.org", 8443, 3);
    Node https3 = getResponsiveHttpsNode(3, "dav03.example.org", 8443, 2);
    List<Node> nodes = Lists.newArrayList(https1, https2, https3);

    BalancingStrategy ws = new WeightStrategy(nodes);

    for (int i = 0; i < 5; i++) {
      assertEquals(https1, ws.getNextElement());
    }
    for (int i = 0; i < 3; i++) {
      assertEquals(https2, ws.getNextElement());
    }
    for (int i = 0; i < 2; i++) {
      assertEquals(https3, ws.getNextElement());
    }

    assertEquals(https1, ws.getNextElement());
  }
}
