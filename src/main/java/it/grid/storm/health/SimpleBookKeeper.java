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

package it.grid.storm.health;

public class SimpleBookKeeper extends BookKeeper {

	public static final String KEY = "BK";

	public SimpleBookKeeper() {

		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.health.BookKeeper#addLogEvent(it.grid.storm.health.LogEvent)
	 */
	@Override
	public synchronized void addLogEvent(LogEvent logEvent) {

		logbook.add(logEvent);
		logDebug("Event is added to Log Book (item #" + (logbook.size() - 1) + "");
		logInfo(logEvent.toString());
	}

	/**
	 * 
	 * @return int
	 */
	public synchronized int getNumberOfAsynchRequest() {

		int result = 0;
		for (int i = 0; i < logbook.size(); i++) {
			if (!(logbook.get(i).getOperationType().isSynchronousOperation())) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @return int
	 */
	public synchronized int getNumberOfSynchRequest() {

		int result = 0;
		for (int i = 0; i < logbook.size(); i++) {
			if (logbook.get(i).getOperationType().isSynchronousOperation()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param opType
	 *          OperationType
	 * @return int
	 */
	public synchronized int getNumberOfRequest(OperationType opType) {

		int result = 0;
		for (int i = 0; i < logbook.size(); i++) {
			if (logbook.get(i).getOperationType().equals(opType)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param opType
	 *          OperationType
	 * @return long
	 */
	public synchronized long getMeansDuration(OperationType opType) {

		long meanTime = 0L;
		long sumTime = 0L;
		int requestNumber = getNumberOfRequest(opType);
		if (requestNumber > 0) {
			for (int i = 0; i < logbook.size(); i++) {
				if (logbook.get(i).getOperationType().equals(opType)) {
					sumTime += logbook.get(i).getDuration();
				}
			}
			meanTime = sumTime / requestNumber;
		}
		return meanTime;
	}

	/**
	 * 
	 * @param opType
	 *          OperationType
	 * @return int
	 */
	public synchronized int getNumberOfSuccess(OperationType opType) {

		int result = 0;
		int requestNumber = getNumberOfRequest(opType);
		if (requestNumber > 0) {
			for (int i = 0; i < logbook.size(); i++) {
				LogEvent logE = logbook.get(i);
				if (logE.getOperationType().equals(opType)) {
					if (logE.isSuccess()) {
						result++;
					}
				}
			}
		}
		return result;
	}
}
