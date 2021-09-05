package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_GLANCE_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_LOGBOOK_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_MEASURING;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HearthbeatSettings {

  public boolean bookkeepingEnabled;
  public boolean performanceMeasuringEnabled;
  public int period;
  public int performanceLogbookTimeInterval;
  public int performanceGlanceTimeInterval;

  public HearthbeatSettings() {
    bookkeepingEnabled = BOOK_KEEPING_ENABLED;
    performanceMeasuringEnabled = PERFORMANCE_MEASURING;
    period = HEARTHBEAT_PERIOD;
    performanceLogbookTimeInterval = PERFORMANCE_LOGBOOK_TIME_INTERVAL;
    performanceGlanceTimeInterval = PERFORMANCE_GLANCE_TIME_INTERVAL;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.bookkeeping_enabled: {}", prefix, bookkeepingEnabled);
    log.info("{}.performance_measuring_enabled: {}", prefix, performanceMeasuringEnabled);
    log.info("{}.period: {}", prefix, period);
    log.info("{}.performance_logbook_time_interval: {}", prefix, performanceLogbookTimeInterval);
    log.info("{}.performance_glance_time_interval: {}", prefix, performanceGlanceTimeInterval);
  }
}
