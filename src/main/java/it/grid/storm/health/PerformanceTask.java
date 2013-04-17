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
package it.grid.storm.health;

import java.util.TimerTask;

import org.slf4j.Logger;

/**
 * @author zappi
 * 
 */
public class PerformanceTask extends TimerTask {

	private PerformanceGlance detective;
	private Logger PERF_LOG = HealthDirector.getPerformanceLogger();
	private long progressivNumber = 0L;

	protected PerformanceTask() {

		detective = new PerformanceGlance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {

		progressivNumber++;
		PerformanceStatus status = detective.haveaLook();
		status.setPulseNumber(progressivNumber);
		PERF_LOG.info(status.toString());

	}

}
