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
 * 
 */
package it.grid.storm.authz.util;

import java.io.File;
import java.util.TimerTask;

/**
 * @author ritz
 */
public abstract class ConfigurationWatcher extends TimerTask {

	private long timeStamp;
	private final File file;

	public ConfigurationWatcher(File file) {

		this.file = file;
		timeStamp = file.lastModified();
	}

	@Override
	public final void run() {

		long timeStamp = file.lastModified();

		if (this.timeStamp != timeStamp) {
			this.timeStamp = timeStamp;
			onChange();
		}
	}

	// Take some actions on file changed
	protected abstract void onChange();

}
