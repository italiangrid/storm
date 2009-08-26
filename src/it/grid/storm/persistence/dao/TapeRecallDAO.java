package it.grid.storm.persistence.dao;

import it.grid.storm.asynch.GlobalStatusManager;
import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLChunkData;
import it.grid.storm.catalogs.ChunkData;
import it.grid.storm.catalogs.PtGChunkCatalog;
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
    private static ConcurrentHashMap<String, GlobalStatusManager> gsmMap = new ConcurrentHashMap<String, GlobalStatusManager>();
    private static ConcurrentHashMap<String, ChunkData> chunkDataMap = new ConcurrentHashMap<String, ChunkData>();

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
     * Method used to monitor the status of the Recall Table Return the number of tasks with the
     * status = QUEUED or IN_PROGRESS
     * 
     * @throws DataAccessException
     */
    public abstract int getNumberOfToDoTasks() throws DataAccessException;

    public abstract int getNumberQueued() throws DataAccessException;

    public abstract int getNumberQueued(String voName) throws DataAccessException;

    public abstract String getRequestToken(String taskId) throws DataAccessException;

    public abstract int getRetryValue(String taskId) throws DataAccessException;

    public abstract RecallTaskTO getTask(String taskId) throws DataAccessException;

    public abstract int getTaskStatus(String taskId) throws DataAccessException;

    public String insertTask(ChunkData chunkData, GlobalStatusManager gsm, String voName)
            throws DataAccessException {

        RecallTaskTO task = getTaskFromChunk(chunkData);
        task.setVoName(voName);

        String taskId = insertTask(task);

        if (!gsmMap.containsKey(taskId)) {

            gsmMap.put(taskId, gsm);
            chunkDataMap.put(taskId, chunkData);

        } else {
            log.error("BUG: duplicated key taskId: " + taskId);
        }

        return taskId;
    }

    public abstract String insertTask(RecallTaskTO task) throws DataAccessException;

    /**
     * Method called by a garbage collector
     * 
     * @throws DataAccessException
     */
    public abstract void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException;

    public abstract void setRetryValue(String taskId, int value) throws DataAccessException;

    public boolean setTaskStatus(String taskId, int status) throws DataAccessException {

        if (!setTaskStatusDBImpl(taskId, status)) {
            // shall the "taskId" data be removed from hashmaps? lets think about it...
            return false;
        }

        if ((status == RecallTaskStatus.IN_PROGRESS.getStatusId())
                || (status == RecallTaskStatus.QUEUED.getStatusId())) {
            log.warn("Setting the status to IN_PROGRESS or QUEUED using setTaskStatus() is not a legal operation, taskId="
                    + taskId);
            return true;
        }

        GlobalStatusManager gsm = gsmMap.remove(taskId);
        ChunkData chunkData = chunkDataMap.remove(taskId);

        if ((gsm == null) || (chunkData == null)) {
            // Happens when the task is inserted with insertTask(RecallTaskTO
            // task)
            log.info("Set status with no internal data. taskId=\"" + taskId + "\" status=" + status);
            return true;
        }

        if (chunkData instanceof PtGChunkData) {
            updateChunk((PtGChunkData) chunkData, gsm, status);
        } else {
            updateChunk((BoLChunkData) chunkData, gsm, status);
        }
        
        return true;
    }

    public abstract RecallTaskTO takeoverTask() throws DataAccessException;
    
    public abstract RecallTaskTO takeoverTask(String voName) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName)
            throws DataAccessException;

    /**
     * Method used to store an updated Task. If the task does not exits then a DataAccessException
     * will be thrown.
     * 
     * @param task
     * @throws DataAccessException
     */
    public abstract void updateTask(RecallTaskTO task) throws DataAccessException;

    protected abstract boolean setTaskStatusDBImpl(String taskId, int status) throws DataAccessException;

    private RecallTaskTO getTaskFromChunk(ChunkData chunkData) {

        RecallTaskTO task = new RecallTaskTO();

        Date currentDate = new Date();
        task.setInsertionInstant(currentDate);

        if (chunkData instanceof PtGChunkData) {

            PtGChunkData ptgChunk = (PtGChunkData) chunkData;

            task.setRequestType(RecallTaskTO.PTG_REQUEST);
            task.setRequestToken(ptgChunk.requestToken().getValue());
            task.setFileName(ptgChunk.fromSURL().getSURLString());
            task.setPinLifetime((int) ptgChunk.lifeTime().value());
            task.setDeferredRecallInstant(currentDate);

        } else if (chunkData instanceof BoLChunkData) {

            BoLChunkData bolChunk = (BoLChunkData) chunkData;

            task.setRequestType(RecallTaskTO.BOL_REQUEST);
            task.setRequestToken(bolChunk.getRequestToken().getValue());
            task.setFileName(bolChunk.getFromSURL().getSURLString());
            task.setPinLifetime((int) bolChunk.getLifeTime().value());

            Date deferredStartDate = new Date(currentDate.getTime()
                    + (bolChunk.getDeferredStartTime() * 1000));
            task.setDeferredRecallInstant(deferredStartDate);

        } else {
            return null;
        }

        return task;
    }

    private void updateChunk(BoLChunkData chunkData, GlobalStatusManager gsm, int status) {

        if (status == RecallTaskStatus.SUCCESS.getStatusId()) {

            chunkData.changeStatusSRM_FILE_PINNED("File recalled from tape");
            BoLChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        } else if (status == RecallTaskStatus.ABORTED.getStatusId()) {

            chunkData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
            BoLChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        } else {

            chunkData.changeStatusSRM_FAILURE("Error recalling file from tape");
            BoLChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        }
    }

    private void updateChunk(PtGChunkData chunkData, GlobalStatusManager gsm, int status) {

        if (status == RecallTaskStatus.SUCCESS.getStatusId()) {

            chunkData.changeStatusSRM_FILE_PINNED("File recalled from tape");
            PtGChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        } else if (status == RecallTaskStatus.ABORTED.getStatusId()) {

            chunkData.changeStatusSRM_ABORTED("Recalling file from tape aborted");
            PtGChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        } else {

            chunkData.changeStatusSRM_FAILURE("Error recalling file from tape");
            PtGChunkCatalog.getInstance().update(chunkData);
            gsm.successfulChunk(chunkData);

        }
    }
}
