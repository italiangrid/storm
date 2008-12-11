package it.grid.storm.namespace.model;

import java.util.List;
import java.util.ArrayList;

public class ProtocolPool {

  private String balanceStrategy;
  private List<PoolMember> poolMembers = new ArrayList<PoolMember>();

  public ProtocolPool() {
  }

  public void setBalanceStrategy(String balanceStrategy) {
    this.balanceStrategy = balanceStrategy;
  }

  public String getBalanceStrategy(){
    return this.balanceStrategy;
  }

  public void addPoolMember(PoolMember member) {
    poolMembers.add(member);
  }

  public List<PoolMember> getPoolMembers() {
    return this.poolMembers;
  }

}
