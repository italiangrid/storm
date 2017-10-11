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

public class RequestsGarbageCollector extends TimerTask {

	private static final Logger log = LoggerFactory
		.getLogger(RequestsGarbageCollector.class);

	private final Configuration config = Configuration.getInstance();
	private final RequestSummaryDAO dao = RequestSummaryDAO.getInstance();
	private final PtGChunkCatalog ptgCat = PtGChunkCatalog.getInstance();
	private final BoLChunkCatalog bolCat = BoLChunkCatalog.getInstance();

	private Timer handler;
	private long delay;

	class TGarbageData {

		int nPurgedRequests = 0;
		int nPurgedRecalls = 0;

		int getTotalPurged() {

			return nPurgedRequests + nPurgedRecalls;
		}
	}

	public RequestsGarbageCollector(Timer handlerTimer, long delay) {

		this.delay = delay;
		handler = handlerTimer;
	}

	@Override
	public void run() {

		try {

			TGarbageData gd = purgeExpiredRequests();

			if (gd.getTotalPurged() == 0) {

				log.trace(
					"GARBAGE COLLECTOR didn't find completed requests older than {} seconds",
					config.getExpiredRequestTime());

			} else if (gd.nPurgedRecalls > 0) {

				log.info(
					"GARBAGE COLLECTOR removed < {} > completed requests (< {} > recall) older than {} seconds",
					gd.nPurgedRequests, gd.nPurgedRecalls,
					config.getExpiredRequestTime());

			} else {

				log.info(
					"GARBAGE COLLECTOR removed < {} > completed requests older than {} seconds",
					gd.nPurgedRequests, config.getExpiredRequestTime());
			}

			long nextDelay = computeNextDelay(gd);

			if (nextDelay != delay) {

				log.info("GARBAGE COLLECTOR: tuning new interval to {} seconds",
					nextDelay / 1000);
				delay = nextDelay;

			}

		} catch (Throwable t) {

			/* useful to prevent unexpected exceptions that would kill the GC */
			log.error(t.getMessage(), t);

		} finally {

			reschedule();
		}
	}

	/**
	 * Delete from database the completed requests older than a specified and
	 * configurable value.
	 * 
	 * @return A TGarbageData object containing info about the deleted requests
	 */
	private TGarbageData purgeExpiredRequests() {

		TGarbageData out = new TGarbageData();

		if (!enabled()) {
			return out;
		}

		out.nPurgedRequests = purgeExpiredRequests(config.getPurgeBatchSize(),
			config.getExpiredRequestTime());

		out.nPurgedRecalls = purgeExpiredRecallRequests(config.getPurgeBatchSize());

		return out;
	}

	/**
	 * Check if Garbage Collector is enabled or not.
	 * 
	 * @return If the purger is enabled. False otherwise.
	 */
	private boolean enabled() {

		return config.getExpiredRequestPurging();
	}

	/**
	 * Method used to purge from db a bunch of completed requests, older than the
	 * specified @expiredRequestTime.
	 * 
	 * @param purgeSize
	 *          The maximum size of the bunch of expired requests that must be
	 *          deleted
	 * @param expiredRequestTime
	 *          The number of seconds after that a request can be considered
	 *          expired
	 * @return The number of requests involved.
	 */
	private synchronized int purgeExpiredRequests(int purgeSize,
		long expiredRequestTime) {

		ptgCat.transitExpiredSRM_FILE_PINNED();
		bolCat.transitExpiredSRM_SUCCESS();

		return dao.purgeExpiredRequests(expiredRequestTime, purgeSize).size();

	}

	/**
	 * Method used to purge from db a bunch of completed recall requests.
	 * 
	 * @param purgeSize
	 *          The maximum size of the bunch of expired requests that must be
	 *          deleted
	 * @return The number of requests involved.
	 */
	private synchronized int purgeExpiredRecallRequests(int purgeSize) {

		int n = new TapeRecallCatalog().purgeCatalog(purgeSize);
		if (n == 0) {
			log.trace("No entries have been purged from tape_recall table");
		} else {
			log.info("{} entries have been purged from tape_recall table", n);
		}
		return n;
	}

	/**
	 * Compute a new delay. It will be decreased if the number of purged requests
	 * is equal to the purge.size value. Otherwise, it will be increased until
	 * default value.
	 * 
	 * @return the computed next interval predicted from last removed requests
	 *         info
	 */
	private long computeNextDelay(TGarbageData gd) {

		/* max delay from configuration in milliseconds */
		long maxDelay = config.getRequestPurgerPeriod() * 1000L;
		/* min delay accepted in milliseconds */
		long minDelay = 10000L;

		long nextDelay;

		/* Check purged requests value */
		if (gd.getTotalPurged() >= config.getPurgeBatchSize()) {

			/* bunch size reached: decrease interval */
			nextDelay = Math.max(delay / 2, minDelay);

		} else {

			/* bunch size not reached: increase interval */
			nextDelay = Math.min(delay * 2, maxDelay);

		}

		return nextDelay;
	}

	/**
	 * Schedule another task after @delay milliseconds.
	 */
	private void reschedule() {

		handler.schedule(new RequestsGarbageCollector(handler, delay), delay);
	}
}
