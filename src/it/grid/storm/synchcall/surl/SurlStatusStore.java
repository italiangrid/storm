package it.grid.storm.synchcall.surl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

public class SurlStatusStore
{

    private static final Logger log = LoggerFactory.getLogger(SurlStatusStore.class);

    private HashMap<Integer, Map<TRequestToken, ModifiableReturnStatus>> surlTokenStatusStore = new HashMap<Integer, Map<TRequestToken, ModifiableReturnStatus>>();

    private HashMap<TRequestToken, Map<Integer, ModifiableReturnStatus>> tokenSurlStatusStore = new HashMap<TRequestToken, Map<Integer, ModifiableReturnStatus>>();

    private ConcurrentSkipListMap<Long, TRequestToken> expirationTokenRegistry = new ConcurrentSkipListMap<Long, TRequestToken>();

    private static final SurlStatusStore instance = new SurlStatusStore();

    private SurlStatusStore()
    {
    };

    public static SurlStatusStore getInstance()
    {
        return instance;
    }

// public boolean testAndSet(TSURL surl, TStatusCode expectedStatusCode, TStatusCode newStatusCode)
// {
// if(true)
// {
// throw new IllegalStateException();
// }
// //da rifare!!
// TStatusCode status = store.get(surl.uniqueId());
// if(status != null)
// {
// synchronized (status)
// {
// if(status.equals(expectedStatusCode))
// {
// store.put(surl.uniqueId(), newStatusCode);
// return true;
// }
// }
// }
// return false;
// }

// public boolean testNewAndSet(TSURL surl, TStatusCode newStatusCode)
// {
// try
// {
// writeLock.lock();
// TStatusCode status = store.get(surl.uniqueId());
// if (status == null)
// {
// store.put(surl.uniqueId(),newStatusCode);
// return true;
// }
// }
// finally
// {
// writeLock.unlock();
// }
// return false;
// }

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
        if (tokenSurlStatusStore.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is already stored");
            throw new IllegalArgumentException("Provided token \'" + requestToken + "\'is already stored");
        }
        log.debug("Storing token " + requestToken);

        Map<Integer, ModifiableReturnStatus> surlStatusMap = new HashMap<Integer, ModifiableReturnStatus>(
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
            Integer surlUid = surlStatus.getKey().uniqueId();
            surlStatusMap.put(surlUid, status);
            Map<TRequestToken, ModifiableReturnStatus> map = surlTokenStatusStore.get(surlUid);
            if (map == null)
            {
                map = new HashMap<TRequestToken, ModifiableReturnStatus>(1);
                surlTokenStatusStore.put(surlUid, map);
            }
            map.put(requestToken, status);
        }
        tokenSurlStatusStore.put(requestToken, surlStatusMap);
        // manage collisions
        if(requestToken.hasExpirationDate())
        {
            Long expiration = requestToken.getExpiration().getTimeInMillis();
            boolean updated = false;
            while (expirationTokenRegistry.containsKey(expiration))
            {
                updated = true;
                expiration += 1;
            }
            if(updated)
            {
                requestToken.updateExpiration(new Date(expiration));    
            }
            expirationTokenRegistry.put(expiration, requestToken);
        }
    }

    public void update(TRequestToken requestToken, TSURL surl, TStatusCode newStatusCode)
            throws IllegalArgumentException, UnknownTokenException
    {
        update(requestToken, surl, newStatusCode, "");
    }

    public void update(TRequestToken requestToken, TSURL surl, TStatusCode newStatusCode, String explanation)
            throws IllegalArgumentException, UnknownTokenException
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
            throws IllegalArgumentException, UnknownTokenException
    {
        update(requestToken, surls, newStatusCode, "");
    }

    public void update(TRequestToken requestToken, List<TSURL> surls, TStatusCode newStatusCode,
            String explanation) throws IllegalArgumentException, UnknownTokenException
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
        checkToken(requestToken);
        Map<Integer, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
        for (TSURL surl : surls)
        {
            if (surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            ModifiableReturnStatus status = surlStatusMap.get(surl.uniqueId());
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

    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        checkAndUpdate(requestToken, surl, expectedStatusCode, newStatusCode, "");
    }

    public void checkAndUpdate(TRequestToken requestToken, TSURL surl, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException,
            UnknownTokenException
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

    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        checkAndUpdate(requestToken, surls, expectedStatusCode, newStatusCode, "");
    }

    public void checkAndUpdate(TRequestToken requestToken, List<TSURL> surls, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws IllegalArgumentException,
            UnknownTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode
                    + " explanation=" + explanation);
        }
        log.debug("Checking and pdating token " + requestToken + " for surls " + surls + " where status is "
                + expectedStatusCode + " with status " + newStatusCode + " " + explanation);
        checkToken(requestToken);
        Map<Integer, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
        for (TSURL surl : surls)
        {
            if (surl == null)
            {
                log.warn("Unexpected null element in input surls list : " + surls);
                continue;
            }
            ModifiableReturnStatus status = surlStatusMap.get(surl.uniqueId());
            if (status == null)
            {
                log.warn("Surl \'" + surl + "\' is not associated to token \'" + requestToken + "\'");
            }
            else
            {
                if (status.getStatusCode().equals(expectedStatusCode))
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
    
    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, TSURL surl)
            throws UnknownTokenException, IllegalArgumentException
    {
        if (surl == null || surl.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: surl=" + surl);
        }
        return getSurlsStatus(requestToken, Arrays.asList(new TSURL[] { surl }));
    }

    /**
     * @param requestToken
     * @param surls
     * @return
     * @throws UnknownTokenException
     */
    public Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, List<TSURL> surls)
            throws UnknownTokenException, IllegalArgumentException
    {
        if (requestToken == null || surls == null || surls.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surls=" + surls);
        }
        log.debug("Retrieving statuses of surls " + surls + " for token " + requestToken);
        checkToken(requestToken);
        HashMap<TSURL, TReturnStatus> builtSurlStatusMap = new HashMap<TSURL, TReturnStatus>(surls.size());
        Map<Integer, ModifiableReturnStatus> surlStatusMap = this.tokenSurlStatusStore.get(requestToken);
        for (TSURL surl : surls)
        {
            if (surl == null)
            {
                log.warn("Unexpected null element in input surls list");
                continue;
            }
            TReturnStatus status = surlStatusMap.get(surl.uniqueId());
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

    public List<TReturnStatus> getSurlStatuses(TSURL surl) throws UnknownSurlException,
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
        return new ArrayList<TReturnStatus>(tokensStatusMap.values());
    }

    public Map<TRequestToken, TReturnStatus> getSurlPerTokenStatuses(TSURL surl)
            throws IllegalArgumentException, UnknownSurlException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the statuses, null arguments: surl=" + surl);
        }
        log.debug("Retrieving status-token for surl " + surl);
        Map<TRequestToken, ModifiableReturnStatus> tokensStatusMap = surlTokenStatusStore.get(surl.uniqueId());
        if (tokensStatusMap == null)
        {
            throw new UnknownSurlException("Surl " + surl + " is not stored");
        }
        Iterator<Entry<TRequestToken, ModifiableReturnStatus>> iterator = tokensStatusMap.entrySet()
                                                                                         .iterator();
        while (iterator.hasNext())
        {
            Entry<TRequestToken, ModifiableReturnStatus> tokenStatus = iterator.next();
            if (tokenStatus.getKey().hasExpirationDate() && tokenStatus.getKey().isExpired())
            {
                iterator.remove();
                cleanToken(tokenStatus.getKey());
            }
        }
        if (tokensStatusMap.isEmpty())
        {
            throw new UnknownSurlException("Surl " + surl + " is not stored");
        }
        return (Map<TRequestToken, TReturnStatus>) ((HashMap) tokensStatusMap).clone();
    }

    public List<TRequestToken> getExpiredTokens()
    {
        return new ArrayList<TRequestToken>(expirationTokenRegistry.headMap(Calendar.getInstance()
                                                                                    .getTimeInMillis(), true)
                                                                   .values());
    }

    private void cleanToken(TRequestToken token)
    {
        Map<Integer, ModifiableReturnStatus> surlStatuses = this.tokenSurlStatusStore.remove(token);
        expirationTokenRegistry.remove(token.getExpiration().getTimeInMillis());
        for (Integer surlUid : surlStatuses.keySet())
        {
            Map<TRequestToken, ModifiableReturnStatus> tokenStatuses = this.surlTokenStatusStore.get(surlUid);
            if (tokenStatuses == null)
            {
                throw new IllegalStateException("Inconsistent Surl Statu Store. Surl uid " + surlUid
                        + " not found in surlTokenStatusStore");
            }
            tokenStatuses.remove(token);
            if (tokenStatuses.isEmpty())
            {
                this.surlTokenStatusStore.remove(surlUid);
            }
        }
    }

    private void checkToken(TRequestToken requestToken) throws UnknownTokenException
    {
        if (!tokenSurlStatusStore.containsKey(requestToken))
        {
            log.warn("Token \'" + requestToken + "\' is not stored");
            throw new UnknownTokenException("Provided token \'" + requestToken + "\'is not stored");
        }
    }

    public void checkIntegrity() throws Exception
    {
        for (Entry<Integer, Map<TRequestToken, ModifiableReturnStatus>> surlTokenStatus : surlTokenStatusStore.entrySet())
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
                Map<Integer, ModifiableReturnStatus> surlStatus = tokenSurlStatusStore.get(tokenSurlStatus.getKey());
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
        for (Entry<TRequestToken, Map<Integer, ModifiableReturnStatus>> tokenSurlStatus : tokenSurlStatusStore.entrySet())
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
            for (Entry<Integer, ModifiableReturnStatus> surlTokenStatus : tokenSurlStatus.getValue()
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

}
