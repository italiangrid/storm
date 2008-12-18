package it.grid.storm.space;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 30, 2008
 * 
 */

public class SRMSpaceFactory {
    
    private static final Logger log = Logger.getLogger("space");

    

    public static SRMSpace get() {
        return null;
    }

    /**
     * @return
     */
    public static SRMSpace createDynamic(GridUserInterface user) {
        
        return null;

     
    }

    public static SRMSpace createStatic() {
        return null;
    }

}
