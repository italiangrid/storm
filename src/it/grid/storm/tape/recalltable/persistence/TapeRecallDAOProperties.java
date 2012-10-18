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

/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private static LinkedHashMap<TRequestToken, TapeRecallTO> tasks;
    private PropertiesDB tasksDB = null;
    private boolean test = false;


    public TapeRecallDAOProperties(boolean test) {
        this.test = true;
    }


//    /*
//     */
//    @Override
//    public List<TapeRecallTO> getInProgressTask() throws DataAccessException {
//        return getInProgressTask(UNSPECIFIED);
//    }
//
//
//    /*
//     */
//    @Override
//    public List<TapeRecallTO> getInProgressTask(String voName) throws DataAccessException {
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getRecallStatus().equals(RecallTaskStatus.IN_PROGRESS)) {
//                    if ((voName.equals(UNSPECIFIED)) || (TapeRecallTO.getVoName().equals(voName))) {
//                        result.add(TapeRecallTO);
//                    }
//                }
//            }
//        }
//        return result;
//    }


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
//    @Override
//    public int getNumberInProgress(String voName) throws DataAccessException {
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getRecallStatus().equals(TapeRecallStatus.IN_PROGRESS)) {
//                    if ((voName.equals(UNSPECIFIED)) || (TapeRecallTO.getVoName().equals(voName))) {
//                        result.add(TapeRecallTO);
//                    }
//                }
//            }
//        }
//        return result.size();
//    }


//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfTasksWithStatus
//     * (it.grid.storm.tape.recalltable.model.RecallTaskStatus)
//     */
//    @Override
//    public int getNumberOfTasksWithStatus(RecallTaskStatus status, String voName) throws DataAccessException {
//        if (voName == null) {
//            voName = UNSPECIFIED;
//        }
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getRecallStatus().equals(status)) {
//                    if ((voName.equals(UNSPECIFIED)) || (TapeRecallTO.getVoName().equals(voName))) {
//                        result.add(TapeRecallTO);
//                    }
//                }
//            }
//        }
//        return result.size();
//    }
//
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfToDoTasks()
//     */
//    @Override
//    public int getNumberOfToDoTasks() throws DataAccessException {
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if ((TapeRecallTO.getRecallStatus().equals(RecallTaskStatus.IN_PROGRESS)) || ((TapeRecallTO.getRecallStatus().equals(RecallTaskStatus.QUEUED)))) {
//                    result.add(TapeRecallTO);
//                }
//
//            }
//        }
//        return result.size();
//    }


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
//    @Override
//    public int getNumberQueued(String voName) throws DataAccessException {
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getRecallStatus().equals(TapeRecallStatus.QUEUED)) {
//                    if ((voName.equals(UNSPECIFIED)) || (TapeRecallTO.getVoName().equals(voName))) {
//                        result.add(TapeRecallTO);
//                    }
//                }
//            }
//        }
//        return result.size();
//    }


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





//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * it.grid.storm.persistence.dao.TapeRecallDAO#getRetryValue(java.lang.String
//     * )
//     */
//    @Override
//    public int getRetryValue(UUID taskId, String requestToken) throws DataAccessException {
//        int result = 0;
//        tasks = getTasks();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getTaskId().equals(taskId)) {
//                    result = TapeRecallTO.getRetryAttempt();
//                }
//            }
//        }
//        return result;
//    }
//
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * it.grid.storm.persistence.dao.TapeRecallDAO#getTask(java.lang.String)
//     */
//    @Override
//    public List<TapeRecallTO> getTask(UUID taskId) throws DataAccessException {
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        tasks = getTasks();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks.values()) {
//                if (TapeRecallTO.getTaskId().equals(taskId)) {
//                    result.add(TapeRecallTO);
//                }
//            }
//        }
//        return result;
//    }
//
//
//    @Override
//    public UUID getTaskId(String requestToken, String pfn) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return UUID.randomUUID();
//    }
//
//
//    /*
//     */
//    @Override
//    public int getTaskStatus(UUID taskId, String requestToken) throws DataAccessException {
//        int result = RecallTaskStatus.UNDEFINED.ordinal();
//        tasks = getTasks();
//        if (tasks.containsKey(taskId)) {
//            result = tasks.get(taskId).getStatusId();
//        } else {
//            throw new DataAccessException("Recall Task with taskId = " + taskId + " does not exists!");
//        }
//        return result;
//    }


    /*
     */
//    @Override
//    public void insertTask(TapeRecallTO task) throws DataAccessException {
//        PropertiesDB tasksDB = getTasksDB();
//        // Retrieve an unique task-id.
//        UUID taskId = UUID.randomUUID();
//        task.setTaskId(taskId);
//        try {
//            tasksDB.addRecallTask(task);
//        } catch (FileNotFoundException e) {
//            log.error("RecallTask DB does not exists!");
//            e.printStackTrace();
//        } catch (IOException e) {
//            log.error("IO Error while reading RecallTaskDB.");
//            e.printStackTrace();
//        }
//    }


    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#purgeCompletedTasks(int)
     */
//    @Override
//    public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException {
//        ArrayList<TapeRecallTO> ordTasks = getOrderedTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : ordTasks) {
//                if ((TapeRecallTO.getRecallStatus().equals(TapeRecallStatus.ERROR)) || ((TapeRecallTO.getRecallStatus().equals(TapeRecallStatus.ABORTED))) || ((TapeRecallTO.getRecallStatus().equals(TapeRecallStatus.SUCCESS)))) {
//                    result.add(TapeRecallTO);
//                }
//            }
//            int count = 0;
//            if (result.size() > 0) {
//                for (TapeRecallTO TapeRecallTO : result) {
//                    try {
//                        tasksDB.deleteRecallTask(TapeRecallTO.getTaskId());
//                    } catch (FileNotFoundException e) {
//                        log.error("RecallTask DB does not exists!");
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        log.error("IO Error while reading RecallTaskDB.");
//                        e.printStackTrace();
//                    }
//                    count++;
//                    if (count >= numMaxToPurge) {
//                        break;
//                    }
//                }
//                log.debug("Purged " + count + " completed tasks from RecallTable.");
//            } else {
//                log.debug("No one completed tasks to purge.");
//            }
//        }
//    }


//    /*
//     */
//    @Override
//    public void setRetryValue(UUID taskId, String requestToken, int value) throws DataAccessException {
//        ArrayList<TapeRecallTO> tasks = new ArrayList<TapeRecallTO>(getTask(taskId));
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks) {
//                TapeRecallTO.setRetryAttempt(value);
//                PropertiesDB tasksDB = getTasksDB();
//                try {
//                    tasksDB.addRecallTask(TapeRecallTO);
//                } catch (FileNotFoundException e) {
//                    log.error("RecallTask DB does not exists!");
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    log.error("IO Error while reading RecallTaskDB.");
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    public void setTestMode() {
        test = true;
    }


    /*
     */
    @Override
    public TapeRecallTO takeoverTask() throws DataAccessException {
        return takeoverTask(UNSPECIFIED);
    }


//    /*
//     */
//    @Override
//    public TapeRecallTO takeoverTask(String voName) throws DataAccessException {
//        return takeoverTasks(1, voName).get(0);
//    }


    /*
//     * (non-Javadoc)
//     * 
//     * @see it.grid.storm.persistence.dao.TapeRecallDAO#takeoverTasks(int)
//     */
//    @Override
//    public List<TapeRecallTO> takeoverTasks(int numberOfTaks) throws DataAccessException {
//        return takeoverTasks(numberOfTaks, UNSPECIFIED);
//    }


//    /*
//     * (non-Javadoc)
//     * 
//     * @see it.grid.storm.persistence.dao.TapeRecallDAO#takeoverTasks(int,
//     * java.lang.String)
//     */
//    @Override
//    public List<TapeRecallTO> takeoverTasks(int numberOfTaks, String voName) throws DataAccessException {
//        int residualTasks = numberOfTaks;
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>();
//        TapeRecallTO TapeRecallTO = null;
//
//        ArrayList<TapeRecallTO> ordTasks = getOrderedTasks();
//        Iterator<TapeRecallTO> scan = ordTasks.iterator();
//
//        while ((scan.hasNext() & (residualTasks > 0))) {
//            TapeRecallTO = scan.next();
//            if ((TapeRecallTO.getVoName().equals(voName)) || (voName.equals(UNSPECIFIED))) {
//                // Check if the Task has the Status = QUEUED
//                if (TapeRecallTO.getRecallStatus().equals(RecallTaskStatus.QUEUED)) {
//                    residualTasks--;
//                    result.add(TapeRecallTO);
//                }
//            }
//        }
//        if (residualTasks > 0) {
//            throw new DataAccessException("Not enough tasks belonging to VO : " + voName + " to takeover. Residual tasks = " + residualTasks);
//        }
//
//        // -------- Change the status from QUEUED to IN_PROGRESS ----------
//        for (TapeRecallTO recT : result) {
//            recT.setStatus(RecallTaskStatus.IN_PROGRESS);
//        }
//
//        // -------- Store into RecallTask properties
//
//        // taskDB should be not NULL due to side effects of 'getOrderedTasks';
//        try {
//            tasksDB.setRecallTask(result);
//        } catch (FileNotFoundException e) {
//            log.error("RecallTask DB does not exists!");
//            e.printStackTrace();
//        } catch (IOException e) {
//            log.error("IO Error while reading RecallTaskDB.");
//            e.printStackTrace();
//        }
//
//        return result;
//    }


//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * it.grid.storm.persistence.dao.TapeRecallDAO#updateTask(it.grid.storm.
//     * persistence.model.TapeRecallTO)
//     */
//    @Override
//    public void updateTask(TapeRecallTO task) throws DataAccessException {
//        try {
//            getTasksDB().updateRecallTask(task);
//        } catch (FileNotFoundException e) {
//            log.error("RecallTask DB does not exists!");
//            e.printStackTrace();
//        } catch (IOException e) {
//            log.error("IO Error while reading RecallTaskDB.");
//            e.printStackTrace();
//        }
//    }


//    private ArrayList<TapeRecallTO> getOrderedTasks() throws DataAccessException {
//        tasks = getTasks();
//        ArrayList<TapeRecallTO> result = new ArrayList<TapeRecallTO>(tasks.values());
//        return result;
//    }


//    private LinkedHashMap<TRequestToken, TapeRecallTO> getTasks() throws DataAccessException {
//        getTasksDB();
//        LinkedHashMap<TRequestToken, TapeRecallTO> result = null;
//        try {
//            result = tasksDB.getAll();
//        } catch (FileNotFoundException e) {
//            log.error("RecallTask DB does not exists!");
//            throw new DataAccessException("RecallTask DB does not exists!");
//        } catch (IOException e) {
//            log.error("IO Error while reading RecallTaskDB.");
//            throw new DataAccessException("IO Error while reading RecallTaskDB.");
//        }
//        return result;
//    }


    private PropertiesDB getTasksDB() {
        tasksDB = new PropertiesDB(test);
        return tasksDB;
    }


//    /*
//     */
//    @Override
//    protected boolean setTaskStatusDBImpl(UUID taskId, String requestToken, int status) throws DataAccessException {
//        boolean result = false;
//        ArrayList<TapeRecallTO> tasks = new ArrayList<TapeRecallTO>(getTask(taskId));
//        if (!(tasks.isEmpty())) {
//            for (TapeRecallTO TapeRecallTO : tasks) {
//                TapeRecallTO.setStatus(RecallTaskStatus.getRecallTaskStatus(status));
//                PropertiesDB tasksDB = getTasksDB();
//                try {
//                    tasksDB.addRecallTask(TapeRecallTO);
//                    result = true;
//                } catch (FileNotFoundException e) {
//                    log.error("RecallTask DB does not exists!");
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    log.error("IO Error while reading RecallTaskDB.");
//                    e.printStackTrace();
//                }
//            }          
//        }
//        return result;
//    }
//
//
//    @Override
//    public List<String> getRequestToken(UUID taskId) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return null;
//    }


//    @Override
//    public Integer setGroupTaskID(UUID taskId, Set<TRequestToken> requestTokens, UUID groupTaskID) throws DataAccessException
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }


//    @Override
//    public List<TapeRecallTO> getGroupTasks(UUID groupTaskId) throws DataAccessException
//    {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//
//    @Override
//    public List<String> getRequestToken(UUID taskId, int status) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return null;
//    }


    @Override
    public void setGroupTaskRetryValue(UUID groupTaskId, int value) throws DataAccessException {
        // TODO Auto-generated method stub
        
    }



//    @Override
//    public Integer setGroupTaskID(UUID taskId, Set<String> requestTokens, UUID groupTaskID) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//
//    @Override
//    public UUID getTaskId(UUID groupTaskId) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//
//    @Override
//    public List<TapeRecallTO> getTask(UUID taskId, int[] statuses) throws DataAccessException {
//        // TODO Auto-generated method stub
//        return null;
//    }


    @Override
    public UUID insertCloneTask(TapeRecallTO task, int[] statuses, UUID proposedGroupTaskId) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean existsGroupTask(UUID groupTaskId) throws DataAccessException {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public TapeRecallTO getTask(UUID taskId, String requestToken) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<TapeRecallTO> getGroupTasks(UUID groupTaskId) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public TapeRecallTO takeoverTask(String voName) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks, String voName) throws DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean setGroupTaskStatus(UUID groupTaskId, int statusId, Date timestamp) throws DataAccessException
    {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean existsTask(UUID taskId, String requestToken) throws DataAccessException
    {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public int getNumberInProgress(String voName) throws DataAccessException
    {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public int getNumberQueued(String voName) throws DataAccessException
    {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException
    {
        // TODO Auto-generated method stub
        
    }

}
