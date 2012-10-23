package it.grid.storm.synchcall.surl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TStatusCode;

class ModifiableReturnStatus extends TReturnStatus
{

    private static final long serialVersionUID = -6694381547014889754L;
    private ReentrantLock statusLock = new ReentrantLock();
    

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
        statusLock.lock();
        super.setStatusCode(statusCode);
        statusLock.unlock();
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
    
    public boolean testAndSetStatusCodeExplanation(TStatusCode expectedStatusCode, TStatusCode statusCode, String explanation)
    {
        try
        {
        statusLock.lock();
        if(expectedStatusCode.equals(statusCode))
        {
            super.setStatusCode(statusCode);
            super.setExplanation(explanation); 
            return true;
        }
        else{return false;}
        
        }finally{statusLock.unlock();}
        
    }
    
}
