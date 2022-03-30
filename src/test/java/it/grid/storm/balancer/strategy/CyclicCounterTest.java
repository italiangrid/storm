package it.grid.storm.balancer.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CyclicCounterTest {

  @Test
  public void basicTest() {
    CyclicCounter cc = new CyclicCounter(4);
    assertEquals(0, cc.next());
    assertEquals(1, cc.next());
    assertEquals(2, cc.next());
    assertEquals(3, cc.next());
    assertEquals(0, cc.next());
  }

  @Test(expected = IllegalArgumentException.class)
  public void errorOnInit() {
    new CyclicCounter(-4);
  }

}
