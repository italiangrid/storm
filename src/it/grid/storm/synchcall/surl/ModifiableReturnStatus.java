package it.grid.storm.synchcall.surl;

import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;

class ModifiableReturnStatus extends TReturnStatus
{

    private static final long serialVersionUID = -6694381547014889754L;

    public ModifiableReturnStatus(TReturnStatus status) throws InvalidTReturnStatusAttributeException
    {
        super(status);
    }

    /**
     * @param statusCode the statusCode to set
     */
    @Override
    public void setStatusCode(TStatusCode statusCode)
    {
        super.setStatusCode(statusCode);
    }

    /**
     * Set explanation string
     * @param expl String
     */
    @Override
    public void setExplanation(String explanationString)
    {
        super.setExplanation(explanationString);
    }
    
}
