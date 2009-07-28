package it.grid.storm.persistence.model;

import it.grid.storm.persistence.dao.TapeRecallDAO;

import java.io.Serializable;
import java.util.Date;

public class RecallTaskTO implements Serializable {

    public static final String PTG_REQUEST = "ptg";
    public static final String BOL_REQUEST = "bol";

    private static final long serialVersionUID = -2907739786996767167L;

    private String taskId = null;
    private String requestToken = null;
    private String requestType = null;
    private String fileName = null;
    private String userID = null;
    private String voName = null;
    private int pinLifetime = 0;
    private int status = TapeRecallDAO.QUEUED;
    private int retryAttempt = 0;
    private Date date = null;
    
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

    public int getStatus() {
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

    public void setStatus(int status) {
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
}
