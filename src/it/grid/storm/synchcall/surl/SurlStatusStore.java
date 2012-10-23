package it.grid.storm.synchcall.surl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

/**
 * @author Michele Dibenedetto
 *
 */
public class SurlStatusStore
{

    private static final Logger log = LoggerFactory.getLogger(SurlStatusStore.class);

    private HashMap<TSURL, Map<TRequestToken, ModifiableReturnStatus>> surlTokenStatusStore = new HashMap<TSURL, Map<TRequestToken, ModifiableReturnStatus>>();

    private HashMap<TRequestToken, Map<TSURL, ModifiableReturnStatus>> tokenSurlStatusStore = new HashMap<TRequestToken, Map<TSURL, ModifiableReturnStatus>>();

    private ConcurrentSkipListMap<Long, TRequestToken> expirationTokenRegistry = new ConcurrentSkipListMap<Long, TRequestToken>();

    private static final SurlStatusStore instance = new SurlStatusStore();

    
    private final ConcurrentHashMap<TSURL,ReentrantReadWriteLock> surlLockMap = new ConcurrentHashMap<TSURL,ReentrantReadWriteLock>();
    private final ConcurrentHashMap<TRequestToken,ReentrantReadWriteLock> tokenLockMap = new ConcurrentHashMap<TRequestToken,ReentrantReadWriteLock>();
    
    private final ReentrantLock expirationTokenRegistryLock = new ReentrantLock(); 
    private final Timer clock;
    private final TimerTask clockTask;
    
    private SurlStatusStore()
    {
        if (Configuration.getInstance().getExpiredRequestPurging())
        {
            clock = new Timer();
            clockTask = new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        try{
                        lockExpirationMap();  
                        for(TRequestToken token : getExpiredTokens())
                        {
                            log.debug("Cleaning expired token " + token);
                            try
                            {
                                cleanToken(token);
                            } catch(UnknownTokenException e)
                            {
                                throw new IllegalStateException("Unexpected UnknownTokenException in cleanToken: "
                                                                + e.getMessage());
                            }
                        }
                        }finally{unlockExpirationMap();}
                        
                    }
                };
            clock.scheduleAtFixedRate(clockTask, Configuration.getInstance().getRequestPurgerDelay() * 1000,
                                      Configuration.getInstance().getRequestPurgerPeriod() * 1000);
        }
        else
        {
            clock = null;
            clockTask = null;
        }
    };
    
    public static SurlStatusStore getInstance()
    {
        return instance;
    }

    /**
     * @param requestToken
     * @param surlStatuses
     * @throws IllegalArgumentException
     * @throws TokenDuplicationException 
     * @throws UnknownTokenException
     */
    public void store(TRequestToken requestToken, HashMap<TSURL, TReturnStatus> surlStatuses)
            throws IllegalArgumentException, TokenDuplicationException
    {
        if (requestToken == null || surlStatuses == null || surlStatuses.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surlStatuses=" + surlStatuses);
        }
        try
        {
            verifyLock(requestToken);
            if(!verifyMissingToken(requestToken))
            {
                throw new IllegalStateException("Token " + requestToken +" has not a lock but is registerd in the store");
            }
            throw new TokenDuplicationException("Token " + requestToken + " is already stored");
        } catch(UnknownTokenException e)
        {
        }
        
        try
        {
            writeLockNew(requestToken);
            if (tokenSurlStatusStore.containsKey(requestToken))
            {
                log.warn("Token \'" + requestToken + "\' is already stored");
                throw new IllegalArgumentException("Provided token \'" + requestToken + "\'is already stored");
            }
            log.debug("Storing token " + requestToken);

            Map<TSURL, ModifiableReturnStatus> surlStatusMap = new HashMap<TSURL, ModifiableReturnStatus>(
                                                                                                          surlStatuses.size());
            for (Entry<TSURL, TReturnStatus> surlStatus : surlStatuses.entrySet())
            {
                log.debug("Storing surl " + surlStatus.getKey() + " with status " + surlStatus.getValue()
                        + " for token " + requestToken);
                ModifiableReturnStatus status;
                try
                {
                    status = new ModifiableReturnStatus(surlStatus.getValue());
                } catch(InvalidTReturnStatusAttributeException e)
                {
                    throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException: " + e);
                }
                surlStatusMap.put(surlStatus.getKey(), status);
                // TODO deadlock danger
                try
                {
                    writeLock(surlStatus.getKey());
                    Map<TRequestToken, ModifiableReturnStatus> map = surlTokenStatusStore.get(surlStatus.getKey());
                    if (map == null)
                    {
                        map = new HashMap<TRequestToken, ModifiableReturnStatus>(1);
                        surlTokenStatusStore.put(surlStatus.getKey(), map);
                    }
                    map.put(requestToken, status);
                }
                finally
                {
                    writeUnlock(surlStatus.getKey());
                }

            }
            tokenSurlStatusStore.put(requestToken, surlStatusMap);
        }
        finally
        {
            writeUnlock(requestToken);
        }

        // manage collisions
        if (requestToken.hasExpirationDate())
        {
            Long expiration = requestToken.getExpiration().getTimeInMillis();
            try
            {
                lockExpirationMap();
                boolean updated = false;
                while (expirationTokenRegistry.containsKey(expiration))
                {
                    updated = true;
                    expiration += 1;
                }
                if (updated)
                {
                    requestToken.updateExpiration(new Date(expiration));
                }
                expirationTokenRegistry.put(expiration, requestToken);
            }
            finally
            {
                unlockExpirationMap();
            }

        }
    }
    
    public void update(TRequestToken requestToken, TSURL surl, TStatusCode newStatusCode)
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        update(requestToken, surl, newStatusCode, "");
    }

    public void update(TRequestToken requestToken, TSURL surl, TStatusCode newStatusCode, String explanation)
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the statuse, " + "null arguments: surl="
                    + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        update(requestToken, surlList, newStatusCode, explanation);
    }

    public void update(TRequestToken requestToken, List<TSURL> surls, TStatusCode newStatusCode)
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        update(requestToken, surls, newStatusCode, "");
    }

    public void update(TRequestToken requestToken, List<TSURL> surls, TStatusCode newStatusCode,
            String explanation) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || newStatusCode == null
                || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls + " newStatusCode="
                    + newStatusCode + " explanation=" + explanation);
        }
        log.debug("Updating token " + requestToken + " for surls " + surls + " with status " + newStatusCode
                + " " + explanation);
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            checkToken(requestToken);
            // TODO deadlock danger
// checkCleanToken(requestToken);
            Map<TSURL, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
            for (TSURL surl : surls)
            {
                if (surl == null)
                {
                    log.warn("Unexpected null element in input surls list : " + surls);
                    continue;
                }
                ModifiableReturnStatus status = surlStatusMap.get(surl);
                if (status == null)
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
        finally
        {
            readUnlock(requestToken);
        }
    }

    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        checkAndUpdate(requestToken, surl, expectedStatusCode, newStatusCode, "");
    }

    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException,
            UnknownTokenException, ExpiredTokenException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the statuse, " + "null arguments: surl="
                    + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        checkAndUpdate(requestToken, surlList, expectedStatusCode, newStatusCode, explanation);
    }
    
    public void update(TSURL surl, TStatusCode statusCode, String explanation)
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the status, " + "null arguments: surl="
                    + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        update(surlList, statusCode, explanation);
    }
    
    private void update(ArrayList<TSURL> surls, TStatusCode statusCode,
            String explanation)
    {
        if (surls == null || surls.isEmpty()
                || statusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to update the statuses, "
                    + "null arguments: surls=" + surls
                    + " statusCode=" + statusCode
                    + " explanation=" + explanation);
        }
        log.debug("Updating surls " + surls + " with status " + statusCode + " " + explanation);
        for (TSURL surl : surls)
        {
            if (surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            try
            {
                readLock(surl);
                Map<TRequestToken, ModifiableReturnStatus> tokenStatusMap = this.surlTokenStatusStore.get(surl);
                for (ModifiableReturnStatus status : tokenStatusMap.values())
                {
                    status.setStatusCode(statusCode);
                    status.setExplanation(explanation);
                }
            }
            finally
            {
                readUnlock(surl);
            }
        }
    }

    public void checkAndUpdate(TSURL surl, TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation)
            throws IllegalArgumentException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to update the status, " + "null arguments: surl="
                    + surl);
        }
        ArrayList<TSURL> surlList = new ArrayList<TSURL>(1);
        surlList.add(surl);
        checkAndUpdate(surlList, expectedStatusCode, newStatusCode, explanation);
    }

    private void checkAndUpdate(ArrayList<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation)
    {
        if (surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode
                    + " explanation=" + explanation);
        }
        log.debug("Checking and updating surls " + surls + " where status is "
                + expectedStatusCode + " with status " + newStatusCode + " " + explanation);
        for (TSURL surl : surls)
        {
            if (surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            try
            {
                readLock(surl);
                Map<TRequestToken, ModifiableReturnStatus> tokenStatusMap = this.surlTokenStatusStore.get(surl);
                for (ModifiableReturnStatus status : tokenStatusMap.values())
                {
                    status.testAndSetStatusCodeExplanation(expectedStatusCode, newStatusCode, explanation);
                }
            }
            finally
            {
                readUnlock(surl);
            }

        }
    }

    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        checkAndUpdate(requestToken, surls, expectedStatusCode, newStatusCode, "");
    }

    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException,
            UnknownTokenException, ExpiredTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode
                    + " explanation=" + explanation);
        }
        log.debug("Checking and updating token " + requestToken + " for surls " + surls + " where status is "
                + expectedStatusCode + " with status " + newStatusCode + " " + explanation);
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            // TODO deadlock danger
// checkCleanToken(requestToken);
            checkToken(requestToken);
            Map<TSURL, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
            for (TSURL surl : surls)
            {
                if (surl == null)
                {
                    log.warn("Unexpected null element in input surls list : " + surls);
                    continue;
                }
                ModifiableReturnStatus status = surlStatusMap.get(surl);
                if (status == null)
                {
                    log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
                }
                else
                {
                    if (!status.testAndSetStatusCodeExplanation(expectedStatusCode, newStatusCode,
                                                                explanation))
                    {
                        log.debug("Surl status not updated, current status \'" + status.getStatusCode()
                                + "\' doesn't match expected status \'" + expectedStatusCode + "\'");
                    }
                }
            }
        }
        finally
        {
            readUnlock(requestToken);
        }
    }
    
    public void checkAndUpdate(TRequestToken requestToken, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws UnknownTokenException, ExpiredTokenException
    {
        if (requestToken == null || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode
                    + " explanation=" + explanation);
        }
        log.debug("Checking and updating token " + requestToken + " for surls where status is "
                + expectedStatusCode + " with status " + newStatusCode + " " + explanation);
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            // TODO deadlock danger
// checkCleanToken(requestToken);
            checkToken(requestToken);
            Map<TSURL, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
            for (Entry<TSURL, ModifiableReturnStatus> surlStatus : surlStatusMap.entrySet())
            {
                if (surlStatus == null)
                {
                    log.warn("Unexpected null element surlStatus for token : " + requestToken);
                    continue;
                }
                if (!surlStatus.getValue().testAndSetStatusCodeExplanation(expectedStatusCode, newStatusCode,
                                                                           explanation))
                {
                    log.debug("Surl status not updated, current status \'"
                            + surlStatus.getValue().getStatusCode() + "\' doesn't match expected status \'"
                            + expectedStatusCode + "\'");
                }
            }
        }
        finally
        {
            readUnlock(requestToken);
        }
        
    }
    
    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken) throws UnknownTokenException, IllegalArgumentException, ExpiredTokenException
    {
        if (requestToken == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken);
        }
        log.debug("Retrieving statuses of surls for token " + requestToken);
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            checkToken(requestToken);
            //TODO deadlock danger
//            checkCleanToken(requestToken);
            return (Map<TSURL, TReturnStatus>) ((HashMap<TSURL, ModifiableReturnStatus>)this.tokenSurlStatusStore.get(requestToken)).clone();    
        } finally
        {
            readUnlock(requestToken);
        }
    }
    
    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, TSURL surl)
            throws UnknownTokenException, IllegalArgumentException, ExpiredTokenException
    {
        if (surl == null || surl.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: surl=" + surl);
        }
        return getSurlsStatus(requestToken, Arrays.asList(new TSURL[] { surl }));
    }

    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, Collection<TSURL> surls)
            throws UnknownTokenException, IllegalArgumentException, ExpiredTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surls=" + surls);
        }
        log.debug("Retrieving statuses of surls " + surls + " for token " + requestToken);
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            // TODO deadlock danger
// checkCleanToken(requestToken);
            checkToken(requestToken);
            HashMap<TSURL, TReturnStatus> builtSurlStatusMap = new HashMap<TSURL, TReturnStatus>(surls.size());
            Map<TSURL, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
            for (TSURL surl : surls)
            {
                if (surl == null)
                {
                    log.warn("Unexpected null element in input surls list");
                    continue;
                }
                TReturnStatus status = surlStatusMap.get(surl);
                if (status == null)
                {
                    log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
                }
                else
                {
                    builtSurlStatusMap.put(surl, status);
                }
            }
            return builtSurlStatusMap;
        }
        finally
        {
            readUnlock(requestToken);
        }
    }

    public Collection<TReturnStatus> getSurlStatuses(TSURL surl) throws UnknownSurlException,
            IllegalArgumentException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the statuses, null arguments: surl=" + surl);
        }
        log.debug("Retrieving statuses of surl " + surl);

        Map<TRequestToken, TReturnStatus> tokensStatusMap = getSurlPerTokenStatuses(surl);
        if (tokensStatusMap == null)
        {
            throw new UnknownSurlException("Surl " + surl + " is not stored");
        }
        return new ArrayList<TReturnStatus>(new HashSet<TReturnStatus>(tokensStatusMap.values()));
    }

    public Map<TRequestToken, TReturnStatus> getSurlPerTokenStatuses(TSURL surl)
            throws IllegalArgumentException, UnknownSurlException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the statuses, null arguments: surl=" + surl);
        }
        log.debug("Retrieving status-token for surl " + surl);
        try
        {
            readLock(surl);
            Map<TRequestToken, ModifiableReturnStatus> tokensStatusMap = surlTokenStatusStore.get(surl);
            if (tokensStatusMap == null)
            {
                throw new UnknownSurlException("Surl " + surl + " is not stored");
            }
        /*commented out to avoid deadlock*/
//        Iterator<Entry<TRequestToken, ModifiableReturnStatus>> iterator = tokensStatusMap.entrySet()
//                                                                                         .iterator();
//        while (iterator.hasNext())
//        {
//            Entry<TRequestToken, ModifiableReturnStatus> tokenStatus = iterator.next();
//            if (tokenStatus.getKey().hasExpirationDate() && tokenStatus.getKey().isExpired())
//            {
//                //TODO deadlock danger
//                writeLock(surl);
//                //TODO deadlock danger
//                iterator.remove();
//                try
//                {
//                    cleanToken(tokenStatus.getKey());
//                } catch(UnknownTokenException e)
//                {
//                    throw new IllegalStateException("Unexpected UnknownTokenException in cleanToken: "
//                                                    + e.getMessage());
//                }
//                writeunlock(surl);
//            }
//        }
        
            if (tokensStatusMap.isEmpty())
            {
                throw new UnknownSurlException("Surl " + surl + " is not stored");
            }
            return (Map<TRequestToken, TReturnStatus>) ((HashMap) tokensStatusMap).clone();
        }
        finally
        {
            readUnlock(surl);
        }
    }

    public List<TRequestToken> getExpiredTokens()
    {
        lockExpirationMap();
        try
        {
            return new ArrayList<TRequestToken>(expirationTokenRegistry.headMap(Calendar.getInstance()
                                                                                .getTimeInMillis(), true)
                                                               .values());    
        }finally
        {
            unlockExpirationMap();
        }
        
    }
    
    private void checkToken(TRequestToken requestToken) throws UnknownTokenException, ExpiredTokenException
    {
        if (!tokenSurlStatusStore.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is not stored");
            throw new UnknownTokenException("Provided token \'" + requestToken + "\'is not stored");
        }
        if(requestToken.hasExpirationDate() && requestToken.isExpired())
        {
            log.warn("Token \'" + requestToken + "\' is expired");
            throw new ExpiredTokenException("Provided token \'" + requestToken + "\'is expired");
        }
    }
    
//    private void checkCleanToken(TRequestToken requestToken) throws UnknownTokenException
//    {
//        writeLock(requestToken);
//        try
//        {
//            checkToken(requestToken);
//        } catch(ExpiredTokenException e)
//        {
//            try
//            {
//                //TODO deadlock danger
//                cleanToken(requestToken);
//            } catch(UnknownTokenException e1)
//            {
//                throw new IllegalStateException("Unexpected UnknownTokenException in cleanToken: "
//                        + e1.getMessage());
//            }
//        }
//        writeunlock(requestToken);
//    }

    private void cleanToken(TRequestToken requestToken) throws UnknownTokenException
    {
        verifyToken(requestToken);
        try
        {
            lockExpirationMap();
            expirationTokenRegistry.remove(getStoredTokenExpiration(requestToken));
        }
        finally
        {
            unlockExpirationMap();
        }
        try
        {
            writeLock(requestToken);
            Map<TSURL, ModifiableReturnStatus> surlStatuses = this.tokenSurlStatusStore.remove(requestToken);
            for (TSURL surl : surlStatuses.keySet())
            {
                try
                {
                    // TODO deadlock danger
                    writeLock(surl);
                    Map<TRequestToken, ModifiableReturnStatus> tokenStatuses = this.surlTokenStatusStore.get(surl);
                    if (tokenStatuses == null)
                    {
                        throw new IllegalStateException("Inconsistent Surl Status Store. Surl uid " + surl
                                + " not found in surlTokenStatusStore");
                    }
                    tokenStatuses.remove(requestToken);
                    if (tokenStatuses.isEmpty())
                    {
                        this.surlTokenStatusStore.remove(surl);
                    }
                }
                finally
                {
                    writeUnlock(surl);
                }
            }
        }
        finally
        {
            writeUnlock(requestToken);
        }

    }
    
    private void verifyLock(TRequestToken requestToken) throws UnknownTokenException
    {
        if (!tokenLockMap.containsKey(requestToken))
        {
            log.debug("Token \'" + requestToken + "\' is not stored in tokenLockMap");
            throw new UnknownTokenException("Provided token \'" + requestToken + "\' has not a lock");
        }
    }

    private void verifyToken(TRequestToken requestToken) throws UnknownTokenException
    {
        if (!tokenSurlStatusStore.containsKey(requestToken))
        {
            log.debug("Token \'" + requestToken + "\' is not stored");
            throw new UnknownTokenException("Provided token \'" + requestToken + "\'is not stored");
        }
        if (!tokenLockMap.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is not stored in tokenLockMap");
            throw new IllegalStateException("Provided token \'" + requestToken + "\'is not stored in tokenLockMap");
        }
        try
        {
            if(!expirationTokenRegistry.containsKey(getStoredTokenExpiration(requestToken)))
            {
                log.warn("Token \'" + requestToken + "\' is not stored in expirationTokenRegistry");
                throw new IllegalStateException("Provided token \'" + requestToken + "\'is not stored in expirationTokenRegistry");    
            }
        } catch(UnknownTokenException e)
        {
            log.warn("Token \'" + requestToken + "\' is not retrieved from getStoredTokenExpiration");
            throw new IllegalStateException("Provided token \'" + requestToken + "\'is not retrieved from getStoredTokenExpiration");
        }
    }
    
    private boolean verifyMissingToken(TRequestToken requestToken) throws IllegalArgumentException
    {
        if(requestToken == null)
        {
            throw new IllegalArgumentException("Unable to verify the token, null arguments: requestToken=" + requestToken);
        }
        return (!tokenSurlStatusStore.containsKey(requestToken) && 
                !tokenLockMap.containsKey(requestToken) && 
                (!requestToken.hasExpirationDate() || 
                 !requestToken.equals(expirationTokenRegistry.get(requestToken.getExpiration()))));
    }

    private long getStoredTokenExpiration(TRequestToken requestToken) throws UnknownTokenException
    {
        verifyLock(requestToken);
        try
        {
            readLock(requestToken);
            ArrayList<TRequestToken> tokenList = new ArrayList<TRequestToken>(
                                                                              this.tokenSurlStatusStore.keySet());
            int index = tokenList.indexOf(requestToken);
            if (index >= 0)
            {
                TRequestToken storedToken = tokenList.get(index);
                if (!storedToken.hasExpirationDate())
                {
                    throw new IllegalStateException("Unexpected stored token with no expiration date");
                }
                return storedToken.getExpiration().getTimeInMillis();
            }
            else
            {
                throw new UnknownTokenException("Token " + requestToken + " not stored in tokenSurlStatusStore");
            }
        }
        finally
        {
            readUnlock(requestToken);
        }
    }

    public void checkIntegrity() throws Exception
    {
        for (Entry<TSURL, Map<TRequestToken, ModifiableReturnStatus>> surlTokenStatus : surlTokenStatusStore.entrySet())
        {
            if(surlTokenStatus.getValue() == null || surlTokenStatus.getValue().isEmpty())
            {
                throw new Exception("Integrity violation: empty tokenStatusMap in surlTokenStatusStore for surl "
                        + surlTokenStatus.getKey());
            }
            for (Entry<TRequestToken, ModifiableReturnStatus> tokenSurlStatus : surlTokenStatus.getValue()
                                                                                               .entrySet())
            {
                if (tokenSurlStatus.getValue() == null)
                {
                    throw new Exception("Integrity violation: Null status in surlTokenStatusStore for surl "
                            + surlTokenStatus.getKey() + " token " + tokenSurlStatus.getKey());
                }
                Map<TSURL, ModifiableReturnStatus> surlStatus = tokenSurlStatusStore.get(tokenSurlStatus.getKey());
                if (surlStatus == null)
                {
                    throw new Exception("Integrity violation: Null surlStatus map in tokenSurlStatusStore for token " + tokenSurlStatus.getKey() + " surl "
                                        + surlTokenStatus.getValue());
                }
                if (surlStatus.get(surlTokenStatus.getKey()) == null)
                {
                    throw new Exception("Integrity violation: Null Status in tokenSurlStatusStore for token " + tokenSurlStatus.getKey() + " surl "
                                        + surlTokenStatus.getValue());
                }
                if (!tokenSurlStatus.getValue().equals(surlStatus.get(surlTokenStatus.getKey())))
                {
                    throw new Exception(
                                        "Integrity violation: value mismatch for status in tokenSurlStatusStore "
                                                + tokenSurlStatus.getValue() + " and surlTokenStatusStore "
                                                + surlStatus.get(surlTokenStatus.getKey()));
                }
                if (!(tokenSurlStatus.getValue() == surlStatus.get(surlTokenStatus.getKey())))
                {
                    throw new Exception("Integrity violation: object mismatch for status in tokenSurlStatusStore "
                                                + tokenSurlStatus.getValue() + " and surlTokenStatusStore "
                                                + surlStatus.get(surlTokenStatus.getKey()));
                }
            }
        }
        for (Entry<TRequestToken, Map<TSURL, ModifiableReturnStatus>> tokenSurlStatus : tokenSurlStatusStore.entrySet())
        {
            if(tokenSurlStatus.getValue() == null || tokenSurlStatus.getValue().isEmpty())
            {
                throw new Exception("Integrity violation: empty surlStatus map in tokenSurlStatusStore for token "
                        + tokenSurlStatus.getKey());
            }
            if(tokenSurlStatus.getKey().hasExpirationDate())
            {
                TRequestToken expirationToken = expirationTokenRegistry.get(tokenSurlStatus.getKey().getExpiration().getTimeInMillis());
                if(expirationToken == null)
                {
                    throw new Exception("Integrity violation: empty expirationToken for token "
                                        + tokenSurlStatus.getKey());
                }
                if(!expirationToken.equals(tokenSurlStatus.getKey()))
                {
                    throw new Exception("Integrity violation: value mismatch for expirationToken "
                            + expirationToken + " and token " + tokenSurlStatus.getKey());
                }
            }
            for (Entry<TSURL, ModifiableReturnStatus> surlTokenStatus : tokenSurlStatus.getValue()
                                                                                         .entrySet())
            {
                if (surlTokenStatus.getValue() == null)
                {
                    throw new Exception("Integrity violation: Null status in tokenSurlStatusStore for token "
                                        + tokenSurlStatus.getKey() + " surl " + surlTokenStatus.getKey());
                }
                Map<TRequestToken, ModifiableReturnStatus> tokenStatus = surlTokenStatusStore.get(surlTokenStatus.getKey());
                if (tokenStatus == null)
                {
                    throw new Exception("Integrity violation: Null tokenStatus map in surlTokenStatusStore for surl " + surlTokenStatus.getKey() + " token "
                                        + tokenSurlStatus.getValue());
                }
                if(tokenStatus.get(tokenSurlStatus.getKey()) == null)
                {
                    throw new Exception("Integrity violation: Null Status in surlTokenStatusStore for surl " + surlTokenStatus.getKey() + " surl "
                                        + tokenSurlStatus.getValue());
                }
                if(!surlTokenStatus.getValue().equals(tokenStatus.get(tokenSurlStatus.getKey())))
                {
                    throw new Exception(
                                        "Integrity violation: value mismatch for status in surlTokenStatusStore "
                                                + surlTokenStatus.getValue() + " and surlTokenStatusStore "
                                                + tokenStatus.get(tokenSurlStatus.getKey()));
                }
                if (!(surlTokenStatus.getValue() == tokenStatus.get(tokenSurlStatus.getKey())))
                {
                    throw new Exception(
                                        "Integrity violation: object mismatch for status in surlTokenStatusStore "
                                                + surlTokenStatus.getValue() + " and surlTokenStatusStore "
                                                + tokenStatus.get(tokenSurlStatus.getKey()));
                }
            }
        }
        for (Entry<Long, TRequestToken> surlexpirationToken : expirationTokenRegistry.entrySet())
        {
            if(!surlexpirationToken.getValue().hasExpirationDate())
            {
                throw new Exception("Integrity violation: stored token " + surlexpirationToken.getValue() 
                                    + " in expirationTokenRegistry has no expiration ");
            }
            if (surlexpirationToken.getKey() != surlexpirationToken.getValue()
                                                                   .getExpiration()
                                                                   .getTimeInMillis())
            {
                throw new Exception("Integrity violation: value mismatch between expiration "
                        + surlexpirationToken.getKey() + " and token expiration "
                        + surlexpirationToken.getValue().getExpiration().getTimeInMillis());
            }
            if (tokenSurlStatusStore.get(surlexpirationToken.getValue()) == null)
            {
                throw new Exception("Integrity violation: Null tokenSurlStatus map for expirationToken " + surlexpirationToken.getValue());
            }
        }
    }
    
    private void writeLockNew(TRequestToken requestToken) throws IllegalArgumentException
    {
        if(tokenLockMap.contains(requestToken))
        {
            throw new IllegalArgumentException("The provided token has already a lock");
        }
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        tokenLockMap.put(requestToken,lock);
        lock.writeLock().lock();
        System.out.println("SurlStatusStore.writeLock(TOKEN) " + requestToken);
    }

    private void writeLock(TRequestToken requestToken) throws IllegalArgumentException
    {
        ReentrantReadWriteLock lock = tokenLockMap.get(requestToken);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided token has not a lock");
        }
        lock.writeLock().lock();
        System.out.println("SurlStatusStore.writeLock(TOKEN) " + requestToken);
    }
    
    private void writeUnlock(TRequestToken requestToken) throws IllegalArgumentException
    {
        ReentrantReadWriteLock lock = tokenLockMap.get(requestToken);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided requestToken has not a lock");
        }
        lock.writeLock().lock();
        System.out.println("SurlStatusStore.writeunlock(TOKEN) " + requestToken);
    }
    
    private void readLock(TRequestToken requestToken)
    {
        ReentrantReadWriteLock lock = tokenLockMap.get(requestToken);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided requestToken has not a lock");
        }
        lock.readLock().lock();
        System.out.println("SurlStatusStore.readLock(TOKEN) " + requestToken);
    }
    
    private void readUnlock(TRequestToken requestToken) throws IllegalArgumentException
    {
        ReentrantReadWriteLock lock = tokenLockMap.get(requestToken);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided requestToken has not a lock");
        }
        lock.readLock().unlock();
        System.out.println("SurlStatusStore.readunlock(TOKEN) " + requestToken);
    }
    
    
    private void writeLock(TSURL surl)
    {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storedLock = surlLockMap.putIfAbsent(surl, lock);
        if(storedLock != null)
        {
            lock = storedLock;
        }
        lock.writeLock().lock();
        System.out.println("SurlStatusStore.writeLock(SURL) " + surl);
    }

    private void writeUnlock(TSURL surl) throws IllegalArgumentException
    {
        ReentrantReadWriteLock lock = surlLockMap.get(surl);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided surl has not a lock");
        }
        lock.writeLock().unlock();
        System.out.println("SurlStatusStore.writeunlock(SURL) " + surl);
    }
    
    private void readLock(TSURL surl)
    {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        ReentrantReadWriteLock storedLock = surlLockMap.putIfAbsent(surl, lock);
        if(storedLock != null)
        {
            lock = storedLock;
        }
        lock.readLock().lock();
        System.out.println("SurlStatusStore.readLock(SURL) " + surl);
    }
    
    private void readUnlock(TSURL surl) throws IllegalArgumentException
    {
        ReentrantReadWriteLock lock = surlLockMap.get(surl);
        if(lock == null) 
        {
            throw new IllegalArgumentException("The provided surl has not a lock");
        }
        lock.readLock().unlock();
        System.out.println("SurlStatusStore.readunlock(SURL) " + surl);
    }
    
    private void unlockExpirationMap()
    {
        expirationTokenRegistryLock.unlock();
        System.out.println("SurlStatusStore.unlockExpirationMap()");
    }

    private void lockExpirationMap()
    {
        expirationTokenRegistryLock.lock();
        System.out.println("SurlStatusStore.lockExpirationMap()");
    }

}
