package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_GLANCE_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_LOGBOOK_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_MEASURING;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HearthbeatSettings {

  private boolean bookkeepingEnabled;
  private boolean performanceMeasuringEnabled;
  private int period;
  private int performanceLogbookTimeInterval;
  private int performanceGlanceTimeInterval;

  public HearthbeatSettings() {
    bookkeepingEnabled = BOOK_KEEPING_ENABLED;
    performanceMeasuringEnabled = PERFORMANCE_MEASURING;
    period = HEARTHBEAT_PERIOD;
    performanceLogbookTimeInterval = PERFORMANCE_LOGBOOK_TIME_INTERVAL;
    performanceGlanceTimeInterval = PERFORMANCE_GLANCE_TIME_INTERVAL;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("HearthbeatSettings [bookkeepingEnabled=");
    builder.append(bookkeepingEnabled);
    builder.append(", performanceMeasuringEnabled=");
    builder.append(performanceMeasuringEnabled);
    builder.append(", period=");
    builder.append(period);
    builder.append(", performanceLogbookTimeInterval=");
    builder.append(performanceLogbookTimeInterval);
    builder.append(", performanceGlanceTimeInterval=");
    builder.append(performanceGlanceTimeInterval);
    builder.append("]");
    return builder.toString();
  }

  public boolean isBookkeepingEnabled() {
    return bookkeepingEnabled;
  }

  public void setBookkeepingEnabled(boolean bookkeepingEnabled) {
    this.bookkeepingEnabled = bookkeepingEnabled;
  }

  public boolean isPerformanceMeasuringEnabled() {
    return performanceMeasuringEnabled;
  }

  public void setPerformanceMeasuringEnabled(boolean performanceMeasuringEnabled) {
    this.performanceMeasuringEnabled = performanceMeasuringEnabled;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public int getPerformanceLogbookTimeInterval() {
    return performanceLogbookTimeInterval;
  }

  public void setPerformanceLogbookTimeInterval(int performanceLogbookTimeInterval) {
    this.performanceLogbookTimeInterval = performanceLogbookTimeInterval;
  }

  public int getPerformanceGlanceTimeInterval() {
    return performanceGlanceTimeInterval;
  }

  public void setPerformanceGlanceTimeInterval(int performanceGlanceTimeInterval) {
    this.performanceGlanceTimeInterval = performanceGlanceTimeInterval;
  }

}
