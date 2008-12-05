package it.grid.storm.balancer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

public abstract class AbstractStrategy<E extends Node> implements Strategy<E>{
    
    protected StrategyType type;
    protected LinkedList<E> nodePool = null;
    
    public AbstractStrategy(List<E> pool) { 
        nodePool = (LinkedList<E>) pool;
        
  }
    
    public StrategyType getType() {
        return type;
    }
    
    public String toString() {
        return this.getClass().getName();
    }

}
