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

public interface Strategy<E extends Node> {
    
    
    public abstract E getNextElement();
    
    public abstract StrategyType getType();
    
    public abstract void notifyChangeInPool();
    
    

}
