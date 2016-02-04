package it.grid.storm.ea;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MetricsEAAdapter implements ExtendedAttributes {

  final ExtendedAttributes delegate;
  final MetricRegistry registry;

  final Timer eaTimer;

  public MetricsEAAdapter(ExtendedAttributes d, MetricRegistry r) {
    delegate = d;
    registry = r;
    eaTimer = registry.timer("extendedAttrsOp");
  }

  public boolean hasXAttr(String fileName, String attributeName) {

    final Timer.Context context = eaTimer.time();
    try {
      return delegate.hasXAttr(fileName, attributeName);
    } finally {
      context.stop();
    }
  }

  public String getXAttr(String fileName, String attributeName) {

    final Timer.Context context = eaTimer.time();
    try {
      return delegate.getXAttr(fileName, attributeName);
    } finally {
      context.stop();
    }
  }

  public void setXAttr(String filename, String attributeName,
    String attributeValue) {

    final Timer.Context context = eaTimer.time();
    try {
      delegate.setXAttr(filename, attributeName, attributeValue);
    } finally {
      context.stop();
    }
  }

  public void rmXAttr(String filename, String attributeName) {

    final Timer.Context context = eaTimer.time();
    try {
      delegate.rmXAttr(filename, attributeName);
    } finally {
      context.stop();
    }
  }

}
