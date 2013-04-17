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
package it.grid.storm.health.external;

import it.grid.storm.health.HealthDirector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author zappi
 * 
 */
public class FSMetadataStatus {

	private String pulseNumberStr = "";
	private long lifetime = -1L;
	private String lifetimeStr = "";
	private final int benchmarkCount = -1;
	private final Hashtable<String, Long> pathName = new Hashtable<String, Long>();

	/**
     * 
     */
	public FSMetadataStatus(ArrayList<String> storageAreasName) {

		super();
		pathName.put("Local", -1L);
		for (Object element : storageAreasName) {
			pathName.put((String) element, -1L);
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

	public void calculateLifeTime() {

		long bornTime = HealthDirector.getBornInstant();
		long now = System.currentTimeMillis();
		this.lifetime = now - bornTime;

		Date date = new Date(this.lifetime);
		SimpleDateFormat formatter = new SimpleDateFormat("mm.ss");
		String minsec = formatter.format(date);
		long hours = this.lifetime / 3600000;
		this.lifetimeStr = hours + ":" + minsec;
	}

	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append(" [#" + this.pulseNumberStr + " lifetime=" + this.lifetimeStr
			+ "]");
		Enumeration<String> sas = pathName.keys();
		while (sas.hasMoreElements()) {
			String sa = sas.nextElement();
			Long average = pathName.get(sa);
			result.append("SA('" + sa + "')=" + average);
		}
		return result.toString();
	}

}
