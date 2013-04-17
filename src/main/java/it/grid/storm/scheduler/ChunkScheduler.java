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

import it.grid.storm.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 * </p>
 * 
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 * 
 */
public class ChunkScheduler implements Scheduler, Streets {

	private static ChunkScheduler istance = null;

	private static final Logger log = LoggerFactory
		.getLogger(ChunkScheduler.class);
	private SchedulerStatus[] schedStatus = null;

	/**
	 * Default values for Pools
	 */
	// Value of "scheduler.chunksched.ptp.workerCorePoolSize"
	private int ptp_workerCorePoolSize = Configuration.getInstance()
		.getPtPCorePoolSize();

	// Value of "scheduler.chunksched.ptp.workerMaxPoolSize"
	private int ptp_workerMaxPoolSize = Configuration.getInstance()
		.getPtPMaxPoolSize();

	// Value of "scheduler.chunksched.ptp.queueSize"
	private int ptp_queueSize = Configuration.getInstance().getPtPQueueSize();

	// Value of "scheduler.chunksched.ptg.workerCorePoolSize"
	private int ptg_workerCorePoolSize = Configuration.getInstance()
		.getPtGCorePoolSize();

	// Value of "scheduler.chunksched.ptg.workerMaxPoolSize"
	private int ptg_workerMaxPoolSize = Configuration.getInstance()
		.getPtGMaxPoolSize();

	// Value of "scheduler.chunksched.ptg.queueSize"
	private int ptg_queueSize = Configuration.getInstance().getPtGQueueSize();

	// Value of "scheduler.chunksched.copy.workerCorePoolSize"
	private int copy_workerCorePoolSize = Configuration.getInstance()
		.getCopyCorePoolSize();

	// Value of "scheduler.chunksched.copy.workerMaxPoolSize"
	private int copy_workerMaxPoolSize = Configuration.getInstance()
		.getCopyMaxPoolSize();

	// Value of "scheduler.chunksched.copy.queueSize"
	private int copy_queueSize = Configuration.getInstance().getCopyQueueSize();

	// Value of "scheduler.chunksched.bol.workerCorePoolSize"
	private int bol_workerCorePoolSize = Configuration.getInstance()
		.getBoLCorePoolSize();

	// Value of "scheduler.chunksched.bol.workerMaxPoolSize"
	private int bol_workerMaxPoolSize = Configuration.getInstance()
		.getBoLMaxPoolSize();

	// Value of "scheduler.chunksched.bol.queueSize"
	private int bol_queueSize = Configuration.getInstance().getBoLQueueSize();

	private WorkerPool ptg_workers = null; // new WorkerPool();
	private WorkerPool ptp_workers = null; // new WorkerPool();
	private WorkerPool copy_workers = null; // new WorkerPool();
	private WorkerPool bol_workers = null; // new WorkerPool();

	private ChunkScheduler() {

		schedStatus = new SchedulerStatus[4];
		schedStatus[0] = new SchedulerStatus("PtG_inner_scheduler");
		schedStatus[1] = new SchedulerStatus("PtP_inner_scheduler");
		schedStatus[2] = new SchedulerStatus("Copy_inner_scheduler");
		schedStatus[3] = new SchedulerStatus("BoL_inner_scheduler");

		/**
		 * @todo Read to Configuration the information about worker Pool structure
		 *       of Crusher Scheduler
		 */

		// Setting for PrepareToGet pool of workers
		ptg_workers = new WorkerPool(ptg_workerCorePoolSize, ptg_workerMaxPoolSize,
			ptg_queueSize);
		schedStatus[0].setCorePoolSize(ptg_workerCorePoolSize);
		schedStatus[0].setMaxPoolSize(ptg_workerMaxPoolSize);

		// Setting for PrepareToPut pool of workers
		ptp_workers = new WorkerPool(ptp_workerCorePoolSize, ptp_workerMaxPoolSize,
			ptp_queueSize);
		schedStatus[1].setCorePoolSize(ptp_workerCorePoolSize);
		schedStatus[1].setMaxPoolSize(ptp_workerMaxPoolSize);

		// Setting for Copy pool of workers
		copy_workers = new WorkerPool(copy_workerCorePoolSize,
			copy_workerMaxPoolSize, copy_queueSize);
		schedStatus[2].setCorePoolSize(copy_workerCorePoolSize);
		schedStatus[2].setMaxPoolSize(copy_workerMaxPoolSize);

		// Setting for BoL pool of workers
		bol_workers = new WorkerPool(bol_workerCorePoolSize, bol_workerMaxPoolSize,
			bol_queueSize);
		schedStatus[3].setCorePoolSize(bol_workerCorePoolSize);
		schedStatus[3].setMaxPoolSize(bol_workerMaxPoolSize);
	}

	public static ChunkScheduler getInstance() {

		if (istance == null) {
			istance = new ChunkScheduler();
		}
		return istance;
	}

	public void schedule(Delegable chunk) throws SchedulerException {

		// Debugging info logging
		log.debug("Delegable Chunk " + chunk.getName() + " arrived at scheduler");
		Chooser c = (Chooser) chunk;
		c.choose(this);
	}

	public SchedulerStatus getStatus(int type) {

		SchedulerStatus st = null;
		switch (type) {
		case 0:
			schedStatus[0].setCompletedTaskCount(ptg_workers.getCompletedTaskCount());
			schedStatus[0].setActiveCount(ptg_workers.getActiveCount());
			schedStatus[0].setLargestPoolSize(ptg_workers.getLargestPoolSize());
			schedStatus[0].setPoolSize(ptg_workers.getActualPoolSize());
			schedStatus[0].setTaskCount(ptg_workers.getTaskCount());
			schedStatus[0].setQueueSize(ptg_queueSize);
			schedStatus[0].setRemainingCapacity(ptg_workers.getRemainingCapacity());
			st = schedStatus[0];
			break;
		case 1:
			schedStatus[1].setCompletedTaskCount(ptp_workers.getCompletedTaskCount());
			schedStatus[1].setActiveCount(ptp_workers.getActiveCount());
			schedStatus[1].setLargestPoolSize(ptp_workers.getLargestPoolSize());
			schedStatus[1].setPoolSize(ptp_workers.getActualPoolSize());
			schedStatus[1].setTaskCount(ptp_workers.getTaskCount());
			schedStatus[1].setQueueSize(ptp_queueSize);
			schedStatus[1].setRemainingCapacity(ptp_workers.getRemainingCapacity());
			st = schedStatus[1];
			break;
		case 2:
			schedStatus[2]
				.setCompletedTaskCount(copy_workers.getCompletedTaskCount());
			schedStatus[2].setActiveCount(copy_workers.getActiveCount());
			schedStatus[2].setLargestPoolSize(copy_workers.getLargestPoolSize());
			schedStatus[2].setPoolSize(copy_workers.getActualPoolSize());
			schedStatus[2].setTaskCount(copy_workers.getTaskCount());
			schedStatus[2].setQueueSize(copy_queueSize);
			schedStatus[2].setRemainingCapacity(copy_workers.getRemainingCapacity());
			st = schedStatus[2];
			break;
		case 3:
			schedStatus[3].setCompletedTaskCount(bol_workers.getCompletedTaskCount());
			schedStatus[3].setActiveCount(bol_workers.getActiveCount());
			schedStatus[3].setLargestPoolSize(bol_workers.getLargestPoolSize());
			schedStatus[3].setPoolSize(bol_workers.getActualPoolSize());
			schedStatus[3].setTaskCount(bol_workers.getTaskCount());
			schedStatus[3].setQueueSize(bol_queueSize);
			schedStatus[3].setRemainingCapacity(bol_workers.getRemainingCapacity());
			st = schedStatus[3];
			break;

		default:
			st = null;
			break;

		}
		return st;
	}

	public void ptgStreet(Delegable chunk) {

		// Debugging info logging
		log.debug("Chunk (declared PTG) " + chunk.getName()
			+ " entered into PTG-street");

		// Build a generic Chunk Task
		// ChunkTask chunkTask = new ChunkTask(chunk, ChunkType.PREPARE_TO_GET);
		ChunkTask chunkTask = new ChunkTask(chunk);
		try {
			ptg_workers.submit(chunkTask);
			log.debug("PtG Worker Pool SIZE = "
				+ getStatus(Scheduler.PtG_WorkerPoolType).getPoolSize());
		} catch (SchedulerException ex) {
			log.error("PrepareToGet_Street", ex);
		}
	}

	public void ptpStreet(Delegable chunk) {

		// Debugging info logging
		log.debug("Chunk (declared PTP) " + chunk.getName()
			+ " entered into PTP-street");

		// Build a generic Chunk Task
		// ChunkTask chunkTask = new ChunkTask(chunk, ChunkType.PREPARE_TO_PUT);
		ChunkTask chunkTask = new ChunkTask(chunk);
		try {
			ptp_workers.submit(chunkTask);
			log.debug("PtP Worker Pool SIZE = "
				+ getStatus(Scheduler.PtP_WorkerPoolType).getPoolSize());
		} catch (SchedulerException ex) {
			log.error("PrepareToPut_Street submission error", ex);
		}

	}

	public void copyStreet(Delegable chunk) {

		// Debugging info logging
		log.debug("Chunk (declared COPY) " + chunk.getName()
			+ " entered into COPY-street");

		// Build a generic Chunk Task
		// ChunkTask chunkTask = new ChunkTask(chunk, ChunkType.COPY);
		ChunkTask chunkTask = new ChunkTask(chunk);
		try {
			copy_workers.submit(chunkTask);
			log.debug("Copy Worker Pool SIZE = "
				+ getStatus(Scheduler.Copy_WorkerPoolType).getPoolSize());
		} catch (SchedulerException ex) {
			log.error("Copy_Street", ex);
		}
	}

	public void bolStreet(Delegable chunk) {

		// Debugging info logging
		log.debug("Chunk (declared BOL) " + chunk.getName()
			+ " entered into BOL-street");

		// Build a generic Chunk Task
		// ChunkTask chunkTask = new ChunkTask(chunk, ChunkType.BOL);
		ChunkTask chunkTask = new ChunkTask(chunk);
		try {
			bol_workers.submit(chunkTask);
			log.debug("BoL Worker Pool SIZE = "
				+ getStatus(Scheduler.BoL_WorkerPoolType).getPoolSize());
		} catch (SchedulerException ex) {
			log.error("BoL_Street", ex);
		}
	}

	public void abort(Delegable task) throws SchedulerException {

	}

	public void suspend(Delegable task) throws SchedulerException {

	}

}
