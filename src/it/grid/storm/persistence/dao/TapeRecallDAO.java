package it.grid.storm.persistence.dao;

import it.grid.storm.asynch.GlobalStatusManager;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tape Recall Data Access Object (DAO)
 */

public abstract class TapeRecallDAO extends AbstractDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAO.class);
    private static ConcurrentHashMap<String, GlobalStatusManager> ptgGSM = new ConcurrentHashMap<String, GlobalStatusManager>();
    private static ConcurrentHashMap<String, PtGChunkData> ptgChunkData = new ConcurrentHashMap<String, PtGChunkData>();

    public abstract List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException;

    public abstract int getNumberInProgress() throws DataAccessException;

    public abstract int getNumberInProgress(String voName) throws DataAccessException;

    public abstract int getNumberQueued() throws DataAccessException;

    public abstract int getNumberQueued(String voName) throws DataAccessException;

    public abstract String getRequestToken(String taskId) throws DataAccessException;

    public abstract int getRetryValue(String taskId) throws DataAccessException;

    public abstract RecallTaskTO getTask(String taskId) throws DataAccessException;

    public abstract int getTaskStatus(String taskId) throws DataAccessException;

    public String insertTask(PtGChunkData chunkData, GlobalStatusManager gsm, String voName)
            throws DataAccessException {

        String requestToken = chunkData.requestToken().getValue();

        RecallTaskTO task = new RecallTaskTO();
        task.setRequestType(RecallTaskTO.PTG_REQUEST);
        task.setRequestToken(requestToken);
        task.setFileName(chunkData.fromSURL().getSURLString());
        task.setVoName(voName);
        task.setPinLifetime((int) chunkData.lifeTime().value());

        String taskId = insertTask(task);

        if (!ptgGSM.containsKey(taskId)) {
            ptgGSM.put(taskId, gsm);
            ptgChunkData.put(taskId, chunkData);
        } else {
            log.error("BUG: duplicated key taskId: " + taskId);
        }

        return taskId;
    }

    public abstract String insertTask(RecallTaskTO task) throws DataAccessException;

    public abstract void setRetryValue(String taskId, int value) throws DataAccessException;

    public boolean setTaskStatus(String taskId, int status) throws DataAccessException {

        if (!setTaskStatusDBImpl(taskId, status)) {
            // shall the "taskId" data be removed from hashmaps? lets think about it...
            return false;
        }

        if ((status == RecallTaskStatus.IN_PROGRESS.getStatusId()) || (status == RecallTaskStatus.QUEUED.getStatusId())) {
            log.warn("Setting the status to IN_PROGRESS or QUEUED using setTaskStatus() is not a legal operation, taskId="
                    + taskId);
            return true;
        }

        GlobalStatusManager gsm = ptgGSM.remove(taskId);
        PtGChunkData chunkData = ptgChunkData.remove(taskId);

        if ((gsm == null) || (chunkData == null)) {
            // Happens when the task is inserted with insertTask(RecallTaskTO task)
            log.info("Set status with no internal data. taskId=\"" + taskId + "\" status=" + status);
            return true;
        }

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
        return true;
    }

    public abstract RecallTaskTO takeoverTask() throws DataAccessException;

    public abstract RecallTaskTO takeoverTask(String voName) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException;

    public abstract List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName)
            throws DataAccessException;

    protected abstract boolean setTaskStatusDBImpl(String taskId, int status) throws DataAccessException;
}
