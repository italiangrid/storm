package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * GetSpaceTokens output data.
 *
 *
 * @author lucamag
 * @author  Alberto Forti
 * 
 * @date May 29, 2008
 *
 */
public class GetSpaceTokensOutputData implements OutputData
{
    private TReturnStatus status = null;
    private ArrayOfTSpaceToken arrayOfSpaceTokens = null;

    public GetSpaceTokensOutputData() { }

    public GetSpaceTokensOutputData(TReturnStatus status, ArrayOfTSpaceToken arrayOfSpaceTokens)
    {
        this.status = status;
        this.arrayOfSpaceTokens = arrayOfSpaceTokens;
    }

    /**
     * Returns the status.
     */
    public TReturnStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     */
    public void setStatus(TReturnStatus status)
    {
        this.status = status;
    }

    /**
     * Returns arrayOfSpaceTokens.
     */
    public ArrayOfTSpaceToken getArrayOfSpaceTokens()
    {
        return this.arrayOfSpaceTokens;
    }

    /**
     * Sets arrayOfSpaceTokens.
     */
    public void setArrayOfSpaceTokens(ArrayOfTSpaceToken arrayOfSpaceTokens)
    {
        this.arrayOfSpaceTokens = arrayOfSpaceTokens;
    }

    //@Override
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }
}
