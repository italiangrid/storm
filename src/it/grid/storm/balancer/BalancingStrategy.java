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

package it.grid.storm.balancer;


/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This is done thorough a strategy pattern in order to let balancers choose the
 * desired strategy at run time.
 * 
 * {@link http://en.wikipedia.org/wiki/Strategy_pattern}
 *  
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 16, 2008
 *
 */

public interface BalancingStrategy<E extends Node> {
    
    public abstract E getNextElement() throws BalancingStrategyException;
    
    public abstract BalancingStrategyType getType();
    
    public abstract void notifyChangeInPool();
}
