/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall;

/**
 * This class is part of the StoRM project.
 * 
 * This class can choose the right dispatcher using configuration parameter etc.
 * 
 * @author lucamag
 * @date May 27, 2008
 * 
 */
public class SynchcallDispatcherFactory {

	/**
	 * @return SynchcallDispatcher
	 */

	public static SynchcallDispatcher getDispatcher() {

		return new SimpleSynchcallDispatcher();
	}
}
