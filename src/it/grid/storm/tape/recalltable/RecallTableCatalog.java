/**
 * 
 */
package it.grid.storm.tape.recalltable;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;
import it.grid.storm.tape.recalltable.persistence.TapeRecallDAOProperties;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
public class RecallTableCatalog {

    private static final Logger log = LoggerFactory.getLogger(RecallTableCatalog.class);
    private final TapeRecallDAO tapeRecallDAO;

    /**
     * Default constructor
     * 
     * @throws DataAccessException
     */
    public RecallTableCatalog(boolean test) throws DataAccessException {
        //log.debug("Building RECALL TABLE Catalog ...");

        if (test) {
            tapeRecallDAO = new TapeRecallDAOProperties(true);
        } else {
            tapeRecallDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();
        }
    }

    public void changeRetryValue(int taskId, int newValue) {
        try {
            tapeRecallDAO.setRetryValue(taskId, newValue);
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
        }
    }

    public boolean changeStatus(int taskId, RecallTaskStatus newStatus) {
        try {
            return tapeRecallDAO.setTaskStatus(taskId, newStatus.getStatusId());
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
            return false;
        }
    }

    public List<RecallTaskTO> getInProgressTasks() {
        ArrayList<RecallTaskTO> taskList = new ArrayList<RecallTaskTO>();
        try {
            taskList.addAll(tapeRecallDAO.getInProgressTask());
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
        }
        return taskList;
    }

    public int getNumberTask(RecallTaskStatus status) {
        int result = -1;
        try {
            result = tapeRecallDAO.getNumberOfTasksWithStatus(status, null);
        } catch (DataAccessException e) {
            log.error("AHH!", e);
        }
        return result;
    }

    public int getNumberTaskInProgress() {
        int result = -1;
        try {
            result = tapeRecallDAO.getNumberInProgress();
        } catch (DataAccessException e) {
            log.error("AHH!", e);
        }
        return result;
    }

    public int getNumberTaskQueued() {
        int result = -1;
        try {
            result = tapeRecallDAO.getNumberQueued();
        } catch (DataAccessException e) {
            log.error("AHH!", e);
        }
        return result;

    }

    public int getReadyForTakeOver() {
        int result = -1;
        try {
            result = tapeRecallDAO.getReadyForTakeOver();
        } catch (DataAccessException e) {
            log.error("AHH!", e);
        }
        return result;
    }

    public int getRecallTableSize() {
        int result = -1;
        try {
            result = tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.ABORTED, null);
            result += tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.ERROR, null);
            result += tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.IN_PROGRESS, null);
            result += tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.QUEUED, null);
            result += tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.SUCCESS, null);
            result += tapeRecallDAO.getNumberOfTasksWithStatus(RecallTaskStatus.UNDEFINED, null);
        } catch (DataAccessException e) {
            log.error("AHH!", e);
        }
        return result;
    }

    public RecallTaskTO getTask(int taskId) throws DataAccessException {
        RecallTaskTO task = null;
        task = tapeRecallDAO.getTask(taskId);
        return task;
    }

    public int getTaskId(String requestToken, String pfn) throws DataAccessException {
        return tapeRecallDAO.getTaskId(requestToken, pfn);
    }

    public void insertNewTask(RecallTaskTO task) {
        try {
            tapeRecallDAO.insertTask(task);
        } catch (DataAccessException e) {
            log.error("Unable to store the task : " + task.toString());
            e.printStackTrace();
        }
    }

    public void purgeCatalog(int n) {
        try {
            log.debug("purging.. '" + n + "' tasks.");
            tapeRecallDAO.purgeCompletedTasks(n);
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
        }
    }

    /**
     * @param n
     * @return
     */
    public ArrayList<RecallTaskTO> takeoverNTasks(int n) {
        ArrayList<RecallTaskTO> taskList = new ArrayList<RecallTaskTO>();
        try {
            taskList.addAll(tapeRecallDAO.takeoverTasks(n));
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
        }
        return taskList;
    }

    public RecallTaskTO takeoverTask() {
        RecallTaskTO task = null;
        try {
            task = tapeRecallDAO.takeoverTask();
        } catch (DataAccessException e) {
            log.error("Unable to takeover a task", e);
        }
        return task;
    }

    public RecallTaskTO taskOverTask() {
        RecallTaskTO task = null;
        try {
            task = tapeRecallDAO.takeoverTask();
        } catch (DataAccessException e) {
            if (task == null) {
                log.error("Unable to update the task. It is NULL!", e);
            } else {
                log.error("Unable to update the task " + task.getTaskId(), e);
            }
        }
        return task;
    }

    public void updateTask(RecallTaskTO task) {
        try {
            tapeRecallDAO.updateTask(task);
        } catch (DataAccessException e) {
            log.error("Unable to update the task : " + task.toString(), e);
        }
    }
}
