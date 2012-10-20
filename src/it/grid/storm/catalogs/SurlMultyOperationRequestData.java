package it.grid.storm.catalogs;

import java.util.HashMap;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.SurlStatusStore;
import it.grid.storm.synchcall.surl.UnknownTokenException;

public abstract class SurlMultyOperationRequestData extends SurlRequestData implements SynchMultyOperationRequestData
{
    
    protected final TRequestToken generatedRequestToken = TRequestToken.getRandom();

    public SurlMultyOperationRequestData(TSURL surl, TReturnStatus status)
        throws InvalidSurlRequestDataAttributesException
    {
        super(surl, status);
        if(!(this instanceof PersistentChunkData))
        {
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
        
    }
    
    private static HashMap<TSURL, TReturnStatus> buildSurlStatusMap(TSURL surl, TStatusCode code, String explanation) throws IllegalArgumentException
    {
        if(surl == null || code == null)
        {
            throw new IllegalArgumentException("Unable to build the status, null arguments: surl=" + surl + " statusCode=" + code);
        }
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(1);
        surlStatusMap.put(surl, buildStatus(code, explanation));
        return surlStatusMap;
    }

    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination)throws IllegalArgumentException, IllegalStateException
    {
        if(statusCode == null)
        {
            throw new IllegalArgumentException("Unable to build the status, null arguments: statusCode=" + statusCode);
        }
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
        if(!(this instanceof PersistentChunkData))
        {
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
    }
    
    @Override
    protected final void setStatus(TStatusCode statusCode, String explanation)
    {
        super.setStatus(statusCode, explanation);
        if(!(this instanceof PersistentChunkData))
        {
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

}
