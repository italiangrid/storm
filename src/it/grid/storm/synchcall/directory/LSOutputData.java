/**
 * This class represents the LS Output Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.directory;

import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;

public class LSOutputData
{

    private TReturnStatus returnStatus = null;
    private TRequestToken requestToken = null;
    private ArrayOfTMetaDataPathDetail details = null;

    public LSOutputData()
    {
    }

    public LSOutputData(TReturnStatus retStatus, TRequestToken token, ArrayOfTMetaDataPathDetail details) throws InvalidLSOutputAttributeException
    {
        boolean ok = (details == null);

        if (!ok) throw new InvalidLSOutputAttributeException(details);

        this.returnStatus = retStatus;
        this.requestToken = token;
        this.details = details;

    }

    /**
     * Method that get return Status.
     */
    public TReturnStatus getStatus()
    {
        return returnStatus;
    }

    /**
     * Set ReturnStatus
     */
    public void setStatus(TReturnStatus retStat)
    {
        this.returnStatus = retStat;
    }

    /**
     * Method that get return Status.
     */
    public TRequestToken getRequestToken()
    {
        return this.requestToken;
    }

    /**
     * Set TRequestToken
     */
    public void setRequestToken(TRequestToken token)
    {
        this.requestToken = token;
    }

    /**
     * Method that return ArrayOfTMetaDataPath.
     */
    public ArrayOfTMetaDataPathDetail getDetails()
    {
        return details;
    }

    /**
     * Set ArrayOfTMetaDataPath
     */
    public void setDetails(ArrayOfTMetaDataPathDetail details)
    {
        this.details = details;
    }
}
