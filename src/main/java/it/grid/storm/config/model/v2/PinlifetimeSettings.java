package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_MAXIMUM;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PinlifetimeSettings [defaultValue=");
    builder.append(defaultValue);
    builder.append(", maximum=");
    builder.append(maximum);
    builder.append("]");
    return builder.toString();
  }

}
