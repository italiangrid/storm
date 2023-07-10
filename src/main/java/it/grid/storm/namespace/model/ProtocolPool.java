/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import com.google.common.collect.Lists;
import it.grid.storm.balancer.BalancingStrategyType;
import java.util.List;

public class ProtocolPool {

  private final Protocol poolType;
  private final BalancingStrategyType balanceStrategy;
  private final List<PoolMember> poolMembers = Lists.newArrayList();

  public ProtocolPool(Protocol protocol, BalancingStrategyType strategy, List<PoolMember> members) {

    this.poolType = protocol;
    this.balanceStrategy = strategy;
    this.poolMembers.addAll(members);
  }

  public ProtocolPool(BalancingStrategyType strategy, List<PoolMember> members) {

    this(members.get(0).getMemberProtocol().getProtocol(), strategy, members);
  }

  public BalancingStrategyType getBalanceStrategy() {

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
  public String toString() {

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
