package it.grid.storm.common;

import it.grid.storm.scheduler.ChunkType;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */


public enum OperationType {
    UNDEF,
    PTG,PTP,COPY,BOL,
    AF, AR, EFL, GSM, GST, LS, 
    MKD, MV, PNG, PD, RF, RESSP, RELSP, RM, RMD;

  
boolean isSynchronousOperation() {
    
   switch(this) {
        case PTG: return false; 
        case PTP: return false; 
        case COPY: return false; 
        case BOL: return false; 
        default: return true;
    }

    }



}
