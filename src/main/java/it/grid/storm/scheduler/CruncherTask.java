/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.scheduler;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2005-2007
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy
 *
 * @author Zappi Riccardo <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 */
public class CruncherTask extends Task {

  private Delegable todo = null;

  public CruncherTask(Delegable todo) {

    super();
    this.todo = todo;
    this.taskName = todo.getName();
  }

  /**
   * Compares this object with the specified object for order.
   *
   * @param o the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   *     or greater than the specified object.
   * @todo Implement this java.lang.Comparable method
   */
  public int compareTo(Object o) {

    /** @todo : make the implementation! */
    return 0;
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread.
   *
   * @todo Implement this java.lang.Runnable method
   */
  public void run() {

    this.runEvent();
    todo.doIt();
    this.endEvent();
  }

  /**
   * Two CruncherTask are equals if and only
   *
   * <p>if the inner Delegable object are equals AND if the name of the Task are equals
   *
   * @param o Object
   * @return boolean
   */
  public boolean equals(Object obj) {

    if (obj == this) return true;
    if (!(obj instanceof CruncherTask)) return false;
    CruncherTask other = (CruncherTask) obj;
    if (!(other.getName().equals(this.getName()))) return false;
    if (!(other.todo.equals(this.todo))) return false;
    else return true;
  }

  /** @return int */
  public int hashCode() {

    int hash = 17;
    if (this.taskName.length() != 0) hash = 37 * hash + taskName.hashCode();
    hash = 37 * hash + this.todo.hashCode();
    return hash;
  }
}
