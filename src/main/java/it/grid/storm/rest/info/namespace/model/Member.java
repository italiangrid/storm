package it.grid.storm.rest.info.namespace.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Member {

  private Integer memberId;
  private Integer weight;

  @JsonProperty("memberId")
  public Integer getMemberId() {
    return memberId;
  }

  @JsonProperty("member-id")
  public void setMemberId(Integer memberId) {
    this.memberId = memberId;
  }

  @JsonProperty("weight")
  public Integer getWeight() {
    return weight;
  }

  @JsonProperty("weight")
  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  

}
