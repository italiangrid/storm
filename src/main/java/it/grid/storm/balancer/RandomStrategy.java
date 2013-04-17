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
import java.util.Random;
import java.util.Date;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a concrete strategy to provide a Random policy. The
 * next Element is chosen in a random way.
 * 
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 16, 2008
 * 
 */

class RandomStrategy<E extends Node> extends AbstractBalancingStrategy<E> {

	private Random random = null;

	public RandomStrategy(List<E> pool) {

		super(pool);
		random = new Random((new Date()).getTime());

	}

	public E getNextElement() {

		// Return index from 0 to size-1
		int index = random.nextInt(nodePool.size());

		// Get random Node.
		return (nodePool.get(index));
	}

	public void notifyChangeInPool() {

		// TODO Auto-generated method stub

	}

}
