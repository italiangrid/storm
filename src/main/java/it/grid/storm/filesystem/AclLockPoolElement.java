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

/**
 * @file AclLockPoolElement.java
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * 
 *         The it.grid.storm.filesystem.AclLockPoolElement class
 */
/*
 * Copyright (c) 2006 Riccardo Murri <riccardo.murri@ictp.it> for the EGRID/INFN
 * joint project StoRM.
 * 
 * You may copy, modify and distribute this file under the same terms as StoRM
 * itself.
 */

package it.grid.storm.filesystem;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;

/**
 * Usage-counted semaphore object.
 * 
 * <p>
 * Each {@link #incrementUsageCountAndReturnSelf()} request increments the usage
 * counter, and each {@link #decrementUsageCountAndGetIt()} request decrements
 * it.
 * 
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.5 $
 */
class AclLockPoolElement extends Semaphore {

	// ---- constructors ----

	/**
	 * Default constructor. The semaphore is initialized for allowing only 1
	 * permit at a time (thus serializing accesses through the acquire() and
	 * release() calls), and with the default fairness setting. The usage count is
	 * initialized to <code>0</code>.
	 * 
	 * @see java.util.concurrent.AtomicInteger;
	 * @see java.util.concurrent.Semaphore;
	 */
	public AclLockPoolElement() {

		super(1);
		usageCount = new AtomicInteger();
	}

	// --- public methods ---

	/**
	 * Return the lock object associated with the given file name, or create a new
	 * one if no mapping for the given path name is already in this map.
	 */
	public void incrementUsageCount() {

		usageCount.incrementAndGet();
	}

	/** Return the stored usage count. */
	public int getUsageCount() {

		return usageCount.intValue();
	}

	/** Decrement the stored usage count. */
	public int decrementUsageCountAndGetIt() {

		return usageCount.decrementAndGet();
	}

	// --- private instance variables --- //

	/** Usage counter. */
	private final AtomicInteger usageCount;
}
