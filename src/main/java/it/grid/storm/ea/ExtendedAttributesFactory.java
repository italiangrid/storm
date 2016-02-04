package it.grid.storm.ea;

import it.grid.storm.metrics.StormMetricRegistry;

public class ExtendedAttributesFactory {

  public static ExtendedAttributes getExtendedAttributes() {

    ExtendedAttributes eaImpl = new ExtendedAttributesSwigImpl();
    
    return new MetricsEAAdapter(eaImpl,
      StormMetricRegistry.INSTANCE.getRegistry());
  }

}
