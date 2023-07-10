/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.metrics;

import com.codahale.metrics.MetricRegistry;

/** Singleton wrapper for the dropwizard metrics registry. */
public enum StormMetricRegistry {
  METRIC_REGISTRY;

  private StormMetricRegistry() {
    registry = new MetricRegistry();
  }

  private final MetricRegistry registry;

  public MetricRegistry getRegistry() {

    return registry;
  }
}
