/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005-2007
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF, Bologna, Italy
 * </p>
 * 
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.2
 * @date
 * 
 */

public class WorkerPool {

	private ThreadPoolExecutor workers = null;
	private int workerCorePoolSize = 10;
	private int workerMaxPoolSize = 100;
	private BlockingQueue taskQueue = new ArrayBlockingQueue(100);
	private int queueSize = 100;

	private static final Logger log = LoggerFactory.getLogger(WorkerPool.class);

	// These values are not configurable in this version!
	private long keepAliveTime = 10000; // 10 seconds.
	private TimeUnit unit = TimeUnit.MILLISECONDS;

	public WorkerPool() {

		super();
		workers = new ThreadPoolExecutor(workerCorePoolSize, workerMaxPoolSize,
			keepAliveTime, unit, taskQueue);
	}

	public WorkerPool(int corePoolSize, int maxPoolSize, int queueSize) {

		workerCorePoolSize = corePoolSize;
		workerMaxPoolSize = maxPoolSize;
		this.queueSize = queueSize;
		taskQueue = new ArrayBlockingQueue(queueSize);
		workers = new ThreadPoolExecutor(workerCorePoolSize, workerMaxPoolSize,
			keepAliveTime, unit, taskQueue);
	}

	/**
	 * 
	 * @param task
	 *          Task
	 * @throws SchedulerException
	 */
	public void submit(Task task) throws SchedulerException {

		log.debug("Taskqueue Size:" + this.queueSize);
		log.debug("Taskqueue RemCap:" + workers.getQueue().remainingCapacity());
		task.enqueueEvent();

		try {
			log.debug("Submitting task with name = " + task.getName());
			workers.execute(task);
		} catch (RejectedExecutionException ret) {
			log.error("Task " + task.getName() + "was rejected!", ret);
			throw new SchedulerException(task.getName(), ret);
		}
	}

	/**
	 * 
	 * @param task
	 *          Task
	 * @throws SchedulerException
	 */
	public void remove(Task task) throws SchedulerException {

		log.debug("Abort request");
		task.abortEvent();
		log.debug("Aborting request with name : " + task.getName());
		boolean taskFound = false;
		// Looking for the task within the Queue
		BlockingQueue queue = workers.getQueue();
		taskFound = queue.contains(task);

		if (taskFound) {
			// If found, remove it
			workers.remove(task);
			workers.purge(); // Remove task named "Future task" from internal Queue.

			// Checking if Task is removed
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
