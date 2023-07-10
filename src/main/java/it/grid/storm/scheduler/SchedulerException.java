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
public class SchedulerException extends Exception {

  private String whichScheduler;

  public SchedulerException(String whichSched) {

    super();
    whichScheduler = whichSched;
  }

  public SchedulerException(String whichSched, String message) {

    super(message);
  }

  public SchedulerException(Throwable cause) {

    super(cause);
  }

  public SchedulerException(String message, Throwable cause) {

    super(message, cause);
  }

  public String toString() {

    return "Exception occurred within scheduler type = " + whichScheduler;
  }
}
