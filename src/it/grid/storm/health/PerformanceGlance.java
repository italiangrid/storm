/**
 * 
 */
package it.grid.storm.health;

import java.util.ArrayList;

import org.slf4j.Logger;

/**
 * @author zappi
 *
 */
public class PerformanceGlance {

    private static Logger PERFLOG = HealthDirector.getPerformanceLogger();

    /**
     *
     * @return StoRMStatus
     */
    public PerformanceStatus haveaLook() {
        HealthDirector.LOGGER.debug("Having a look..");
        PerformanceStatus performanceStatus = null;

        PerformanceBookKeeper pbk = HealthDirector.getHealthMonitor().getPerformanceBookKeeper();

        if (pbk!=null) {
            performanceStatus = pbk.getPerformanceStatus();
            ArrayList<LogEvent> zombies = pbk.removeZombieEvents();
            HealthDirector.LOGGER.debug("Removed # <"+zombies.size()+"> zombies.");
            HealthDirector.LOGGER.debug("have a look : "+performanceStatus);
            // *********************** pbk.cleanLogBook();
        }

        HealthDirector.LOGGER.debug(".. glance ended.");
        return performanceStatus;
    }

}
