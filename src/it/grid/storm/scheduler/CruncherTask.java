/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.scheduler;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005-2007</p>
 *
 * <p>Company: Project 'Grid.it' for INFN-CNAF, Bologna, Italy </p>
 *
 * @author Zappi Riccardo  <mailto://riccardo.zappi@cnaf.infn.it>
 * @version 1.0
 *
 */

public class CruncherTask extends Task {
    private Delegable todo = null;

    public CruncherTask(Delegable todo)
    {
	super();
	this.todo = todo;
	this.taskName = todo.getName();
    }


    /**
     * Compares this object with the specified object for order.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *   less than, equal to, or greater than the specified object.
     * @todo Implement this java.lang.Comparable method
     */
    public int compareTo(Object o)
    {
	/**
	 * @todo : make the implementation!
	 */
	return 0;
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread.
     *
     * @todo Implement this java.lang.Runnable method
     */
    public void run()
    {
	this.runEvent();
	todo.doIt();
	this.endEvent();
    }

    /**
     * Two CruncherTask are equals if and only
     *
     *   if the inner Delegable object are equals
     *                   AND
     *   if the name of the Task are equals
     *
     * @param o Object
     * @return boolean
     */
    public boolean equals(Object obj) {
      if (obj==this) return true;
      if (!(obj instanceof CruncherTask)) return false;
      CruncherTask other = (CruncherTask) obj;
      if (!(other.getName().equals(this.getName()))) return false;
      if (!(other.todo.equals(this.todo))) return false;
      else return true;
    }

    /**
     *
     * @return int
     */
    public int hashCode() {
        int hash = 17;
        if (this.taskName.length()!=0) hash = 37*hash + taskName.hashCode();
        hash = 37*hash + this.todo.hashCode();
        return hash;
    }
}
