package it.grid.storm.synchcall.surl;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurlStatusManager
{
    
    private static final Logger log = LoggerFactory.getLogger(SurlStatusManager.class);
    private static final HashMap<TSURL, TReturnStatus> EMPTY_SURL_RESULT = new HashMap<TSURL, TReturnStatus>(0);
    private static final HashMap<TRequestToken, TReturnStatus> EMPTY_TOKEN_RESULT = new HashMap<TRequestToken, TReturnStatus>(0);

    public static void checkAndUpdateStatus(TRequestToken requestToken, List<TSURL> surls,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode);
        }
        if(isPersisted(requestToken))
        {
            PtPChunkCatalog.getInstance().transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(requestToken, surls);    
        }
        else
        {
            SurlStatusStore.getInstance().checkAndUpdate(requestToken, surls, expectedStatusCode, newStatusCode);
        }
    }

    public static Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, List<TSURL> surls) throws IllegalArgumentException
    {
        if(requestToken == null || surls == null || surls.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surls=" + surls);
        }
        
        if(isPersisted(requestToken))
        {
            Collection<ReducedPtPChunkData> chunksData = PtPChunkCatalog.getInstance()
            .lookupReducedPtPChunkData(requestToken, surls);
            Map<TSURL, TReturnStatus> surlsStatuses = new HashMap<TSURL,TReturnStatus>(chunksData.size()); 
            for(ReducedPtPChunkData chunkData : chunksData)
            {
                surlsStatuses.put(chunkData.toSURL(), chunkData.status());
            }
            return surlsStatuses;
        }
        else
        {
            try
            {
                return SurlStatusStore.getInstance().getSurlsStatus(requestToken, surls);
            } catch(UnknownTokenException e)
            {
                log.warn("Unable to get surl statuses. UnknownTokenException: " + e.getMessage());
                return EMPTY_SURL_RESULT;
            }
        }
    }
    
    public static Map<TRequestToken, TReturnStatus> getSurlCurrentStatuses(TSURL surl) throws IllegalArgumentException
    {
        if(surl == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: surl="
                    + surl);
        }
        try
        {
            return filterOutFinalStatuses(SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl));
        } catch(IllegalArgumentException e)
        {
            log.error("Unexpected IllegalArgumentException during surl statuses retrieving: " + e.getMessage());
            throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
        } catch(UnknownSurlException e)
        {
            log.info("Unable to get surl statuses. UnknownTokenException: " + e.getMessage());
            return EMPTY_TOKEN_RESULT;
        }
    }
    
    public static TReturnStatus getSurlsStatus(TSURL surl) throws IllegalArgumentException, UnknownSurlException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the status, null arguments: surl=" + surl);
        }
        List<TReturnStatus> statuses = SurlStatusStore.getInstance().getSurlStatuses(surl);
        if(statuses.isEmpty())
        {
            throw new IllegalStateException("Unexpected empty result from getSurlsStatuses");
        }
        LinkedList<TReturnStatus> nonFinalStatuses = extractNonFinalStatuses(statuses);
        removeStartingStatus(nonFinalStatuses);
        
        if(nonFinalStatuses.isEmpty())
        {
            return extractMostRecentStatus(statuses);
        }
        if(nonFinalStatuses.size() > 1)
        {
            log.warn("Inconsistent status for surl " + surl + " . Not final statuses are: " + nonFinalStatuses);
            return extractMostRecentStatus(nonFinalStatuses);
        }
        else
        {
            return nonFinalStatuses.getFirst();
        }
    }

    private static void removeStartingStatus(List<TReturnStatus> statuses)
    {
        Iterator<TReturnStatus> iterator = statuses.iterator();
        while(iterator.hasNext())
        {
            TReturnStatus status = iterator.next();
            if(!status.getStatusCode().equals(TStatusCode.SRM_REQUEST_QUEUED))
            {
                iterator.remove();
            }
        }
    }

    private static LinkedList<TReturnStatus> extractNonFinalStatuses(Collection<TReturnStatus> statuses)
    {
        LinkedList<TReturnStatus> filteredStatuses = new LinkedList<TReturnStatus>();
        for(TReturnStatus status : statuses)
        {
            if(!status.getStatusCode().isFinalStatus())
            {
                filteredStatuses.add(status);
            }
        }
        return filteredStatuses;
    }
    
    private static HashMap<TRequestToken, TReturnStatus> filterOutFinalStatuses(Map<TRequestToken, TReturnStatus> statuses)
    {
        HashMap<TRequestToken, TReturnStatus> filteredStatuses = new HashMap<TRequestToken, TReturnStatus>();
        for(Entry<TRequestToken, TReturnStatus> status : statuses.entrySet())
        {
            if(!status.getValue().getStatusCode().isFinalStatus())
            {
                filteredStatuses.put(status.getKey(), status.getValue());
            }
        }
        return filteredStatuses;
    }
    
    private static TReturnStatus extractMostRecentStatus(List<TReturnStatus> statuses)
    {
        TReturnStatus min = null;
        for (TReturnStatus status : statuses)
        {
            if(min == null || min.getLastUpdateTIme() < status.getLastUpdateTIme())
            {
                min = status;
            }
        }
        return min;
    }

    private static boolean isPersisted(TRequestToken requestToken) throws IllegalArgumentException
    {
        if(requestToken == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken);
        }
        return false;
    }

}
