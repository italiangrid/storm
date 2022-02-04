package it.grid.storm.rest.info.namespace.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"noNamespaceSchemaLocation"})
public class Namespace {

  private String version;
  private List<Filesystem> filesystems;
  private List<MappingRule> mappingRules;
  private List<ApproachableRule> approachableRules;

  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }

  @JsonProperty("filesystems")
  public List<Filesystem> getFilesystems() {
    return filesystems;
  }

  @JsonProperty("filesystems")
  public void setFilesystems(List<Filesystem> filesystems) {
    this.filesystems = filesystems;
  }

  @JsonProperty("mappingRules")
  public List<MappingRule> getMappingRules() {
    return mappingRules;
  }

  @JsonProperty("mapping-rules")
  public void setMappingRules(List<MappingRule> mappingRules) {
    this.mappingRules = mappingRules;
  }

  @JsonProperty("approachableRules")
  public List<ApproachableRule> getApproachableRules() {
    return approachableRules;
  }

  @JsonProperty("approachable-rules")
  public void setApproachableRules(List<ApproachableRule> approachableRules) {
    this.approachableRules = approachableRules;
  }

  
}
