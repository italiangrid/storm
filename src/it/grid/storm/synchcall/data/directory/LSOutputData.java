package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the LS Output Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class LSOutputData implements OutputData
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

    //@Override
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }
}
