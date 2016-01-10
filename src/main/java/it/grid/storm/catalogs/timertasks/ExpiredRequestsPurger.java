package it.grid.storm.catalogs.timertasks;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryDAO;
import it.grid.storm.config.Configuration;


public class ExpiredRequestsPurger extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(ExpiredRequestsPurger.class);
	private final Configuration config = Configuration.getInstance();
	private final RequestSummaryDAO dao = RequestSummaryDAO.getInstance();
	private final PtGChunkCatalog ptgCat = PtGChunkCatalog.getInstance();
	private final BoLChunkCatalog bolCat = BoLChunkCatalog.getInstance();

	private Timer handler;
	
	public ExpiredRequestsPurger(Timer handlerTimer) {
		
		handler = handlerTimer;
	}
	
	@Override
	public void run() {

		if (!enabled()) {
			return;
		}
		
		int n = purgeExpiredRequests(config.getPurgeBatchSize(), config.getExpiredRequestTime());
		log.info("REQUEST SUMMARY CATALOG; removed from DB < {} > expired requests",
			n);
		
		handler.schedule(new ExpiredRequestsPurger(handler), config.getRequestPurgerPeriod() * 1000);
	}
	
	/**
	 * @return True if the purger is enabled. False otherwise.
	 */
	private boolean enabled() {
		
		return Configuration.getInstance().getExpiredRequestPurging();
	}

	/**
	 * Method used to purge the DB of expired requests, and remove the
	 * corresponding proxies if available.
	 * 
	 * @param purgeSize
	 * 		The maximum size of the bunch of expired requests that must be deleted
	 * @param expiredRequestTime
	 *    The number of seconds after that a request can be considered expired
	 * @return The list of the request tokens involved.
	 */
	synchronized private int purgeExpiredRequests(int purgeSize, long expiredRequestTime) {

		ptgCat.transitExpiredSRM_FILE_PINNED();
		bolCat.transitExpiredSRM_SUCCESS();

		return dao.purgeExpiredRequests(expiredRequestTime, purgeSize).size();

	}
}
