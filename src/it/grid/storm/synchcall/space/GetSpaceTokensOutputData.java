/**
 * GetSpaceTokens output data.
 *
 * @author  Alberto Forti
 * @author  CNAF Bologna
 * @date    November 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;

public class GetSpaceTokensOutputData
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
}
