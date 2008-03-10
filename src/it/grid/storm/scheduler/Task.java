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

public abstract class Task implements Runnable, Comparable {
    private static String UNDEF_TASKNAME = "undefined";
    private long creationTime = System.currentTimeMillis();
    private long enqueueTime = 0L;
    private long startExecutionTime = 0L;
    private long endExecutionTime = 0L;
    private long abortingEventTime = 0L;
    private long suspendingEventTime = 0L;
    protected String taskName = null;

    protected Task()
    {
	taskName = UNDEF_TASKNAME;
	creationTime = System.currentTimeMillis();
    }


    protected Task(String name)
    {
	taskName = name;
        if (taskName == null) taskName = UNDEF_TASKNAME;
	creationTime = System.currentTimeMillis();
    }


    public long getStartExecutionTime() {
      return this.startExecutionTime;
    }


    public long howlongBeforeUnqueue()
    {
	return enqueueTime-creationTime;
    }


    public long howlongInQueue()
    {
	return startExecutionTime-enqueueTime;
    }


    public long howlongInExecution()
    {
	return endExecutionTime-startExecutionTime;
    }


    protected void enqueueEvent()
    {
	this.enqueueTime = System.currentTimeMillis();
    }


    protected void abortEvent() {
      this.abortingEventTime = System.currentTimeMillis();
    }

    protected void suspendEvent() {
      this.suspendingEventTime = System.currentTimeMillis();
    }

    protected void runEvent()
    {
	this.startExecutionTime = System.currentTimeMillis();
    }


    protected void endEvent()
    {
	this.endExecutionTime = System.currentTimeMillis();
    }


    protected String getName()
    {
	return taskName;
    }


    public abstract void run();


    public abstract int compareTo(Object o);


    public abstract boolean equals(Object o);
}
