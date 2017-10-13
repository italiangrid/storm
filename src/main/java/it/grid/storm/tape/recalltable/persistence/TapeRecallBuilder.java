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
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallData;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 * 
 */
public class TapeRecallBuilder {

	private static final Logger log = LoggerFactory
		.getLogger(TapeRecallBuilder.class);

	/**
	 * { "filename":"<file-name>"; "dn":"<DN>"; "fqans":["fqan":"<FQAN>",
	 * "fqan":"<FQAN>"]; "vo-name":"<vo-name>" }
	 **/
	public static final String taskStart = "{";
	public static final String taskEnd = "}";
	public static final String elementSep = " # ";
	public static final String fnPrefix = "filename";
	public static final String dnPrefix = "dn";
	public static final String fqansPrefix = "fqans";
	public static final String fqansArrayStart = "[";
	public static final String fqansArrayEnd = "]";
	public static final String fqanPrefix = "fqan";
	public static final String fqanSep = ",";
	public static final String voNamePrefix = "vo-name";
	public static final String userIdPrefix = "userId";
	public static final String equalChar = "=";

	/**
	 * 
	 * @param bodyInput
	 *          : 4 fields "stfn, dn, fqans, vo-name"
	 * @return
	 */
	public static TapeRecallTO buildFromPOST(TapeRecallData rtd) {

		// Create a new RecallTaskTO
		TapeRecallTO task = new TapeRecallTO();

		Date currentDate = new Date();
		task.setInsertionInstant(currentDate);

		task.setRequestType(TapeRecallTO.TRequestType.BACK_REQUEST);

		String localRequestToken = "local-" + UUID.randomUUID();
		TRequestToken localRT;
		try {
			localRT = new TRequestToken(localRequestToken, Calendar.getInstance()
				.getTime());
		} catch (InvalidTRequestTokenAttributesException e) {
			// unexpected
			throw new IllegalStateException(
				"Unexpected InvalidTRequestTokenAttributesException: " + e);
		}
		task.setRequestToken(localRT);

		task.setPinLifetime(-1);
		task.setDeferredRecallInstant(currentDate);

		task.setUserID(rtd.getUserID());
		task.setVoName(rtd.getVoName());

		// Setting values into RecallTaskTO
		// - note: the setting of filename triggers the building of taskId
		task.setFileName(rtd.getFileName());

		return task;
	}

	/**
	 * @param string
	 * @return
	 */
	private static TapeRecallStatus parseTaskStatus(String taskStatus) {

		TapeRecallStatus result = TapeRecallStatus.UNDEFINED;
		try {
			result = TapeRecallStatus.getRecallTaskStatus(taskStatus);
		} catch (Exception e) {
			log.error("Task Status '{}' is not in a valid format." , taskStatus , e);
		}
		return result;
	}

	/**
	 * @param string
	 * @return
	 */
	private static int parseInt(String number) {

		int result = 0;
		try {
			result = Integer.parseInt(number);
		} catch (Exception e) {
			log.error("Number '{}' is not in a valid format." , number , e);
		}
		return result;
	}

	/**
	 * @param string
	 * @return
	 */
	private static Date parseDate(String dateString) {

		DateFormat formatter = new SimpleDateFormat(TapeRecallTO.DATE_FORMAT);
		Date date = new Date();
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			log.error("Date '{}' is not in a valid format." , dateString , e);
		}
		return date;
	}

}
