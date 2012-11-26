package it.grid.storm.synchcall.surl;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.CopyPersistentChunkData;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGPersistentChunkData;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.ReducedBoLChunkData;
import it.grid.storm.catalogs.ReducedChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.ReducedPtPChunkData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
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

    public static Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        if(requestToken == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken);
        }
        
        TRequestType requestType = isPersisted(requestToken);
        if(!requestType.isEmpty())
        {
            return getPersistentSurlsStatuses(requestType, requestToken);
        }
        else
        {
            return SurlStatusStore.getInstance().getSurlsStatus(requestToken);
        }
        
    }
    
    public static void checkAndUpdateStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation) throws UnknownTokenException, ExpiredTokenException
    {
        if (requestToken == null || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " expectedStatusCode="
                    + expectedStatusCode + " newStatusCode=" + newStatusCode + " explanation=" + explanation);
        }
        TRequestType requestType = isPersisted(requestToken);
        if(!requestType.isEmpty())
        {
            checkAndUpdatePersistentStatus(requestType, requestToken, expectedStatusCode, newStatusCode, explanation);
        }
        else
        {
            SurlStatusStore.getInstance().checkAndUpdate(requestToken, expectedStatusCode, newStatusCode, explanation);
        }
    }

    public static void checkAndUpdateStatus(TRequestToken requestToken, List<TSURL> surls,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, UnknownSurlException
    {
        if (requestToken == null || surls == null || surls.isEmpty() || expectedStatusCode == null
                || newStatusCode == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surls=" + surls
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode);
        }
        TRequestType requestType = isPersisted(requestToken);
        if(!requestType.isEmpty())
        {
            checkAndUpdatePersistentStatus(requestType, requestToken, surls, expectedStatusCode, newStatusCode);
        }
        else
        {
            SurlStatusStore.getInstance().checkAndUpdate(requestToken, surls, expectedStatusCode, newStatusCode);
        }
    }
    
    public static void checkAndUpdateStatus(TRequestType requestType, TSURL surl,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation) throws IllegalArgumentException, UnknownSurlException
    {
        if (requestType == null || surl == null || expectedStatusCode == null
                || newStatusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestType=" + requestType + " surl=" + surl
                    + " expectedStatusCode=" + expectedStatusCode + " newStatusCode=" + newStatusCode + " explanation=" + explanation);
        }
        if(!requestType.isEmpty())
        {
            checkAndUpdatePersistentStatus(requestType, surl, expectedStatusCode, newStatusCode, explanation);
        }
        else
        {
            SurlStatusStore.getInstance().checkAndUpdate(surl, expectedStatusCode, newStatusCode, explanation);
        }
    }
    
    public static void updateStatus(TRequestToken requestToken, TSURL surl, TStatusCode statusCode,
            String explanation) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, UnknownSurlException
    {
        if (requestToken == null || surl == null
                || statusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestToken=" + requestToken + " surl=" + surl
                    + " statusCode=" + statusCode + " explanation=" + explanation);
        }
        TRequestType requestType = isPersisted(requestToken);
        if(!requestType.isEmpty())
        {
            updatePersistentStatus(requestType, requestToken, surl, statusCode, explanation);
        }
        else
        {
            SurlStatusStore.getInstance().update(requestToken, surl, statusCode, explanation);
        }
    }
    
    public static void updateStatus(TRequestType requestType, TSURL surl, TStatusCode statusCode,
            String explanation) throws UnknownSurlException
    {
        if (requestType == null || surl == null
                || statusCode == null || explanation == null)
        {
            throw new IllegalArgumentException("unable to check and update the statuses, "
                    + "null arguments: requestType=" + requestType + " surl=" + surl
                    + " statusCode=" + statusCode + " explanation=" + explanation);
        }
        if(!requestType.isEmpty())
        {
            updatePersistentStatus(requestType, surl, statusCode, explanation);
        }
        else
        {
            SurlStatusStore.getInstance().update(surl, statusCode, explanation);
        }
    }
    
    

    public static Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken, Collection<TSURL> surls)
            throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        if(requestToken == null || surls == null || surls.isEmpty())
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " surls=" + surls);
        }
        
        TRequestType requestType = isPersisted(requestToken);
        if(!requestType.isEmpty())
        {
            return getPersistentSurlsStatuses(requestType, requestToken, surls);
        }
        else
        {
            return SurlStatusStore.getInstance().getSurlsStatus(requestToken, surls);
        }
    }
    
    private static Map<TRequestToken, TReturnStatus> getSurlCurrentStatuses(TSURL surl, GridUserInterface user)
    {
        if (surl == null || user == null)
        {
            throw new IllegalArgumentException("Unable to get the statuses, null arguments: surl=" + surl + " user=" + surl);
        }
        Map<TRequestToken, TReturnStatus> persistentTokensStatusMap = getSurlPerPersistentTokenStatuses(surl, user);
        try
        {
            persistentTokensStatusMap.putAll(SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl, user));
        } catch(UnknownSurlException e)
        {
            log.debug("Unable to get surl statuses. UnknownTokenException: " + e.getMessage());
        } catch(IllegalArgumentException e)
        {
            log.error("Unexpected IllegalArgumentException during surl statuses retrieving: "
                    + e);
            throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
        }

        return filterOutFinalStatuses(persistentTokensStatusMap);
    }
    
    public static Map<TRequestToken, TReturnStatus> getSurlCurrentStatuses(TSURL surl)
            throws IllegalArgumentException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: surl=" + surl);
        }
        Map<TRequestToken, TReturnStatus> persistentTokensStatusMap = getSurlPerPersistentTokenStatuses(surl);
        try
        {
            persistentTokensStatusMap.putAll(SurlStatusStore.getInstance().getSurlPerTokenStatuses(surl));
        } catch(UnknownSurlException e)
        {
            log.debug("Unable to get surl statuses. UnknownTokenException: " + e.getMessage());
        } catch(IllegalArgumentException e)
        {
            log.error("Unexpected IllegalArgumentException during surl statuses retrieving: "
                    + e);
            throw new IllegalStateException("Unexpected IllegalArgumentException: " + e.getMessage());
        }

        return filterOutFinalStatuses(persistentTokensStatusMap);
    }
    
    public static TReturnStatus getSurlsStatus(TSURL surl, GridUserInterface user) throws UnknownSurlException
    {
        if (surl == null || user == null)
        {
            throw new IllegalArgumentException("Unable to get the status, null arguments: surl=" + surl + " user=" + surl);
        }
        
        Collection<TReturnStatus> statuses = getSurlCurrentStatuses(surl, user).values();
        if(statuses.isEmpty())
        {
            throw new UnknownSurlException("The surl is not stored");
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
    
    public static TReturnStatus getSurlsStatus(TSURL surl) throws IllegalArgumentException, UnknownSurlException
    {
        if (surl == null)
        {
            throw new IllegalArgumentException("Unable to get the status, null arguments: surl=" + surl);
        }
        
        Collection<TReturnStatus> statuses = getSurlCurrentStatuses(surl).values();
        if(statuses.isEmpty())
        {
            throw new UnknownSurlException("The surl is not stored");
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

    private static Map<TSURL, TReturnStatus> getPersistentSurlsStatuses(TRequestType requestType,
            TRequestToken requestToken)
    {
        Map<TSURL, TReturnStatus> surlsStatuses;
        switch (requestType)
        {
            case PREPARE_TO_GET:
                Collection<ReducedChunkData> ptgChunksData = PtGChunkCatalog.getInstance()
                                                                            .lookupReducedPtGChunkData(requestToken);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(ptgChunksData.size());
                for (ReducedChunkData chunkData : ptgChunksData)
                {
                    surlsStatuses.put(chunkData.fromSURL(), chunkData.status());
                }
                return surlsStatuses;
            case PREPARE_TO_PUT:
                Collection<ReducedPtPChunkData> ptpChunksData = PtPChunkCatalog.getInstance()
                                                                               .lookupReducedPtPChunkData(requestToken);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(ptpChunksData.size());
                for (ReducedPtPChunkData chunkData : ptpChunksData)
                {
                    surlsStatuses.put(chunkData.toSURL(), chunkData.status());
                }
                return surlsStatuses;
            case COPY:
                Collection<CopyPersistentChunkData> copyChunksData = CopyChunkCatalog.getInstance()
                                                                                     .lookup(requestToken);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(copyChunksData.size());
                for (CopyPersistentChunkData chunkData : copyChunksData)
                {
                    surlsStatuses.put(chunkData.getSURL(), chunkData.getStatus());
                    surlsStatuses.put(chunkData.getDestinationSURL(), chunkData.getStatus());
                }
                return surlsStatuses;
            case BRING_ON_LINE:
                Collection<ReducedBoLChunkData> bolChunksData = BoLChunkCatalog.getInstance()
                                                                               .lookupReducedBoLChunkData(requestToken);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(bolChunksData.size());
                for (ReducedChunkData chunkData : bolChunksData)
                {
                    surlsStatuses.put(chunkData.fromSURL(), chunkData.status());
                }
                return surlsStatuses;
            case EMPTY:
                return new HashMap<TSURL, TReturnStatus>();
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static void updatePersistentStatus(TRequestType requestType, TRequestToken requestToken,
            TSURL surl, TStatusCode statusCode, String explanation)
    {
        switch (requestType)
        {
            case PREPARE_TO_GET:
                PtGChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                break;
            case PREPARE_TO_PUT:
                PtPChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                break;
            case COPY:
                //TODO if needed do it
//                CopyChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                		"CopyChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);");
            case BRING_ON_LINE:
                //TODO if needed do it
//                BoLChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "BoLChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);");
            case EMPTY:
                throw new IllegalArgumentException("Received Empty TRequestType: " + requestType);
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static void updatePersistentStatus(TRequestType requestType, TSURL surl, TStatusCode statusCode,
            String explanation)
    {
        switch (requestType)
        {
            case PREPARE_TO_GET:
              //TODO if needed do it
//                PtGChunkCatalog.getInstance().updateStatus(surl, StatusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "PtGChunkCatalog.getInstance().updateStatus(surl, statusCode, explanation);");
            case PREPARE_TO_PUT:
                PtPChunkCatalog.getInstance().updateStatus(surl, statusCode, explanation);
                break;
            case COPY:
                //TODO if needed do it
//                CopyChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "CopyChunkCatalog.getInstance().updateStatus(surl, statusCode, explanation);");
            case BRING_ON_LINE:
                //TODO if needed do it
//                BoLChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "BoLChunkCatalog.getInstance().updateStatus(surl, statusCode, explanation);");
            case EMPTY:
                throw new IllegalArgumentException("Received Empty TRequestType: " + requestType);
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static void checkAndUpdatePersistentStatus(TRequestType requestType, TSURL surl,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation)
    {
        switch (requestType)
        {
            case PREPARE_TO_GET:
              //TODO if needed do it
//                PtGChunkCatalog.getInstance().updateFromPreviousStatus(surl, expectedStatusCode, newStatusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "PtGChunkCatalog.getInstance().updateFromPreviousStatus(surl, expectedStatusCode, newStatusCode, explanation);");
            case PREPARE_TO_PUT:
                PtPChunkCatalog.getInstance().updateFromPreviousStatus(surl, expectedStatusCode, newStatusCode, explanation);
                break;
            case COPY:
                //TODO if needed do it
//                CopyChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "CopyChunkCatalog.getInstance().updateFromPreviousStatus(surl, expectedStatusCode, newStatusCode, explanation);");
            case BRING_ON_LINE:
                //TODO if needed do it
//                BoLChunkCatalog.getInstance().updateStatus(requestToken, surl, statusCode, explanation);
                throw new IllegalStateException("Not implemented, contact storm developers: " +
                "BoLChunkCatalog.getInstance().updateFromPreviousStatus(surl, expectedStatusCode, newStatusCode, explanation);");
            case EMPTY:
                throw new IllegalArgumentException("Received Empty TRequestType: " + requestType);
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static void checkAndUpdatePersistentStatus(TRequestType requestType, TRequestToken requestToken,
            TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation)
    {
        switch (requestType)
        {
            case PREPARE_TO_GET:
                PtGChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
                break;
            case PREPARE_TO_PUT:
                PtPChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
                break;
            case COPY:
                CopyChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
                break;
            case BRING_ON_LINE:
                BoLChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
                break;
            case EMPTY:
                break;
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static void checkAndUpdatePersistentStatus(TRequestType requestType, TRequestToken requestToken,
            List<TSURL> surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode) throws IllegalArgumentException
    {
        switch (requestType)
        {
            case PREPARE_TO_GET:
                PtGChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, surls, expectedStatusCode, newStatusCode);
                break;
            case PREPARE_TO_PUT:
                PtPChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, surls, expectedStatusCode, newStatusCode);
                break;
            case COPY:
                CopyChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, surls, expectedStatusCode, newStatusCode);
                break;
            case BRING_ON_LINE:
                BoLChunkCatalog.getInstance().updateFromPreviousStatus(requestToken, surls, expectedStatusCode, newStatusCode);
                break;
            case EMPTY:
                break;
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static Map<TSURL, TReturnStatus> getPersistentSurlsStatuses(TRequestType requestType,
            TRequestToken requestToken, Collection<TSURL> surls) throws IllegalArgumentException
    {
        Map<TSURL, TReturnStatus> surlsStatuses;
        switch (requestType)
        {
            case PREPARE_TO_GET:
                Collection<ReducedChunkData> ptgChunksData = PtGChunkCatalog.getInstance().lookupReducedPtGChunkData(requestToken, surls);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(ptgChunksData.size());
                for (ReducedChunkData chunkData : ptgChunksData)
                {
                    surlsStatuses.put(chunkData.fromSURL(), chunkData.status());
                }
                return surlsStatuses;
            case PREPARE_TO_PUT:
                Collection<ReducedPtPChunkData> ptpChunksData = PtPChunkCatalog.getInstance().lookupReducedPtPChunkData(requestToken, surls);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(ptpChunksData.size());
                for (ReducedPtPChunkData chunkData : ptpChunksData)
                {
                    surlsStatuses.put(chunkData.toSURL(), chunkData.status());
                }
                return surlsStatuses;
            case COPY:
                Collection<CopyPersistentChunkData> copyChunksData = CopyChunkCatalog.getInstance().lookupCopyChunkData(requestToken, surls);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(copyChunksData.size());
                for (CopyPersistentChunkData chunkData : copyChunksData)
                {
                    surlsStatuses.put(chunkData.getSURL(), chunkData.getStatus());
                    surlsStatuses.put(chunkData.getDestinationSURL(), chunkData.getStatus());
                }
                return surlsStatuses;
            case BRING_ON_LINE:
                Collection<ReducedBoLChunkData> bolChunksData = BoLChunkCatalog.getInstance().lookupReducedBoLChunkData(requestToken, surls);
                surlsStatuses = new HashMap<TSURL, TReturnStatus>(bolChunksData.size());
                for (ReducedChunkData chunkData : bolChunksData)
                {
                    surlsStatuses.put(chunkData.fromSURL(), chunkData.status());
                }
                return surlsStatuses;
            case EMPTY:
                return new HashMap<TSURL, TReturnStatus>();
            default:
                throw new IllegalArgumentException("Received unknown TRequestType: " + requestType);
        }
    }
    
    private static Map<TRequestToken, TReturnStatus> getSurlPerPersistentTokenStatuses(TSURL surl,
            GridUserInterface user)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        tokenStatusMap.putAll(buildPtGTokenStatusMap(PtGChunkCatalog.getInstance().lookupPtGChunkData(surl, user)));
        tokenStatusMap.putAll(buildPtPTokenStatusMap(PtPChunkCatalog.getInstance().lookupPtPChunkData(surl, user)));
        tokenStatusMap.putAll(buildCopyTokenStatusMap(CopyChunkCatalog.getInstance()
                                                                      .lookupCopyChunkData(surl, user)));
        tokenStatusMap.putAll(buildBoLTokenStatusMap(BoLChunkCatalog.getInstance().lookupBoLChunkData(surl, user)));
        return tokenStatusMap;
    }
    
    private static Map<TRequestToken, TReturnStatus> getSurlPerPersistentTokenStatuses(TSURL surl)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        tokenStatusMap.putAll(buildPtGTokenStatusMap(PtGChunkCatalog.getInstance().lookupPtGChunkData(surl)));
        tokenStatusMap.putAll(buildPtPTokenStatusMap(PtPChunkCatalog.getInstance().lookupPtPChunkData(surl)));
        tokenStatusMap.putAll(buildCopyTokenStatusMap(CopyChunkCatalog.getInstance().lookupCopyChunkData(surl)));
        tokenStatusMap.putAll(buildBoLTokenStatusMap(BoLChunkCatalog.getInstance().lookupBoLChunkData(surl)));
        return tokenStatusMap;
    }

  
    
    private static Map<TRequestToken, TReturnStatus> buildPtGTokenStatusMap(
            Collection<PtGPersistentChunkData> chunksData)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        for(PtGPersistentChunkData chunkData : chunksData)
        {
            tokenStatusMap.put(chunkData.getRequestToken(), chunkData.getStatus());
        }
        return tokenStatusMap;
    }

    private static Map<TRequestToken, TReturnStatus> buildPtPTokenStatusMap(
            Collection<PtPPersistentChunkData> chunksData)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        for (PtPPersistentChunkData chunkData : chunksData)
        {
            tokenStatusMap.put(chunkData.getRequestToken(), chunkData.getStatus());
        }
        return tokenStatusMap;
    }

    private static Map<TRequestToken, TReturnStatus> buildCopyTokenStatusMap(
            Collection<CopyPersistentChunkData> chunksData)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        for (CopyPersistentChunkData chunkData : chunksData)
        {
            tokenStatusMap.put(chunkData.getRequestToken(), chunkData.getStatus());
        }
        return tokenStatusMap;
    }

    private static Map<TRequestToken, TReturnStatus> buildBoLTokenStatusMap(
            Collection<BoLPersistentChunkData> chunksData)
    {
        HashMap<TRequestToken, TReturnStatus> tokenStatusMap = new HashMap<TRequestToken, TReturnStatus>();
        for (BoLPersistentChunkData chunkData : chunksData)
        {
            tokenStatusMap.put(chunkData.getRequestToken(), chunkData.getStatus());
        }
        return tokenStatusMap;
    }
    
    private static void removeStartingStatus(List<TReturnStatus> statuses)
    {
        Iterator<TReturnStatus> iterator = statuses.iterator();
        while(iterator.hasNext())
        {
            TReturnStatus status = iterator.next();
            if(status.getStatusCode().equals(TStatusCode.SRM_REQUEST_QUEUED))
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
    
    private static TReturnStatus extractMostRecentStatus(Collection<TReturnStatus> statuses)
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

    public static TRequestType isPersisted(TRequestToken requestToken) throws IllegalArgumentException
    {
        if(requestToken == null)
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken);
        }
        return RequestSummaryCatalog.getInstance().typeOf(requestToken);
    }

}
