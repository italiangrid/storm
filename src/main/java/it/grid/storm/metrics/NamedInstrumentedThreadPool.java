package it.grid.storm.metrics;

import static com.codahale.metrics.MetricRegistry.name;

import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;

public class NamedInstrumentedThreadPool extends QueuedThreadPool{

  public NamedInstrumentedThreadPool(String name, MetricRegistry registry) {
    super();
    setName(name);
    String tpName = name+"-tp";
    registry.register(name(tpName, "percent-idle"), new RatioGauge() {
      @Override
      protected Ratio getRatio() {
          return Ratio.of(getIdleThreads(),
                          getThreads());
      }
  });
  registry.register(name(tpName, "active-threads"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
          return getThreads();
      }
  });
  registry.register(name(tpName, "idle-threads"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
          return getIdleThreads();
      }
  });
  registry.register(name(tpName, "jobs"), new Gauge<Integer>() {
      @Override
      public Integer getValue() {
          // This assumes the QueuedThreadPool is using a BlockingArrayQueue or
          // ArrayBlockingQueue for its queue, and is therefore a constant-time operation.
          return getQueue() != null ? getQueue().size() : 0;
      }
  });
  registry.register(name(tpName, "utilization-max"), new RatioGauge() {
      @Override
      protected Ratio getRatio() {
          return Ratio.of(getThreads() - getIdleThreads(), getMaxThreads());
      }
  });
  }

}
