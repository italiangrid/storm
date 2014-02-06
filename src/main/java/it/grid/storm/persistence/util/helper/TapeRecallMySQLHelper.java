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
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetTask(Connection conn, UUID taskId,
		String requestToken) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TASK_ID + "=?"
			+ " AND " + COL_REQUEST_TOKEN + "=?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setString(1, taskId.toString());
		preparedStatement.setString(2, requestToken);

		return preparedStatement;
	}

	/**
	 * @param groupTaskId
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetGroupTasks(Connection conn,
		UUID groupTaskId) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_GROUP_TASK_ID + "=?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setString(1, groupTaskId.toString());

		return preparedStatement;
	}

	/**
	 * @param taskId
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetGroupTaskIds(Connection conn, UUID taskId)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT DISTINCT " + COL_GROUP_TASK_ID + " , " + COL_STATUS + " , "
			+ COL_IN_PROGRESS_DATE + " , " + COL_FINAL_STATUS_DATE + " FROM "
			+ TABLE_NAME + " WHERE " + COL_TASK_ID + "=?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setString(1, taskId.toString());

		return preparedStatement;
	}

	/**
	 * @param taskId
	 * @param statuses
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetGroupTaskIds(Connection conn,
		UUID taskId, int[] statuses) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT DISTINCT " + COL_GROUP_TASK_ID + " , " + COL_STATUS + " , "
			+ COL_IN_PROGRESS_DATE + " , " + COL_FINAL_STATUS_DATE + " FROM "
			+ TABLE_NAME + " WHERE " + COL_TASK_ID + "=?" + " AND " + COL_STATUS
			+ " IN ( ";

		boolean first = true;
		for (int status : statuses) {
			if (first) {
				first = false;
			} else {
				str += " , ";
			}
			str += status;
		}
		str += " )";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setString(1, taskId.toString());

		return preparedStatement;
	}

	/**
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryNumberQueued(Connection conn)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());

		return preparedStatement;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryNumberQueued(Connection conn, String voName)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?" + " AND " + COL_VO_NAME + "=?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setString(2, voName);

		return preparedStatement;
	}

	/**
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryReadyForTakeOver(Connection conn)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?" + " AND " + COL_DEFERRED_STARTTIME
			+ "<=NOW()";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());

		return preparedStatement;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryReadyForTakeOver(Connection conn,
		String voName) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?" + " AND " + COL_VO_NAME + "=?" + " AND "
			+ COL_DEFERRED_STARTTIME + "<=NOW()";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setString(2, voName);

		return preparedStatement;
	}

	/**
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryNumberInProgress(Connection conn)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.IN_PROGRESS.getStatusId());

		return preparedStatement;
	}

	/**
	 * @param voName
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryNumberInProgress(Connection conn,
		String voName) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT COUNT(DISTINCT " + COL_GROUP_TASK_ID + ") FROM " + TABLE_NAME
			+ " WHERE " + COL_STATUS + "=?" + " AND " + COL_VO_NAME + "=?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.IN_PROGRESS.getStatusId());
		preparedStatement.setString(2, voName);

		return preparedStatement;
	}

	/**
	 * @param numberOfTasks
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetTakeoverTasksWithDoubles(Connection conn,
		int numberOfTasks) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "=?"
			+ " AND " + COL_DEFERRED_STARTTIME + "<=NOW() ORDER BY "
			+ COL_DEFERRED_STARTTIME + " LIMIT ?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setInt(2, numberOfTasks);

		return preparedStatement;
	}

	/**
	 * @param numberOfTasks
	 * @param voName
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetTakeoverTasksWithDoubles(Connection conn,
		int numberOfTasks, String voName) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "=?"
			+ " AND " + COL_VO_NAME + "=?" + " AND " + COL_DEFERRED_STARTTIME
			+ "<=NOW() ORDER BY " + COL_DEFERRED_STARTTIME + " LIMIT ?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setString(2, voName);
		preparedStatement.setInt(3, numberOfTasks);

		return preparedStatement;
	}

	/**
	 * Creates the query string for looking up all the information related to in
	 * progress tasks in the recall database.
	 * 
	 * @param numberOfTasks
	 *          the maximum number of task returned
	 * @return the query string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryGetAllTasksInProgress(Connection conn,
		int numberOfTasks) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "=?"
			+ " ORDER BY " + COL_IN_PROGRESS_DATE + " ASC LIMIT ?";

		preparedStatement = conn.prepareStatement(str);
		preparedStatement.setInt(1, TapeRecallStatus.IN_PROGRESS.getStatusId());
		preparedStatement.setInt(2, numberOfTasks);

		return preparedStatement;

	}

	/**
	 * @param taskList
	 * @param date
	 * @param j
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement getQueryUpdateTasksStatus(Connection conn,
		List<TapeRecallTO> taskList, int statusId, String timestampColumn,
		Date timestamp) throws IllegalArgumentException, SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		if (taskList.size() == 0) {
			return null;
		}
		if (validTimestampColumnName(timestampColumn)) {
			str = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "=?" + " , "
				+ timestampColumn + "=?" + " WHERE " + COL_GROUP_TASK_ID + "=?";

			for (int i = 1; i < taskList.size(); i++) {
				str += " OR " + COL_GROUP_TASK_ID + "=?";
			}

			preparedStatement = conn.prepareStatement(str);

			preparedStatement.setInt(1, statusId);
			preparedStatement.setTimestamp(2,
				new java.sql.Timestamp(timestamp.getTime()));
			preparedStatement.setString(3, taskList.get(0).getGroupTaskId()
				.toString());

			int idx = 4;
			for (int i = 1; i < taskList.size(); i++) {
				preparedStatement.setString(idx, taskList.get(i).getGroupTaskId()
					.toString());
				idx++;
			}
		} else {
			throw new IllegalArgumentException(
				"Unable to update row status and timestamp. The priovided timestamp column \'"
					+ timestampColumn + "\' is not valid");
		}

		return preparedStatement;
	}

	/**
	 * @param groupTaskId
	 * @param status
	 * @param timestampColumn
	 * @param timestamp
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public PreparedStatement getQueryUpdateGroupTaskStatus(Connection conn,
		UUID groupTaskId, int status, String timestampColumn, Date timestamp)
		throws IllegalArgumentException, SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		if (validTimestampColumnName(timestampColumn)) {
			str = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "=?" + " , "
				+ timestampColumn + "=?" + " WHERE " + COL_GROUP_TASK_ID + "=?"
				+ " AND " + COL_STATUS + "!=?";

		} else {
			throw new IllegalArgumentException(
				"Unable to update row status and timestamp. The priovided timestamp column \'"
					+ timestampColumn + "\' is not valid");
		}

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setInt(1, status);
		preparedStatement.setTimestamp(2,
			new java.sql.Timestamp(timestamp.getTime()));
		preparedStatement.setString(3, groupTaskId.toString());
		preparedStatement.setInt(4, status);

		return preparedStatement;

	}

	/**
	 * @param groupTaskId
	 * @param status
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQuerySetGroupTaskStatus(Connection conn,
		UUID groupTaskId, int status) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "UPDATE " + TABLE_NAME + " SET " + COL_STATUS + "=?" + " WHERE "
			+ COL_GROUP_TASK_ID + "=?" + " AND " + COL_STATUS + "!=?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setInt(1, status);
		preparedStatement.setString(2, groupTaskId.toString());
		preparedStatement.setInt(3, status);

		return preparedStatement;
	}

	/**
	 * @param groupTaskId
	 * @param value
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQuerySetGroupTaskRetryValue(Connection conn,
		UUID groupTaskId, int value) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "UPDATE " + TABLE_NAME + " SET " + COL_RETRY_ATTEMPT + "=?"
			+ " WHERE " + COL_GROUP_TASK_ID + "=?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setInt(1, value);
		preparedStatement.setString(2, groupTaskId.toString());

		return preparedStatement;
	}

	/**
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryDeleteCompletedTasks(Connection conn)
		throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_STATUS + "<>?"
			+ " AND " + COL_STATUS + "<>?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setInt(2, TapeRecallStatus.IN_PROGRESS.getStatusId());

		return preparedStatement;
	}

	/**
	 * @param maxNumTasks
	 * @return the requested query as string
	 * @throws SQLException
	 */
	public PreparedStatement getQueryDeleteCompletedTasks(Connection conn,
		int maxNumTasks) throws SQLException {

		String str = null;
		PreparedStatement preparedStatement = null;

		str = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_STATUS + " != ?"
			+ " AND " + COL_STATUS + " != ?" + " LIMIT  ?";

		preparedStatement = conn.prepareStatement(str);

		preparedStatement.setInt(1, TapeRecallStatus.QUEUED.getStatusId());
		preparedStatement.setInt(2, TapeRecallStatus.IN_PROGRESS.getStatusId());
		preparedStatement.setInt(3, maxNumTasks);

		return preparedStatement;
	}

}
