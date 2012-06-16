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

import java.util.LinkedList;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Nov 24, 2008
 *
 * @param <E>
 */
public class Balancer<E extends Node> {

    private LinkedList<E> pool = null; //Set of Element to balance
    private Strategy<E> strategy = null; //Balancing Policy


    /**
     * @param pool
     * @param strategy
     */
    public Balancer() {
    }

    public Balancer(BalancerStrategyType tp) {
        pool = new LinkedList<E>();
        this.strategy =  StrategyFactory.getStrategy(tp, pool);
    }

    public void addElement(E element) {
        pool.add(element);
        strategy.notifyChangeInPool();

    }

    /**
     *
     * @param element
     */

    public void removeElement(E element) {
        int index = pool.indexOf(element);
        while(index!=-1) {
            pool.remove(element);
            index = pool.indexOf(element);
        }
        strategy.notifyChangeInPool();
    }

    /**
     * Method that choose the next element.
     *
     * @return Element
     */
    public E getNextElement() throws BalancerException {
        return strategy.getNextElement();
    }


    public void setStrategy(BalancerStrategyType type) {
        this.strategy =  StrategyFactory.getStrategy(type,pool);
    }
    
    public BalancerStrategyType getStrategy() {
    	return this.strategy.getType();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BALANCER\n");
        sb.append(" STRATEGY: "+strategy+"\n");
        int index = 0;
        for(E e:pool) {
            sb.append(" NODE("+ index++ +") = ");
            sb.append(" "+e);
            //sb.append("Hostname: "+e.getHostName()+"\n");
            sb.append(" --> WEIGHT: "+e.getWeight()+"\n");
        }

        return sb.toString();
    }

}
