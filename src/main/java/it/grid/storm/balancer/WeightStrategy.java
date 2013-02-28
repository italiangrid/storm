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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a concrete strategy to provide a RoundRobin policy.
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 16, 2008
 *
 */

public class WeightStrategy<E extends Node> extends RoundRobinStrategy<E> {
    
    //Contains a list of index/key with replica depending on weight
    private ArrayList<Integer> weighedList = null;
    
    private int ind=0;
    
    public WeightStrategy(List<E> pool) {
        super(pool);
        initializeList();
    }
    
    private void initializeList() {
        
        weighedList = new ArrayList<Integer>();
        
        for(Node node:nodePool) {
            int weight= node.getWeight();
            for(int i=0;i<=weight;i++) {
                weighedList.add(nodePool.indexOf(node));
            }
        }
    }

    public E getNextElement() {
        //Loop over the weighted list of index
        
        ind = (ind >= weighedList.size()) ? 0 : ind ;
        return nodePool.get(weighedList.get(ind++));   
    }

    public void notifyChangeInPool() {
        initializeList();
    }
}
