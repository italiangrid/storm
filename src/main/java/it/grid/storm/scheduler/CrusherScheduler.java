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
 * @date
 * 
 */

public class CrusherScheduler implements Scheduler {

  private static final Logger log = LoggerFactory.getLogger(CrusherScheduler.class);

  private WorkerPool crusherPool;

  private int workerCorePoolSize;
  private int workerMaxPoolSize;
  private int queueSize;

  private static CrusherScheduler istance = null;

  private CrusherScheduler(Configuration configuration) {

    workerCorePoolSize = configuration.getCorePoolSize();
    workerMaxPoolSize = configuration.getMaxPoolSize();
    queueSize = configuration.getQueueSize();

    crusherPool = new WorkerPool(workerCorePoolSize, workerMaxPoolSize, queueSize);
  }

  public static CrusherScheduler getInstance() {

    log.trace("CrusherScheduler.getInstance");

    if (istance == null) {
      istance = new CrusherScheduler(Configuration.getInstance());
    }
    return istance;
  }

  public void schedule(Delegable cruncherTask) throws SchedulerException {

    log.trace("CrusherScheduler.schedule() - cruncherTask: {}", cruncherTask.getName());

    try {
      Task task = new CruncherTask(cruncherTask);
      crusherPool.submit(task);
      log.debug("Feed task nr. = {}", crusherPool.getTaskCount());
      log.debug("Scheduled feed {}", cruncherTask.getName());
    } catch (SchedulerException se) {
      log.error(se.getMessage(), se);
      throw se;
    }
  }

  public SchedulerStatus getStatus() {

    SchedulerStatus schedStatus = new SchedulerStatus("CrusherScheduler");
    schedStatus.setCorePoolSize(workerCorePoolSize);
    schedStatus.setMaxPoolSize(workerMaxPoolSize);
    schedStatus.setQueueSize(queueSize);
    schedStatus.setCompletedTaskCount(crusherPool.getCompletedTaskCount());
    schedStatus.setActiveCount(crusherPool.getActiveCount());
    schedStatus.setLargestPoolSize(crusherPool.getLargestPoolSize());
    schedStatus.setPoolSize(crusherPool.getActualPoolSize());
    schedStatus.setTaskCount(crusherPool.getTaskCount());
    schedStatus.setRemainingCapacity(crusherPool.getRemainingCapacity());
    return schedStatus;
  }

  /**
   * @param task Delegable
   * @throws SchedulerException
   */
  public void abort(Delegable task) throws SchedulerException {

  }

  /**
   * @param task Delegable
   * @throws SchedulerException
   */
  public void suspend(Delegable task) throws SchedulerException {

    throw new SchedulerException("CruscherScheduler", "Suspend request not implemented yet!");
  }

}
