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

import java.util.ArrayList;
import java.util.Hashtable;

import org.slf4j.Logger;

/**
 * @author zappi
 * 
 */
public class PerformanceStatus {

	private Logger PERF_LOG = HealthDirector.getPerformanceLogger();

	private String pulseNumberStr = "";
	private Hashtable<OperationType, PerformanceEvent> perfStatus = new Hashtable<OperationType, PerformanceEvent>();
	private static int timeWindows = HealthDirector.getHealthMonitor().perfGlanceTimeInterval;

	public PerformanceStatus(ArrayList<LogEvent> eventToAnalyze) {

		PERF_LOG.debug("PERFORMANCE STATUS");
		PerformanceEvent pEvent;
		OperationType ot;
		if (eventToAnalyze != null) {
			PERF_LOG.debug("PERFORMANCE STATUS : {}",
			  eventToAnalyze.size());
			for (LogEvent event : eventToAnalyze) {
				ot = event.getOperationType();
				if (perfStatus.containsKey(ot)) {
					pEvent = perfStatus.get(event.getOperationType());
				} else {
					pEvent = new PerformanceEvent(ot);
				}
				pEvent.addLogEvent(event);
				perfStatus.put(ot, pEvent);
			}
		} else {
			PERF_LOG.debug("NO EVENTS TO ANALYZE!!!");
		}
	}

	/**
	 * 
	 * @param number
	 *          long
	 */
	public void setPulseNumber(long number) {

		this.pulseNumberStr = number + "";
		String prefix = "";
		for (int i = 0; i < (6 - pulseNumberStr.length()); i++) {
			prefix += ".";
		}
		this.pulseNumberStr = prefix + this.pulseNumberStr;
	}

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("#" + this.pulseNumberStr + ": ");
		if (perfStatus.isEmpty()) {
			result.append("No activity in the last " + timeWindows + " seconds");
		} else {
			result.append("\n=== last " + timeWindows + " seconds ===\n");
			for (PerformanceEvent pEvent : perfStatus.values()) {
				result.append(pEvent);
				result.append("\n");
			}
		}
		// result.append("\n");
		return result.toString();
	}

}
