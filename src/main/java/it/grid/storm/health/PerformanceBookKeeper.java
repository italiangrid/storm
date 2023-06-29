/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.health;

import java.util.ArrayList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 * 
 */
public class PerformanceBookKeeper extends BookKeeper {

	private static final Logger log = LoggerFactory.getLogger(PerformanceBookKeeper.class);
	
	public static final String KEY = "PERF";

	private static long THOUSAND = 1000L;

	private DelayQueue<LogEvent> timedLogBook = new DelayQueue<LogEvent>();
	private int lengthInSeconds = 0;
	private long lengthInMSec = 0;
	private long visibleToGlancerInMSec = 0;

	public PerformanceBookKeeper(int timeWindowInSecond, int glancerPeriodInSec) {

		this.lengthInSeconds = timeWindowInSecond;
		this.lengthInMSec = timeWindowInSecond * THOUSAND;
		this.visibleToGlancerInMSec = glancerPeriodInSec * THOUSAND;
	}

	@Override
	public void addLogEvent(LogEvent logEvent) {

		boolean result = timedLogBook.offer(logEvent);
		HealthDirector.LOGGER.debug("TimedLOGBOOK (offering result) {}", result);
		HealthDirector.LOGGER.debug("TimedLOGBOOK : {}", timedLogBook.size());
	}

	public long getGlanceWindowInMSec() {

		return this.visibleToGlancerInMSec;
	}

	public int getTimeWindowInSecond() {

		return this.lengthInSeconds;
	}

	/**
	 * getZombieEvents
	 * 
	 * Remove from the queue LogBook the event with lifetime expired
	 * 
	 * @return the arraylist of removed delayed Log Event
	 */
	public ArrayList<LogEvent> removeZombieEvents() {

		ArrayList<LogEvent> zombies = new ArrayList<LogEvent>();
		int nZombies = timedLogBook.drainTo(zombies);
		logDebug("Removed " + nZombies + "oldest event in Delayed log book.");
		return zombies;
	}

	/**
	 * getSnapshot
	 * 
	 * create a purged copy of LogBook of all LogEvent yet alive.
	 * 
	 * @return
	 */
	public ArrayList<LogEvent> getCompleteSnapshot() {

		removeZombieEvents(); // discard the zombies
		ArrayList<LogEvent> snapshot = new ArrayList<LogEvent>(timedLogBook);
		return snapshot;
	}

	/**
	 * getGlancedLogBook
	 * 
	 * return the list cointaing only the LogEvents within the Glance time
	 * interval specified by the parameter 'timeToLiveGraterThan'.
	 * 
	 * Note: When the event is inserted into the timedLogBook has a maximum delay
	 * and when the delay is negative the event is tagged as zombie
	 * 
	 * @return
	 */
	public ArrayList<LogEvent> getEventsGlanced(long timeToLiveGraterThan) {

		ArrayList<LogEvent> eGlanced = new ArrayList<LogEvent>();
		log.debug("time to live - glance: {}",timeToLiveGraterThan);
		removeZombieEvents();
		for (LogEvent event : timedLogBook) {
			log.debug("event: {}", event.getDelay(TimeUnit.MILLISECONDS));
			if ((event.getDelay(TimeUnit.MILLISECONDS)) < timeToLiveGraterThan) {
				eGlanced.add(event);
			}
		}
		log.debug("Nr. Events to analyze: {}", eGlanced.size());

		return eGlanced;
	}

	public PerformanceStatus getPerformanceStatus(long timeToLiveGraterThan) {
		PerformanceStatus pStatus = new PerformanceStatus(
			getEventsGlanced(timeToLiveGraterThan));
		return pStatus;
	}

	public PerformanceStatus getPerformanceStatus() {

		return getPerformanceStatus(this.visibleToGlancerInMSec);
	}

}
