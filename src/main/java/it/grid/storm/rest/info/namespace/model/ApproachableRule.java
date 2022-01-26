package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ApproachableRule {

  private String name;
  private Subject subjects;
  private String approachableFs;
  private Boolean anonymousHttpRead;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("subjects")
  public Subject getSubjects() {
    return subjects;
  }

  @JsonProperty("subjects")
  public void setSubjects(Subject subjects) {
    this.subjects = subjects;
  }

  @JsonProperty("approachableFs")
  public String getApproachableFs() {
    return approachableFs;
  }

  @JsonProperty("approachable-fs")
  public void setApproachableFs(String approachableFs) {
    this.approachableFs = approachableFs;
  }

  @JsonProperty("anonymousHttpRead")
  public Boolean getAnonymousHttpRead() {
    return anonymousHttpRead;
  }

  @JsonProperty("anonymous-http-read")
  public void setAnonymousHttpRead(Boolean anonymousHttpRead) {
    this.anonymousHttpRead = anonymousHttpRead;
  }


}
