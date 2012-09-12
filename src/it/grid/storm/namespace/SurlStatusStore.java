package it.grid.storm.namespace;

import java.util.HashMap;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public class SurlStatusStore
{
    private HashMap<Integer,TStatusCode> store = new HashMap<Integer,TStatusCode>(); 

    private static final SurlStatusStore instance =  new SurlStatusStore();
    
    private SurlStatusStore(){};
    
    public synchronized void storeSurlStatus(TSURL surl, TStatusCode status) throws IllegalArgumentException
    {
        if(surl == null || status == null)
        {
            throw new IllegalArgumentException("Unable to store surl status. Received" +
            		"null arguments: surl=" + surl + " status=" + status);
        }
        store.put(surl.uniqueId(), status);
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
        return store.get(surl.uniqueId());
    }

    public static SurlStatusStore getInstance()
    {
        return instance;
    }

}
