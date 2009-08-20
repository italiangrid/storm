package it.grid.storm.persistence.model;

import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class RecallTaskTO implements Serializable, Comparable<RecallTaskTO> {

    public static final String PTG_REQUEST = "ptg";
    public static final String BOL_REQUEST = "bol";
    public static final String startChar = "";
    public static final char sepChar = '\u0009';
    // public static final char endChar = '#';
    public static final String dateFormat = "dd-MM-yyyy HH.mm.ss";

    private static final long serialVersionUID = -2907739786996767167L;

    private String taskId = null;
    private String requestToken = null;
    private String requestType = null;
    private String fileName = null;
    private String userID = null;
    private String voName = null;
    private int pinLifetime = 0;
    private RecallTaskStatus status = RecallTaskStatus.QUEUED;
    private int retryAttempt = 0;
    private Date date = null;

    public RecallTaskTO() {
        taskId = UUID.randomUUID().toString();
    }

    public Date getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPinLifetime() {
        return pinLifetime;
    }

    public String getRequestToken() {
        return requestToken;
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

    public RecallTaskStatus getRecallStatus() {
        return status;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUserID() {
        return userID;
    }

    public String getVoName() {
        return voName;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPinLifetime(int pinLifetime) {
        this.pinLifetime = pinLifetime;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setRetryAttempt(int retryAttempt) {
        this.retryAttempt = retryAttempt;
    }

    public void setStatusId(int statusId) {
        status = RecallTaskStatus.getRecallTaskStatus(statusId);
    }

    public void setStatus(RecallTaskStatus status) {
        this.status = status;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setVoName(String voName) {
        this.voName = voName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(startChar);
        sb.append(taskId);
        sb.append(sepChar);

        Format formatter = new SimpleDateFormat(dateFormat);
        sb.append(formatter.format(date));

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
        sb.append(pinLifetime);
        sb.append(sepChar);
        sb.append(requestToken);
        // sb.append(endChar);
        return sb.toString();
    }

    /*
     * Implementing the natural order (by age)
     */
    @Override
    public int compareTo(RecallTaskTO arg0) {
        if (arg0 == null) {
            return 0;
        }
        return date.compareTo(arg0.getDate());
    }

    public static RecallTaskTO createRandom(Date date, String voName) {

        RecallTaskTO result = new RecallTaskTO();
        result.setFileName("/root/" + voName + "/test/" + Math.round(Math.random() * 1000));
        result.setRequestToken(voName + Math.round(Math.random() * 1000));
        result.setRetryAttempt(0);
        result.setPinLifetime((int) Math.round(Math.random() * 1000));
        result.setVoName(voName);
        result.setDate(date);
        return result;
    }
}
