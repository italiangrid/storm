/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.persistence.model.RecallTaskTO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
public class RecallTaskBuilder {

    private static final Logger log = LoggerFactory.getLogger(RecallTaskBuilder.class);

    public static RecallTaskTO build(String recallTaskLine) {
        RecallTaskTO recallTask = new RecallTaskTO();
        String startChar = "> ";
        String sepChar = "\t";

        // Check if the line is empty
        if (((recallTaskLine != null)) && ((recallTaskLine.length() > 0))) {
            // Check if start with the right starter string
            if (recallTaskLine.startsWith(startChar)) {
                log.debug("RecallTable Row : '" + recallTaskLine + "'");
                recallTaskLine = recallTaskLine.substring(2);
                log.debug("RecallTable Row : '" + recallTaskLine + "'");
                String[] fields = recallTaskLine.split(sepChar);
                log.debug("RecallTable Row # fields = " + fields.length);
                // Check if number of Fields is 10.
                if (fields.length == 10) {
                    // ####### Manage the fields #######
                    // FIELD-0 = TaskId (String)
                    recallTask.setTaskId(fields[0]);
                    // FIELD-1 = Date (java.util.Date)
                    recallTask.setDate(parseDate(fields[1]));
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
                    recallTask.setStatusId(parseInt(fields[7]));
                    // FIELD-8 = pinLifetime (int)
                    recallTask.setPinLifetime(parseInt(fields[8]));
                    // FIELD-9 = requestToken (String)
                    recallTask.setRequestToken(fields[9]);
                } else {
                    log.debug("RecallTable Row contains # fields not equal to 10.");
                }
           } else {
                // Invalid Row
                log.debug("RecallTable Row does not starts with '" + startChar + "'");
            }
        } else {
            // Empty row
            log.debug("RecallTable Row is EMPTY");
        }
        return recallTask;
    }

    
    
    
    /**
     * @param string
     * @return
     */
    private static int parseInt(String integer) {
        int result = 0;
        try {
            result = Integer.parseInt(integer);
        } catch (Exception e) {
            log.error("Number '" + integer + "' is not in a valid format.");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param string
     * @return
     */
    private static Date parseDate(String dateString) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            log.error("Date '" + dateString + "' is not in a valid format.");
            e.printStackTrace();
        }
        return date;
    }




    public static void main(String[] args) {
        String recallTaskLine = "> taskid\t12:23:45 13.08.2009\treqtpye\tfilename\tvoname\tuserid\t2\t1\t2\treqtoken";
        RecallTaskTO task = RecallTaskBuilder.build(recallTaskLine);
        log.debug("Recall Task : '" + task + "'");
        Date d = new Date();
        log.debug("" + d);
    }
}
