package it.grid.storm.info.du;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.namespace.model.VirtualFS;

public class DiskUsageService {

  private static final Logger log = LoggerFactory.getLogger(DiskUsageService.class);

  private List<VirtualFS> monitoredSAs;

  private ScheduledExecutorService executor;
  private boolean running;
  private int delay;
  private long period;

  private DiskUsageService(List<VirtualFS> vfss, ScheduledExecutorService executor,
      int delay, long period) {

    Preconditions.checkNotNull(vfss, "Invalid null list of Virtual FS");
    Preconditions.checkNotNull(executor, "Invalid null scheduled executor service");

    this.monitoredSAs = Lists.newArrayList(vfss);
    this.executor = executor;
    this.running = false;
    this.delay = delay;
    this.period = period;
  }

  public int getDelay() {

    return delay;
  }

  public long getPeriod() {

    return period;
  }

  public void setDelay(int delay) {

    this.delay = delay;
  }

  public void setPeriod(long period) {

    this.period = period;
  }

  public static DiskUsageService getSingleThreadScheduledService(List<VirtualFS> vfss,
      int delay, long period) {

    return new DiskUsageService(vfss, Executors.newSingleThreadScheduledExecutor(), delay, period);
  }

  public static DiskUsageService getScheduledThreadPoolService(List<VirtualFS> vfss,
      int delay, long period) {

    return new DiskUsageService(vfss, Executors.newScheduledThreadPool(vfss.size()), delay, period);
  }

  public List<VirtualFS> getMonitoredSAs() {

    return monitoredSAs;
  }

  public synchronized int start() {

    if (running) {
      log.info("DiskUsage service is already running");
      return 0;
    }

    log.debug("Starting DiskUsageService ...");
    monitoredSAs.forEach(vfs -> {
      DiskUsageTask task = new DiskUsageTask(vfs);
      log.debug("Schedule task {} with delay {}s and period {}s", task, delay, period);
      executor.scheduleAtFixedRate(task, delay, period, TimeUnit.SECONDS);
    });
    log.debug("Scheduled {} tasks", monitoredSAs.size());
    running = true;
    return monitoredSAs.size();
  }

  public synchronized void stop() {

    if (!running) {
      log.info("DiskUsage service is not running");
      return;
    }

    executor.shutdown();
    running = false;
  }

  public boolean isRunning() {

    return running;
  }
}
