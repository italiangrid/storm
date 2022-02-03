package it.grid.storm.config.model.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum QualityLevel {

  DEVELOPMENT("development"),
  TESTING("testing"),
  PREPRODUCTION("pre-production"),
  PRODUCTION("production");

  private String value;

  private QualityLevel(String value) {
    this.value = value.toLowerCase();
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
