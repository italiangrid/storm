package it.grid.storm.namespace;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public class SurlStatusStore
{
    private final ReentrantLock writeLock = new ReentrantLock(true);
    private HashMap<Integer,SurlStatus> store = new HashMap<Integer,SurlStatus>(); 

    private static final SurlStatusStore instance =  new SurlStatusStore();
    
    private SurlStatusStore(){};
    
    public synchronized void storeSurlStatus(TSURL surl, TStatusCode status) throws IllegalArgumentException
    {
        if(surl == null || status == null)
        {
            throw new IllegalArgumentException("Unable to store surl status. Received" +
            		"null arguments: surl=" + surl + " status=" + status);
        }
        store.put(surl.uniqueId(), new SurlStatus(status));
    }

    public synchronized void forgetSurl(TSURL surl)
    {
        if(surl == null)
        {
            throw new IllegalArgumentException("Unable to forget surl. Received" +
                    "null arguments: surl=" + surl);
        }
        store.remove(surl.uniqueId());
    }

    public synchronized TStatusCode getSurlStatus(TSURL surl)
    {
        return store.get(surl.uniqueId()).statusCode;
    }

    public static SurlStatusStore getInstance()
    {
        return instance;
    }

    public boolean testAndSet(TSURL surl, TStatusCode expectedStatusCode, TStatusCode newStatusCode)
    {
        SurlStatus status = store.get(surl.uniqueId());
        if(status != null)
        {
            synchronized (status)
            {
                if(status.statusCode.equals(expectedStatusCode))
                {
                    status.statusCode = newStatusCode; 
                    return true;
                }
            }            
        }
        return false;
    }

    public boolean testNewAndSet(TSURL surl, TStatusCode newStatusCode)
    {
        try
        {
            writeLock.lock();
            SurlStatus status = store.get(surl.uniqueId());
            if (status == null)
            {
                store.put(surl.uniqueId(), new SurlStatus(newStatusCode));
                return true;
            }
        }
        finally
        {
            writeLock.unlock();
        }
        return false;
    }
    
    
    private class SurlStatus
    {
        TStatusCode statusCode;
        
        SurlStatus(TStatusCode statusCode)
        {
            this.statusCode = statusCode;
        }
    }

}
