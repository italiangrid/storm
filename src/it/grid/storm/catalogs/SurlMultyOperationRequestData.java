package it.grid.storm.catalogs;

import java.util.HashMap;
import it.grid.storm.namespace.SurlStatusStore;
import it.grid.storm.namespace.UnknownTokenException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public abstract class SurlMultyOperationRequestData extends SurlRequestData implements SynchMultyOperationRequestData
{
    
    protected final TRequestToken generatedRequestToken = TRequestToken.getRandom();

    public SurlMultyOperationRequestData(TSURL surl, TReturnStatus status)
        throws InvalidSurlRequestDataAttributesException
    {
        super(surl, status);
        try
        {
            SurlStatusStore.getInstance().store(generatedRequestToken,
                                                buildSurlStatusMap(surl, status.getStatusCode(),
                                                                   status.getExplanation()));
        } catch(IllegalArgumentException e)
        {
            throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
        }
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code, String explanation)
    {
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        surlStatusMap.put(surl, buildStatus(code, explanation));
        return surlStatusMap;
    }

    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination)
            throws IllegalStateException
    {
        try
        {
            return new TReturnStatus(statusCode, explaination);
        } catch(InvalidTReturnStatusAttributeException e1)
        {
            // Never thrown
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                    + "in building TReturnStatus: " + e1.getMessage());
        }
    }

    @Override
    public TRequestToken getGeneratedRequestToken()
    {
        return generatedRequestToken;
    }
    
    /**
     * Method used to set the Status associated to this chunk.
     * If status is null, then nothing gets set!
     */
    @Override
    public final void setStatus(TReturnStatus status)
    {
        super.setStatus(status);
        try
        {
            if(status.getExplanation() == null)
            {
                SurlStatusStore.getInstance().update(generatedRequestToken, this.SURL, status.getStatusCode());
            }
            else
            {
                SurlStatusStore.getInstance().update(generatedRequestToken, this.SURL, status.getStatusCode(),
                                                     status.getExplanation());
            }
        } catch(IllegalArgumentException e)
        {
         // Never thrown
            throw new IllegalStateException("Unexpected IllegalArgumentException "
                    + "in updating status store: " + e.getMessage());
        } catch(UnknownTokenException e)
        {
         // Never thrown
            throw new IllegalStateException("Unexpected UnknownTokenException "
                    + "in updating status store: " + e.getMessage());
        }
    }
    
    @Override
    protected final void setStatus(TStatusCode statusCode, String explanation)
    {
        super.setStatus(statusCode, explanation);
        try
        {
            if(explanation == null)
            {
                SurlStatusStore.getInstance().update(generatedRequestToken, this.SURL, statusCode);
            }
            else
            {
                SurlStatusStore.getInstance().update(generatedRequestToken, this.SURL, statusCode,
                                                     explanation);
            }
        } catch(IllegalArgumentException e)
        {
         // Never thrown
            throw new IllegalStateException("Unexpected IllegalArgumentException "
                    + "in updating status store: " + e.getMessage());
        } catch(UnknownTokenException e)
        {
         // Never thrown
            throw new IllegalStateException("Unexpected UnknownTokenException "
                    + "in updating status store: " + e.getMessage());
        }
    }

}
