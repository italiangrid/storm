/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.RecallTaskData;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zappi
 * 
 */
public class RecallTaskBuilder {

    private static final Logger log = LoggerFactory.getLogger(RecallTaskBuilder.class);

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
    
    

	public static RecallTaskTO build(String recallTaskLine) {

		RecallTaskTO recallTask = new RecallTaskTO();
		String startChar = RecallTaskTO.startChar;
		String sepChar = "" + RecallTaskTO.sepChar;

		// Check if the line is empty
		if(((recallTaskLine != null)) && ((recallTaskLine.length() > 0)))
		{
			// Check if start with the right starter string
			if(recallTaskLine.startsWith(startChar))
			{
				// log.debug("RecallTable Row : '" + recallTaskLine + "'");
				// recallTaskLine = recallTaskLine.substring(2);
				// log.debug("RecallTable Row : '" + recallTaskLine + "'");
				String[] fields = recallTaskLine.split(sepChar);
				// log.debug("RecallTable Row # fields = " + fields.length);
				// Check if number of Fields is 10.
//				if(fields.length == 10)
				if(fields.length == 11)
				{
					// ####### Manage the fields #######
					// FIELD-0 = TaskId (int)
					UUID taskId = null;
					try
					{
						taskId = UUID.fromString(fields[0]);
					} catch(IllegalArgumentException e)
					{
						log.error("Unable to parse the taskId '" + fields[0] + "'.");
					}
					recallTask.setTaskId(taskId);
					// FIELD-1 = insertionInstant (java.util.Date)
					recallTask.setInsertionInstant(parseDate(fields[1]));
					// FIELD-2 = requestType (String)
					recallTask.setRequestType(fields[2]);
					// FIELD-3 = fileName (String)
					recallTask.setFileName(fields[3]);
					// FIELD-4 = voName (String)
					recallTask.setVoName(fields[4]);
					// FIELD-5 = userID (String)
					recallTask.setUserID(fields[5]);
					// FIELD-6 = retryAttempt (int)
					recallTask.setRetryAttempt(parseInt(fields[6]));
					// FIELD-7 = status (int)
					recallTask.setStatus(parseTaskStatus(fields[7]));
					// FIELD-8 = pinLifetime (int)
					recallTask.setPinLifetime(parseInt(fields[8]));
					// FIELD-9 = deferredRecallInstant (java.util.Date)
					recallTask.setDeferredRecallInstant(parseDate(fields[9]));
					// FIELD-10 = requestToken (String)
					try
					{
//						recallTask.setRequestTokenStr(fields[9]);
						recallTask.setRequestTokenStr(fields[10]);
					} catch(InvalidTRequestTokenAttributesException e)
					{
						log.error("Unable to parse the RequestToken '" + fields[9] + "'.");
					}
				}
				else
				{
					log.debug("RecallTable Row contains # fields not equal to 10.");
				}
			}
			else
			{
				// Invalid Row
				log.debug("RecallTable Row does not starts with '" + startChar + "'");
			}
		}
		else
		{
			// Empty row
			log.debug("RecallTable Row is EMPTY");
		}
		return recallTask;
	}


    /**
     * 
     * @param bodyInput
     *            : 4 fields "stfn, dn, fqans, vo-name"
     * @return
     */
    public static RecallTaskTO buildFromPOST(RecallTaskData rtd) {
        // Create a new RecallTaskTO
        RecallTaskTO task = new RecallTaskTO();

        Date currentDate = new Date();
        task.setInsertionInstant(currentDate);

        task.setRequestType(RecallTaskTO.BACK_REQUEST);
        
        String localRequestToken = "local-"+UUID.randomUUID();
        TRequestToken localRT = TRequestToken.buildLocalRT(localRequestToken);
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
    private static RecallTaskStatus parseTaskStatus(String taskStatus) {
        RecallTaskStatus result = RecallTaskStatus.UNDEFINED;
        try {
            result = RecallTaskStatus.getRecallTaskStatus(taskStatus);
        } catch (Exception e) {
            log.error("Task Status '" + taskStatus + "' is not in a valid format.");
            e.printStackTrace();
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
            log.error("Number'" + number + "' is not in a valid format.");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @param string
     * @return
     */
    private static Date parseDate(String dateString) {
        DateFormat formatter = new SimpleDateFormat(RecallTaskTO.dateFormat);
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            log.error("Date '" + dateString + "' is not in a valid format.");
            e.printStackTrace();
        }
        return date;
    }

}
