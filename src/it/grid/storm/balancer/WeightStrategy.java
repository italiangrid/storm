package it.grid.storm.balancer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
        
        System.out.println("list: "+weighedList);
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
