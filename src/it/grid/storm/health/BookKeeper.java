package it.grid.storm.health;

import java.util.ArrayList;

import org.slf4j.Logger;

public abstract class BookKeeper {

    protected Logger bookKeepingLog = HealthDirector.getBookKeepingLogger();
    protected Logger performanceLog = HealthDirector.getPerformanceLogger();

    protected ArrayList<LogEvent> logbook = new ArrayList<LogEvent>();

    /**
     *
     * @param logEvent LogEvent
     */
    public abstract void addLogEvent(LogEvent logEvent);



    /**
     *
     */
    public void cleanLogBook() {
        logbook.clear();
    }


    ////////////////////////////////////////////////////////////////

    /**
     *
     * @param msg String
     */
    protected void logDebug(String msg) {
        if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
            bookKeepingLog.debug("BK: " + msg);
        }
        /**
        if ((HealthDirector.isPerformanceMonitorConfigured())&&(HealthDirector.isPerformanceMonitorEnabled())) {
            performanceLog.debug("P: " + msg);
        }
         **/
    }

    /**
     *
     * @param msg String
     */
    protected void logInfo(String msg) {
        if ((HealthDirector.isBookKeepingConfigured())&&(HealthDirector.isBookKeepingEnabled())) {
            bookKeepingLog.info(msg);
        }
        /**
        if ((HealthDirector.isPerformanceMonitorConfigured())&&(HealthDirector.isPerformanceMonitorEnabled())) {
            performanceLog.info(msg);
        }
         **/

    }


}
