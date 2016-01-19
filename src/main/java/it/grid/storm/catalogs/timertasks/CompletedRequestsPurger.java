package it.grid.storm.catalogs.timertasks;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryDAO;
import it.grid.storm.config.Configuration;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;


public class CompletedRequestsPurger extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(CompletedRequestsPurger.class);

	private final Configuration config = Configuration.getInstance();
	private final RequestSummaryDAO dao = RequestSummaryDAO.getInstance();
	private final PtGChunkCatalog ptgCat = PtGChunkCatalog.getInstance();
	private final BoLChunkCatalog bolCat = BoLChunkCatalog.getInstance();

	private Timer handler;
	private long delay;
	private int nExpiredRequests;
	private int nExpiredRecalls;
	
	public CompletedRequestsPurger(Timer handlerTimer, long delay) {
		
		this.delay = delay;
		handler = handlerTimer;
		nExpiredRequests = 0;
		nExpiredRecalls = 0;
	}
	
	@Override
	public void run() {

		purgeExpiredRequests();
		recomputeDelay();
		reschedule();
	}
	
	/**
	 * Delete the expired requests from database
	 * 
	 * @return the number of purged requests
	 */
	private void purgeExpiredRequests() {
		
		if (!enabled()) {
			log.debug("GARBAGE COLLECTOR disabled; set purging=true to enable it");
			return;
		}
		
		nExpiredRequests = purgeExpiredRequests(config.getPurgeBatchSize(),
			config.getExpiredRequestTime());
		
		nExpiredRecalls = purgeExpiredRecallRequests(config.getPurgeBatchSize());
		
		if (nExpiredRequests == 0 && nExpiredRecalls == 0) {

			log.trace(
				"GARBAGE COLLECTOR didn't find completed requests older than {} seconds",
				config.getExpiredRequestTime());
		
		} else if (nExpiredRecalls > 0) {
		
			log.info(
				"GARBAGE COLLECTOR removed < {} > completed requests (< {} > recall) older than {} seconds",
				nExpiredRequests, nExpiredRecalls, config.getExpiredRequestTime());
		
		} else {
		
			log.info(
				"GARBAGE COLLECTOR removed < {} > completed requests older than {} seconds",
				nExpiredRequests, config.getExpiredRequestTime());
		}
	}
	
	/**
	 * @return True if the purger is enabled. False otherwise.
	 */
	private boolean enabled() {
		
		return config.getExpiredRequestPurging();
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
	
	synchronized private int purgeExpiredRecallRequests(int purgeSize) {
		
		int n = new TapeRecallCatalog().purgeCatalog(purgeSize);
		if (n == 0) {
			log.trace("No entries have been purged from tape_recall table");
		} else {
			log.info("{} entries have been purged from tape_recall table", n);
		}
		return n;
	}
	
	/**
	 * Compute a new delay. It will be decreased if the number of purged
	 * requests is equal to the purge.size value. Otherwise, it will be increased
	 * until default value.
	 */
	private void recomputeDelay() {
		
		/* max delay from configuration in milliseconds */
		long maxDelay = config.getRequestPurgerPeriod() * 1000;
		/* min delay accepted in milliseconds */
		long minDelay = 10000;
		
		long nextDelay;
		if ((nExpiredRequests + nExpiredRecalls) >= config.getPurgeBatchSize()) {
			nextDelay = Math.max(delay / 2, minDelay);
		} else {
			nextDelay = Math.min(delay * 2, maxDelay);
		}
		
		if (nextDelay != delay) {
			log.info("GARBAGE COLLECTOR: tuning new interval to {} seconds",
				nextDelay / 1000);
		}
		
		delay = nextDelay;
	}
	
	/**
	 * Schedule another task after @delay milliseconds.
	 */
	private void reschedule() {
		
		handler.schedule(new CompletedRequestsPurger(handler, delay), delay);
	}
}
