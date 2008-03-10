package it.grid.storm.synchcall.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.srm.types.TSizeInBytes;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidReserveSpaceInputDataAttributesException extends Exception {

    private boolean nullAuth = true;
    private boolean nullSpaceDes = true;
    private boolean nullRetentionPolicyInfo = true;

    public InvalidReserveSpaceInputDataAttributesException(GridUserInterface guser,
                                                  TSizeInBytes spaceDes,
                                                  TRetentionPolicyInfo retentionPolicyInfo)
    {
        nullAuth = (guser == null);
        nullSpaceDes = (spaceDes == null);
        nullRetentionPolicyInfo = (retentionPolicyInfo == null);
    }


    public String toString()
    {
        return "The Problem is: null-Auth= " + nullAuth
                + ", nullSpaceDesired= " + nullSpaceDes
                + ", nullRetentionPolicyInfo= " + nullRetentionPolicyInfo;
    }
}
