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

import it.grid.storm.srm.types.TSURL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class LogEvent implements Delayed {

	// Attributes to manage the Event within the BookKeeper
	private static long THOUSAND = 1000L;
	public final long birthTime;
	public final long deathTime;
	private long timeToLive = 60000L; // Expressed in MILLISEC (1 min)

	// Attributes of EVENT
	private OperationType opType = null;
	private String userDN = null;
	private String surl = null;
	private long startTime = -1L;
	private String startTimeStr = null;
	private long duration = -1L;
	private String requestToken = null;
	private boolean successResult = false;

	public LogEvent(OperationType opType, String userDN, String surl,
		long startTime, long duration, String requestToken, boolean successResult) {

		this.opType = opType;
		this.userDN = userDN;
		this.surl = surl;
		this.startTime = startTime;
		this.duration = duration;
		this.requestToken = requestToken;
		Date date = new Date(startTime);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS");
		this.startTimeStr = formatter.format(date);
		this.successResult = successResult;

		this.timeToLive = HealthDirector.timeToLiveLogEventInSec;
		this.deathTime = System.currentTimeMillis()
			+ (HealthDirector.timeToLiveLogEventInSec * LogEvent.THOUSAND);
		this.birthTime = System.currentTimeMillis();

	}

	public LogEvent(OperationType opType, String userDN, long startTime,
		long duration, boolean successResult) {

		this.opType = opType;
		this.userDN = userDN;
		// Empty SURL
		this.surl = TSURL.makeEmpty().toString();
		this.startTime = startTime;
		// Store the duration in MicroSeconds (10^-6 sec)
		this.duration = TimeUnit.MICROSECONDS.convert(duration,
			TimeUnit.NANOSECONDS);
		this.requestToken = "SYNCH";
		Date date = new Date(startTime);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS");
		this.startTimeStr = formatter.format(date);
		this.successResult = successResult;

		this.timeToLive = HealthDirector.timeToLiveLogEventInSec;
		this.deathTime = System.currentTimeMillis()
			+ (timeToLive * LogEvent.THOUSAND);
		this.birthTime = System.currentTimeMillis();

		HealthDirector.LOGGER.debug("Event TTL (milliSec): {}", timeToLive);
	}

	public LogEvent(OperationType opType, String userDN, String surl,
		long startTime, long duration, boolean successResult) {

		this.opType = opType;
		this.userDN = userDN;
		this.surl = surl;
		this.startTime = startTime;
		// Store the duration in MicroSeconds (10^-6 sec)
		this.duration = TimeUnit.MICROSECONDS.convert(duration,
			TimeUnit.NANOSECONDS);
		this.requestToken = "SYNCH";
		Date date = new Date(startTime);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SSS");
		this.startTimeStr = formatter.format(date);
		this.successResult = successResult;

		this.timeToLive = HealthDirector.timeToLiveLogEventInSec;
		this.deathTime = System.currentTimeMillis()
			+ (timeToLive * LogEvent.THOUSAND);
		this.birthTime = System.currentTimeMillis();

		HealthDirector.LOGGER.debug("Event TTL (milliSec): {}", timeToLive);
	}

	public OperationType getOperationType() {

		return this.opType;
	}

	public String getDN() {

		return this.userDN;
	}

	public String getSURL() {

		return this.surl;
	}

	public long getStartTime() {

		return this.startTime;
	}

	public String getStartTimeString() {

		return this.startTimeStr;
	}

	public long getDuration() {

		return this.duration;
	}

	public String getRequestToken() {

		return this.requestToken;
	}

	public boolean isSuccess() {

		return this.successResult;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		final char fieldSeparator = '\t';
		sb.append(userDN).append(fieldSeparator);
		sb.append(opType.toString()).append(fieldSeparator);
		sb.append(opType.getOperationTypeCategory()).append(fieldSeparator);
		if (this.successResult) {
			sb.append("-OK-").append(fieldSeparator);
		} else {
			sb.append("#ko#").append(fieldSeparator);
		}
		sb.append(surl).append(fieldSeparator);
		sb.append(startTimeStr).append(fieldSeparator);
		sb.append(duration).append(fieldSeparator);
		sb.append(requestToken).append(fieldSeparator);
		return sb.toString();
	}

	public long getDelay(TimeUnit unit) {

		long result = -1;
		result = unit.convert(deathTime - System.currentTimeMillis(),
			TimeUnit.MILLISECONDS);
		HealthDirector.LOGGER.debug("Event TimeToLive : {} result: {}",
		  timeToLive, result);

		return result;
	}

	public int compareTo(Delayed other) {

		LogEvent otherEvent = (LogEvent) other;
		if (deathTime < otherEvent.deathTime) {
			return -1;
		}
		if (deathTime > otherEvent.deathTime) {
			return 1;
		}
		return 0;
	}
}
