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

import it.grid.storm.balancer.BalancingStrategyType;

import java.util.List;
import java.util.ArrayList;

public class ProtocolPool {

  private final Protocol poolType;
  private final BalancingStrategyType balanceStrategy;
  private final List<PoolMember> poolMembers = new ArrayList<PoolMember>();

    public ProtocolPool(Protocol protocol, BalancingStrategyType strategy, List<PoolMember> members)
    {
        this.poolType = protocol;
        this.balanceStrategy = strategy;
        this.poolMembers.addAll(members);
    }

    public ProtocolPool(BalancingStrategyType strategy, List<PoolMember> members)
    {
        this(members.get(0).getMemberProtocol().getProtocol(), strategy, members);
    }

  public BalancingStrategyType getBalanceStrategy(){
    return this.balanceStrategy;
  }

  public Protocol getPoolType() {
    return this.poolType;
  }

  public List<PoolMember> getPoolMembers() {
    return this.poolMembers;
  }

  public void addPoolMember(PoolMember member) {
    poolMembers.add(member);
  }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ProtocolPool [poolType=");
        builder.append(poolType);
        builder.append(", balanceStrategy=");
        builder.append(balanceStrategy);
        builder.append(", poolMembers=");
        builder.append(poolMembers);
        builder.append("]");
        return builder.toString();
    }

}
