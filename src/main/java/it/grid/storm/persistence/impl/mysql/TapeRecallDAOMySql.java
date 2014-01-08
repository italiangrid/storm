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

package it.grid.storm.persistence.impl.mysql;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallDAOMySql extends TapeRecallDAO {

	private static final Logger log = LoggerFactory
		.getLogger(TapeRecallDAOMySql.class);

	private final TapeRecallMySQLHelper sqlHelper;

	public TapeRecallDAOMySql() {

		sqlHelper = new TapeRecallMySQLHelper(PersistenceDirector.getDataBase()
			.getDbmsVendor());
	}

	@Override
	public int getNumberInProgress() throws DataAccessException {

		return getNumberInProgress(null);
	}

	@Override
	public int getNumberInProgress(String voName) throws DataAccessException {

		Connection dbConnection = getConnection();
		int status = 0;
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			if (voName == null) {
				prepStatement = sqlHelper.getQueryNumberInProgress(dbConnection);
			} else {
				prepStatement = sqlHelper
					.getQueryNumberInProgress(dbConnection, voName);
			}

			log.debug("QUERY: " + prepStatement.toString());

			res = prepStatement.executeQuery();

			if (res.first() == true) {
				status = res.getInt(1);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return status;
	}

	@Override
	public int getNumberQueued() throws DataAccessException {

		return getNumberQueued(null);
	}

	@Override
	public int getNumberQueued(String voName) throws DataAccessException {

		Connection dbConnection = getConnection();
		int status = 0;
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			if (voName == null) {
				prepStatement = sqlHelper.getQueryNumberQueued(dbConnection);
			} else {
				prepStatement = sqlHelper.getQueryNumberQueued(dbConnection, voName);
			}

			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();

			if (res.first() == true) {
				status = res.getInt(1);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return status;
	}

	@Override
	public int getReadyForTakeOver() throws DataAccessException {

		return getReadyForTakeOver(null);
	}

	@Override
	public int getReadyForTakeOver(String voName) throws DataAccessException {

		Connection dbConnection = getConnection();
		int status = 0;
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			if (voName == null) {
				prepStatement = sqlHelper.getQueryReadyForTakeOver(dbConnection);
			} else {
				prepStatement = sqlHelper
					.getQueryReadyForTakeOver(dbConnection, voName);
			}

			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();

			if (res.first() == true) {
				status = res.getInt(1);
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return status;
	}

	@Override
	public List<TapeRecallTO> getGroupTasks(UUID groupTaskId)
		throws DataAccessException {

		TapeRecallTO task = null;
		ArrayList<TapeRecallTO> taskList = new ArrayList<TapeRecallTO>();

		Connection dbConnection = getConnection();
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper
				.getQueryGetGroupTasks(dbConnection, groupTaskId);

			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();

			if (res.first() == false) {
				log.error("No tasks with GroupTaskId='" + groupTaskId + "'");
				throw new DataAccessException(
					"No recall table row retrieved executing query: '"
						+ prepStatement.toString() + "'");
			}
			do {
				task = new TapeRecallTO();
				setTaskInfo(task, res);
				taskList.add(task);
			} while (res.next());
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return taskList;
	}

	@Override
	public boolean existsGroupTask(UUID groupTaskId) throws DataAccessException {

		boolean response = false;

		Connection dbConnection = getConnection();
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper
				.getQueryGetGroupTasks(dbConnection, groupTaskId);

			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();
			response = res.first();
			if (response == false) {
				log.info("No tasks found with GroupTaskId='" + groupTaskId + "'");
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return response;
	}

	@Override
	public TapeRecallTO getTask(UUID taskId, String requestToken)
		throws DataAccessException {

		TapeRecallTO task;
		Connection dbConnection = getConnection();
		ResultSet res = null;
		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper.getQueryGetTask(dbConnection, taskId,
				requestToken);
			log.debug("QUERY: " + prepStatement);
			res = prepStatement.executeQuery();

			if (res.first() == false) {
				log.error("No task found for requestToken=" + requestToken + " "
					+ " taskId=" + taskId.toString() + ". Query = "
					+ prepStatement.toString());
				throw new DataAccessException("No task found for requestToken="
					+ requestToken + " " + "taskId=" + taskId.toString() + ". Query = "
					+ prepStatement.toString());
			}
			task = new TapeRecallTO();
			setTaskInfo(task, res);
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return task;
	}

	@Override
	public boolean existsTask(UUID taskId, String requestToken)
		throws DataAccessException {

		boolean response;

		Connection dbConnection = getConnection();
		ResultSet res = null;

		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper.getQueryGetTask(dbConnection, taskId,
				requestToken);

			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();
			response = res.first();
			log.debug("Task for requestToken=" + requestToken + " " + " taskId="
				+ taskId.toString() + " does " + (response ? "" : "NOT ") + "exists");
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: '"
				+ prepStatement.toString() + "' " + e.getMessage(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.persistence.dao.TapeRecallDAO#insertCloneTask(it.grid.storm
	 * .persistence.model.TapeRecallTO, int[], java.util.UUID)
	 */
	@Override
	public UUID insertCloneTask(TapeRecallTO task, int[] statuses,
		UUID proposedGroupTaskId) throws DataAccessException {

		if (task.getTaskId() == null || task.getRequestToken() == null
			|| task.getRequestToken().getValue().trim().equals("")) {
			log
				.error("received Task insert request with empty primary key field TaskId or RequestToken. TaskId = "
					+ task.getTaskId() + " , request token = " + task.getRequestToken());
			throw new DataAccessException(
				"Unable to create insert the task wth the provided UUID and "
					+ "request token using UUID-namebased algorithm. TaskId = "
					+ task.getTaskId() + " , request token = " + task.getRequestToken());
		}
		Integer status = task.getStatusId();

		Connection dbConnection = getConnection();
		PreparedStatement prepStat = null;

		try {
			dbConnection.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Error setting autocommit to false! " + e.getMessage());
			throw new DataAccessException("Error setting autocommit to false! "
				+ e.getMessage(), e);
		}

		ResultSet res = null;
		try {

			if (statuses == null || statuses.length == 0) {
				prepStat = sqlHelper.getQueryGetGroupTaskIds(dbConnection,
					task.getTaskId());
			} else {
				prepStat = sqlHelper.getQueryGetGroupTaskIds(dbConnection,
					task.getTaskId(), statuses);
			}
			log.debug("QUERY: " + prepStat.toString());

			res = prepStat.executeQuery();

			if (res.first() == true) {
				/* Take the first, but there can be more than one result */
				String uuidString = res
					.getString(TapeRecallMySQLHelper.COL_GROUP_TASK_ID);
				status = new Integer(res.getInt(TapeRecallMySQLHelper.COL_STATUS));
				task.setStatusId(status.intValue());
				task.setGroupTaskId(UUID.fromString(uuidString));
				Calendar calendar = new GregorianCalendar();
				try {
					task.forceStatusUpdateInstants(
						res.getDate(TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE, calendar),
						res.getDate(TapeRecallMySQLHelper.COL_FINAL_STATUS_DATE, calendar));
				} catch (IllegalArgumentException e) {
					log.error("Unable to set status update timestamps on the coned task");
				}
			} else {
				log.debug("No task found for taskId=" + task.getTaskId()
					+ " Creating a new group entry");
				task.setGroupTaskId(proposedGroupTaskId);
				task.setStatusId(status.intValue());
			}

			prepStat = sqlHelper.getQueryInsertTask(dbConnection, task);
			if (prepStat == null) {
				// this case is possible if and only if the task is null or empty
				log.error("Cannot create the query because the task is null or empty.");
				throw new DataAccessException(
					"Cannot create the query because the task is null or empty.");
			}
			try {
				log.debug("Query(insert-task)=" + prepStat.toString());
				prepStat.executeUpdate();
				commit(dbConnection);
			} catch (SQLException e) {
				rollback(dbConnection);
				throw new DataAccessException("Error executing query : "
					+ prepStat.toString() + " ; " + e.getMessage(), e);
			}
		} catch (SQLException e) {
			rollback(dbConnection);
			throw new DataAccessException("Error executing query : " + " ; "
				+ e.getMessage(), e);
		} finally {
			releaseConnection(new ResultSet[] { res }, new Statement[] { prepStat },
				dbConnection);
		}
		return task.getGroupTaskId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.persistence.dao.TapeRecallDAO#purgeCompletedTasks(int)
	 */
	@Override
	public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException {

		PreparedStatement prepStatement = null;
		Connection dbConnection = getConnection();

		try {
			if (numMaxToPurge == -1) {
				prepStatement = sqlHelper.getQueryDeleteCompletedTasks(dbConnection);
			} else {
				prepStatement = sqlHelper.getQueryDeleteCompletedTasks(dbConnection,
					numMaxToPurge);
			}

			int count = prepStatement.executeUpdate();
			if (count == 0) {
				log.trace("No entries have been purged from tape_recall table");
			} else {
				log.info(count + " entries have been purged from tape_recall table");
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: "
				+ prepStatement.toString(), e);
		} finally {
			releaseConnection(null, prepStatement, dbConnection);
		}
	}

	@Override
	public void setGroupTaskRetryValue(UUID groupTaskId, int value)
		throws DataAccessException {

		Connection dbConnection = getConnection();
		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper.getQuerySetGroupTaskRetryValue(dbConnection,
				groupTaskId, value);

			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error executing query: "
				+ prepStatement.toString(), e);
		} finally {
			releaseConnection(null, prepStatement, dbConnection);
		}

	}

	@Override
	public TapeRecallTO takeoverTask() throws DataAccessException {

		return takeoverTask(null);
	}

	@Override
	public TapeRecallTO takeoverTask(String voName) throws DataAccessException {

		List<TapeRecallTO> taskList = takeoverTasksWithDoubles(1, voName);

		if (taskList.isEmpty()) {
			return null;
		}
		return taskList.get(0);
	}

	@Override
	public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks)
		throws DataAccessException {

		return takeoverTasksWithDoubles(numberOfTaks, null);
	}

	@Override
	public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks,
		String voName) throws DataAccessException {

		Connection dbConnection = getConnection();

		List<TapeRecallTO> taskList = new LinkedList<TapeRecallTO>();
		TapeRecallTO task = null;
		ResultSet res = null;

		PreparedStatement prepStatement = null;

		try {
			dbConnection.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Error setting autocommit to false! " + e.getMessage());
			throw new DataAccessException("Error setting autocommit to false! "
				+ e.getMessage(), e);
		}
		try {
			if (voName == null) {
				prepStatement = sqlHelper.getQueryGetTakeoverTasksWithDoubles(
					dbConnection, numberOfTaks);
			} else {
				prepStatement = sqlHelper.getQueryGetTakeoverTasksWithDoubles(
					dbConnection, numberOfTaks, voName);
			}
			// start transaction
			log.debug("QUERY: " + prepStatement.toString());
			res = prepStatement.executeQuery();
			if (res.first() == false) {
				log.info("No tape recall rows ready for takeover");
				return taskList;
			}
			do {
				task = new TapeRecallTO();
				setTaskInfo(task, res);
				task.setStatus(TapeRecallStatus.IN_PROGRESS);
				taskList.add(task);
			} while (res.next());
			if (!taskList.isEmpty()) {
				try {
					prepStatement = sqlHelper.getQueryUpdateTasksStatus(dbConnection,
						taskList, TapeRecallStatus.IN_PROGRESS.getStatusId(),
						TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE, new Date());
				} catch (IllegalArgumentException e) {
					log
						.error("Unable to obtain the query to update task status and set status transition timestamp. IllegalArgumentException: "
							+ e.getMessage());
					throw new DataAccessException(
						"Unable to obtain the query to update task status and set status transition timestamp");
				}
				prepStatement.executeUpdate();
			}
			commit(dbConnection);
		} catch (SQLException e) {
			rollback(dbConnection);
			throw new DataAccessException("Error executing query: "
				+ prepStatement.toString(), e);
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return taskList;
	}

	@Override
	public List<TapeRecallTO> getAllInProgressTasks(int numberOfTaks)
		throws DataAccessException {

		Connection dbConnection = getConnection();
		ResultSet res = null;
		List<TapeRecallTO> taskList = new ArrayList<TapeRecallTO>();

		PreparedStatement prepStatement = null;

		try {
			prepStatement = sqlHelper.getQueryGetAllTasksInProgress(dbConnection,
				numberOfTaks);

			log.debug("getAllInProgressTasks query: " + prepStatement.toString());

			res = prepStatement.executeQuery();

			boolean emptyResultSet = true;

			while (res.next()) {

				emptyResultSet = false;
				TapeRecallTO task = new TapeRecallTO();
				setTaskInfo(task, res);
				taskList.add(task);
			}

			if (emptyResultSet) {

				log.debug("No in progress recall tasks found.");
			}

		} catch (Exception e) {

			log.error("Error executing query: {}", prepStatement.toString(), e);
			throw new DataAccessException("Error executing query: "
				+ prepStatement.toString(), e);

		} finally {

			releaseConnection(res, prepStatement, dbConnection);
		}

		return taskList;
	}

	private void setTaskInfo(TapeRecallTO task, ResultSet res)
		throws DataAccessException {

		if (res == null) {
			throw new DataAccessException("Unable to build Task from NULL ResultSet");
		}

		String requestTokenStr = null;
		Timestamp insertionInstant;
		try {
			requestTokenStr = res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN);
			insertionInstant = res.getTimestamp(TapeRecallMySQLHelper.COL_DATE);

		} catch (SQLException e) {
			throw new DataAccessException(
				"Unable to retrieve RequestToken String from ResultSet. " + e);
		}
		try {
			task
				.setRequestToken(new TRequestToken(requestTokenStr, insertionInstant));
		} catch (InvalidTRequestTokenAttributesException e) {
			throw new DataAccessException(
				"Unable to build TRequestToken from token='" + requestTokenStr + "'. "
					+ e);
		}

		UUID groupTaskId = null;
		String groupTaskIdStr = null;
		try {
			groupTaskIdStr = res.getString(TapeRecallMySQLHelper.COL_GROUP_TASK_ID);
			if (groupTaskIdStr != null) {
				try {
					groupTaskId = UUID.fromString(groupTaskIdStr);
					task.setGroupTaskId(groupTaskId);
				} catch (IllegalArgumentException iae) {
					throw new DataAccessException(
						"Unable to build UUID from GroupTaskId='" + groupTaskId + "'. "
							+ iae);
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(
				"Unable to retrieve GroupTaskId String from ResultSet. " + e);
		}

		// do not set the task ID, it is produced by the setFilename call

		try {

			task
				.setRequestType(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TYPE));
			task.setFileName(res.getString(TapeRecallMySQLHelper.COL_FILE_NAME));
			task.setPinLifetime(res.getInt(TapeRecallMySQLHelper.COL_PIN_LIFETIME));
			task.setStatusId(res.getInt(TapeRecallMySQLHelper.COL_STATUS));
			task.setVoName(res.getString(TapeRecallMySQLHelper.COL_VO_NAME));
			task.setUserID(res.getString(TapeRecallMySQLHelper.COL_USER_ID));
			task.setRetryAttempt(res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT));
			Calendar calendar = new GregorianCalendar();
			task.setDeferredRecallInstant(res.getTimestamp(
				TapeRecallMySQLHelper.COL_DEFERRED_STARTTIME, calendar));
			task.setInsertionInstant(res.getTimestamp(TapeRecallMySQLHelper.COL_DATE,
				calendar));
			try {
				task.forceStatusUpdateInstants(res.getTimestamp(
					TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE, calendar), res
					.getTimestamp(TapeRecallMySQLHelper.COL_FINAL_STATUS_DATE, calendar));
			} catch (IllegalArgumentException e) {
				log.error("Unable to set status update timestamps on the coned task");
			}
		} catch (SQLException e) {
			throw new DataAccessException("Unable to getting info from ResultSet. "
				+ e);
		}
	}

	@Override
	public boolean setGroupTaskStatus(UUID groupTaskId, int newStatusId,
		Date timestamp) throws DataAccessException {

		PreparedStatement prepStatement = null;
		Connection dbConnection = getConnection();

		try {
			dbConnection.setAutoCommit(false);
		} catch (SQLException e) {
			log.error("Error setting autocommit to false! " + e.getMessage());
			throw new DataAccessException("Error setting autocommit to false! "
				+ e.getMessage(), e);
		}

		ResultSet res = null;
		boolean ret = false;
		int oldStatusId = -1;

		try {

			try {
				prepStatement = sqlHelper.getQueryGetGroupTasks(dbConnection,
					groupTaskId);

				log.debug("QUERY: " + prepStatement.toString());
				// retrieves the tasks of this task group
				res = prepStatement.executeQuery();

				if (res.first() == false) {
					log.error("No tasks with GroupTaskId='" + groupTaskId + "'");
					throw new DataAccessException(
						"No recall table row retrieved executing query: '"
							+ prepStatement.toString() + "'");
				}
				// verify if their stored status is equal for all
				oldStatusId = res.getInt(TapeRecallMySQLHelper.COL_STATUS);
				do {
					int currentStatusId = res.getInt(TapeRecallMySQLHelper.COL_STATUS);
					if (currentStatusId != oldStatusId) {
						log.warn("The tasks with groupTaskId " + groupTaskId
							+ " have different statuses: " + currentStatusId + " from task "
							+ res.getString(TapeRecallMySQLHelper.COL_TASK_ID)
							+ " differs from expected " + oldStatusId);
						break;
					}
					oldStatusId = currentStatusId;
				} while (res.next());
			} catch (SQLException e) {
				log
					.error("Unable to retrieve groupTaskId related tasks. SQLException: "
						+ e);
				throw new DataAccessException(
					"Unable to retrieve groupTaskId related tasks. ");
			}
			if (oldStatusId != newStatusId) {
				// update the task status and if is a valid transition set the relative
				// transition timestamp
				if (!TapeRecallStatus.getRecallTaskStatus(oldStatusId).precedes(
					newStatusId)) {
					log
						.warn("Requested the update of the status of a recall task group to status "
							+ newStatusId
							+ " that is precedent to the recorded status "
							+ oldStatusId + " performing the request the same...");
				}
				String timestampColumn = null;
				if (TapeRecallStatus.isFinalStatus(newStatusId)) {
					timestampColumn = TapeRecallMySQLHelper.COL_FINAL_STATUS_DATE;
				} else {
					if (TapeRecallStatus.IN_PROGRESS.equals(TapeRecallStatus
						.getRecallTaskStatus(newStatusId))) {
						timestampColumn = TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE;
					} else {
						log
							.warn("unable to determine the status update timestamp column to use given the new statusId \'"
								+ newStatusId + "\'");
					}
				}
				if (timestampColumn != null) {
					try {
						prepStatement = sqlHelper.getQueryUpdateGroupTaskStatus(
							dbConnection, groupTaskId, newStatusId, timestampColumn,
							timestamp);
					} catch (IllegalArgumentException e) {
						log
							.error("Unable to obtain the query to update task status and set status transition timestamp. IllegalArgumentException: "
								+ e.getMessage());
						throw new DataAccessException(
							"Unable to obtain the query to update task status and set status transition timestamp");
					} catch (SQLException e) {
						throw new DataAccessException("Error executing query: "
							+ prepStatement.toString(), e);
					}
				} else {
					try {
						prepStatement = sqlHelper.getQuerySetGroupTaskStatus(dbConnection,
							groupTaskId, newStatusId);
					} catch (SQLException e) {
						throw new DataAccessException("Error executing query: "
							+ prepStatement.toString(), e);
					}
				}
				try {
					if (prepStatement.executeUpdate() > 0) {
						ret = true;
					}
					commit(dbConnection);
				} catch (SQLException e) {
					throw new DataAccessException("Error executing query: "
						+ prepStatement.toString(), e);
				}
			} else {
				log
					.warn("Skipping the status upadate operation, the status already stored is equal to the new one provided");
			}
		} finally {
			releaseConnection(res, prepStatement, dbConnection);
		}
		return ret;
	}
}
