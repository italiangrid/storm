package it.grid.storm.metrics;

import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

import it.grid.storm.common.OperationType;
import it.grid.storm.filesystem.MetricsFilesystemAdapter.FilesystemMetric;

public class StormMetricsReporter extends ScheduledReporter {

  public static final String METRICS_LOGGER_NAME = StormMetricsReporter.class.getName();

  protected static final String[] REPORTED_TIMERS = {"synch", OperationType.AF.getOpName(),
      OperationType.AR.getOpName(), OperationType.EFL.getOpName(), OperationType.GSM.getOpName(),
      OperationType.GST.getOpName(), OperationType.LS.getOpName(), OperationType.MKD.getOpName(),
      OperationType.MV.getOpName(), OperationType.PD.getOpName(), OperationType.PNG.getOpName(),
      OperationType.RF.getOpName(), OperationType.RM.getOpName(), OperationType.RMD.getOpName(),
      FilesystemMetric.FILE_ACL_OP.getOpName(), FilesystemMetric.FILE_ATTRIBUTE_OP.getOpName(),
      "ea"};

  private static final Logger LOG = LoggerFactory.getLogger(METRICS_LOGGER_NAME);

  private StormMetricsReporter(MetricRegistry registry, MetricFilter filter, TimeUnit rateUnit,
      TimeUnit durationUnit) {

    super(registry, "storm", filter, rateUnit, durationUnit);
  }

  public static Builder forRegistry(MetricRegistry registry) {

    return new Builder(registry);
  }

  public static class Builder {

    private final MetricRegistry registry;
    private MetricFilter filter;
    private TimeUnit rateUnit;
    private TimeUnit durationUnit;

    private Builder(MetricRegistry r) {

      this.registry = r;
      filter = MetricFilter.ALL;
      rateUnit = TimeUnit.MINUTES;
      durationUnit = TimeUnit.MILLISECONDS;
    }

    public Builder filter(MetricFilter filter) {

      this.filter = filter;
      return this;
    }

    public Builder rateUnit(TimeUnit unit) {

      this.rateUnit = unit;
      return this;
    }

    public Builder durationUnit(TimeUnit unit) {

      this.durationUnit = unit;
      return this;
    }

    public StormMetricsReporter build() {

      return new StormMetricsReporter(registry, filter, rateUnit, durationUnit);
    }
  }

  public StormMetricsReporter(MetricRegistry registry, String name, MetricFilter filter,
      TimeUnit rateUnit, TimeUnit durationUnit) {

    super(registry, name, filter, rateUnit, durationUnit);

  }

  @SuppressWarnings({"rawtypes"})
  @Override
  public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
      SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
      SortedMap<String, Timer> timers) {

    for (String metricName : REPORTED_TIMERS) {
      Timer t = timers.get(metricName);
      if (t != null) {
        reportMetric(metricName, t);
      } else {
        LOG.error("Invalid metric name: {}", metricName);
      }
    }

    reportThreadPoolMetrics("xmlrpc-tp", gauges);
    reportThreadPoolMetrics("rest-tp", gauges);

  }

  private void reportMetric(String name, Timer timer) {

    final Snapshot snapshot = timer.getSnapshot();

    LOG.info(
        "{} [(count={}, m1_rate={}, m5_rate={}, m15_rate={}) (max={}, min={}, mean={}, p95={}, p99={})] duration_units={}, rate_units={}",
        name, timer.getCount(), convertRate(timer.getOneMinuteRate()),
        convertRate(timer.getFiveMinuteRate()), convertRate(timer.getFifteenMinuteRate()),
        convertDuration(snapshot.getMax()), convertDuration(snapshot.getMin()),
        convertDuration(snapshot.getMean()), convertDuration(snapshot.get95thPercentile()),
        convertDuration(snapshot.get99thPercentile()), getDurationUnit(), getRateUnit());
  }

  @SuppressWarnings({"rawtypes"})
  private void reportThreadPoolMetrics(String tpName, SortedMap<String, Gauge> gauges) {

    double percentIdle = round2dec(getDoubleValue(gauges.get(tpName + ".percent-idle")));
    int activeThreads = getIntValue(gauges.get(tpName + ".active-threads"));
    int idleThreads = getIntValue(gauges.get(tpName + ".idle-threads"));
    int jobs = getIntValue(gauges.get(tpName + ".jobs"));
    double utilizationMax = round2dec(getDoubleValue(gauges.get(tpName + ".utilization-max")));

    LOG.info("{} [active-threads={}, idle-threads={}, jobs={}, utilization-max={}, percent-idle={}]",
        tpName, activeThreads, idleThreads, jobs, utilizationMax, percentIdle);
  }

  @Override
  public String getRateUnit() {

    return "events/" + super.getRateUnit();
  }

  private double round2dec(double n) {

    return Math.round(n * 100.0) / 100.0;
  }

  private double getDoubleValue(Gauge<?> gauge) {

    return (Double) gauge.getValue();
  }

  private int getIntValue(Gauge<?> gauge) {

    return (Integer) gauge.getValue();
  }
}
