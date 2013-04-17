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
package it.grid.storm.griduser;

import java.nio.IntBuffer;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.jna.lcmaps.StormLcmapsLibrary;
import it.grid.storm.jna.lcmaps.StormLcmapsLibrary.Errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class StormLcmapsJNAMapper implements MapperInterface {

	private static final Logger log = LoggerFactory
		.getLogger(StormLcmapsJNAMapper.class);
	/** To synchronize on LCMAPS invocation. */
	private final Object lock = new Object();

	private final String LCMAPS_DEFAULT_LOG_FILE = "/var/log/lcmaps.log";

	private final String LCMAPS_LOG_FILE_PATH_ENV_VARIABLE = "LCMAPS_LOG_FILE";

	private static final StormLcmapsJNAMapper instance = new StormLcmapsJNAMapper();

	private StormLcmapsJNAMapper() {

	}

	public static StormLcmapsJNAMapper getInstance() {

		return instance;
	}

	/**
	 * @return lcmaps log file path
	 */
	private String getLcmapsLogFile() {

		String lcmapsLogFile = System.getenv(LCMAPS_LOG_FILE_PATH_ENV_VARIABLE);
		if (lcmapsLogFile == null) {
			lcmapsLogFile = LCMAPS_DEFAULT_LOG_FILE;
		}
		return lcmapsLogFile.trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.griduser.MapperInterface#map(java.lang.String,
	 * java.lang.String[])
	 */
	public LocalUser map(final String dn, final String[] fqans)
		throws CannotMapUserException {

		IntBuffer userId = IntBuffer.allocate(1), groupId = IntBuffer.allocate(1);
		int retVal;
		synchronized (lock) {
			retVal = StormLcmapsLibrary.INSTANCE.map_user(getLcmapsLogFile(), dn,
				fqans, 1, userId, groupId);
		}
		if (retVal != 0) {
			Errors error = StormLcmapsLibrary.Errors.getError(retVal);
			if (!error.equals(Errors.UNKNOW_ERROR)) {
				log
					.error("Unable to call successfully native map_user() method. Return value is "
						+ error.toString());
			} else {
				log
					.error("Unable to call successfully native map_user() method. Return value is "
						+ retVal + " . This is an unknown return value");
			}
			throw new CannotMapUserException(
				"LCMAPS error, cannot map user credentials to local user.");
		}
		LocalUser localUser = new LocalUser(userId.get(),
			new int[] { groupId.get() }, 1);
		return localUser;
	}
}
