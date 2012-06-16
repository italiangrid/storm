/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.namespace.model;

import it.grid.storm.balancer.BalancerStrategyType;

import java.util.List;
import java.util.ArrayList;

public class ProtocolPool {

  private Protocol poolType = Protocol.EMPTY;
  private BalancerStrategyType balanceStrategy;
  private List<PoolMember> poolMembers = new ArrayList<PoolMember>();

  public ProtocolPool() {
  }

  public void setBalanceStrategy(BalancerStrategyType balanceStrategy) {
    this.balanceStrategy = balanceStrategy;
  }

  public BalancerStrategyType getBalanceStrategy(){
    return this.balanceStrategy;
  }

  public void setPoolType(Protocol poolType) {
    this.poolType = poolType;
  }

  public Protocol getPoolType() {
    return this.poolType;
  }

  public void setPoolMembers(List<PoolMember> poolMembers) {
    this.poolMembers = poolMembers;
  }

  public List<PoolMember> getPoolMembers() {
    return this.poolMembers;
  }

  public void addPoolMember(PoolMember member) {
    poolMembers.add(member);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    String sep = System.getProperty("line.separator");
    sb.append(sep + "......... POOL DEFINITION ........." + sep);
    sb.append(" Balancer Strategy : "+this.balanceStrategy+sep);
    int count = 0;
    for (PoolMember member: poolMembers) {
      sb.append(" Member "+count+" = " + member + sep);
      count++;
    }
    sb.append("..................................." + sep);
    return sb.toString();
  }

}
