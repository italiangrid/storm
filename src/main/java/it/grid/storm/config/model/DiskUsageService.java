package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_TASKS_INTERVAL;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DiskUsageService {

  public boolean enabled;
  public boolean parallelTasksEnabled;
  public int initialDelay;
  public long tasksInterval;

  public DiskUsageService() {
    enabled = DISKUSAGE_SERVICE_ENABLED;
    parallelTasksEnabled = DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED;
    initialDelay = DISKUSAGE_SERVICE_INITIAL_DELAY;
    tasksInterval = DISKUSAGE_SERVICE_TASKS_INTERVAL;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.enabled: {}", prefix, enabled);
    log.info("{}.parallel_tasks_enabled: {}", prefix, parallelTasksEnabled);
    log.info("{}.initial_delay: {}", prefix, initialDelay);
    log.info("{}.tasks_interval: {}", prefix, tasksInterval);
  }
}
