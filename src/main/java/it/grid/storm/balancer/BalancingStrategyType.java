package it.grid.storm.balancer;

import java.util.EnumSet;

public enum BalancingStrategyType {

	RANDOM("random", Weighted.NOWEIGHT), ROUNDROBIN("round-robin",
		Weighted.NOWEIGHT), WEIGHT("weight", Weighted.WEIGHTED), SMART_RR(
		"smart-rr", Weighted.NOWEIGHT);

	private enum Weighted {
		WEIGHTED, NOWEIGHT
	};

	private String name;
	private boolean withWeight;

	BalancingStrategyType(String name, Weighted w) {

		this.name = name;
		if (w == Weighted.WEIGHTED)
			withWeight = true;
		else
			withWeight = false;
	}

	@Override
	public String toString() {

		return name;
	}

	public boolean requireWeight() {

		return this.withWeight;
	}

	public static BalancingStrategyType getByValue(String name) {

		for (final BalancingStrategyType element : EnumSet
			.allOf(BalancingStrategyType.class)) {
			if (element.toString().equals(name)) {
				return element;
			}
		}
		return null;
	}
}
