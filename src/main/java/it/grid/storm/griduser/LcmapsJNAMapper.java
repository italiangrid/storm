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

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.jna.lcmaps.LcmapsAccountInterface;
import it.grid.storm.jna.lcmaps.LcmapsInterface;
import it.grid.storm.jna.lcmaps.LcmapsPoolindexInterface;
import it.grid.storm.jna.lcmaps.lcmaps_account_info_t;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jna.LastErrorException;

/**
 * @author dibenedetto_m
 * 
 */
public class LcmapsJNAMapper implements MapperInterface {

	private static final Object lock = new Object();

	private static final Logger log = LoggerFactory
		.getLogger(LcmapsJNAMapper.class);

	private lcmaps_account_info_t account = new lcmaps_account_info_t();

	private final String LCMAPS_DEFAULT_LOG_FILE = "/var/log/lcmaps.log";

	private final String LCMAPS_LOG_FILE_PATH_ENV_VARIABLE = "LCMAPS_LOG_FILE";

	private final short LCMAPS_LOG_TYPE = 3;

	/**
	 * @return
	 */
	private String getLcmapsLogFile() {

		String lcmaps_log_file = System.getenv(LCMAPS_LOG_FILE_PATH_ENV_VARIABLE);
		if (lcmaps_log_file == null) {
			lcmaps_log_file = LCMAPS_DEFAULT_LOG_FILE;
		}
		return lcmaps_log_file.trim();
	}

	public LocalUser map(String dn, String[] fqans) throws CannotMapUserException {

		LocalUser mappedUser = null;
		synchronized (LcmapsJNAMapper.lock) {
		  log.debug("Mapping user with dn = {} and fqans='{}'",
		    dn, ArrayUtils.toString(fqans));

			log.debug("Initializing Lcmaps");
			String lcmapsLogFile = getLcmapsLogFile();
			log.debug("Lcmaps log file is {}", lcmapsLogFile);

			int retVal = LcmapsInterface.INSTANCE.lcmaps_init_and_logfile(
				lcmapsLogFile, null, LCMAPS_LOG_TYPE);
			if (retVal != 0) {
				log.error("Unable to initialize lcmaps. Return value is {}" , retVal);
				throw new CannotMapUserException(
					"Unable to initialize lcmaps. Return value is " + retVal);
			}
			retVal = LcmapsAccountInterface.INSTANCE
				.lcmaps_account_info_init(account);
			if (retVal != 0) {
				throw new CannotMapUserException(
					"Unable to initialize lcmaps. Return value is " + retVal);
			}
			int numFqans = (fqans == null ? 0 : fqans.length);
			try {
				retVal = LcmapsPoolindexInterface.INSTANCE
					.lcmaps_return_account_without_gsi(dn, fqans, numFqans, 0, account);
			} catch (LastErrorException e) {
				log.error("Unable to map user dn <{}> fqans <{}>. Error: {}. Error code: {}",
				  dn, ArrayUtils.toString(fqans),
				  e.getMessage(),
				  e.getErrorCode(),
				  e);
				throw new CannotMapUserException(
					"Unable to initialize lcmaps. Return value is " + retVal);
			}
			if (retVal != 0) {
				log.error("Unable to map user dn <{}> fqans <{}>. Retval: {}",
				  dn, ArrayUtils.toString(fqans),
				  retVal);
				throw new CannotMapUserException("Unable to map user dn <" + dn
					+ "> fqans <" + ArrayUtils.toString(fqans) + "> . Return value is "
					+ retVal);
			}

			if (account.uid < 0) {
			  log.error("Negative uid returned by lcmaps: {}", account.uid);
				throw new CannotMapUserException(
					"Unacceptable lower than zero uid returned by Lcmaps : "
						+ account.uid + " . Mapping error");
			}
			if (account.npgid < 0 || account.nsgid < 0) {
			  log.error("Negative primary or secondary gid array size. npgid: {} nsgid: {}",
			    account.npgid, account.nsgid);

				throw new CannotMapUserException(
					"Negative primary or secondary gid array size returned by Lcmaps : primary = "
						+ account.npgid
						+ ", secondary = "
						+ account.nsgid +". Mapping error");
			}
			int[] gids = null;
			int numGids = account.npgid + account.nsgid;
			if (numGids > account.npgid) {
				gids = new int[numGids];
				int index = 0;
				if (account.npgid > 0) {
					for (int gid : account.pgid_list.getPointer().getIntArray(0,
						account.npgid)) {
						gids[index] = gid;
						index++;
					}
				} else {
					log.warn("No primary gid returned by Lcmaps! Mapping error");
				}
				for (int gid : account.sgid_list.getPointer().getIntArray(0,
					account.nsgid)) {
					gids[index] = gid;
					index++;
				}
			} else {
				if (account.npgid > 0) {
					gids = account.pgid_list.getPointer().getIntArray(0, account.npgid);
				}
			}
			log.info("Mapped user to : <uid={},gids={}>",
			  account.uid,
			  ArrayUtils.toString(gids)); 
			mappedUser = new LocalUser(account.uid, gids, numGids);
		}
		return mappedUser;
	}
}