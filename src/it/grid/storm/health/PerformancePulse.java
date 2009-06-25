/**
 * 
 */
package it.grid.storm.health;

import java.util.TimerTask;

import org.slf4j.Logger;

/**
 * @author zappi
 *
 */
public class PerformancePulse extends TimerTask {

    private PerformanceGlance perfMonitor;
    private Logger PERF_LOG = HealthDirector.getPerformanceLogger();
    private long progressivNumber = 0L;


    protected PerformancePulse() {
        perfMonitor = new PerformanceGlance();
    }

    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        HealthDirector.LOGGER.debug("PERFORMANCE PULSE");
        progressivNumber++;
        PerformanceStatus status = perfMonitor.haveaLook();
        status.setPulseNumber(progressivNumber);
        PERF_LOG.info(status.toString());
    }

}
