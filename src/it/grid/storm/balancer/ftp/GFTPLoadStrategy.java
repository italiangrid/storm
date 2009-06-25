package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.Node;
import it.grid.storm.balancer.RoundRobinStrategy;
import java.util.List;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This class implements a concrete strategy to provide a policy based on the number 
 * of active FTP processes.
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 16, 2008
 *
 */

public class GFTPLoadStrategy <E extends Node> extends RoundRobinStrategy<E> {


    public GFTPLoadStrategy(List<E> pool) {
        super(pool);
    }

    public E getNextElement() {
        //Reset index if over the pool size.
        // % Not used to avoid overflow.
        int min = -1;
        int minIndex = 0;
        for(E node:nodePool){

            //This cast should not be done here
            int ftpProc = ((FTPNode)node).getFTPProc();
            if( (ftpProc!=-1) &&
                    ((min == -1)||(ftpProc<min))) {
                min = ftpProc;
                minIndex = nodePool.indexOf(node);
            }
        }

        return nodePool.get(minIndex);

    }

    public void notifyChangeInPool() {
        //Nothing to do

    }


}
