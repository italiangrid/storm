/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ritz
 * 
 */
public class TapeRecallDAOProperties extends TapeRecallDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAOProperties.class);
    public static final String UNSPECIFIED = "unspecified-VO";
    private static LinkedHashMap<TRequestToken, RecallTaskTO> tasks;
    private PropertiesDB tasksDB = null;
    private boolean test = false;


    public TapeRecallDAOProperties(boolean test) {
        this.test = true;
    }


    /*
     */
    @Override
    public List<RecallTaskTO> getInProgressTask() throws DataAccessException {
        return getInProgressTask(UNSPECIFIED);
    }


    /*
     */
    @Override
    public List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException {
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getRecallStatus().equals(RecallTaskStatus.IN_PROGRESS)) {
                    if ((voName.equals(UNSPECIFIED)) || (recallTaskTO.getVoName().equals(voName))) {
                        result.add(recallTaskTO);
                    }
                }
            }
        }
        return result;
    }


    /*
     */
    @Override
    public int getNumberInProgress() throws DataAccessException {
        return getNumberInProgress(UNSPECIFIED);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#getNumberInProgress(java.
     * lang.String)
     */
    @Override
    public int getNumberInProgress(String voName) throws DataAccessException {
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getRecallStatus().equals(RecallTaskStatus.IN_PROGRESS)) {
                    if ((voName.equals(UNSPECIFIED)) || (recallTaskTO.getVoName().equals(voName))) {
                        result.add(recallTaskTO);
                    }
                }
            }
        }
        return result.size();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfTasksWithStatus
     * (it.grid.storm.tape.recalltable.model.RecallTaskStatus)
     */
    @Override
    public int getNumberOfTasksWithStatus(RecallTaskStatus status, String voName) throws DataAccessException {
        if (voName == null) {
            voName = UNSPECIFIED;
        }
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getRecallStatus().equals(status)) {
                    if ((voName.equals(UNSPECIFIED)) || (recallTaskTO.getVoName().equals(voName))) {
                        result.add(recallTaskTO);
                    }
                }
            }
        }
        return result.size();
    }


    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfToDoTasks()
     */
    @Override
    public int getNumberOfToDoTasks() throws DataAccessException {
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if ((recallTaskTO.getRecallStatus().equals(RecallTaskStatus.IN_PROGRESS)) || ((recallTaskTO.getRecallStatus().equals(RecallTaskStatus.QUEUED)))) {
                    result.add(recallTaskTO);
                }

            }
        }
        return result.size();
    }


    /*
     */
    @Override
    public int getNumberQueued() throws DataAccessException {
        return getNumberQueued(UNSPECIFIED);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#getNumberQueued(java.lang
     * .String)
     */
    @Override
    public int getNumberQueued(String voName) throws DataAccessException {
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getRecallStatus().equals(RecallTaskStatus.QUEUED)) {
                    if ((voName.equals(UNSPECIFIED)) || (recallTaskTO.getVoName().equals(voName))) {
                        result.add(recallTaskTO);
                    }
                }
            }
        }
        return result.size();
    }


    @Override
    public int getReadyForTakeOver() throws DataAccessException {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public int getReadyForTakeOver(String voName) throws DataAccessException {
        // TODO Auto-generated method stub
        return 0;
    }





    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#getRetryValue(java.lang.String
     * )
     */
    @Override
    public int getRetryValue(UUID taskId) throws DataAccessException {
        int result = 0;
        tasks = getTasks();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getTaskId().equals(taskId)) {
                    result = recallTaskTO.getRetryAttempt();
                }
            }
        }
        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#getTask(java.lang.String)
     */
    @Override
    public List<RecallTaskTO> getTask(UUID taskId) throws DataAccessException {
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        tasks = getTasks();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks.values()) {
                if (recallTaskTO.getTaskId().equals(taskId)) {
                    result.add(recallTaskTO);
                }
            }
        }
        return result;
    }


    @Override
    public UUID getTaskId(String requestToken, String pfn) throws DataAccessException {
        // TODO Auto-generated method stub
        return UUID.randomUUID();
    }


    /*
     */
    @Override
    public int getTaskStatus(UUID taskId) throws DataAccessException {
        int result = RecallTaskStatus.UNDEFINED.ordinal();
        tasks = getTasks();
        if (tasks.containsKey(taskId)) {
            result = tasks.get(taskId).getStatusId();
        } else {
            throw new DataAccessException("Recall Task with taskId = " + taskId + " does not exists!");
        }
        return result;
    }


    /*
     */
    @Override
    public UUID insertTask(RecallTaskTO task) throws DataAccessException {
        PropertiesDB tasksDB = getTasksDB();
        // Retrieve an unique task-id.
        UUID taskId = UUID.randomUUID();
        task.setTaskId(taskId);
        try {
            tasksDB.addRecallTask(task);
        } catch (FileNotFoundException e) {
            log.error("RecallTask DB does not exists!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO Error while reading RecallTaskDB.");
            e.printStackTrace();
        }
        return taskId;
    }


    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#purgeCompletedTasks(int)
     */
    @Override
    public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException {
        ArrayList<RecallTaskTO> ordTasks = getOrderedTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : ordTasks) {
                if ((recallTaskTO.getRecallStatus().equals(RecallTaskStatus.ERROR)) || ((recallTaskTO.getRecallStatus().equals(RecallTaskStatus.ABORTED))) || ((recallTaskTO.getRecallStatus().equals(RecallTaskStatus.SUCCESS)))) {
                    result.add(recallTaskTO);
                }
            }
            int count = 0;
            if (result.size() > 0) {
                for (RecallTaskTO recallTaskTO : result) {
                    try {
                        tasksDB.deleteRecallTask(recallTaskTO.getTaskId());
                    } catch (FileNotFoundException e) {
                        log.error("RecallTask DB does not exists!");
                        e.printStackTrace();
                    } catch (IOException e) {
                        log.error("IO Error while reading RecallTaskDB.");
                        e.printStackTrace();
                    }
                    count++;
                    if (count >= numMaxToPurge) {
                        break;
                    }
                }
                log.debug("Purged " + count + " completed tasks from RecallTable.");
            } else {
                log.debug("No one completed tasks to purge.");
            }
        }
    }


    /*
     */
    @Override
    public void setRetryValue(UUID taskId, int value) throws DataAccessException {
        ArrayList<RecallTaskTO> tasks = new ArrayList<RecallTaskTO>(getTask(taskId));
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks) {
                recallTaskTO.setRetryAttempt(value);
                PropertiesDB tasksDB = getTasksDB();
                try {
                    tasksDB.addRecallTask(recallTaskTO);
                } catch (FileNotFoundException e) {
                    log.error("RecallTask DB does not exists!");
                    e.printStackTrace();
                } catch (IOException e) {
                    log.error("IO Error while reading RecallTaskDB.");
                    e.printStackTrace();
                }
            }
        }
    }


    public void setTestMode() {
        test = true;
    }


    /*
     */
    @Override
    public RecallTaskTO takeoverTask() throws DataAccessException {
        return takeoverTask(UNSPECIFIED);
    }


    /*
     */
    @Override
    public RecallTaskTO takeoverTask(String voName) throws DataAccessException {
        return takeoverTasks(1, voName).get(0);
    }


    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#takeoverTasks(int)
     */
    @Override
    public List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException {
        return takeoverTasks(numberOfTaks, UNSPECIFIED);
    }


    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#takeoverTasks(int,
     * java.lang.String)
     */
    @Override
    public List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName) throws DataAccessException {
        int residualTasks = numberOfTaks;
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>();
        RecallTaskTO recallTaskTO = null;

        ArrayList<RecallTaskTO> ordTasks = getOrderedTasks();
        Iterator<RecallTaskTO> scan = ordTasks.iterator();

        while ((scan.hasNext() & (residualTasks > 0))) {
            recallTaskTO = scan.next();
            if ((recallTaskTO.getVoName().equals(voName)) || (voName.equals(UNSPECIFIED))) {
                // Check if the Task has the Status = QUEUED
                if (recallTaskTO.getRecallStatus().equals(RecallTaskStatus.QUEUED)) {
                    residualTasks--;
                    result.add(recallTaskTO);
                }
            }
        }
        if (residualTasks > 0) {
            throw new DataAccessException("Not enough tasks belonging to VO : " + voName + " to takeover. Residual tasks = " + residualTasks);
        }

        // -------- Change the status from QUEUED to IN_PROGRESS ----------
        for (RecallTaskTO recT : result) {
            recT.setStatus(RecallTaskStatus.IN_PROGRESS);
        }

        // -------- Store into RecallTask properties

        // taskDB should be not NULL due to side effects of 'getOrderedTasks';
        try {
            tasksDB.setRecallTask(result);
        } catch (FileNotFoundException e) {
            log.error("RecallTask DB does not exists!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO Error while reading RecallTaskDB.");
            e.printStackTrace();
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * it.grid.storm.persistence.dao.TapeRecallDAO#updateTask(it.grid.storm.
     * persistence.model.RecallTaskTO)
     */
    @Override
    public void updateTask(RecallTaskTO task) throws DataAccessException {
        try {
            getTasksDB().updateRecallTask(task);
        } catch (FileNotFoundException e) {
            log.error("RecallTask DB does not exists!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO Error while reading RecallTaskDB.");
            e.printStackTrace();
        }
    }


    private ArrayList<RecallTaskTO> getOrderedTasks() throws DataAccessException {
        tasks = getTasks();
        ArrayList<RecallTaskTO> result = new ArrayList<RecallTaskTO>(tasks.values());
        return result;
    }


    private LinkedHashMap<TRequestToken, RecallTaskTO> getTasks() throws DataAccessException {
        getTasksDB();
        LinkedHashMap<TRequestToken, RecallTaskTO> result = null;
        try {
            result = tasksDB.getAll();
        } catch (FileNotFoundException e) {
            log.error("RecallTask DB does not exists!");
            throw new DataAccessException("RecallTask DB does not exists!");
        } catch (IOException e) {
            log.error("IO Error while reading RecallTaskDB.");
            throw new DataAccessException("IO Error while reading RecallTaskDB.");
        }
        return result;
    }


    private PropertiesDB getTasksDB() {
        tasksDB = new PropertiesDB(test);
        return tasksDB;
    }


    /*
     */
    @Override
    protected boolean setTaskStatusDBImpl(UUID taskId, int status) throws DataAccessException {
        boolean result = false;
        ArrayList<RecallTaskTO> tasks = new ArrayList<RecallTaskTO>(getTask(taskId));
        if (!(tasks.isEmpty())) {
            for (RecallTaskTO recallTaskTO : tasks) {
                recallTaskTO.setStatus(RecallTaskStatus.getRecallTaskStatus(status));
                PropertiesDB tasksDB = getTasksDB();
                try {
                    tasksDB.addRecallTask(recallTaskTO);
                    result = true;
                } catch (FileNotFoundException e) {
                    log.error("RecallTask DB does not exists!");
                    e.printStackTrace();
                } catch (IOException e) {
                    log.error("IO Error while reading RecallTaskDB.");
                    e.printStackTrace();
                }
            }          
        }
        return result;
    }


    @Override
    public String getRequestToken(UUID taskId) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }

}
