package it.grid.storm.health;

import java.util.TimerTask;

import org.slf4j.Logger;

public class Hearthbeat extends TimerTask {

    private DetectiveGlance detective;
    private Logger HEALTH_LOG = HealthDirector.HEARTLOG;
    private long progressivNumber = 0L;

    protected Hearthbeat() {
        detective = new DetectiveGlance();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     *
     * @todo Implement this java.lang.Runnable method
     */
    @Override
    public void run() {
        progressivNumber++;
        StoRMStatus status = detective.haveaLook();
        status.setPulseNumber(progressivNumber);
        HEALTH_LOG.debug("*** HEARTHBEAT ***");
        HEALTH_LOG.info(status.toString());
    }
}
