package it.grid.storm.balancer;

import java.util.List;
import java.util.Random;
import java.util.Date;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a concrete strategy to provide a Random policy.
 * The next Element is chosen in a random way. 
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 16, 2008
 *
 */

 class RandomStrategy<E extends Node> extends  AbstractStrategy<E> {
   

    private Random random = null;
    

    public RandomStrategy(List<E> pool) { 
          super(pool);
          random = new Random((new Date()).getTime());
          
          
    }
    

    public  E getNextElement() {
        
        //  Return index from 0 to size-1
        int index = random.nextInt(nodePool.size());
        
        //Get random Node. 
        return (nodePool.get(index));
    }

    public void notifyChangeInPool() {
        // TODO Auto-generated method stub
        
    }

    

}
