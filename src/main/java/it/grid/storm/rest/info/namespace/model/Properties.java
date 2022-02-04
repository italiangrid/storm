package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Properties {

  private RetentionPolicy retentionPolicy;
  private AccessLatency accessLatency;
  private ExpirationMode expirationMode;
  private TotalOnlineSize totalOnlineSize;
  private TotalNearLineSize totalNearlineSize;

  @JsonProperty("retentionPolicy")
  public RetentionPolicy getRetentionPolicy() {
    return retentionPolicy;
  }

  @JsonProperty("RetentionPolicy")
  public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
    this.retentionPolicy = retentionPolicy;
  }

  @JsonProperty("accessLatency")
  public AccessLatency getAccessLatency() {
    return accessLatency;
  }

  @JsonProperty("AccessLatency")
  public void setAccessLatency(AccessLatency accessLatency) {
    this.accessLatency = accessLatency;
  }

  @JsonProperty("expirationMode")
  public ExpirationMode getExpirationMode() {
    return expirationMode;
  }

  @JsonProperty("ExpirationMode")
  public void setExpirationMode(ExpirationMode expirationMode) {
    this.expirationMode = expirationMode;
  }

  @JsonProperty("totalOnlineSize")
  public TotalOnlineSize getTotalOnlineSize() {
    return totalOnlineSize;
  }

  @JsonProperty("TotalOnlineSize")
  public void setTotalOnlineSize(TotalOnlineSize totalOnlineSize) {
    this.totalOnlineSize = totalOnlineSize;
  }

  @JsonProperty("totalNearlineSize")
  public TotalNearLineSize getTotalNearlineSize() {
    return totalNearlineSize;
  }

  @JsonProperty("TotalNearlineSize")
  public void setTotalNearlineSize(TotalNearLineSize totalNearlineSize) {
    this.totalNearlineSize = totalNearlineSize;
  }

}
