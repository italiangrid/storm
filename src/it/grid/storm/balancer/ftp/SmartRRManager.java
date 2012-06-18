package it.grid.storm.balancer.ftp;

public class SmartRRManager
{

    private static SmartRRManager instance = new SmartRRManager();
    private long cacheEntryLifetime = 20000; // 5 seconds
    private final ResponsivenessCache cache = new ResponsivenessCache(cacheEntryLifetime);

    /**
     * 
     */
    private SmartRRManager()
    {
    }

    /**
     * @return
     */
    public static SmartRRManager getInstance()
    {
        return instance;
    }

    /**
     * @param hostname
     * @param port
     */
    public void add(String hostname, int port)
    {
        cache.addEntry(hostname, port);
    }
    
    /**
     * @param hostname
     * @param port
     * @return
     * @throws Exception 
     */
    public boolean isResponsive(String hostname, int port) throws Exception
    {
        return cache.getResponsiveness(hostname, port).equals(ResponsivenessCache.Responsiveness.RESPONSIVE);
    }

    public void setCacheEntryLifetime(Long lifetime)
    {
        this.cacheEntryLifetime = lifetime;
    }

}
