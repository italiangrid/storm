/**
 * This is the Abort executor for a Copy request. 
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.dataTransfer;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.catalogs.*;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.griduser.CannotMapUserException;

public class CopyAbortExecutor implements AbortExecutorInterface {
    
    private static final Logger log = Logger.getLogger("dataTransfer");
    
    public CopyAbortExecutor() {};
    
    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData) {
        
        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        boolean requestFailure, requestSuccess;
        TReturnStatus globalStatus = null;

        log.info("SrmAbortRequest: Started AbortCopyExecutor");

        return outputData;
        }
}