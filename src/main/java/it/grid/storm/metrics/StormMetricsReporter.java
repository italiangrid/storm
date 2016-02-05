package it.grid.storm.metrics;

import java.util.SortedMap;
import java.util.TreeMap;
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

  public static final String METRICS_LOGGER_NAME = "storm-metrics-logger";

  public static final String[] REPORTED_METRICS = { "synch",
    OperationType.AF.getOpName(), OperationType.AR.getOpName(),
    OperationType.EFL.getOpName(), OperationType.GSM.getOpName(),
    OperationType.GST.getOpName(), OperationType.LS.getOpName(),
    OperationType.MKD.getOpName(), OperationType.MV.getOpName(),
    OperationType.PD.getOpName(), OperationType.PNG.getOpName(),
    OperationType.RF.getOpName(), OperationType.RM.getOpName(),
    OperationType.RMD.getOpName(), FilesystemMetric.FILE_ACL_OP.getOpName(),
    FilesystemMetric.FILE_ATTRIBUTE_OP.getOpName(), "ea" };

  private static final Logger LOG = LoggerFactory
    .getLogger(METRICS_LOGGER_NAME);

  private SortedMap<String, Long> lastSnapshotCountMap = new TreeMap<String, Long>();

  private StormMetricsReporter(MetricRegistry registry, MetricFilter filter,
    TimeUnit rateUnit, TimeUnit durationUnit) {

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
      rateUnit = TimeUnit.SECONDS;
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

  public StormMetricsReporter(MetricRegistry registry, String name,
    MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {

    super(registry, name, filter, rateUnit, durationUnit);

  }

  @SuppressWarnings({ "rawtypes" })
  @Override
  public void report(SortedMap<String, Gauge> gauges,
    SortedMap<String, Counter> counters,
    SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters,
    SortedMap<String, Timer> timers) {

    for (String metricName : REPORTED_METRICS) {
      Timer t = timers.get(metricName);
      if (t != null) {
        reportMetric(metricName, t);
      } else {
        LOG.error("Invalid metric name: {}", metricName);
      }
    }

  }

  private void reportMetric(String name, Timer timer) {

    final Snapshot snapshot = timer.getSnapshot();

    // This will contain the count for this snapshot
    Long thisSnapshotCount = null;

    // This contains the total count recorded at the previous
    // snapshot, if null this is the first iteration
    Long lastSnapshotCount = lastSnapshotCountMap.get(name);

    if (lastSnapshotCount != null) {
      thisSnapshotCount = timer.getCount() - lastSnapshotCount;
    }

    lastSnapshotCountMap.put(name, timer.getCount());

    LOG.info(
      "{} [(m1_count={}, count={}) (max={}, min={}, mean={}, p99={}) (m1_rate={}, mean_rate={})] duration_units={}, rate_units={}",
      name, thisSnapshotCount, timer.getCount(),
      convertDuration(snapshot.getMax()), convertDuration(snapshot.getMin()),
      convertDuration(snapshot.getMean()),
      convertDuration(snapshot.get99thPercentile()),
      convertRate(timer.getOneMinuteRate()), convertRate(timer.getMeanRate()),
      getDurationUnit(), getRateUnit());
  }

  public String getRateUnit() {

    return "events/" + super.getRateUnit();
  }
}
