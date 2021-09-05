package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_MAXIMUM;

import org.slf4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PinlifetimeSettings {

  @JsonProperty("default")
  public long defaultValue;
  public long maximum;
  
  public PinlifetimeSettings() {
    defaultValue = PIN_LIFETIME_DEFAULT;
    maximum = PIN_LIFETIME_MAXIMUM;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.default: {}", prefix, defaultValue);
    log.info("{}.maximum: {}", prefix, maximum);
  }
}
