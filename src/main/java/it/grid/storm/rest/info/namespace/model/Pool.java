package it.grid.storm.rest.info.namespace.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Pool {

  private BalanceStrategy balanceStrategy;
  private List<Member> members;

  @JsonProperty("balanceStrategy")
  public BalanceStrategy getBalanceStrategy() {
    return balanceStrategy;
  }

  @JsonProperty("balance-strategy")
  public void setBalanceStrategy(BalanceStrategy balanceStrategy) {
    this.balanceStrategy = balanceStrategy;
  }

  @JsonProperty("members")
  public List<Member> getMembers() {
    return members;
  }

  @JsonProperty("members")
  public void setMembers(List<Member> members) {
    this.members = members;
  }
  
  
}
