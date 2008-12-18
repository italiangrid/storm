package it.grid.storm.balancer;

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

public class RoundRobinStrategy <E extends Node> extends AbstractStrategy<E> {
    
    private int index = 0;
   
    public RoundRobinStrategy(List<E> pool) { 
        super(pool);
    }    
        
    public E getNextElement() {
        //Reset index if over the pool size.
        // % Not used to avoid overflow.
        
        index = (index >= nodePool.size()) ? 0 : index ;
        
        return (nodePool.get(index++)); 

    }

    public void notifyChangeInPool() {
        //Nothing to do
        
    }
    

}
