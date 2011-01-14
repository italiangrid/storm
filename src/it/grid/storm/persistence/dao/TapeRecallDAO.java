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

package it.grid.storm.persistence.dao;

import it.grid.storm.asynch.SuspendedChunk;
import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tape Recall Data Access Object (DAO)
 */

public abstract class TapeRecallDAO extends AbstractDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAO.class);
    private static ConcurrentHashMap<TRequestToken, SuspendedChunk> chunkMap = new ConcurrentHashMap<TRequestToken, SuspendedChunk>();

    /**
     * 
     * @return
     * @throws DataAccessException
     */
    public abstract List<RecallTaskTO> getInProgressTask() throws DataAccessException;

    /**
     * 
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException;

    /**
     * 
     * @return
     * @throws DataAccessException
     */
    public abstract int getNumberInProgress() throws DataAccessException;

    /**
     * 
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract int getNumberInProgress(String voName) throws DataAccessException;

    /**
     * Method used to monitor the status of the Recall Table
     * 
     * @throws DataAccessException
     */
    public abstract int getNumberOfTasksWithStatus(RecallTaskStatus status, String voName)
            throws DataAccessException;

    /**
     * Method used to monitor the status of the Recall Table Return the number of tasks with the status = QUEUED or
     * IN_PROGRESS
     * 
     * @throws DataAccessException
     */
    public abstract int getNumberOfToDoTasks() throws DataAccessException;

    /**
     * 
     * @return
     * @throws DataAccessException
     */
    public abstract int getNumberQueued() throws DataAccessException;

    /**
     * 
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract int getNumberQueued(String voName) throws DataAccessException;

    /**
     * 
     * @return
     * @throws DataAccessException
     */
    public abstract int getReadyForTakeOver() throws DataAccessException;

    /**
     * 
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract int getReadyForTakeOver(String voName) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @return
     * @throws DataAccessException
     */
    public abstract String getRequestToken(UUID taskId) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @return
     * @throws DataAccessException
     */
    public abstract int getRetryValue(UUID taskId) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @return
     * @throws DataAccessException
     */
    public abstract List<RecallTaskTO> getTask(UUID taskId) throws DataAccessException;

    /**
     * 
     * @param requestToken
     * @param pfn
     * @return
     * @throws DataAccessException
     */
    public abstract UUID getTaskId(String requestToken, String pfn) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @return
     * @throws DataAccessException
     */
    public abstract int getTaskStatus(UUID taskId) throws DataAccessException;

    /**
     * 
     * @param task
     * @return
     * @throws DataAccessException
     */
    public abstract void insertTask(RecallTaskTO task) throws DataAccessException;

    /**
     * Method used by PtGChunk and BoLChunk
     * 
     * @param chunk
     * @param voName
     * @param absoluteFileName
     * @return
     * @throws DataAccessException
     */
    public TRequestToken insertTask(SuspendedChunk chunk, String voName, String absoluteFileName) throws DataAccessException {

        RecallTaskTO task = getTaskFromChunk(chunk.getChunkData());
        task.setFileName(absoluteFileName);
        task.setVoName(voName);

        TRequestToken taskToken = task.getRequestToken();
        insertTask(task);

        if (chunkMap.containsKey(taskToken)) {

            log.error("File 'absoluteFileName' already recalled by another Recall: " + taskToken);
            return taskToken;

        }
        chunkMap.put(taskToken, chunk);
        return taskToken;
    }

    /**
     * Method called by a garbage collector that removes all tape recalls that are not in QUEUED 
     * (1) or IN_PROGRESS (2) status
     * 
     * @throws DataAccessException
     */
    public abstract void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @param value
     * @throws DataAccessException
     */
    public abstract void setRetryValue(UUID taskId, int value) throws DataAccessException;

    /**
     * 
     * @param taskId
     * @param status
     * @return
     * @throws DataAccessException
     */
    public boolean setTaskStatus(UUID taskId, int status) throws DataAccessException {

        RecallTaskStatus recallTaskStatus = RecallTaskStatus.getRecallTaskStatus(status);

        if (!setTaskStatusDBImpl(taskId, recallTaskStatus.getStatusId())) {
            /*
             * The status of the given task hasn't been changed. The most probable reason is that the new status was
             * equal to the one already stored in the DB.
             * 
             * "taskId" is not removed from the hash map.
             */
            
            log.debug("Task status has been left unchanged. TaskId=" + taskId + " requestedStatus=" + status);
            
            return false;
        }

        if ((recallTaskStatus == RecallTaskStatus.IN_PROGRESS)
                || (recallTaskStatus == RecallTaskStatus.QUEUED)) {

            log.warn("Setting the status to IN_PROGRESS or QUEUED using setTaskStatus() is not a legal operation, doing it anyway. taskId="
                    + taskId);
            return true;

        }

        SuspendedChunk chunk = chunkMap.remove(taskId);

        if (chunk == null) {
            /*
             * Happens when the task is inserted with insertTask(RecallTaskTO task) or when the status of the same task
             * has been set multiple times.
             */
            log.info("Task status has been set but no information found about PtG or BoL. taskId=\"" + taskId
                    + "\" status=" + recallTaskStatus.getStatusId());
            return true;
        }

        chunk.completeRequest(recallTaskStatus);

        return true;
    }

    /**
     * 
     * @return
     * @throws DataAccessException
     */
    public abstract RecallTaskTO takeoverTask() throws DataAccessException;

    /**
     * 
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract RecallTaskTO takeoverTask(String voName) throws DataAccessException;

    /**
     * 
     * @param numberOfTaks
     * @return
     * @throws DataAccessException
     */
    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException;

    /**
     * 
     * @param numberOfTaks
     * @param voName
     * @return
     * @throws DataAccessException
     */
    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName)
            throws DataAccessException;

    /**
     * Method used to store an updated Task. If the task does not exits then a DataAccessException will be thrown.
     * 
     * @param task
     * @throws DataAccessException
     */
    public abstract void updateTask(RecallTaskTO task) throws DataAccessException;

    /**
     * 
     * @param chunkData
     * @return
     * @throws DataAccessException 
     */
    private RecallTaskTO getTaskFromChunk(ChunkData chunkData) throws DataAccessException {

        RecallTaskTO task = new RecallTaskTO();

        Date currentDate = new Date();
        task.setInsertionInstant(currentDate);

        if (chunkData instanceof PtGChunkData) {

            PtGChunkData ptgChunk = (PtGChunkData) chunkData;

            task.setRequestType(RecallTaskTO.PTG_REQUEST);
            task.setRequestToken(ptgChunk.requestToken());
            
            task.setPinLifetime((int) ptgChunk.getPinLifeTime().value());
            task.setDeferredRecallInstant(currentDate);

        } else if (chunkData instanceof BoLChunkData) {

            BoLChunkData bolChunk = (BoLChunkData) chunkData;

            task.setRequestType(RecallTaskTO.BOL_REQUEST);
            task.setRequestToken(bolChunk.getRequestToken());
            task.setPinLifetime((int) bolChunk.getLifeTime().value());

            Date deferredStartDate = new Date(currentDate.getTime()
                    + (bolChunk.getDeferredStartTime() * 1000));
            task.setDeferredRecallInstant(deferredStartDate);

        } else {
            throw new DataAccessException("Unable to build a RecallTaskTO because unknown chunk type.");
        }
        return task;
    }

    /**
     *     
     * @param taskId
     * @param status
     * @return
     * @throws DataAccessException
     */
    protected abstract boolean setTaskStatusDBImpl(UUID taskId, int status) throws DataAccessException;
}
