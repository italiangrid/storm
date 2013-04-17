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

package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.persistence.util.db.SQLHelper;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallMySQLHelper extends SQLHelper {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
		.getLogger(TapeRecallMySQLHelper.class);

	private final static String TABLE_NAME = "tape_recall";

	// primary key COL_TASK_ID + COL_REQUEST_TOKEN
	public final static String COL_TASK_ID = "taskId";
	public final static String COL_REQUEST_TOKEN = "requestToken";
	public final static String COL_REQUEST_TYPE = "requestType";
	public final static String COL_FILE_NAME = "fileName";
	public final static String COL_PIN_LIFETIME = "pinLifetime";
	public final static String COL_STATUS = "status";
	public final static String COL_USER_ID = "userID";
	public final static String COL_VO_NAME = "voName";
	public final static String COL_DATE = "timeStamp";
	public final static String COL_RETRY_ATTEMPT = "retryAttempt";
	public final static String COL_DEFERRED_STARTTIME = "deferredStartTime";
	public final static String COL_GROUP_TASK_ID = "groupTaskId";
	public final static String COL_IN_PROGRESS_DATE = "inProgressTime";
	public final static String COL_FINAL_STATUS_DATE = "finalStatusTime";

	/**
	 * Verifies if the given string is the name of one of the timestamp columns
	 * 
	 * @param columnName
	 * @return
	 */
	private static boolean validTimestampColumnName(String columnName) {

		return COL_DATE.equals(columnName)
			|| COL_IN_PROGRESS_DATE.equals(columnName)
			|| COL_FINAL_STATUS_DATE.equals(columnName);
	}

	public TapeRecallMySQLHelper(String dbmsVendor) {

		super(dbmsVendor);
	}

	private String formatString(String s) {

		if (s == null) {
			return null;
		}
		return "'" + s + "'";
	}

	/**
	 * @param conn
	 * @param recallTask
	 * @return a PreparedStatement for the requested query
	 */
	public PreparedStatement getQueryInsertTask(Connection conn,
		TapeRecallTO recallTask) {

		if (recallTask == null) {
			return null;
		}

		String query = "INSERT INTO " + TABLE_NAME + " (" + COL_TASK_ID + ", "
			+ COL_REQUEST_TOKEN + ", " + COL_REQUEST_TYPE + ", " + COL_FILE_NAME
			+ ", " + COL_PIN_LIFETIME + ", " + COL_STATUS + ", " + COL_VO_NAME + ", "
			+ COL_USER_ID + ", " + COL_RETRY_ATTEMPT + ", " + COL_DEFERRED_STARTTIME
			+ ", " + COL_DATE + ", " + COL_GROUP_TASK_ID
			+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			PreparedStatement prepStat = conn.prepareStatement(query);

			int idx = 1;
			prepStat.setString(idx++, recallTask.getTaskId().toString());
			prepStat.setString(idx++, recallTask.getRequestToken().getValue());
			prepStat.setString(idx++, recallTask.getRequestType());
			prepStat.setString(idx++, recallTask.getFileName());
			prepStat.setInt(idx++, recallTask.getPinLifetime());
			prepStat.setInt(idx++, recallTask.getStatusId());

			prepStat.setString(idx++, recallTask.getVoName());
			prepStat.setString(idx++, recallTask.getUserID());
			prepStat.setInt(idx++, recallTask.getRetryAttempt());
			prepStat.setTimestamp(idx++, new java.sql.Timestamp(recallTask
				.getDeferredRecallInstant().getTime()));
			prepStat.setTimestamp(idx++, new java.sql.Timestamp(recallTask
				.getInsertionInstant().getTime()));
			prepStat.setString(idx++, recallTask.getGroupTaskId().toString());
			return prepStat;

		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * @param taskId
	 * @param requestToken
	 * @return the requested query as string
	 */
	public String getQueryGetTask(UUID taskId, String requestToken) {

		String queryFormat = "SELECT * FROM " + TABLE_NAME + " WHERE "
			+ COL_TASK_ID + "=" + formatString(taskId.toString()) + " AND "
			+ COL_REQUEST_TOKEN + "=" + formatString(requestToken);

		return queryFormat;
	}

	/**
	 * @param groupTaskId
	 * @return the requested query as string
	 */
	public String getQueryGetGroupTasks(UUID groupTaskId) {

		String queryFormat = "SELECT * FROM " + TABLE_NAME + " WHERE "
			+ COL_GROUP_TASK_ID + "=" + formatString(groupTaskId.toString());
		return queryFormat;
	}

	/**
	 * @param taskId
	 * @return the requested query as string
	 */
	public String getQueryGetGroupTaskIds(UUID taskId) {

		String queryFormat = "SELECT DISTINCT " + COL_GROUP_TASK_ID + " , "
			+ COL_STATUS + " , " + COL_IN_PROGRESS_DATE + " , "
			+ COL_FINAL_STATUS_DATE + " FROM " + TABLE_NAME + " WHERE " + COL_TASK_ID
			+ "=" + formatString(taskId.toString());
		return queryFormat;
	}

	/**
	 * @param taskId
	 * @param statuses
	 * @return the requested query as string
	 */
	public String getQueryGetGroupTaskIds(UUID taskId, int[] statuses) {

		String queryFormat = "SELECT DISTINCT " + COL_GROUP_TASK_ID + " , "
			+ COL_STATUS + " , " + COL_IN_PROGRESS_DATE + " , "
			+ COL_FINAL_STATUS_DATE + " FROM " + TABLE_NAME + " WHERE " + COL_TASK_ID
			+ "=" + formatString(taskId.toString()) + " AND " + COL_STATUS + " IN ( ";
		boolean first = true;
		for (int status : statuses) {
			if (first) {
				first = false;
			} else {
				queryFormat += " , ";
			}
			queryFormat += status;
		}
		queryFormat += " )";
		return queryFormat;
	}

	/**
	 * @return the requested query as string
	 */
	public String getQueryNumberQueued() {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.QUEUED.getStatusId();

		return queryFormat;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 */
	public String getQueryNumberQueued(String voName) {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.QUEUED.getStatusId() + " AND " + COL_VO_NAME + "="
			+ formatString(voName);

		return queryFormat;
	}

	/**
	 * @return the requested query as string
	 */
	public String getQueryReadyForTakeOver() {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.QUEUED.getStatusId() + " AND "
			+ COL_DEFERRED_STARTTIME + "<=NOW()";
		return queryFormat;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 */
	public String getQueryReadyForTakeOver(String voName) {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.QUEUED.getStatusId() + " AND " + COL_VO_NAME + "="
			+ formatString(voName) + " AND " + COL_DEFERRED_STARTTIME + "<=NOW()";
		return queryFormat;
	}

	/**
	 * @return the requested query as string
	 */
	public String getQueryNumberInProgress() {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.IN_PROGRESS.getStatusId();
		return queryFormat;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 */
	public String getQueryNumberInProgress(String voName) {

		String queryFormat = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID
			+ ") FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "="
			+ TapeRecallStatus.IN_PROGRESS.getStatusId() + " AND " + COL_VO_NAME
			+ "=" + formatString(voName);

		return queryFormat;
	}

	/**
	 * @param numberOfTasks
	 * @return the requested query as string
	 */
	public String getQueryGetTakeoverTasksWithDoubles(int numberOfTasks) {

		String queryFormat = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_STATUS
			+ "=" + TapeRecallStatus.QUEUED.getStatusId() + " AND "
			+ COL_DEFERRED_STARTTIME + "<=NOW() ORDER BY " + COL_DEFERRED_STARTTIME
			+ " LIMIT " + numberOfTasks;

		return queryFormat;
	}

	/**
	 * @param numberOfTasks
	 * @param voName
	 * @return the requested query as string
	 */
	public String getQueryGetTakeoverTasksWithDoubles(int numberOfTasks,
		String voName) {

		String queryFormat = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_STATUS
			+ "=" + TapeRecallStatus.QUEUED.getStatusId() + " AND " + COL_VO_NAME
			+ "=" + formatString(voName) + " AND " + COL_DEFERRED_STARTTIME
			+ "<=NOW() ORDER BY " + COL_DEFERRED_STARTTIME + " LIMIT "
			+ numberOfTasks;

		return queryFormat;
	}

	/**
	 * @param taskList
	 * @param date
	 * @param j
	 * @return
	 */
	public String getQueryUpdateTasksStatus(List<TapeRecallTO> taskList,
		int statusId, String timestampColumn, Date timestamp)
		throws IllegalArgumentException {

		if (taskList.size() == 0) {
			return null;
		}
		if (validTimestampColumnName(timestampColumn)) {
			String queryFormat = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "="
				+ statusId + " , " + timestampColumn + "=\'"
				+ new java.sql.Timestamp(timestamp.getTime()) + "\' " + " WHERE "
				+ COL_GROUP_TASK_ID + "="
				+ formatString(taskList.get(0).getGroupTaskId().toString());
			for (int i = 1; i < taskList.size(); i++) {
				queryFormat += " OR " + COL_GROUP_TASK_ID + "="
					+ formatString(taskList.get(i).getGroupTaskId().toString());
			}
			return queryFormat;
		} else {
			throw new IllegalArgumentException(
				"Unable to update row status and timestamp. The priovided timestamp column \'"
					+ timestampColumn + "\' is not valid");
		}
	}

	/**
	 * @param groupTaskId
	 * @param status
	 * @param timestampColumn
	 * @param timestamp
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String getQueryUpdateGroupTaskStatus(UUID groupTaskId, int status,
		String timestampColumn, Date timestamp) throws IllegalArgumentException {

		if (validTimestampColumnName(timestampColumn)) {
			String queryFormat = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "="
				+ status + " , " + timestampColumn + "=\'"
				+ new java.sql.Timestamp(timestamp.getTime()) + "\' " + " WHERE "
				+ COL_GROUP_TASK_ID + "=" + formatString(groupTaskId.toString())
				+ " AND " + COL_STATUS + "!=" + status;
			return queryFormat;
		} else {
			throw new IllegalArgumentException(
				"Unable to update row status and timestamp. The priovided timestamp column \'"
					+ timestampColumn + "\' is not valid");
		}
	}

	/**
	 * @param groupTaskId
	 * @param status
	 * @return the requested query as string
	 */
	public String getQuerySetGroupTaskStatus(UUID groupTaskId, int status) {

		String queryFormat = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "="
			+ status + " WHERE " + COL_GROUP_TASK_ID + "="
			+ formatString(groupTaskId.toString()) + " AND " + COL_STATUS + "!="
			+ status;

		return queryFormat;
	}

	/**
	 * @param groupTaskId
	 * @param value
	 * @return the requested query as string
	 */
	public String getQuerySetGroupTaskRetryValue(UUID groupTaskId, int value) {

		String queryFormat = "UPDATE " + TABLE_NAME + " SET " + COL_RETRY_ATTEMPT
			+ "=" + value + " WHERE " + COL_GROUP_TASK_ID + "="
			+ formatString(groupTaskId.toString());

		return queryFormat;
	}

	/**
	 * @return the requested query as string
	 */
	public String getQueryDeleteCompletedTasks() {

		String queryFormat = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_STATUS
			+ "<>" + TapeRecallStatus.QUEUED.getStatusId() + " AND " + COL_STATUS
			+ "<>" + TapeRecallStatus.IN_PROGRESS.getStatusId();

		return queryFormat;
	}

	/**
	 * @param maxNumTasks
	 * @return the requested query as string
	 */
	public String getQueryDeleteCompletedTasks(int maxNumTasks) {

		String queryFormat = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_STATUS
			+ " != " + TapeRecallStatus.QUEUED.getStatusId() + " AND " + COL_STATUS
			+ " != " + TapeRecallStatus.IN_PROGRESS.getStatusId() + " LIMIT "
			+ maxNumTasks;

		return queryFormat;
	}
}
