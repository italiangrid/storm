/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.ea;

import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;

public class ExtendedAttributesFactory {

  public static ExtendedAttributes getExtendedAttributes() {

    ExtendedAttributes eaImpl = new ExtendedAttributesSwigImpl();

    return new MetricsEAAdapter(eaImpl, METRIC_REGISTRY.getRegistry());
  }
}
