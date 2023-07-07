/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.impl.mysql;

import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.valueOf;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_DATE;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_DEFERRED_STARTTIME;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_FILE_NAME;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_FINAL_STATUS_DATE;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_GROUP_TASK_ID;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_PIN_LIFETIME;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_REQUEST_TYPE;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_RETRY_ATTEMPT;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_STATUS;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_TASK_ID;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_USER_ID;
import static it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper.COL_VO_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.persistence.pool.impl.StormBeIsamConnectionPool;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

public class TapeRecallDAOMySql extends AbstractDAO implements TapeRecallDAO {

  private static final Logger log = LoggerFactory.getLogger(TapeRecallDAOMySql.class);

  private static TapeRecallDAO instance;

  public static synchronized TapeRecallDAO getInstance() {
    if (instance == null) {
      instance = new TapeRecallDAOMySql();
    }
    return instance;
  }

  private final TapeRecallMySQLHelper sqlHelper;

  private TapeRecallDAOMySql() {

    super(StormBeIsamConnectionPool.getInstance());
    sqlHelper = new TapeRecallMySQLHelper();
  }

  @Override
  public int getNumberInProgress() throws DataAccessException {

    return getNumberInProgress(null);
  }

  @Override
  public int getNumberInProgress(String voName) throws DataAccessException {

    Connection con = null;
    int status = 0;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      if (voName == null) {
        ps = sqlHelper.getQueryNumberInProgress(con);
      } else {
        ps = sqlHelper.getQueryNumberInProgress(con, voName);
      }

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();

      if (res.first()) {
        status = res.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return status;
  }

  @Override
  public int getNumberQueued() throws DataAccessException {

    return getNumberQueued(null);
  }

  @Override
  public int getNumberQueued(String voName) throws DataAccessException {

    Connection con = null;
    int status = 0;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();

      if (voName == null) {
        ps = sqlHelper.getQueryNumberQueued(con);
      } else {
        ps = sqlHelper.getQueryNumberQueued(con, voName);
      }

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();

      if (res.first()) {
        status = res.getInt(1);
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return status;
  }

  @Override
  public int getReadyForTakeOver() throws DataAccessException {

    return getReadyForTakeOver(null);
  }

  @Override
  public int getReadyForTakeOver(String voName) throws DataAccessException {

    Connection con = null;
    int status = 0;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();

      if (voName == null) {
        ps = sqlHelper.getQueryReadyForTakeOver(con);
      } else {
        ps = sqlHelper.getQueryReadyForTakeOver(con, voName);
      }

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();

      if (res.first()) {
        status = res.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return status;
  }

  @Override
  public List<TapeRecallTO> getGroupTasks(UUID groupTaskId) throws DataAccessException {

    TapeRecallTO task = null;
    List<TapeRecallTO> taskList = Lists.newArrayList();

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();
      ps = sqlHelper.getQueryGetGroupTasks(con, groupTaskId);

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();

      if (res.first()) {
        do {
          task = new TapeRecallTO();
          setTaskInfo(task, res);
          taskList.add(task);
        } while (res.next());
      } else {
        log.info("No tasks with GroupTaskId='{}'", groupTaskId);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return taskList;
  }

  @Override
  public boolean existsGroupTask(UUID groupTaskId) throws DataAccessException {

    boolean response = false;

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();
      ps = sqlHelper.getQueryGetGroupTasks(con, groupTaskId);

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();
      response = res.first();
      if (!response) {
        log.info("No tasks found with GroupTaskId='{}'", groupTaskId);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return response;
  }

  @Override
  public Optional<TapeRecallTO> getTask(UUID taskId, String requestToken)
      throws DataAccessException {

    TapeRecallTO task = null;
    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();
      ps = sqlHelper.getQueryGetTask(con, taskId, requestToken);
      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();

      if (res.first()) {
        task = new TapeRecallTO();
        setTaskInfo(task, res);
      } else {
        log.info("No task found for requestToken={} taskId={}. Query={}", requestToken, taskId, ps);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return Optional.ofNullable(task);
  }

  @Override
  public boolean existsTask(UUID taskId, String requestToken) throws DataAccessException {

    boolean response = false;

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getConnection();
      ps = sqlHelper.getQueryGetTask(con, taskId, requestToken);

      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();
      response = res.first();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return response;
  }

  @Override
  public UUID insertCloneTask(TapeRecallTO task, int[] statuses, UUID proposedGroupTaskId)
      throws DataAccessException {

    if (task.getTaskId() == null || task.getRequestToken() == null
        || task.getRequestToken().getValue().trim().isEmpty()) {
      log.error(
          "received Task insert request with empty primary key field TaskId or RequestToken. TaskId = {}, request token = {}",
          task.getTaskId(), task.getRequestToken());
      throw new DataAccessException("Unable to create insert the task with the provided UUID and "
          + "request token using UUID-namebased algorithm. TaskId = " + task.getTaskId()
          + " , request token = " + task.getRequestToken());
    }
    int status = task.getStatusId();

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet res = null;

    try {

      con = getConnection();

      if (statuses == null || statuses.length == 0) {
        ps = sqlHelper.getQueryGetGroupTaskIds(con, task.getTaskId());
      } else {
        ps = sqlHelper.getQueryGetGroupTaskIds(con, task.getTaskId(), statuses);
      }
      log.debug("QUERY: {}", ps);

      res = ps.executeQuery();

      if (res.first()) {
        /* Take the first, but there can be more than one result */
        String uuidString = res.getString(COL_GROUP_TASK_ID);
        status = res.getInt(COL_STATUS);
        task.setStatusId(status);
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
        log.debug("No task found for taskId={} Creating a new group entry", task.getTaskId());
        task.setGroupTaskId(proposedGroupTaskId);
        task.setStatusId(status);
      }

      ps = sqlHelper.getQueryInsertTask(con, task);
      if (ps == null) {
        // this case is possible if and only if the task is null or empty
        log.error("Cannot create the query because the task is null or empty.");
        throw new DataAccessException("Cannot create the query because the task is null or empty.");
      }
      log.debug("Query(insert-task)={}", ps);
      int n = ps.executeUpdate();
      log.debug("Query(insert-task)={} exited with {}", ps, n);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return task.getGroupTaskId();
  }

  @Override
  public int purgeCompletedTasks(long expirationTime, int numTasks) throws DataAccessException {

    PreparedStatement ps = null;
    Connection con = null;
    int count = 0;
    boolean hasLimit = numTasks > 0;

    try {

      con = getConnection();
      if (hasLimit) {
        ps = sqlHelper.getQueryDeleteCompletedTasks(con, expirationTime, numTasks);
      } else {
        ps = sqlHelper.getQueryDeleteCompletedTasks(con, expirationTime);
      }

      count = ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }

    return count;
  }

  @Override
  public void setGroupTaskRetryValue(UUID groupTaskId, int value) throws DataAccessException {

    Connection con = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();
      ps = sqlHelper.getQuerySetGroupTaskRetryValue(con, groupTaskId, value);
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
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
  public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks) throws DataAccessException {

    return takeoverTasksWithDoubles(numberOfTaks, null);
  }

  @Override
  public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks, String voName)
      throws DataAccessException {

    List<TapeRecallTO> taskList = Lists.newLinkedList();
    TapeRecallTO task = null;

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {

      con = getConnection();

      if (voName == null) {
        ps = sqlHelper.getQueryGetTakeoverTasksWithDoubles(con, numberOfTaks);
      } else {
        ps = sqlHelper.getQueryGetTakeoverTasksWithDoubles(con, numberOfTaks, voName);
      }

      // start transaction
      log.debug("QUERY: {}", ps);
      res = ps.executeQuery();
      if (res.first()) {
        do {
          task = new TapeRecallTO();
          setTaskInfo(task, res);
          task.setStatus(TapeRecallStatus.IN_PROGRESS);
          taskList.add(task);
        } while (res.next());
        if (!taskList.isEmpty()) {
          try {
            ps = sqlHelper.getQueryUpdateTasksStatus(con, taskList,
                TapeRecallStatus.IN_PROGRESS.getStatusId(), COL_IN_PROGRESS_DATE, new Date());
          } catch (IllegalArgumentException e) {
            log.error(
                "Unable to obtain the query to update task status and set status transition timestamp. IllegalArgumentException: "
                    + e.getMessage());
            throw new DataAccessException(
                "Unable to obtain the query to update task status and set status transition timestamp");
          }
          ps.executeUpdate();
        }
      } else {
        log.info("No tape recall rows ready for takeover");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return taskList;
  }

  @Override
  public List<TapeRecallTO> getAllInProgressTasks(int numberOfTaks) throws DataAccessException {

    List<TapeRecallTO> taskList = Lists.newArrayList();

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet res = null;

    try {
      con = getConnection();
      ps = sqlHelper.getQueryGetAllTasksInProgress(con, numberOfTaks);

      log.debug("getAllInProgressTasks query: {}", ps);

      res = ps.executeQuery();

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

    } catch (SQLException e) {

      e.printStackTrace();

    } finally {

      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }

    return taskList;
  }

  private void setTaskInfo(TapeRecallTO task, ResultSet res) throws DataAccessException {

    if (res == null) {
      throw new DataAccessException("Unable to build Task from NULL ResultSet");
    }

    String requestTokenStr = null;
    Timestamp insertionInstant;
    try {
      requestTokenStr = res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN);
      insertionInstant = res.getTimestamp(TapeRecallMySQLHelper.COL_DATE);

    } catch (SQLException e) {
      throw new DataAccessException("Unable to retrieve RequestToken String from ResultSet. " + e);
    }
    try {
      task.setRequestToken(new TRequestToken(requestTokenStr, insertionInstant));
    } catch (InvalidTRequestTokenAttributesException e) {
      throw new DataAccessException(
          "Unable to build TRequestToken from token='" + requestTokenStr + "'. " + e);
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
              "Unable to build UUID from GroupTaskId='" + groupTaskId + "'. " + iae);
        }
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to retrieve GroupTaskId String from ResultSet. " + e);
    }

    // do not set the task ID, it is produced by the setFilename call

    try {

      task.setRequestType(valueOf(res.getString(COL_REQUEST_TYPE)));
      task.setFileName(res.getString(COL_FILE_NAME));
      task.setPinLifetime(res.getInt(COL_PIN_LIFETIME));
      task.setStatusId(res.getInt(COL_STATUS));
      task.setVoName(res.getString(COL_VO_NAME));
      task.setUserID(res.getString(COL_USER_ID));
      task.setRetryAttempt(res.getInt(COL_RETRY_ATTEMPT));
      Calendar calendar = new GregorianCalendar();
      task.setDeferredRecallInstant(res.getTimestamp(COL_DEFERRED_STARTTIME, calendar));
      task.setInsertionInstant(res.getTimestamp(COL_DATE, calendar));
      try {
        task.forceStatusUpdateInstants(res.getTimestamp(COL_IN_PROGRESS_DATE, calendar),
            res.getTimestamp(COL_FINAL_STATUS_DATE, calendar));
      } catch (IllegalArgumentException e) {
        log.error("Unable to set status update timestamps on the coned task");
      }
    } catch (SQLException e) {
      throw new DataAccessException("Unable to getting info from ResultSet. " + e);
    }
  }

  @Override
  public boolean setGroupTaskStatus(UUID groupTaskId, int newStatusId, Date timestamp)
      throws DataAccessException {

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet res = null;

    boolean ret = false;
    int oldStatusId = -1;

    try {
      con = getConnection();

      ps = sqlHelper.getQueryGetGroupTasks(con, groupTaskId);

      log.debug("QUERY: {}", ps);

      // retrieves the tasks of this task group
      res = ps.executeQuery();

      if (!res.first()) {
        log.error("No tasks with GroupTaskId='{}'", groupTaskId);
        throw new DataAccessException(
            "No recall table row retrieved executing query: '" + ps + "'");
      }

      // verify if their stored status is equal for all
      oldStatusId = res.getInt(COL_STATUS);
      do {
        int currentStatusId = res.getInt(COL_STATUS);
        if (currentStatusId != oldStatusId) {
          log.warn(
              "The tasks with groupTaskId {} have different statuses: {} from task {} differs "
                  + "from expected {}",
              groupTaskId, currentStatusId, res.getString(COL_TASK_ID), oldStatusId);
          break;
        }
        oldStatusId = currentStatusId;
      } while (res.next());

      if (oldStatusId != newStatusId) {
        // update the task status and if is a valid transition set the relative transition timestamp
        if (!TapeRecallStatus.getRecallTaskStatus(oldStatusId).precedes(newStatusId)) {
          log.warn(
              "Requested the update of the status of a recall task group to status {} that is precedent "
                  + "to the recorded status performing the request the same...",
              newStatusId, oldStatusId);
        }
        String timestampColumn = null;
        if (TapeRecallStatus.isFinalStatus(newStatusId)) {
          timestampColumn = COL_FINAL_STATUS_DATE;
        } else {
          if (TapeRecallStatus.IN_PROGRESS
            .equals(TapeRecallStatus.getRecallTaskStatus(newStatusId))) {
            timestampColumn = COL_IN_PROGRESS_DATE;
          } else {
            log.warn(
                "unable to determine the status update timestamp column to use given the new statusId '{}'",
                newStatusId);
          }
        }
        if (timestampColumn != null) {
          ps = sqlHelper.getQueryUpdateGroupTaskStatus(con, groupTaskId, newStatusId,
              timestampColumn, timestamp);
        } else {
          ps = sqlHelper.getQuerySetGroupTaskStatus(con, groupTaskId, newStatusId);
        }
        if (ps.executeUpdate() > 0) {
          ret = true;
        }
      } else {
        log.warn(
            "Skipping the status upadate operation, the status already stored is equal to the new one provided");
      }
    } catch (IllegalArgumentException | SQLException e) {
      e.printStackTrace();
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
    return ret;
  }
}
