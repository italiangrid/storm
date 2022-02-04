package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Quota {

  private Boolean enabled;
  private String device;
  private Object quotaElement;

  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  @JsonProperty("enabled")
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @JsonProperty("device")
  public String getDevice() {
    return device;
  }

  @JsonProperty("device")
  public void setDevice(String device) {
    this.device = device;
  }

  @JsonProperty("quotaElement")
  public Object getQuotaElement() {
    return quotaElement;
  }

  @JsonProperty("quotaElement")
  public void setQuotaElement(Object quotaElement) {
    this.quotaElement = quotaElement;
  }


}
