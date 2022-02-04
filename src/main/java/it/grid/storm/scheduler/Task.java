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
 * @date
 * 
 */

public abstract class Task implements Runnable, Comparable<Task> {

  private static String UNDEF_TASKNAME = "undefined";
  private long creationTime = System.currentTimeMillis();
  private long enqueueTime = 0L;
  private long startExecutionTime = 0L;
  private long endExecutionTime = 0L;
  private long abortingEventTime = 0L;
  private long suspendingEventTime = 0L;
  protected String taskName = null;

  protected Task() {

    this(UNDEF_TASKNAME);
  }

  protected Task(String name) {

    taskName = name;
    if (taskName == null) {
      taskName = UNDEF_TASKNAME;
    }
    creationTime = System.currentTimeMillis();
  }

  public long getStartExecutionTime() {

    return startExecutionTime;
  }

  public long getAbortingEventTime() {

    return abortingEventTime;
  }

  public long getSuspendingEventTime() {

    return suspendingEventTime;
  }

  public long howlongBeforeUnqueue() {

    return enqueueTime - creationTime;
  }

  public long howlongInQueue() {

    return startExecutionTime - enqueueTime;
  }

  public long howlongInExecution() {

    return endExecutionTime - startExecutionTime;
  }

  protected void enqueueEvent() {

    enqueueTime = System.currentTimeMillis();
  }

  protected void abortEvent() {

    abortingEventTime = System.currentTimeMillis();
  }

  protected void suspendEvent() {

    suspendingEventTime = System.currentTimeMillis();
  }

  protected void runEvent() {

    startExecutionTime = System.currentTimeMillis();
  }

  protected void endEvent() {

    endExecutionTime = System.currentTimeMillis();
  }

  protected String getName() {

    return taskName;
  }

  public abstract void run();

  public abstract int compareTo(Task o);

  public abstract boolean equals(Object o);

}
