package it.grid.storm.namespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public class SurlStatusStore
{
    
    private static final Logger log = LoggerFactory.getLogger(SurlStatusStore.class);
    
//    private final ReentrantLock writeLock = new ReentrantLock(true);
    private HashMap<Integer, Map<TRequestToken, TReturnStatus>> surlTokenStatusStore = new HashMap<Integer, Map<TRequestToken, TReturnStatus>>();
    
    private HashMap<TRequestToken, Map<Integer, TReturnStatus>> tokenSurlStatusStore = new HashMap<TRequestToken, Map<Integer, TReturnStatus>>();

    private static final SurlStatusStore instance =  new SurlStatusStore();
    
    private SurlStatusStore(){};
    
//    public synchronized void storeSurlStatus(TSURL surl, TStatusCode status) throws IllegalArgumentException
//    {
//        if(surl == null || status == null)
//        {
//            throw new IllegalArgumentException("Unable to store surl status. Received" +
//            		"null arguments: surl=" + surl + " status=" + status);
//        }
//        store.put(surl.uniqueId(),status);
//    }

//    public synchronized void forgetSurl(TSURL surl)
//    {
//        if(surl == null)
//        {
//            throw new IllegalArgumentException("Unable to forget surl. Received" +
//                    "null arguments: surl=" + surl);
//        }
//        store.remove(surl.uniqueId());
//    }

//    public synchronized TStatusCode getSurlStatus(TSURL surl)
//    {
//        return store.get(surl.uniqueId());
//    }

    public static SurlStatusStore getInstance()
    {
        return instance;
    }

//    public boolean testAndSet(TSURL surl, TStatusCode expectedStatusCode, TStatusCode newStatusCode)
//    {
//        if(true)
//        {
//            throw new IllegalStateException();    
//        }
//        //da rifare!!
//        TStatusCode status = store.get(surl.uniqueId());
//        if(status != null)
//        {
//            synchronized (status)
//            {
//                if(status.equals(expectedStatusCode))
//                {
//                    store.put(surl.uniqueId(), newStatusCode); 
//                    return true;
//                }
//            }            
//        }
//        return false;
//    }

//    public boolean testNewAndSet(TSURL surl, TStatusCode newStatusCode)
//    {
//        try
//        {
//            writeLock.lock();
//            TStatusCode status = store.get(surl.uniqueId());
//            if (status == null)
//            {
//                store.put(surl.uniqueId(),newStatusCode);
//                return true;
//            }
//        }
//        finally
//        {
//            writeLock.unlock();
//        }
//        return false;
//    }
    
    
//    private class SurlStatus
//    {
//        TStatusCode statusCode;
//        
//        SurlStatus(TStatusCode statusCode)
//        {
//            this.statusCode = statusCode;
//        }
//    }
    
    /**
     * @param requestToken
     * @param surlStatuses
     * @throws IllegalArgumentException
     * @throws UnknownTokenException 
     */
    public void store(TRequestToken requestToken, HashMap<TSURL, TReturnStatus> surlStatuses)
            throws IllegalArgumentException
    {
        if (requestToken == null || surlStatuses == null || surlStatuses.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surlStatuses=" + surlStatuses);
        }
        if(tokenSurlStatusStore.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is already stored");
            throw new IllegalArgumentException("Provided token \'" + requestToken + "\'is already stored");
        }
        log.debug("Storing token " + requestToken);
        Map<Integer, TReturnStatus> surlStatusMap = new HashMap<Integer, TReturnStatus>(surlStatuses.size());
        for (Entry<TSURL, TReturnStatus> surlStatus : surlStatuses.entrySet())
        {
            log.debug("Storing surl " + surlStatus.getKey() + " with status " + surlStatus.getValue()
                    + " for token " + requestToken);
            TReturnStatus status = surlStatus.getValue().clone();
            Integer surlUid = surlStatus.getKey().uniqueId();
            surlStatusMap.put(surlUid, status);
            Map<TRequestToken, TReturnStatus> map = surlTokenStatusStore.get(surlUid);
            if (map == null)
            {
                map = new HashMap<TRequestToken, TReturnStatus>(1);
                surlTokenStatusStore.put(surlUid, map);
            }
            map.put(requestToken, status);
        }
        tokenSurlStatusStore.put(requestToken, surlStatusMap);
    }
    
    public void update(TRequestToken requestToken, TSURL surl,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        update(requestToken, surl, newStatusCode, "");
    }
    
    public void update(TRequestToken requestToken, TSURL surl,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException, UnknownTokenException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the statuse, "
                    + "null arguments: surl=" + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        update(requestToken, surlList, newStatusCode, explanation);
    }
    
    public void update(TRequestToken requestToken, List<TSURL> surls,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        update(requestToken, surls, newStatusCode, "");
    }
    
    public void update(TRequestToken requestToken, List<TSURL> surls,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException, UnknownTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty()
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " newStatusCode=" + newStatusCode + " explanation=" + explanation);
        }
        log.debug("Updating token " + requestToken + " for surls " + surls + " with status " + newStatusCode
                + " " + explanation);
        checkToken(requestToken);
        Map<Integer, TReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
        for(TSURL surl : surls)
        {
            if(surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            TReturnStatus status = surlStatusMap.get(surl.uniqueId());
            if(status == null)
            {
                log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
            }
            else
            {
                status.setStatusCode(newStatusCode);
                status.setExplanation(explanation);
            }
        }
    }


    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        checkAndUpdate(requestToken, surl, expectedStatusCode, newStatusCode, "");
    }
    
    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException, UnknownTokenException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the statuse, "
                    + "null arguments: surl=" + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        checkAndUpdate(requestToken, surlList, expectedStatusCode, newStatusCode, explanation);
    }
    
    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        checkAndUpdate(requestToken, surls, expectedStatusCode, newStatusCode, "");
    }
    
    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException, UnknownTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode + " explanation=" + explanation);
        }
        log.debug("Checking and pdating token " + requestToken + " for surls " + surls + " where status is "
                + expectedStatusCode + " with status " + newStatusCode + " " + explanation);
        checkToken(requestToken);
        Map<Integer, TReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
        for(TSURL surl : surls)
        {
            if(surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            TReturnStatus status = surlStatusMap.get(surl.uniqueId());
            if(status == null)
            {
                log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
            }
            else
            {
                if(status.getStatusCode().equals(expectedStatusCode))
                {
                    status.setStatusCode(newStatusCode);
                    status.setExplanation(explanation);
                }
                else
                {
                    log.warn("Surl status not updated, current status \'" + status.getStatusCode()
                            + "\' doesn't match expected status \'" + expectedStatusCode + "\'");
                }
            }
        }
    }

    /**
     * @param requestToken
     * @param surls
     * @return
     * @throws UnknownTokenException
     */
    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, List<TSURL> surls)
            throws UnknownTokenException
    {
        if(requestToken == null || surls == null || surls.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surls=" + surls);
        }
        log.debug("Retrieving statuses of surls " + surls + " for token " + requestToken);
        checkToken(requestToken);
        HashMap<TSURL, TReturnStatus> surlStatusMap = new HashMap<TSURL, TReturnStatus>(surls.size()); 
        for(TSURL surl : surls)
        {
            if(surl == null)
            {
                log.warn("Unexpected null element in input surls list");
                continue;
            }
            Map<TRequestToken, TReturnStatus> tokenStatusMap = this.surlTokenStatusStore.get(surl.uniqueId());
            if(tokenStatusMap == null)
            {
                log.warn("Surl \'" + surl + "\' is not stored");
            }
            else
            {
                TReturnStatus status = tokenStatusMap.get(requestToken);
                if(status == null)
                {
                    log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
                }
                else
                {
                    surlStatusMap.put(surl, status.clone());
                }
            }
        }
        return surlStatusMap;
    }
    
    public List<TReturnStatus> getSurlsStatuses(TSURL surl) throws UnknownSurlException, IllegalArgumentException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the statuses, null arguments: surl=" + surl);
        }
        log.debug("Retrieving statuses of surl " + surl);
        Map<TRequestToken, TReturnStatus> tokensStatusMap = surlTokenStatusStore.get(surl.uniqueId());
        if(tokensStatusMap == null)
        {
            throw new UnknownSurlException("Surl " + surl + " is not stored");
        }
        ArrayList<TReturnStatus> statuses = new ArrayList<TReturnStatus>(tokensStatusMap.size());
        for(TReturnStatus status :tokensStatusMap.values())
        {
            statuses.add(status.clone());
        }
        return statuses;
    }
    
    private void checkToken(TRequestToken requestToken) throws UnknownTokenException
    {
        if(!tokenSurlStatusStore.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is not stored");
            throw new UnknownTokenException("Provided token \'" + requestToken + "\'is not stored");
        }
    }

}
