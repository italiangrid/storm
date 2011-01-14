/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * 
 */
package it.grid.storm.health;

import java.util.ArrayList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author zappi
 *
 */
public class PerformanceBookKeeper extends BookKeeper {

    public static final String KEY = "PERF";

    private static long THOUSAND = 1000L;

    private DelayQueue<LogEvent> timedLogBook = new DelayQueue<LogEvent>();
    private int lenghtInSeconds = 0;
    private long lenghtInMSec = 0;
    private long visibleToGlancerInMSec = 0;

    public PerformanceBookKeeper(int timeWindowInSecond, int glancerPeriodInSec) {
        this.lenghtInSeconds = timeWindowInSecond;
        this.lenghtInMSec = timeWindowInSecond * THOUSAND;
        this.visibleToGlancerInMSec = glancerPeriodInSec * THOUSAND;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.health.BookKeeper#addLogEvent(it.grid.storm.health.LogEvent)
     */
    @Override
    public void addLogEvent(LogEvent logEvent) {
        boolean result = timedLogBook.offer(logEvent);
        HealthDirector.LOGGER.debug("TimedLOGBOOK (offering result) "+result);
        HealthDirector.LOGGER.debug("TimedLOGBOOK :"+timedLogBook.size());
    }


    public long getGlanceWindowInMSec() {
        return this.visibleToGlancerInMSec;
    }

    public int getTimeWindowInSecond() {
        return this.lenghtInSeconds;
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
        logDebug("Removed "+nZombies+"oldest event in Delayed log book.");
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
        removeZombieEvents(); //discard the zombies
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
        //System.out.println("time to live - glance: "+timeToLiveGraterThan);
        removeZombieEvents();
        for (LogEvent event : timedLogBook) {
            //System.out.println("event: "+event.getDelay(TimeUnit.MILLISECONDS));
            if ((event.getDelay(TimeUnit.MILLISECONDS)) < timeToLiveGraterThan) {
                eGlanced.add(event);
            }
        }
        //System.out.println("Nr. Events to analyze: "+eGlanced.size());
        return eGlanced;
    }


    public PerformanceStatus getPerformanceStatus(long timeToLiveGraterThan) {
        PerformanceStatus pStatus = new PerformanceStatus(getEventsGlanced(timeToLiveGraterThan));
        return pStatus;
    }

    public PerformanceStatus getPerformanceStatus() {
        return getPerformanceStatus(this.visibleToGlancerInMSec);
    }


}
