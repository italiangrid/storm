package it.grid.storm.balancer;


public class SmartRRManager {

	private static SmartRRManager instance = new SmartRRManager();
	private long cacheEntryLifetime = 20000; // 5 seconds
	private final ResponsivenessCache cache = new ResponsivenessCache(
		cacheEntryLifetime);

	/**
     * 
     */
	private SmartRRManager() {

	}

	/**
	 * @return
	 */
	public static SmartRRManager getInstance() {

		return instance;
	}

	/**
	 * @param hostname
	 * @param port
	 */
	public void add(Node node) {

		cache.addEntry(node);
	}

	/**
	 * @param hostname
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public boolean isResponsive(Node node) throws Exception {

		return cache.getResponsiveness(node).equals(
			ResponsivenessCache.Responsiveness.RESPONSIVE);
	}

	public void setCacheEntryLifetime(Long lifetime) {

		this.cacheEntryLifetime = lifetime;
	}

}
