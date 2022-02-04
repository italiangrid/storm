package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Subject {

  private String dn;
  private String voName;

  @JsonProperty("dn")
  public String getDn() {
    return dn;
  }

  @JsonProperty("dn")
  public void setDn(String dn) {
    this.dn = dn;
  }

  @JsonProperty("voName")
  public String getVoName() {
    return voName;
  }

  @JsonProperty("vo-name")
  public void setVoName(String voName) {
    this.voName = voName;
  }


}
