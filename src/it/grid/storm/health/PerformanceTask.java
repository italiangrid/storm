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
public class PerformanceTask extends TimerTask {

    private PerformanceGlance detective;
    private Logger PERF_LOG = HealthDirector.getPerformanceLogger();
    private long progressivNumber = 0L;

    protected PerformanceTask() {
        detective = new PerformanceGlance();
    }

    /* (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        progressivNumber++;
        PerformanceStatus status = detective.haveaLook();
        status.setPulseNumber(progressivNumber);
        PERF_LOG.info(status.toString());

    }

}
