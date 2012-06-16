package it.grid.storm.balancer.ftp;

import it.grid.storm.balancer.ftp.ResponsivenessCacheEntry.Responsiveness;


public class SmartRRManager {

	private static SmartRRManager instance = new SmartRRManager();
	private ResponsivenessCache cache;
	private long cacheEntryLifetime = 5000; //5 seconds
	
	private SmartRRManager() {
		cache = new ResponsivenessCache(cacheEntryLifetime); 
	}
	
	public static SmartRRManager getInstance() {
		return instance;
	}
	

	public void add(String hostname, int port) {
		cache.addEntry(hostname, port);
	}
	
	//Method used to retrieve 
	public boolean isResponsive(String hostname, int port) {
		boolean result = false;
		if (cache.getResponsiveness(hostname, port)==Responsiveness.RESPONSIVE) {
			result = true;
		}
		return result;
	}


	
 


}

