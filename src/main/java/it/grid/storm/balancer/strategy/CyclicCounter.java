/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.strategy;

import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicCounter {

  private int maxVal;
  private AtomicInteger counter;

  public CyclicCounter(int maxVal) {

    Preconditions.checkArgument(maxVal >= 0, "Maximum counter value should be >= 0");
    this.maxVal = maxVal;
    counter = new AtomicInteger(0);
  }

  public int next() {

    return counter.getAndUpdate(
        value -> {
          value++;
          if (value >= maxVal) {
            value = 0;
          }
          return value;
        });
  }
}
