package it.grid.storm.balancer;

import it.grid.storm.balancer.ftp.CyclicCounter;
import it.grid.storm.balancer.ftp.SmartRRManager;
import it.grid.storm.config.Configuration;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class SmartRoundRobinStrategy <E extends Node> extends AbstractBalancingStrategy<E> {

    private static final Logger log = LoggerFactory.getLogger(SmartRoundRobinStrategy.class);
    
    
	private final CyclicCounter counter;
	
    public SmartRoundRobinStrategy(List<E> nodes) throws IllegalArgumentException
    {
        super(nodes);
        if(nodes == null || nodes.size() == 0)
        {
            throw new IllegalArgumentException("Unable to build SmartRoundRobinStrategy, " +
            		"received null/empty node pool");
        }
        
        counter = new CyclicCounter(nodes.size() - 1);
        SmartRRManager manager = SmartRRManager.getInstance();
        for(E node : nodes)
        {
            manager.add(node.getHostName(), node.getPort());
        }
        Long lifetime = Configuration.getInstance().getGridftpPoolStatusCheckTimeout();
        if(lifetime != null)
        {
            manager.setCacheEntryLifetime(lifetime);
        }
    }
	
	@Override
    public E getNextElement() throws BalancingStrategyException
    {
        int attempts = 0;
        SmartRRManager manager = SmartRRManager.getInstance();

        E remoteService = null;
        boolean responsiveFound = false;
        while (!responsiveFound)
        {
            attempts++;
            //maybe should be better to remove the CyclicCounterand use a concurrent list
            remoteService =  nodePool.get(counter.next());
            try
            {
                responsiveFound = manager.isResponsive(remoteService.getHostName(), remoteService.getPort());
            } catch(Exception e)
            {
                log.warn("Unable to check the status of the The GFTP " + remoteService.toString()
                        + " . .Exception : " + e.getMessage());
                throw new BalancingStrategyException("Unable to check the status of the The GFTP");
            }
            if (!responsiveFound)
            {
                if (attempts >= nodePool.size())
                {
                    // No one remote service is responsive!
                    log.warn("No one remote service is responsive!");
                    throw new BalancingStrategyException("No remote services are responsive");
                }
            }
        }
        return remoteService;
    }

	@Override
	public void notifyChangeInPool() {
	}
}
