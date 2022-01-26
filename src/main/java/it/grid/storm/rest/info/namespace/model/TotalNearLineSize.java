package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TotalNearLineSizeDeserializer.class)
@JsonInclude(Include.NON_NULL)
public class TotalNearLineSize {

  private UnitType unit;
  private Long value;

  @JsonProperty("unit")
  public UnitType getUnit() {
    return unit;
  }

  @JsonProperty("unit")
  public void setUnit(UnitType unit) {
    this.unit = unit;
  }

  @JsonProperty("value")
  public Long getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(Long value) {
    this.value = value;
  }
}
