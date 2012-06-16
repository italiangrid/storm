package it.grid.storm.balancer;

import it.grid.storm.balancer.ftp.CyclicCounter;
import it.grid.storm.balancer.ftp.FTPNode;
import it.grid.storm.balancer.ftp.SmartRRManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class SmartRoundRobinStrategy <E extends Node> extends AbstractStrategy<E> {

    private static final Logger logger = Logger.getLogger(SmartRoundRobinStrategy.class.getName());
	
	private CyclicCounter counter;
	private int poolSize;
	
	public SmartRoundRobinStrategy(List<E> pool) {
		super(pool);
		poolSize = pool.size();
		counter = new CyclicCounter(poolSize);
		for (E e : pool) {
			SmartRRManager mngr = SmartRRManager.getInstance();
			mngr.add(e.getHostName(),e.getPort());
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public E getNextElement() throws BalancerException {
		AtomicInteger attempts = new AtomicInteger(0);
		SmartRRManager manager = SmartRRManager.getInstance();

		FTPNode remoteService = null;
		boolean responsiveFound = false;
		while (!responsiveFound) {
			remoteService = (FTPNode) nodePool.get(counter.cyclicallyIncrementAndGet());
			responsiveFound = manager.isResponsive(remoteService.getHostName(),remoteService.getPort());
			if (!responsiveFound) {
				if (attempts.addAndGet(1) > poolSize) {
					// No one remote service is responsive!
					logger.warning("No one remote service is responsive!");
					throw new BalancerException();
				}
			}
		}
		return (E)remoteService;
	}

	@Override
	public void notifyChangeInPool() {
		// TODO Auto-generated method stub
		
	}

}
