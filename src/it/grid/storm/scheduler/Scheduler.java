package it.grid.storm.scheduler;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy </p>
 *
 * @author Zappi Riccardo  <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 * @date
 *
 */



public interface Scheduler {

    public static int PtG_WorkerPoolType =0;
    public static int PtP_WorkerPoolType = 1;
    public static int Copy_WorkerPoolType = 2;
    public static int BoL_WorkerPoolType = 3;
    public static int CrusherSchedule = 4;

    /**
     * Method that accepts a Task for scheduling.
     *
     * @param t Delegable
     * @throws SchedulerException
     */
    public void schedule(Delegable t) throws SchedulerException;

    /**
     *
     * @param task Delegable
     * @throws SchedulerException
     */
    public void abort(Delegable task) throws SchedulerException;

    /**
     *
     * @param task Delegable
     * @throws SchedulerException
     */
    public void suspend(Delegable task) throws SchedulerException;

    /**
     *
     * @param workerPoolType int
     * @return SchedulerStatus
     */
    public SchedulerStatus getStatus(int workerPoolType);

}
