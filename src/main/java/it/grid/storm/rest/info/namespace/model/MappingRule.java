package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MappingRule {

  private String name;
  private String stfnRoot;
  private String mappedFs;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("stfnRoot")
  public String getStfnRoot() {
    return stfnRoot;
  }

  @JsonProperty("stfn-root")
  public void setStfnRoot(String stfnRoot) {
    this.stfnRoot = stfnRoot;
  }

  @JsonProperty("mappedFs")
  public String getMappedFs() {
    return mappedFs;
  }

  @JsonProperty("mapped-fs")
  public void setMappedFs(String mappedFs) {
    this.mappedFs = mappedFs;
  }

}
