/*
 *
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 *
 */
package it.grid.storm.tape.recalltable;

import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.BOL;
import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.PTG;

import com.google.common.collect.Lists;

import it.grid.storm.asynch.Suspendedable;
import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.catalogs.PersistentChunkData;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.catalogs.RequestData;
import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class TapeRecallCatalog {

  private static final Logger log = LoggerFactory.getLogger(TapeRecallCatalog.class);

  private final TapeRecallDAO tapeRecallDAO;

  /**
   * Default constructor
   *
   * @throws DataAccessException
   */
  public TapeRecallCatalog() {

    tapeRecallDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();
  }

  /**
   * Change the retry field of the rows identified by the provided groupTaskId
   *
   * @param groupTaskId @param newValue
   */
  public void changeGroupTaskRetryValue(UUID groupTaskId, int newValue) throws DataAccessException {

    tapeRecallDAO.setGroupTaskRetryValue(groupTaskId, newValue);
  }

  /**
   * Determines how many task rows have an in-progress state
   *
   * @return @throws DataAccessException
   */
  public int getNumberTaskInProgress() throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getNumberInProgress();
    } catch (DataAccessException e) {
      log.error(
          "Unable to retrieve the number of tasks currently in progress. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   * Determines how many task rows have an in-progress state given a certain VO
   *
   * @param voName @return @throws DataAccessException
   */
  public int getNumberTaskInProgress(String voName) throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getNumberInProgress(voName);
    } catch (DataAccessException e) {
      log.error(
          "Unable to retrieve the number of tasks currently in progress. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   * Determines how many task rows have a queued state
   *
   * @return @throws DataAccessException
   */
  public int getNumberTaskQueued() throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getNumberQueued();
    } catch (DataAccessException e) {
      log.error("Unable to retrieve the number of tasks queued. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   * Determines how many task rows have a queued state given a certain VO
   *
   * @return @throws DataAccessException
   */
  public int getNumberTaskQueued(String voName) throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getNumberQueued(voName);
    } catch (DataAccessException e) {
      log.error("Unable to retrieve the number of tasks queued. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   *
   * Determines how many task rows have a queued state and their deferred start time is elapsed
   *
   * @return @throws DataAccessException
   */
  public int getReadyForTakeOver() throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getReadyForTakeOver();
    } catch (DataAccessException e) {
      log.error(
          "Unable to retrieve the number of tasks ready for the take-over. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   * Determines how many task rows given a certain VO have a queued state and their deferred start
   * time is elapsed
   *
   * @return @throws DataAccessException
   */
  public int getReadyForTakeOver(String voName) throws DataAccessException {

    int result = -1;
    try {
      result = tapeRecallDAO.getReadyForTakeOver(voName);
    } catch (DataAccessException e) {
      log.error(
          "Unable to retrieve the number of tasks ready for the take-over. DataAccessException: {}",
          e.getMessage(), e);
      throw e;
    }
    return result;
  }

  /**
   * @param taskId @param requestToken @return @throws DataAccessException
   */
  public TapeRecallTO getTask(UUID taskId, String requestToken) throws DataAccessException {

    return tapeRecallDAO.getTask(taskId, requestToken);
  }

  /**
   * Verifies that a recall task with the given taskId and request token exists on the database
   *
   * @param taskId @param requestToken @return true if the recall task exists @throws
   *        DataAccessException
   */
  public boolean existsTask(UUID taskId, String requestToken) throws DataAccessException {

    return tapeRecallDAO.existsTask(taskId, requestToken);
  }

  /**
   * @param groupTaskId @return @throws DataAccessException
   */
  public List<TapeRecallTO> getGroupTasks(UUID groupTaskId) throws DataAccessException {

    return tapeRecallDAO.getGroupTasks(groupTaskId);
  }

  /**
   * @param groupTaskId @return @throws DataAccessException
   */
  public boolean existsGroupTask(UUID groupTaskId) throws DataAccessException {

    return tapeRecallDAO.existsGroupTask(groupTaskId);
  }

  /**
   * @param maxSize The max number of purged requests @return the number of purged requests
   */
  public int purgeCatalog(long expirationTime, int maxSize) {

    log.debug("purging '{}' completed tasks older than '{}' seconds ...", maxSize, expirationTime);
    try {
      return tapeRecallDAO.purgeCompletedTasks(expirationTime, maxSize);
    } catch (DataAccessException e) {
      log.error("Unable to takeover a task {}", e);
      return 0;
    }
  }

  /**
   * Performs the take-over of max numberOfTaks tasks possibly returning more than one file recall
   * task for some files
   *
   * @param n @return
   */
  public List<TapeRecallTO> takeoverNTasksWithDoubles(int numberOfTaks) {

    List<TapeRecallTO> taskList = Lists.newArrayList();
    try {
      taskList.addAll(tapeRecallDAO.takeoverTasksWithDoubles(numberOfTaks));
    } catch (DataAccessException e) {
      log.error("Unable to takeover {} tasks", numberOfTaks, e);
    }
    return taskList;
  }

  /**
   * @param numberOfTasks @return
   */
  public List<TapeRecallTO> getAllInProgressTasks(int numberOfTaks) {

    List<TapeRecallTO> taskList;

    try {

      taskList = tapeRecallDAO.getAllInProgressTasks(numberOfTaks);

    } catch (DataAccessException e) {

      log.error(e.getMessage(), e);
      taskList = Collections.emptyList();
    }

    return taskList;
  }

  /**
   * @return
   */
  public TapeRecallTO takeoverTask() {

    TapeRecallTO task = null;
    try {
      task = tapeRecallDAO.takeoverTask();
    } catch (DataAccessException e) {
      log.error("Unable to takeove a task.", e);
    }
    return task;
  }

  /**
   * @param voName @return
   */
  public TapeRecallTO takeoverTask(String voName) {

    TapeRecallTO task = null;
    try {
      task = tapeRecallDAO.takeoverTask(voName);
    } catch (DataAccessException e) {
      log.error("Unable to takeover a task for vo {}", voName, e);
    }
    return task;
  }

  /**
   * @param numberOfTaks @param voName @return
   */
  public List<TapeRecallTO> takeoverTasks(int numberOfTaks, String voName) {

    List<TapeRecallTO> taskList = Lists.newArrayList();
    try {
      taskList.addAll(tapeRecallDAO.takeoverTasksWithDoubles(numberOfTaks, voName));
    } catch (DataAccessException e) {
      log.error("Unable to takeover {} tasks for vo {}", numberOfTaks, voName, e);
    }
    return taskList;
  }

  /**
   * Method used by PtGChunk and BoLChunk to request the recall of a file
   *
   * @param chunk @param voName @param absoluteFileName @return the id of the recall task in charge
   *        of recall the file @throws DataAccessException
   */
  public UUID insertTask(Suspendedable chunk, String voName, String absoluteFileName)
      throws DataAccessException {

    TapeRecallTO task = getTaskFromChunk(chunk.getRequestData());
    task.setFileName(absoluteFileName);
    task.setVoName(voName);

    UUID groupTaskId;

    synchronized (this) {
      groupTaskId = this.insertNewTask(task);
    }
    return groupTaskId;
  }

  /**
   * Insert a recall task not necessary related to a chunk
   *
   * @param task @return @throws DataAccessException
   */
  public UUID insertNewTask(TapeRecallTO task) throws DataAccessException {

    UUID newGroupTaskId = UUID.randomUUID();
    /*
     * if no tasks are in status queued or in_progress for this taskId (the file to be recalled)
     * then add a new task row and assign if the provided group task id If there are, insert the row
     * setting the status and the group id to the one of the found row
     */
    UUID groupTaskId =
        tapeRecallDAO.insertCloneTask(task, new int[] {TapeRecallStatus.QUEUED.getStatusId(),
            TapeRecallStatus.IN_PROGRESS.getStatusId()}, newGroupTaskId);

    if (newGroupTaskId != groupTaskId) {
      log.debug(
          "Task with taskId {} of request with token {} has benn added to an existent group: {}",
          task.getTaskId(), task.getRequestToken().getValue(), groupTaskId);
    }
    return groupTaskId;
  }

  /**
   *
   * @param chunkData @return @throws DataAccessException
   */
  private TapeRecallTO getTaskFromChunk(RequestData chunkData) throws DataAccessException {

    TapeRecallTO task = new TapeRecallTO();

    Date currentDate = new Date();
    task.setInsertionInstant(currentDate);

    if (chunkData instanceof PtGData) {

      PtGData ptgChunk = (PtGData) chunkData;

      task.setRequestType(PTG);
      task.setPinLifetime((int) ptgChunk.getPinLifeTime().value());
      task.setDeferredRecallInstant(currentDate);

    } else if (chunkData instanceof BoLPersistentChunkData) {

      BoLPersistentChunkData bolChunk = (BoLPersistentChunkData) chunkData;

      task.setRequestType(BOL);
      task.setPinLifetime((int) bolChunk.getLifeTime().value());

      Date deferredStartDate =
          new Date(currentDate.getTime() + (bolChunk.getDeferredStartTime() * 1000));
      task.setDeferredRecallInstant(deferredStartDate);

    } else {
      throw new DataAccessException("Unable to build a TapeRecallTO because unknown chunk type.");
    }
    if (chunkData instanceof PersistentChunkData) {
      task.setRequestToken(((PersistentChunkData) chunkData).getRequestToken());
    } else {
      task.setFakeRequestToken();
    }
    return task;
  }

  public boolean changeGroupTaskStatus(UUID groupTaskId, TapeRecallStatus status, Date timestamp)
      throws DataAccessException {

    synchronized (this) {

      boolean response =
          tapeRecallDAO.setGroupTaskStatus(groupTaskId, status.getStatusId(), timestamp);

      if (!response) {
        log.debug("Updating to status {} at {} didn't affect groupTask {}", status, timestamp,
            groupTaskId);
      }
      return response;

    }
  }
}
