package it.grid.storm.scheduler;

import org.apache.log4j.Logger;

import it.grid.storm.config.Configuration;

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

public class CrusherScheduler implements Scheduler

{
    private static int clients = 0;
    private WorkerPool crusherPool = null;
    private static final Logger log = Logger.getLogger("scheduler");
    private SchedulerStatus[] schedStatus = null;

    /**
     *   Default values for Pools
     */
    // Value of "scheduler.crusher.workerCorePoolSize"
    private int workerCorePoolSize = Configuration.getInstance().getCorePoolSize();


    // Value of "scheduler.crusher.workerMaxPoolSize"
    private int workerMaxPoolSize = Configuration.getInstance().getMaxPoolSize();


    // Value of "scheduler.crusher.queueSize"
    private int queueSize = Configuration.getInstance().getQueueSize();


    private static CrusherScheduler istance = null;

    private CrusherScheduler()
    {
	schedStatus = new SchedulerStatus[1];
	schedStatus[0] = new SchedulerStatus("CrusherScheduler");

	/**
	 * @todo  Read to Configuration the information about worker Pool
	 * structure of Crusher Scheduler
	 */

	crusherPool = new WorkerPool(workerCorePoolSize, workerMaxPoolSize, queueSize);
	schedStatus[0].setCorePoolSize(workerCorePoolSize);
	schedStatus[0].setMaxPoolSize(workerMaxPoolSize);
        schedStatus[0].setQueueSize(queueSize);
    }


    public static CrusherScheduler getInstance()
    {
	log.debug("Called multi-thread scheduler");
	if (istance==null) {
	    istance = new CrusherScheduler();
	}
	return istance;
    }


    public void schedule(Delegable cruncherTask) throws SchedulerException
    {
	log.debug("Scheduling feed "+cruncherTask.getName());

	try {
	    Task task = new CruncherTask(cruncherTask);
	    crusherPool.submit(task);
	    log.debug("Feed task nr. = "+crusherPool.getTaskCount());
	    log.debug("Scheduled feed "+cruncherTask.getName());
	}
	catch (SchedulerException se) {
	    log.error(se);
	    throw se;
	}
    }


    public SchedulerStatus getStatus(int workerPoolType)
    {
	SchedulerStatus st = null;
	switch (workerPoolType) {
	    case 0:
		schedStatus[0].setCompletedTaskCount(crusherPool.getCompletedTaskCount());
		schedStatus[0].setActiveCount(crusherPool.getActiveCount());
		schedStatus[0].setLargestPoolSize(crusherPool.getLargestPoolSize());
		schedStatus[0].setPoolSize(crusherPool.getActualPoolSize());
		schedStatus[0].setTaskCount(crusherPool.getTaskCount());
		schedStatus[0].setRemainingCapacity(crusherPool.getRemainingCapacity());
		st = schedStatus[0];
		break;
	    default:
		st = null;
		break;
	}
	return st;
    }

    /**
     *
     * @param task Delegable
     * @throws SchedulerException
     */
    public void abort(Delegable task) throws SchedulerException {
      //crusherPool.remove(task);
    }


    /**
     *
     * @param task Delegable
     * @throws SchedulerException
     */
    public void suspend(Delegable task) throws SchedulerException {
      throw new SchedulerException("CruscherScheduler","Suspend request not implemented yet!");
    }

}
