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

package it.grid.storm.filesystem;

import it.grid.storm.filesystem.swig.gpfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a SpaceSystem that is able to use native GPFS support
 * to carry out space reservation operations.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date May 2006
 */
public class GPFSSpaceSystem implements SpaceSystem {

	private gpfs fs = null; // instance of filesystem that will be used to invoke
													// native operation!
	private static Logger log = LoggerFactory.getLogger(GPFSSpaceSystem.class);

	public GPFSSpaceSystem(String mountpoint) throws SpaceSystemException {

		if (mountpoint == null) {
			throw new SpaceSystemException("Supplied mountpoint is null!");
		}
		try {
			this.fs = new gpfs(mountpoint);
		} catch (Exception e) {
			throw new SpaceSystemException(
				"Unable to instantiate GPFS filesystem on " + mountpoint
					+ "; exception: " + e);
		}
	}

	/**
	 * Method that follows the contract specified in SpaceSystem Interface: please
	 * refer there for further info.
	 */
	public long reserveSpace(String pathToFile, long size)
		throws ReservationException {

		try {
			log.debug("GPFSSpaceSystem : pathToFile: {}" , pathToFile);
			fs.prealloc(pathToFile, size);
			return size;
		} catch (Exception e) {
			String explanation = "An exception was thrown by the native underlying filesystem: "
				+ e.toString();
			log.debug(explanation);
			throw new ReservationException(explanation);
		}
	}

	/**
	 * Method that follows the contract specified in SpaceSystem Interface: please
	 * refer there for further info.
	 */
	public long compactSpace(String pathToFile) throws ReservationException {

		String explanation = "Compact Space operation currently not supported!";
		log.debug(explanation);
		throw new ReservationException(explanation);
	}

	/**
	 * Method that follows the contract specified in SpaceSystem Interface: please
	 * refer there for further info.
	 */
	public void removeSpace(String pathToFile) throws ReservationException {

		String explanation = "Remove Space operation currently not supported!";
		log.debug(explanation);
		throw new ReservationException(explanation);
	}

	/**
	 * Method that follows the contract specified in SpaceSystem Interface: please
	 * refer there for further info.
	 */
	public long changeSize(String pathToFile, long newSize)
		throws ReservationException {

		String explanation = "Change Size operation currently not supported!";
		log.debug(explanation);
		throw new ReservationException(explanation);
	}

	@Override
	public String toString() {

		return "GPFSSpaceSystem";
	}
}
