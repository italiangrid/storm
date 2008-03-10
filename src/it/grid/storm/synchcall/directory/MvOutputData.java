/**
 * This class represents the Mv Output Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import java.util.Vector;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;

public class MvOutputData {

    private TReturnStatus returnStatus = null;

    public MvOutputData()
    {

    }


    public MvOutputData(TReturnStatus retStatus) throws InvalidMvOutputAttributeException
    {
    	boolean ok = (retStatus==null);
    	if (!ok) {
    	    throw new InvalidMvOutputAttributeException(retStatus);
    	}
    	this.returnStatus = retStatus;
    }


    /**
     * Method that return Status.
     */

    public TReturnStatus getStatus()
    {
        return returnStatus;
    }

    /**
     * Set ReturnStatus
     *
     */
    public void setStatus(TReturnStatus retStat)
    {
        this.returnStatus = retStat;
    }



}
