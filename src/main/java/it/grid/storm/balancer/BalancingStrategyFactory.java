/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.balancer;

import java.util.List;

public class BalancingStrategyFactory {

	public static <E extends Node> BalancingStrategy<E> getBalancingStrategy(
		BalancingStrategyType type, List<E> pool) throws IllegalArgumentException {

		switch (type) {
		case RANDOM:
			return new RandomStrategy<E>(pool);
		case ROUNDROBIN:
			return new RoundRobinStrategy<E>(pool);
		case WEIGHT:
			return new WeightStrategy<E>(pool);
		case SMART_RR:
			return new SmartRoundRobinStrategy<E>(pool);
		}
		throw new IllegalArgumentException(
			"StrategyFactory: Unknown BalancingStrategyType: " + type);
	}

}
