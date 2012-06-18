package it.grid.storm.balancer.ftp;


import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponsivenessCache
{

    private static final Logger log = LoggerFactory.getLogger(ResponsivenessCache.class);
    private final Map<InetSocketAddress, ResponsivenessCacheEntry> cache =  new HashMap<InetSocketAddress, ResponsivenessCacheEntry>();
    private long entryLifetime;
    
    public enum Responsiveness
    {
        RESPONSIVE, UNRESPONSIVE, UNKNOWN
    }

    public ResponsivenessCache(long entryLifetimeMillisec)
    {
        this.entryLifetime = entryLifetimeMillisec;
    }

    public ResponsivenessCacheEntry addEntry(String hostname, int port)
    {
        log.debug("Adding cache entry for " + hostname + ":" + port);
        ResponsivenessCacheEntry newEntry = this.new ResponsivenessCacheEntry(hostname, port,Responsiveness.UNKNOWN);
        cache.put(newEntry.getRemoteService(), newEntry);
        return newEntry;
    }

    /**
     * @param hostname
     * @param port
     * @return
     * @throws Exception 
     */
    public Responsiveness getResponsiveness(String hostname, int port) throws Exception
    {
        Responsiveness resp = Responsiveness.UNKNOWN;
        ResponsivenessCacheEntry entry = getEntry(hostname, port);
        if (entry != null)
        {
            if (isExpired(entry))
            {
                log.debug("Cache entry " + entry.getRemoteService().toString() + " expired. Refresching");
                resp = entry.refresh();
            }
            else
            {
                log.debug("Found valid cache entry for " + entry.getRemoteService().toString());
                resp = entry.getStatus();
            }
        }
        else
        {
            log.debug("Missing cache entry for " + hostname + ":" + port + " .Adding and refresching");
            entry = addEntry(hostname, port);
            resp = entry.refresh();
        }
        return resp;
    }

    /**
     * @param entry
     * @return
     */
    public boolean isExpired(ResponsivenessCacheEntry entry)
    {
        if (entry != null)
        {
            return entry.isExpired(this.entryLifetime);
        }
        return true;
    }
    
    /**
     * @param hostname
     * @param port
     * @return null if there is no entry for the provided parameters
     */
    public ResponsivenessCacheEntry getEntry(String hostname, int port)
    {
        return cache.get(new InetSocketAddress(hostname, port));
    }
    
    private class ResponsivenessCacheEntry
    {

        private final InetSocketAddress remoteService;
        private long checkTime = -1;
        private Responsiveness status = Responsiveness.UNKNOWN;

        public ResponsivenessCacheEntry(String hostname, int port, Responsiveness status)
        {
            remoteService = new InetSocketAddress(hostname, port);
//            if (remoteService.isUnresolved())
//            {
//                resolvedAddress = false;
//            }
//            else
//            {
//                resolvedAddress = true;
//            }
            this.status = status;
        }

        /**
         * @return the remoteService
         */
        public final InetSocketAddress getRemoteService()
        {
            return remoteService;
        }

        /**
         * @return the status
         */
        public final Responsiveness getStatus()
        {
            return status;
        }

        /**
         * @param status the status to set
         */
        private final void setStatus(Responsiveness status)
        {
            this.status = status;
        }

        /**
         * @param lifetime
         * @return
         */
        public boolean isExpired(long lifetime)
        {
            return System.currentTimeMillis() - checkTime >= lifetime;
        }

        /**
         * @return
         * @throws Exception 
         */
        public final Responsiveness refresh() throws Exception
        {
            Responsiveness respness = Responsiveness.UNKNOWN;
            if (CheckControlChannel.checkGFtpServer(remoteService))
            {
                respness = Responsiveness.RESPONSIVE;
            }
            else
            {
                respness = Responsiveness.UNRESPONSIVE;
            }
            this.checkTime = new Date().getTime();
            this.setStatus(respness);
            return this.status;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("ResponsivenessCacheEntry [remoteService=");
            builder.append(remoteService);
            builder.append(", checkTime=");
            builder.append(checkTime);
            builder.append(", status=");
            builder.append(status);
            builder.append("]");
            return builder.toString();
        }
    }
}
