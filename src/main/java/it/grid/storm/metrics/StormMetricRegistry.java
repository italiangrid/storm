package it.grid.storm.metrics;

import com.codahale.metrics.MetricRegistry;

/**
 * Singleton wrapper for the dropwizard metrics registry.
 * 
 *
 */
public enum StormMetricRegistry {

  INSTANCE;

  private StormMetricRegistry() {
    registry = new MetricRegistry();
  }

  private final MetricRegistry registry;

  public MetricRegistry getRegistry() {

    return registry;
  }

}
