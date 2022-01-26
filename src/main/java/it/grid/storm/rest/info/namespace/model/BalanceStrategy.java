package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BalanceStrategy {

  ROUND_ROBIN("round-robin"), RANDOM("random"), WEIGHT("weight"), SMART_ROUND_ROBIN("smart-rr");

  private String value;

  private BalanceStrategy(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
