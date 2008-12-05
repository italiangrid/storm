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
        //Base strategy
        this(StrategyType.ROUNDROBIN);
    }
    
    public Balancer(StrategyType tp) {
        pool = new LinkedList<E>();
        this.strategy =  StrategyFactory.getStrategy(tp, pool);
    }

    public void addElement(E element) {
        //Crea nuovo elemento con peso 0
        //element.setWeight(0);
        pool.add(element);
        strategy.notifyChangeInPool();
        
    }
        
    /**
     * 
     * Peso compreso da 1 a 10?
     * 
     * @param element
     * @param weight
     */
    private void addElementWithWeight(E element, int weight) {
        element.setWeight(weight);
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
    public E getNextElement() {
        return strategy.getNextElement();
    }

     
    public void setStrategy(StrategyType type) {
        this.strategy =  StrategyFactory.getStrategy(type,pool);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Balancer\n");
        sb.append("Strategy: "+strategy+"\n");
        sb.append("Node pool:\n");
        int index = 0;
        for(E e:pool) {
            sb.append("Node: "+ index++ +"\n");
            //sb.append("Hostname: "+e.getHostName()+"\n");        
            sb.append("Weight: "+e.getWeight()+"\n");
        }
        
        return sb.toString();
    }
    
}
