package it.grid.storm.asynch;

import it.grid.storm.scheduler.Scheduler;

/**
 * This is a Facade to the Schedulers.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    April 25th, 2005
 * @version 1.0
 */
public class SchedulerFacade {

    private static SchedulerFacade sf = new SchedulerFacade();
    private Scheduler crusherSched = SchedulerFactory.crusherSched(); //Scheduler that manages Feeder tasks
    private Scheduler chunkSched = SchedulerFactory.chunkSched(); //Scheduler that manages Chunk tasks

    private SchedulerFacade() {}

    /**
     * Method that returns the only instance of SchedulerFacade.
     */
    public static SchedulerFacade getInstance() {
        return sf;
    }

    /**
     * Method that returns the Scheduler in charge of handling Chunk.
     */
    public Scheduler chunkScheduler() {
        return chunkSched;
    }

    /**
     * Method that returns the Scheduler in charge of handling Feeder
     */
    public Scheduler crusherScheduler() {
        return crusherSched;
    }
}
