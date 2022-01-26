package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = TotalOnlineSizeDeserializer.class)
@JsonInclude(Include.NON_NULL)
public class TotalOnlineSize {

  private UnitType unit;
  private Boolean limitedSize;
  private Long value;

  @JsonProperty("unit")
  public UnitType getUnit() {
    return unit;
  }

  @JsonProperty("unit")
  public void setUnit(UnitType unit) {
    this.unit = unit;
  }

  @JsonProperty("limitedSize")
  public Boolean getLimitedSize() {
    return limitedSize;
  }

  @JsonProperty("limited-size")
  public void setLimitedSize(Boolean limitedSize) {
    this.limitedSize = limitedSize;
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
