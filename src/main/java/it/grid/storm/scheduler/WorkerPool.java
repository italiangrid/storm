/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.scheduler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2005-2007
 *
 * <p>Company: INFN-CNAF, Bologna, Italy
 *
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.2
 * @date
 */
public class WorkerPool {

  public static final int DEFAULT_CORE_POOL_SIZE = 10;
  public static final int DEFAULT_MAX_POOL_SIZE = 100;
  public static final int DEFAULT_TASK_QUEUE_SIZE = 100;

  private static final Logger log = LoggerFactory.getLogger(WorkerPool.class);

  private ThreadPoolExecutor workers;
  private BlockingQueue<Runnable> taskQueue;
  private int queueSize = 100;

  private long keepAliveTime = 10000; // 10 seconds.
  private TimeUnit unit = TimeUnit.MILLISECONDS;

  public WorkerPool() {

    this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_TASK_QUEUE_SIZE);
  }

  public WorkerPool(int poolSize, int maxPoolSize, int queueSize) {

    this.queueSize = queueSize;
    taskQueue = new ArrayBlockingQueue<>(queueSize);
    workers = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, unit, taskQueue);
  }

  /**
   * @param task Task
   * @throws SchedulerException
   */
  public void submit(Task task) throws SchedulerException {

    log.trace("WorkerPool.submit");
    log.debug("Taskqueue Size: {}", queueSize);
    log.debug("Taskqueue Remaining Capacity: {}", workers.getQueue().remainingCapacity());

    task.enqueueEvent();

    try {
      log.debug("Submitting task {}", task.getName());
      workers.execute(task);
    } catch (RejectedExecutionException e) {
      log.error("Task {} was rejected. {}", task.getName(), e.getMessage(), e);
      throw new SchedulerException(task.getName(), e);
    }
  }

  /**
   * @param task Task
   * @throws SchedulerException
   */
  public void remove(Task task) {

    log.trace("WorkerPool.remove");
    task.abortEvent();
    log.debug("Aborting task {}", task.getName());
    if (workers.remove(task)) {
      // Remove task named "Future task" from internal Queue.
      workers.purge();
    }
  }

  // INFORMATIONS about THREADPOOL

  protected int getActualPoolSize() {

    return workers.getPoolSize();
  }

  protected int getActiveCount() {

    return workers.getActiveCount();
  }

  protected int getLargestPoolSize() {

    return workers.getLargestPoolSize();
  }

  protected long getCompletedTaskCount() {

    return workers.getCompletedTaskCount();
  }

  protected long getTaskCount() {

    return workers.getTaskCount();
  }

  protected int getRemainingCapacity() {

    return workers.getQueue().remainingCapacity();
  }
}
