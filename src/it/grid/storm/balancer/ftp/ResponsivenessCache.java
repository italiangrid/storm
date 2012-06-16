package it.grid.storm.balancer.ftp;

import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ResponsivenessCache {

	private static final Logger log = LoggerFactory.getLogger(ResponsivenessCache.class);
	private Map<InetSocketAddress, ResponsivenessCacheEntry> cache ;
	private long entryLifetime;
	
	public ResponsivenessCache(long entryLifetimeMillisec) {		
       this.entryLifetime = entryLifetimeMillisec;
	}

	public void addEntry(String hostname, int port)  {
		ResponsivenessCacheEntry newEntry = new ResponsivenessCacheEntry(hostname, port);
		cache.put(newEntry.getRemoteService(),newEntry);
	}
	
	public ResponsivenessCacheEntry getByHostname(String hostname) {
       for (InetSocketAddress inetsocket : cache.keySet()) {
    	   if (inetsocket.getHostName().equals(hostname)) {
    		   return cache.get(inetsocket);
    	   }
	   }
       return null;
	}
	
	public ResponsivenessCacheEntry getByHostname(String hostname, int port) {
		InetSocketAddress inetsocket = new InetSocketAddress(hostname, port);
		return cache.get(inetsocket);
	}
	
	
	public boolean existCacheEntry(String hostname) {
		 for (InetSocketAddress inetsocket : cache.keySet()) {
	    	   if (inetsocket.getHostName().equals(hostname)) {
	    		   return true;
	    	   }
		 }
		 return false;
	}
	
	
	public boolean isExpired(String hostname) {
		ResponsivenessCacheEntry entry = getByHostname(hostname);
		if (entry!=null) {
			if (System.currentTimeMillis() - entry.getCheckTime() > this.entryLifetime) {
				return true;
			} else {
				return false;
			}	
		} else {
			return false;
		}
	}
	
	public boolean isExpired(String hostname, int port) {
		ResponsivenessCacheEntry entry = getByHostname(hostname, port);
		if (entry!=null) {
           return entry.isExpired(this.entryLifetime);	
		} 
        return true;
	}
	
	
	public void refreshCacheEntry(String hostname) {
		ResponsivenessCacheEntry entry = getByHostname(hostname);
		if (entry!=null) {
			if (isExpired(hostname)) {
			    entry.refresh();
		    } 
		}
	}
	
	
	public void refreshCacheEntry(String hostname, int port) {
		ResponsivenessCacheEntry entry = getByHostname(hostname, port);
		if (entry!=null) {
			if (isExpired(hostname)) {
			    entry.refresh();
		    } 
		}
	}
	
	public ResponsivenessCacheEntry.Responsiveness getResponsiveness(String hostname, int port) {
		if (isExpired(hostname, port)) {
			refreshCacheEntry(hostname, port);
		}
		ResponsivenessCacheEntry entry = getByHostname(hostname, port);
		if (entry!=null) {
			return entry.getStatus();
		} 
		return ResponsivenessCacheEntry.Responsiveness.UNKNOWN;
	}
	
	
}
