package it.grid.storm.ea;

import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;

public class ExtendedAttributesFactory {

  public static ExtendedAttributes getExtendedAttributes() {

    ExtendedAttributes eaImpl = new ExtendedAttributesSwigImpl();
    
    return new MetricsEAAdapter(eaImpl,
      METRIC_REGISTRY.getRegistry());
  }

}
