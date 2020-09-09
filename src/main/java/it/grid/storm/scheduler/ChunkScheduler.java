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

  private static final Logger log = LoggerFactory.getLogger(ChunkScheduler.class);

  private SchedulerStatus ptgSchedulerStatus;
  private SchedulerStatus ptpSchedulerStatus;
  private SchedulerStatus bolSchedulerStatus;

  private WorkerPool ptgWorkerPool;
  private WorkerPool ptpWorkerPool;
  private WorkerPool bolWorkerPool;

  private ChunkScheduler(Configuration configuration) {

    int ptgWorkerCorePoolSize = configuration.getPtGCorePoolSize();
    int ptgWorkerMaxPoolSize = configuration.getPtGMaxPoolSize();
    int ptgQueueSize = configuration.getPtGQueueSize();

    ptgWorkerPool = new WorkerPool(ptgWorkerCorePoolSize, ptgWorkerMaxPoolSize, ptgQueueSize);
    ptgSchedulerStatus = new SchedulerStatus("PtG_inner_scheduler");
    ptgSchedulerStatus.setCorePoolSize(ptgWorkerCorePoolSize);
    ptgSchedulerStatus.setMaxPoolSize(ptgWorkerMaxPoolSize);
    ptgSchedulerStatus.setQueueSize(ptgQueueSize);

    int ptpWorkerCorePoolSize = configuration.getPtPCorePoolSize();
    int ptpWorkerMaxPoolSize = configuration.getPtPMaxPoolSize();
    int ptpQueueSize = configuration.getPtPQueueSize();

    ptpWorkerPool = new WorkerPool(ptpWorkerCorePoolSize, ptpWorkerMaxPoolSize, ptpQueueSize);
    ptpSchedulerStatus = new SchedulerStatus("PtP_inner_scheduler");
    ptpSchedulerStatus.setCorePoolSize(ptpWorkerCorePoolSize);
    ptpSchedulerStatus.setMaxPoolSize(ptpWorkerMaxPoolSize);
    ptpSchedulerStatus.setQueueSize(ptpQueueSize);

    int bolWorkerCorePoolSize = configuration.getBoLCorePoolSize();
    int bolWorkerMaxPoolSize = configuration.getBoLMaxPoolSize();
    int bolQueueSize = configuration.getBoLQueueSize();

    bolWorkerPool = new WorkerPool(bolWorkerCorePoolSize, bolWorkerMaxPoolSize, bolQueueSize);
    bolSchedulerStatus = new SchedulerStatus("BoL_inner_scheduler");
    bolSchedulerStatus.setCorePoolSize(bolWorkerCorePoolSize);
    bolSchedulerStatus.setMaxPoolSize(bolWorkerMaxPoolSize);
    bolSchedulerStatus.setQueueSize(bolQueueSize);

  }

  public static ChunkScheduler getInstance() {

    if (istance == null) {
      istance = new ChunkScheduler(Configuration.getInstance());
    }
    return istance;
  }

  public void schedule(Delegable chunk) throws SchedulerException {

    log.debug("Scheduling chunk: {}", chunk.getName());
    Chooser c = (Chooser) chunk;
    c.choose(this);
  }

  private void updatePtgStatus() {

    ptgSchedulerStatus.setCompletedTaskCount(ptgWorkerPool.getCompletedTaskCount());
    ptgSchedulerStatus.setActiveCount(ptgWorkerPool.getActiveCount());
    ptgSchedulerStatus.setLargestPoolSize(ptgWorkerPool.getLargestPoolSize());
    ptgSchedulerStatus.setPoolSize(ptgWorkerPool.getActualPoolSize());
    ptgSchedulerStatus.setTaskCount(ptgWorkerPool.getTaskCount());
    ptgSchedulerStatus.setRemainingCapacity(ptgWorkerPool.getRemainingCapacity());
  }

  private void updatePtpStatus() {

    ptpSchedulerStatus.setCompletedTaskCount(ptpWorkerPool.getCompletedTaskCount());
    ptpSchedulerStatus.setActiveCount(ptpWorkerPool.getActiveCount());
    ptpSchedulerStatus.setLargestPoolSize(ptpWorkerPool.getLargestPoolSize());
    ptpSchedulerStatus.setPoolSize(ptpWorkerPool.getActualPoolSize());
    ptpSchedulerStatus.setTaskCount(ptpWorkerPool.getTaskCount());
    ptpSchedulerStatus.setRemainingCapacity(ptpWorkerPool.getRemainingCapacity());
  }

  private void updateBolStatus() {

    bolSchedulerStatus.setCompletedTaskCount(bolWorkerPool.getCompletedTaskCount());
    bolSchedulerStatus.setActiveCount(bolWorkerPool.getActiveCount());
    bolSchedulerStatus.setLargestPoolSize(bolWorkerPool.getLargestPoolSize());
    bolSchedulerStatus.setPoolSize(bolWorkerPool.getActualPoolSize());
    bolSchedulerStatus.setTaskCount(bolWorkerPool.getTaskCount());
    bolSchedulerStatus.setRemainingCapacity(bolWorkerPool.getRemainingCapacity());
  }

  public SchedulerStatus getPtgStatus() {

    updatePtgStatus();
    return ptgSchedulerStatus;
  }

  public SchedulerStatus getPtpStatus() {

    updatePtpStatus();
    return ptpSchedulerStatus;
  }

  public SchedulerStatus getBolStatus() {

    updateBolStatus();
    return bolSchedulerStatus;
  }

  public SchedulerStatus getStatus() {

    return null;
  }

  public void ptgStreet(Delegable chunk) {

    // Debugging info logging
    log.trace("ptgStreet got chunk: {}", chunk.getName());

    ChunkTask chunkTask = new ChunkTask(chunk);
    try {
      ptgWorkerPool.submit(chunkTask);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void ptpStreet(Delegable chunk) {

    log.trace("ptpStreet got chunk: {}", chunk.getName());
    ChunkTask chunkTask = new ChunkTask(chunk);
    try {
      ptpWorkerPool.submit(chunkTask);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }

  }

  public void bolStreet(Delegable chunk) {

    log.trace("bolStret got chunk: {}", chunk.getName());

    ChunkTask chunkTask = new ChunkTask(chunk);
    try {
      bolWorkerPool.submit(chunkTask);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void abort(Delegable task) throws SchedulerException {
    log.warn("abort is not implemented");
  }

  public void suspend(Delegable task) throws SchedulerException {
    log.warn("suspend is not implemented");
  }

}
