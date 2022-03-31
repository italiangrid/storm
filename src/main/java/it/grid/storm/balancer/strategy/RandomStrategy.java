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

package it.grid.storm.balancer.strategy;

import static it.grid.storm.balancer.BalancingStrategyType.RANDOM;

import java.util.Date;
import java.util.List;
import java.util.Random;

import it.grid.storm.balancer.Node;

public class RandomStrategy<E extends Node> extends AbstractBalancingStrategy<E> {

  private final Random random;

  public RandomStrategy(List<E> nodes) {
    super(nodes);
    setType(RANDOM);
    random = new Random((new Date()).getTime());
  }

  public E getNextElement() {
    // Return index from 0 to size-1
    int index = random.nextInt(getNodePool().size());
    // Get random Node.
    return (getNodePool().get(index));
  }
}
