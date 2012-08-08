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

package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallTO implements Serializable, Comparable<TapeRecallTO> {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallTO.class);

    private static final long serialVersionUID = -2907739786996767167L;
    
    public static final String PTG_REQUEST = "ptg";
    public static final String BOL_REQUEST = "bol";
    public static final String BACK_REQUEST = "back";
    public static final String startChar = "";
    public static final char sepChar = '\u0009';
    public static final String dateFormat = "dd-MM-yyyy HH.mm.ss";


    
    private UUID taskId = null;
    private TRequestToken requestToken = null;
    private String requestType = null;
    private String fileName = null;
    private String userID = null;
    private String voName = null;
    private int pinLifetime = 0;
    private TapeRecallStatus status = TapeRecallStatus.QUEUED;
    private int retryAttempt = 0;
    private Date insertionInstant = null;
    private Date inProgressInstant = null;
    private Date finalStateInstant = null;
    private Date deferredRecallInstant = null;
    private UUID groupTaskId = null;
    
    public TapeRecallTO() {
    }

    public static TapeRecallTO createRandom(Date date, String voName) {

        TapeRecallTO result = new TapeRecallTO();
        result.setFileName("/root/" + voName + "/test/" + Math.round(Math.random() * 1000));
        try {
            result.setRequestToken(TRequestToken.getRandom());
        } catch (InvalidTRequestTokenAttributesException e) {
            log.warn("unable to create a random Request Token");
        }
        if(Math.random() % 2 == 0)
        {
            result.setRequestType(BOL_REQUEST);
        }
        else
        {
            result.setRequestType(PTG_REQUEST);
        }
        result.setUserID("FakeId");
        result.setRetryAttempt(0);
        result.setPinLifetime((int) Math.round(Math.random() * 1000));
        result.setVoName(voName);
        result.setInsertionInstant(date);
        int deferred = 0;
        if(Math.random() % 2 == 0)
        {
            deferred = 1;
        }
        Date deferredRecallTime = new Date(date.getTime() + (deferred * (long)Math.random())); 
        result.setDeferredRecallInstant(deferredRecallTime);
        result.setGroupTaskId(UUID.randomUUID());
        return result;
    }

    /*
     * Implementing the natural order (by age)
     */
    public int compareTo(TapeRecallTO arg0) {
        if (arg0 == null) {
            return 0;
        }
        return insertionInstant.compareTo(arg0.getInsertionInstant());
    }    
    
    public Date getDeferredRecallInstant() {
        return deferredRecallInstant;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getInsertionInstant() {
        return insertionInstant;
    }

    public Date getInProgressInstant() {
        return inProgressInstant;
    }
    
    public Date getFinalStateInstant() {
        return finalStateInstant;
    }
    public int getPinLifetime() {
        return pinLifetime;
    }

    public TapeRecallStatus getRecallStatus() {
        return status;
    }

    /**
     * RequestToken is the primary key of the table
     * @return
     */
    public TRequestToken getRequestToken() {
        return requestToken;
    }
    
    public String getRequestTokenStr() {
        return requestToken.getValue();
    }

    public String getRequestType() {
        return requestType;
    }

    public int getRetryAttempt() {
        return retryAttempt;
    }

    public int getStatusId() {
        return status.getStatusId();
    }

    public UUID getTaskId() {
        buildTaskId();   
        return taskId;
    }

    public UUID getGroupTaskId() {
        return groupTaskId;
    }
    
    public String getUserID() {
        return userID;
    }

    public String getVoName() {
        return voName;
    }    
    
    public void setDeferredRecallInstant(Date date) {
        deferredRecallInstant = date;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        buildTaskId();
    }

    public void setInsertionInstant(Date date) {
        insertionInstant = date;
    }

    private void setInProgressInstant(Date date) {
        inProgressInstant = date;
    }
    
    private void setFinalStateInstant(Date date) {
        finalStateInstant = date;
    }
    
    public void setPinLifetime(int pinLifetime) {
        this.pinLifetime = pinLifetime;
    }

    /**
     * 
     * @param requestToken
     */
    public void setRequestToken(TRequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public void setRequestTokenStr(String requestToken) throws InvalidTRequestTokenAttributesException {
        TRequestToken rToken = new TRequestToken(requestToken);
        setRequestToken(rToken);
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setRetryAttempt(int retryAttempt) {
        this.retryAttempt = retryAttempt;
    }

    /**
     * Sets the status of the recall task and if a transition is performed records
     * the appropriate time-stamp
     * @param status
     */
    public void setStatus(TapeRecallStatus status) {
        this.status = status;
        if(this.status.equals(TapeRecallStatus.IN_PROGRESS) && this.inProgressInstant == null)
        {
            this.setInProgressInstant(new Date());
        }
        else
        {
            if(TapeRecallStatus.isFinalStatus(this.status.getStatusId()) && this.inProgressInstant == null)
            {
                this.setFinalStateInstant(new Date());
            }
        }
    }

    /**
     * @param statusId
     */
    public void setStatusId(int statusId) {
        this.setStatus(TapeRecallStatus.getRecallTaskStatus(statusId));
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public void setGroupTaskId(UUID groupTaskId) {
        this.groupTaskId = groupTaskId;
    }
    
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setVoName(String voName) {
        this.voName = voName;
    }

    /**
     * Does not print the taskId but the group task Id
     * Does not print the state transition time stamps
     * 
     * @return
     */
    public String toGEMSS() {
        StringBuffer sb = new StringBuffer();

        sb.append(startChar);
        sb.append(groupTaskId);
        sb.append(sepChar);

        Format formatter = new SimpleDateFormat(dateFormat);
        if (insertionInstant != null) {
            sb.append(formatter.format(insertionInstant));
        } else {
            Calendar endOfTheWorld = new GregorianCalendar(2012, Calendar.DECEMBER, 21);
            insertionInstant = endOfTheWorld.getTime();
            sb.append(formatter.format(insertionInstant));
        }

        sb.append(sepChar);
        sb.append(requestType);
        sb.append(sepChar);
        sb.append(fileName);
        sb.append(sepChar);
        sb.append(voName);
        sb.append(sepChar);
        sb.append(userID);
        sb.append(sepChar);
        sb.append(retryAttempt);
        sb.append(sepChar);
        sb.append(status);
        sb.append(sepChar);
        
        if (deferredRecallInstant!=null) {
            sb.append(formatter.format(deferredRecallInstant));
        } else {
            sb.append(formatter.format(insertionInstant));
        }
        
        sb.append(sepChar);
        sb.append(pinLifetime);
        sb.append(sepChar);
        sb.append(requestToken);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(startChar);
        sb.append(taskId);
        sb.append(sepChar);

        Format formatter = new SimpleDateFormat(dateFormat);
        if (insertionInstant != null) {
            sb.append(formatter.format(insertionInstant));
        } else {
            Calendar endOfTheWorld = new GregorianCalendar(2012, Calendar.DECEMBER, 21);
            insertionInstant = endOfTheWorld.getTime();
            sb.append(formatter.format(insertionInstant));
        }

        sb.append(sepChar);
        sb.append(requestType);
        sb.append(sepChar);
        sb.append(fileName);
        sb.append(sepChar);
        sb.append(voName);
        sb.append(sepChar);
        sb.append(userID);
        sb.append(sepChar);
        sb.append(retryAttempt);
        sb.append(sepChar);
        sb.append(status);
        sb.append(sepChar);
        
        if (inProgressInstant != null) {
            sb.append(formatter.format(inProgressInstant));
        } else {
            sb.append("null");
        }
        sb.append(sepChar);
        
        if (finalStateInstant != null) {
            sb.append(formatter.format(finalStateInstant));
        } else {
            sb.append("null");
        }
        sb.append(sepChar);
        
        if (deferredRecallInstant!=null) {
            sb.append(formatter.format(deferredRecallInstant));
        } else {
            sb.append(formatter.format(insertionInstant));
        }
        
        sb.append(sepChar);
        sb.append(pinLifetime);
        sb.append(sepChar);
        sb.append(requestToken);
        sb.append(sepChar);
        sb.append(groupTaskId);
        return sb.toString();
    }

    /**
     * This method generate a TaskId from fileName
     * @return
     */
    private void buildTaskId() {
        
            if (this.fileName!=null) {
                this.taskId = buildTaskIdFromFileName(this.fileName);
            } else {
                log.error("Unable to create taskId because filename is NULL");
            }    
    }
    
    public static UUID buildTaskIdFromFileName(String fileName)
    {
        return UUID.nameUUIDFromBytes(fileName.getBytes());
    }

    /**
     * Intended to be used when building this object from a database row
     * NOTE: before to call this method, call the set status method  
     * 
     * @param inProgressInstant
     * @param finalStateInstant
     */
    public void forceStatusUpdateInstants(Date inProgressInstant, Date finalStateInstant) throws IllegalArgumentException
    {
        if(inProgressInstant != null)
        {
            if (this.status.equals(TapeRecallStatus.IN_PROGRESS) || TapeRecallStatus.isFinalStatus(this.status.getStatusId()))
            {
                this.inProgressInstant = inProgressInstant;
            }
            else
            {
                log.error("Unable to force the in progress transition time-stamp, current status " + this.status + " is not congruent");
            }
        }
        if(finalStateInstant != null)
        {
            if (TapeRecallStatus.isFinalStatus(this.status.getStatusId()))
            {
                this.finalStateInstant = finalStateInstant;
            }
            else
            {
                log.error("Unable to force the in final status transition time-stamp, current status " + this.status + " is not final");
            }
        }
    }

    public void setFakeRequestToken()
    {
        String FAKE_PREFIX = "FAKE-";
        try
        {
            this.setRequestToken(new TRequestToken(FAKE_PREFIX.concat(UUID.randomUUID().toString().substring(FAKE_PREFIX.length()))));
        } catch(InvalidTRequestTokenAttributesException e)
        {
            //never thrown
            log.error("Unexpected InvalidTRequestTokenAttributesException: " + e.getMessage());
        }
    }
}
