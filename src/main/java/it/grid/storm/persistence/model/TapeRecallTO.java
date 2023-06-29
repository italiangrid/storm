/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.BOL;
import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.PTG;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

public class TapeRecallTO implements Serializable, Comparable<TapeRecallTO> {

	public enum RecallTaskType {

		PTG, BOL, BACK, RCLL;
	}

	private static final Logger log = LoggerFactory.getLogger(TapeRecallTO.class);

	private static final long serialVersionUID = -2907739786996767167L;

	public static final String START_CHAR = "";
	public static final char SEPARATOR_CHAR = '\u0009';
	public static final String DATE_FORMAT = "dd-MM-yyyy HH.mm.ss";

	private UUID taskId = null;
	private TRequestToken requestToken = null;
	private RecallTaskType requestType = null;
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

	private final Calendar endOfTheWorld = new GregorianCalendar(2012, Calendar.DECEMBER, 21);

	public static TapeRecallTO createRandom(Date date, String voName) {

		TapeRecallTO result = new TapeRecallTO();
		Random r = new Random();
		result.setFileName("/root/" + voName + "/test/" + r.nextInt(1001));
		result.setRequestToken(TRequestToken.getRandom());
		if (r.nextInt(2) == 0) {
			result.setRequestType(BOL);
		} else {
			result.setRequestType(PTG);
		}
		result.setUserID("FakeId");
		result.setRetryAttempt(0);
		result.setPinLifetime(r.nextInt(1001));
		result.setVoName(voName);
		result.setInsertionInstant(date);
		int deferred = r.nextInt(2);
		Date deferredRecallTime = new Date(date.getTime() + (deferred * (long) Math.random()));
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

	public TapeRecallStatus getStatus() {

		return status;
	}

	/**
	 * RequestToken is the primary key of the table
	 * 
	 * @return
	 */
	public TRequestToken getRequestToken() {

		return requestToken;
	}

	public RecallTaskType getRequestType() {

		return requestType;
	}

	public int getRetryAttempt() {

		return retryAttempt;
	}

	@JsonIgnore
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

	public void setRequestType(RecallTaskType requestType) {

		this.requestType = requestType;
	}

	public void setRetryAttempt(int retryAttempt) {

		this.retryAttempt = retryAttempt;
	}

	/**
	 * Sets the status of the recall task and if a transition is performed records the appropriate
	 * time-stamp
	 * 
	 * @param status
	 */
	public void setStatus(TapeRecallStatus status) {

		this.status = status;
		if (this.status.equals(TapeRecallStatus.IN_PROGRESS) && this.inProgressInstant == null) {
			this.setInProgressInstant(new Date());
		} else {
			if (TapeRecallStatus.isFinalStatus(this.status.getStatusId())
					&& this.inProgressInstant == null) {
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
	 * Does not print the taskId but the group task Id Does not print the state transition time
	 * stamps
	 * 
	 * @return
	 */
	public String toGEMSS() {

		StringBuilder sb = new StringBuilder();

		sb.append(START_CHAR);
		sb.append(groupTaskId);
		sb.append(SEPARATOR_CHAR);

		Format formatter = new SimpleDateFormat(DATE_FORMAT);
		if (insertionInstant != null) {
			sb.append(formatter.format(insertionInstant));
		} else {
			insertionInstant = endOfTheWorld.getTime();
			sb.append(formatter.format(insertionInstant));
		}

		sb.append(SEPARATOR_CHAR);
		sb.append(requestType);
		sb.append(SEPARATOR_CHAR);
		sb.append(fileName);
		sb.append(SEPARATOR_CHAR);
		sb.append(voName);
		sb.append(SEPARATOR_CHAR);
		sb.append(userID);
		sb.append(SEPARATOR_CHAR);
		sb.append(retryAttempt);
		sb.append(SEPARATOR_CHAR);
		sb.append(status);
		sb.append(SEPARATOR_CHAR);

		if (deferredRecallInstant != null) {
			sb.append(formatter.format(deferredRecallInstant));
		} else {
			sb.append(formatter.format(insertionInstant));
		}

		sb.append(SEPARATOR_CHAR);
		sb.append(pinLifetime);
		sb.append(SEPARATOR_CHAR);
		sb.append(requestToken);
		sb.append(SEPARATOR_CHAR);

		if (inProgressInstant != null)
			sb.append(formatter.format(inProgressInstant));
		else
			sb.append("null");

		return sb.toString();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append(START_CHAR);
		sb.append(taskId);
		sb.append(SEPARATOR_CHAR);

		Format formatter = new SimpleDateFormat(DATE_FORMAT);
		if (insertionInstant != null) {
			sb.append(formatter.format(insertionInstant));
		} else {
			insertionInstant = endOfTheWorld.getTime();
			sb.append(formatter.format(insertionInstant));
		}

		sb.append(SEPARATOR_CHAR);
		sb.append(requestType);
		sb.append(SEPARATOR_CHAR);
		sb.append(fileName);
		sb.append(SEPARATOR_CHAR);
		sb.append(voName);
		sb.append(SEPARATOR_CHAR);
		sb.append(userID);
		sb.append(SEPARATOR_CHAR);
		sb.append(retryAttempt);
		sb.append(SEPARATOR_CHAR);
		sb.append(status);
		sb.append(SEPARATOR_CHAR);

		if (inProgressInstant != null) {
			sb.append(formatter.format(inProgressInstant));
		} else {
			sb.append("null");
		}
		sb.append(SEPARATOR_CHAR);

		if (finalStateInstant != null) {
			sb.append(formatter.format(finalStateInstant));
		} else {
			sb.append("null");
		}
		sb.append(SEPARATOR_CHAR);

		if (deferredRecallInstant != null) {
			sb.append(formatter.format(deferredRecallInstant));
		} else {
			sb.append(formatter.format(insertionInstant));
		}

		sb.append(SEPARATOR_CHAR);
		sb.append(pinLifetime);
		sb.append(SEPARATOR_CHAR);
		sb.append(requestToken);
		sb.append(SEPARATOR_CHAR);
		sb.append(groupTaskId);
		return sb.toString();
	}

	/**
	 * This method generate a TaskId from fileName
	 * 
	 * @return
	 */
	private void buildTaskId() {

		if (this.fileName != null) {
			this.taskId = buildTaskIdFromFileName(this.fileName);
		} else {
			log.error("Unable to create taskId because filename is NULL");
		}
	}

	public static UUID buildTaskIdFromFileName(String fileName) {

		return UUID.nameUUIDFromBytes(fileName.getBytes());
	}

	/**
	 * Intended to be used when building this object from a database row NOTE: before to call this
	 * method, call the set status method
	 * 
	 * @param inProgressInstant
	 * @param finalStateInstant
	 */
	public void forceStatusUpdateInstants(Date inProgressInstant, Date finalStateInstant) {

		if (inProgressInstant != null) {
			if (this.status.equals(TapeRecallStatus.IN_PROGRESS)
					|| TapeRecallStatus.isFinalStatus(this.status.getStatusId())) {
				this.inProgressInstant = inProgressInstant;
			} else {
				log.error("Unable to force the in progress transition time-stamp. "
						+ "Invalid status: {}", status);
			}
		}
		if (finalStateInstant != null) {
			if (TapeRecallStatus.isFinalStatus(this.status.getStatusId())) {
				this.finalStateInstant = finalStateInstant;
			} else {
				log.error("Unable to force the in final status transition time-stamp. "
						+ "current status {} is not finale", status);
			}
		}
	}

	public void setFakeRequestToken() {

		final String FAKE_PREFIX = "FAKE-";
		try {
			this.setRequestToken(new TRequestToken(
					FAKE_PREFIX
							.concat(UUID.randomUUID().toString().substring(FAKE_PREFIX.length())),
					Calendar.getInstance().getTime()));
		} catch (InvalidTRequestTokenAttributesException e) {
			log.error(e.getMessage(), e);
		}
	}

}
