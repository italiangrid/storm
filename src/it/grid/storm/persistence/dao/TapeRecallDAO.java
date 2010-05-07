package it.grid.storm.persistence.dao;

import it.grid.storm.asynch.SuspendedChunk;
import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tape Recall Data Access Object (DAO)
 */

public abstract class TapeRecallDAO extends AbstractDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAO.class);
    private static ConcurrentHashMap<Long, SuspendedChunk> chunkMap = new ConcurrentHashMap<Long, SuspendedChunk>();

    public abstract List<RecallTaskTO> getInProgressTask() throws DataAccessException;

    public abstract List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException;

    public abstract int getNumberInProgress() throws DataAccessException;

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

    public abstract int getNumberQueued() throws DataAccessException;

    public abstract int getNumberQueued(String voName) throws DataAccessException;

    public abstract int getReadyForTakeOver() throws DataAccessException;

    public abstract int getReadyForTakeOver(String voName) throws DataAccessException;

    public abstract String getRequestToken(long taskId) throws DataAccessException;

    public abstract int getRetryValue(long taskId) throws DataAccessException;

    public abstract RecallTaskTO getTask(long taskId) throws DataAccessException;

    public abstract int getTaskId(String requestToken, String pfn) throws DataAccessException;

    public abstract int getTaskStatus(long taskId) throws DataAccessException;

    public abstract long insertTask(RecallTaskTO task) throws DataAccessException;

    public long insertTask(SuspendedChunk chunk, String voName, String absoluteFileName)
            throws DataAccessException {

        RecallTaskTO task = getTaskFromChunk(chunk.getChunkData());
        task.setFileName(absoluteFileName);
        task.setVoName(voName);

        long taskId = insertTask(task);

        if (chunkMap.containsKey(taskId)) {

            log.error("BUG: duplicated key taskId: " + taskId);
            return -1;

        }

        chunkMap.put(taskId, chunk);

        return taskId;
    }

    /**
     * Method called by a garbage collector
     * 
     * @throws DataAccessException
     */
    public abstract void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException;

    public abstract void setRetryValue(long taskId, int value) throws DataAccessException;

    public boolean setTaskStatus(long taskId, int status) throws DataAccessException {

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

    public abstract RecallTaskTO takeoverTask() throws DataAccessException;

    public abstract RecallTaskTO takeoverTask(String voName) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName)
            throws DataAccessException;

    /**
     * Method used to store an updated Task. If the task does not exits then a DataAccessException will be thrown.
     * 
     * @param task
     * @throws DataAccessException
     */
    public abstract void updateTask(RecallTaskTO task) throws DataAccessException;

    private RecallTaskTO getTaskFromChunk(ChunkData chunkData) {

        RecallTaskTO task = new RecallTaskTO();

        Date currentDate = new Date();
        task.setInsertionInstant(currentDate);

        if (chunkData instanceof PtGChunkData) {

            PtGChunkData ptgChunk = (PtGChunkData) chunkData;

            task.setRequestType(RecallTaskTO.PTG_REQUEST);
            task.setRequestToken(ptgChunk.requestToken().getValue());
            task.setPinLifetime((int) ptgChunk.getPinLifeTime().value());
            task.setDeferredRecallInstant(currentDate);

        } else if (chunkData instanceof BoLChunkData) {

            BoLChunkData bolChunk = (BoLChunkData) chunkData;

            task.setRequestType(RecallTaskTO.BOL_REQUEST);
            task.setRequestToken(bolChunk.getRequestToken().getValue());
            task.setPinLifetime((int) bolChunk.getLifeTime().value());

            Date deferredStartDate = new Date(currentDate.getTime()
                    + (bolChunk.getDeferredStartTime() * 1000));
            task.setDeferredRecallInstant(deferredStartDate);

        } else {
            return null;
        }

        return task;
    }

    protected abstract boolean setTaskStatusDBImpl(long taskId, int status) throws DataAccessException;
}
