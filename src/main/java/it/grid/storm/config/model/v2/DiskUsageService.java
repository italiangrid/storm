package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_TASKS_INTERVAL;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DiskUsageService [enabled=");
    builder.append(enabled);
    builder.append(", parallelTasksEnabled=");
    builder.append(parallelTasksEnabled);
    builder.append(", initialDelay=");
    builder.append(initialDelay);
    builder.append(", tasksInterval=");
    builder.append(tasksInterval);
    builder.append("]");
    return builder.toString();
  }

}
