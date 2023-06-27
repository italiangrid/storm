package it.grid.storm.balancer.strategy;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;

public class CyclicCounter {

  private int maxVal;
  private AtomicInteger counter;

  public CyclicCounter(int maxVal) {

    Preconditions.checkArgument(maxVal >=0, "Maximum counter value should be >= 0");
    this.maxVal = maxVal;
    counter = new AtomicInteger(0);
  }

  public int next() {

    return counter.getAndUpdate(value ->
    {
      value++;
      if (value >= maxVal) {
        value = 0;
      }
      return value;
    } );
  }

}
