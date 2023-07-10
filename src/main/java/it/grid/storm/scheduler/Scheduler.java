/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.scheduler;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2005
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 *
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 * @date
 */
public interface Scheduler {

  /**
   * Method that accepts a Task for scheduling.
   *
   * @param t Delegable
   * @throws SchedulerException
   */
  public void schedule(Delegable t) throws SchedulerException;

  /**
   * @param task Delegable
   * @throws SchedulerException
   */
  public void abort(Delegable task) throws SchedulerException;

  /**
   * @param task Delegable
   * @throws SchedulerException
   */
  public void suspend(Delegable task) throws SchedulerException;

  /** @return SchedulerStatus */
  public SchedulerStatus getStatus();
}
