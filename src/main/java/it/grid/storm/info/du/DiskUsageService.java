package it.grid.storm.info.du;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import it.grid.storm.namespace.VirtualFSInterface;

public class DiskUsageService {

  private static final Logger log = LoggerFactory.getLogger(DiskUsageService.class);

  private List<VirtualFSInterface> monitoredSAs;

  private ScheduledExecutorService executor;
  private boolean running;

  public DiskUsageService(List<VirtualFSInterface> vfss, ScheduledExecutorService executor) {

    Preconditions.checkNotNull(vfss, "Invalid null list of Virtual FS");
    Preconditions.checkNotNull(executor, "Invalid null scheduled executor service");

    this.monitoredSAs = Lists.newArrayList(vfss);
    this.executor = executor;
    this.running = false;

  }

  public DiskUsageService(List<VirtualFSInterface> vfss) {

    this(vfss, Executors.newSingleThreadScheduledExecutor());
  }

  public List<VirtualFSInterface> getMonitoredSAs() {

    return monitoredSAs;
  }

  public void addMonitoredSA(VirtualFSInterface vfs) {

    monitoredSAs.add(vfs);
  }

  public synchronized int runScheduled(long delay, long period) {

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

  public synchronized void stopScheduled() {

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

  public static synchronized int runTasksOnce(List<VirtualFSInterface> vfss) {

    log.debug("Starting DiskUsageService runTasksOnce ...");
    ExecutorService executor = Executors.newSingleThreadExecutor();
    vfss.forEach(vfs -> {
      DiskUsageTask task = new DiskUsageTask(vfs);
      log.debug("Run task {}", task);
      executor.submit(task);
    });
    log.debug("Submitted {} tasks", vfss.size());
    executor.shutdown();
    return vfss.size();

  }
}
