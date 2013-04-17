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

package it.grid.storm.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Space Reservation native Library. This class whill provide space
 * reserevation functionality usgin native library of StoRM. The reserveSpace
 * functionality return: 1 if correct -1 if Path specified does not exist -2 if
 * Space File Specified can't be created -3 if Space reservation functionality
 * (gpfs only) does not work.
 * 
 */
public class SpaceReservationWrapper {

	/**
	 * Logger. This Logger it's used to log information.
	 */
	private static final Logger log = LoggerFactory
		.getLogger(SpaceReservationWrapper.class);

	native int reserveSpace(String pathToFile, long size);

	static {
		// System.out.println("File: "+pathToFile+", size = "+ size );
		try {
			System.loadLibrary("spacenativelib");
		} catch (UnsatisfiedLinkError e) {
			log.error("SpaceReservation native library failed to load!", e);
			System.exit(1);
		}

	}

}
