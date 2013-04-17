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

package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.AuthzDirector;

import java.io.File;

import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;

public class FileAuthzDBReloadingStrategy extends FileChangedReloadingStrategy {

	private final Logger log = AuthzDirector.getLogger();
	private long reloadingTime;
	private boolean notifing = false;

	protected void notifingPerformed() {

		// log.debug(" NOTIFING set to FALSE");
		this.notifing = false;
	}

	protected boolean notifingRequired() {

		// log.debug(" NOTIFING is "+notifing);
		return notifing;
	}

	protected void notifyNeeded() {

		// log.debug(" NOTIFING set to TRUE");
		this.notifing = true;

	}

	@Override
	public boolean reloadingRequired() {

		// log.debug("Reloading is required?");
		boolean reloading = false;
		long now = System.currentTimeMillis();

		if (now > lastChecked + refreshDelay) {
			lastChecked = now;
			if (hasChanged()) {
				reloading = true;
			}
		}
		return reloading;
	}

	@Override
	public void reloadingPerformed() {

		updateLastModified();
		this.reloadingTime = System.currentTimeMillis();
		// @todo REFRESH DELLA CACHE!!
		log.debug("REFRESHING the AUTHZDB CACHE!!!");
	}

	/**
	 * Check if the configuration has changed since the last time it was loaded.
	 * 
	 * @return a flag whether the configuration has changed
	 */
	@Override
	protected boolean hasChanged() {

		File file = getConfigurationFile();
		if (file == null || !file.exists()) {
			return false;
		}
		boolean result = file.lastModified() > lastModified;
		if (result) {
			notifyNeeded();
			log.debug(" AuthZ DB " + file.getName()
				+ " is CHANGED ---> Notify needed..");
		}
		return result;
	}

	public long getLastReload() {

		return this.reloadingTime;
	}

	public File getConfigurationFile() {

		return this.configuration.getFile();
	}

}
