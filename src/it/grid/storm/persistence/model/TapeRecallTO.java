package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

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
    // public static final char endChar = '#';
    public static final String dateFormat = "dd-MM-yyyy HH.mm.ss";


    
    private UUID taskId = null;
    private TRequestToken requestToken = null;
    private String requestType = null;
    private String fileName = null;
    private String userID = null;
    private String voName = null;
    private int pinLifetime = 0;
    private RecallTaskStatus status = RecallTaskStatus.QUEUED;
    private int retryAttempt = 0;
    private Date insertionInstant = null;
    private Date deferredRecallInstant = null;

    public TapeRecallTO() {
    }

    public static TapeRecallTO createRandom(Date date, String voName) {

        TapeRecallTO result = new TapeRecallTO();
        result.taskId = UUID.randomUUID();
        result.setFileName("/root/" + voName + "/test/" + Math.round(Math.random() * 1000));
        try {
            result.setRequestToken(TRequestToken.getRandom());
        } catch (InvalidTRequestTokenAttributesException e) {
            log.warn("unable to create a random Request Token");
        }
        result.setRetryAttempt(0);
        result.setPinLifetime((int) Math.round(Math.random() * 1000));
        result.setVoName(voName);
        result.setInsertionInstant(date);
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

    public int getPinLifetime() {
        return pinLifetime;
    }

    public RecallTaskStatus getRecallStatus() {
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

    public void setStatus(RecallTaskStatus status) {
        this.status = status;
    }

    public void setStatusId(int statusId) {
        status = RecallTaskStatus.getRecallTaskStatus(statusId);
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setVoName(String voName) {
        this.voName = voName;
    }

    
    
    public String toString(boolean[] verbosity) {
        StringBuffer sb = new StringBuffer();
        Format formatter = new SimpleDateFormat(dateFormat);
        sb.append(startChar);
        if ((verbosity != null) && (verbosity.length == 11)) {
            if (verbosity[0]) {
                sb.append(taskId);
                sb.append(sepChar);
            }
            if (verbosity[1]) {

                if (insertionInstant != null) {
                    sb.append(formatter.format(insertionInstant));
                } else {
                    Calendar xmas = new GregorianCalendar(2008, Calendar.DECEMBER, 25);
                    insertionInstant = xmas.getTime();
                    sb.append(formatter.format(insertionInstant));
                }
                sb.append(sepChar);
            }
            if (verbosity[2]) {
                sb.append(requestType);
                sb.append(sepChar);
            }
            if (verbosity[3]) {
                sb.append(fileName);
                sb.append(sepChar);
            }
            if (verbosity[4]) {
                sb.append(voName);
                sb.append(sepChar);
            }
            if (verbosity[5]) {
                sb.append(userID);
                sb.append(sepChar);
            }
            if (verbosity[6]) {
                sb.append(retryAttempt);
                sb.append(sepChar);
            }
            if (verbosity[7]) {
                sb.append(status);
                sb.append(sepChar);
            }
            if (verbosity[8]) {
                if (deferredRecallInstant != null) {
                    sb.append(formatter.format(deferredRecallInstant));
                } else {
                    sb.append(formatter.format(insertionInstant));
                }
                sb.append(sepChar);
            }
            if (verbosity[9]) {
                sb.append(pinLifetime);
                sb.append(sepChar);
            }
            if (verbosity[10]) {
                sb.append(requestToken);
            }
        }
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
            Calendar xmas = new GregorianCalendar(2008, Calendar.DECEMBER, 25);
            insertionInstant = xmas.getTime();
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
        // sb.append(endChar);
        return sb.toString();
    }

    /**
     * This method generate a TaskId from fileName
     * @return
     */
    private void buildTaskId() {
        
        if (this.taskId==null) {
            if (this.fileName!=null) {
                this.taskId = UUID.nameUUIDFromBytes(this.fileName.getBytes());   
            } else {
                log.error("Unable to create taskId because filename is NULL");
            }    
        }
    }


}
