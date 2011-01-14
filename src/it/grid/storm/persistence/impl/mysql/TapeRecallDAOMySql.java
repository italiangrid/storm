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

package it.grid.storm.persistence.impl.mysql;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
//import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallDAOMySql extends TapeRecallDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAOMySql.class);

    private final TapeRecallMySQLHelper sqlHelper;

    public TapeRecallDAOMySql() {
        sqlHelper = new TapeRecallMySQLHelper(PersistenceDirector.getDataBase().getDbmsVendor());
    }

//    public static void main(String[] args) throws DataAccessException {
//
//        TapeRecallDAO trDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();
//
//        RecallTaskTO rtTO = new RecallTaskTO();
//
//        rtTO.setFileName("pippo_00");
//        try {
//            rtTO.setRequestToken(TRequestToken.getRandom());
//        } catch (InvalidTRequestTokenAttributesException e) {
//            throw new DataAccessException(e);
//        }
//        rtTO.setStatus(RecallTaskStatus.IN_PROGRESS);
//        rtTO.setVoName("infngrid");
//
//        trDAO.insertTask(rtTO);
//        UUID taskId = rtTO.getTaskId();
//        
//        int status = trDAO.getTaskStatus(taskId);
//
//        System.out.println("GOT STATUS " + status + " for taskId=" + taskId);
//    }

    @Override
    public List<RecallTaskTO> getInProgressTask() throws DataAccessException {
        return getInProgressTask(null);
    }

    @Override
    public List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException {

        String query;
        if (voName == null)
        {
            query = sqlHelper.getQueryGetInprograssTask();
        }
        else
        {
            query = sqlHelper.getQueryGetInprograssTask(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        List<RecallTaskTO> taskList = new LinkedList<RecallTaskTO>();
        RecallTaskTO task = null;
        ResultSet res = null;

        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == false)
            {
                return taskList;
            }
            do
            {
                task = new RecallTaskTO();
                setTaskInfo(task, res);
                taskList.add(task);
            } while (res.next());
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return taskList;
    }

    @Override
    public int getNumberInProgress() throws DataAccessException {

        return getNumberInProgress(null);
    }

    @Override
    public int getNumberInProgress(String voName) throws DataAccessException {

        String query;
        if (voName == null) {
            query = sqlHelper.getQueryNumberInProgress();
        } else {
            query = sqlHelper.getQueryNumberInProgress(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        int status = 0;
        ResultSet res = null;

        try {
        	log.debug("QUERY: "+query);
        	statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == true) {
                status = res.getInt(1);    
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfTasksWithStatus
     * (it.grid.storm.tape.recalltable.model.RecallTaskStatus)
     */
    @Override
    public int getNumberOfTasksWithStatus(RecallTaskStatus status, String voName) throws DataAccessException {
        // TODO Auto-generated method stub
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#getNumberOfToDoTasks()
     */
    @Override
    public int getNumberOfToDoTasks() throws DataAccessException {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public int getNumberQueued() throws DataAccessException {
        return getNumberQueued(null);
    }

    @Override
    public int getNumberQueued(String voName) throws DataAccessException {

        String query;
        if (voName == null)
        {
            query = sqlHelper.getQueryNumberQueued();
        }
        else
        {
            query = sqlHelper.getQueryNumberQueued(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        int status = 0;
        ResultSet res = null;
        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == true)
            {
                status = res.getInt(1);
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return status;
    }

    @Override
    public int getReadyForTakeOver() throws DataAccessException {
        return getReadyForTakeOver(null);
    }

    @Override
    public int getReadyForTakeOver(String voName) throws DataAccessException {

        String query;
        if (voName == null)
        {
            query = sqlHelper.getQueryReadyForTakeOver();
        }
        else
        {
            query = sqlHelper.getQueryReadyForTakeOver(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        int status = 0;
        ResultSet res = null;

        try
        {

            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == true)
            {
                status = res.getInt(1);
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return status;
    }

    @Override
    public String getRequestToken(UUID taskId) throws DataAccessException
    {

        String query = sqlHelper.getQueryGetRequestToken(taskId);
        Connection dbConnection = getConnection();
        Statement statment = null;

        String requestToken = null;
        ResultSet res = null;
        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == false)
            {   
                log.error("No task found for taskId=" + taskId);
                throw new DataAccessException("No task found for taskId=" + taskId);
            }
            requestToken = res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return requestToken;
    }

    @Override
    public int getRetryValue(UUID taskId) throws DataAccessException
    {

        String query = sqlHelper.getQueryGetRetryValue(taskId);

        Connection dbConnection = getConnection();
        Statement statment = null;

        int retryValue = -1;
        ResultSet res = null;

        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == false)
            {
                log.error("No task found for taskId=" + taskId);
                throw new DataAccessException("No task found for taskId=" + taskId);
            }
            retryValue = res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return retryValue;
    }

    @Override
    public List<RecallTaskTO> getTask(UUID taskId) throws DataAccessException
    {

        String query = sqlHelper.getQueryGetTask(taskId);
        
        RecallTaskTO task = null;
        ArrayList<RecallTaskTO> taskList = new ArrayList<RecallTaskTO>();

        Connection dbConnection = getConnection();
        Statement statment = null;
        ResultSet res = null;
        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);
            if (res.first() == false)
            {
                log.error("No tasks with TaskId='" + taskId + "'");
                throw new DataAccessException("No recall table row retrieved executing query: '" + query + "'");
            }
            do
            {
                task = new RecallTaskTO();
                setTaskInfo(task, res);
            } while (res.next());
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return taskList;
    }

    
    @Override
    public UUID getTaskId(String requestToken, String pfn) throws DataAccessException
    {

        String query = sqlHelper.getQueryRetrieveTaskId(requestToken, pfn);

        String uuidString = null;
        UUID taskId = null;
        
        Connection dbConnection = getConnection();
        Statement statment = null;
        ResultSet res = null;
        
        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == false)
            {
                log.error("No task found for requestToken=" + requestToken + " " + "pfn="
                          + pfn + ". Query = " + query);
                throw new DataAccessException("No task found for requestToken=" + requestToken + " " + "pfn="
                        + pfn + ". Query = " + query);
            }
            uuidString = res.getString(TapeRecallMySQLHelper.COL_TASK_ID);
            taskId = UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e)
        {
            throw new DataAccessException("Error creating UUID from string : " + query, e);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: '" + query + "' " + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return taskId;
    }

    @Override
    public int getTaskStatus(UUID taskId) throws DataAccessException {

        String query = sqlHelper.getQueryRetrieveTaskStatus(taskId);

        Connection dbConnection = getConnection();
        Statement statment = null;

        int status = -1;
        ResultSet res = null;
        try
        {
            log.debug("QUERY: " + query);
            statment = getStatement(dbConnection);
            res = statment.executeQuery(query);

            if (res.first() == false)
            {
                log.error("No task found for taskId=" + taskId);
                throw new DataAccessException("No task found for taskId=" + taskId);
            }
            status = res.getInt(TapeRecallMySQLHelper.COL_STATUS);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: " + query, e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return status;
    }

    @Override
    public void insertTask(RecallTaskTO task) throws DataAccessException
    {

        if (task.getTaskId() == null || task.getRequestToken() == null
                || task.getRequestToken().getValue().trim().equals(""))
        {
            log.error("received Task insert request with empty primary key field TaskId or RequestToken. TaskId = "
                    + task.getTaskId() + " , request token = " + task.getRequestToken());
            throw new DataAccessException("Unable to create insert the task wth the provided UUID and "
                    + "request token using UUID-namebased algorithm. TaskId = " + task.getTaskId()
                    + " , request token = " + task.getRequestToken());
        }

        Connection dbConnection = getConnection();
        PreparedStatement prepStat = null;
        try
        {
            prepStat = sqlHelper.getQueryInsertTask(dbConnection, task);
            if (prepStat == null)
            {
                // this case is possible if and only if the task is null or empty
                log.error("Cannot create the query because the task is null or empty.");
                throw new DataAccessException("Cannot create the query because the task is null or empty.");
            }
            log.debug("Query(insert-task)=" + prepStat.toString());
            prepStat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: " + prepStat.toString() + " ; "
                    + e.getMessage(), e);
        }
        finally
        {
            releaseConnection(null, prepStat, dbConnection);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#purgeCompletedTasks(int)
     */
    @Override
    public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException
    {

        String query = null;
        if (numMaxToPurge == -1)
        {
            query = sqlHelper.getQueryPurgeCompletedTasks();
        }
        else
        {
            query = sqlHelper.getQueryPurgeCompletedTasks(numMaxToPurge);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        try
        {
            statment = getStatement(dbConnection);
            int count = statment.executeUpdate(query);
            if (count == 0)
            {
                log.trace("No entries have been purged from tape_recall table");
            }
            else
            {
                log.info(count + " entries have been purged from tape_recall table");
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: " + query, e);
        }
        finally
        {
            releaseConnection(null, statment, dbConnection);
        }
    }

    @Override
    public void setRetryValue(UUID taskId, int value) throws DataAccessException
    {

        String query = sqlHelper.getQuerySetRetryValue(taskId, value);

        Connection dbConnection = getConnection();
        Statement statment = null;
        try
        {
            statment = getStatement(dbConnection);
            statment.executeUpdate(query);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: " + query, e);
        }
        finally
        {
            releaseConnection(null, statment, dbConnection);
        }
    }

    @Override
    public RecallTaskTO takeoverTask() throws DataAccessException {
        return takeoverTask(null);
    }

    @Override
    public RecallTaskTO takeoverTask(String voName) throws DataAccessException {

        List<RecallTaskTO> taskList = takeoverTasks(1, voName);

        if (taskList.isEmpty()) {
            return null;
        }
        return taskList.get(0);
    }

    @Override
    public List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException {
        return takeoverTasks(numberOfTaks, null);
    }

    @Override
    public List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName) throws DataAccessException
    {

        String query;
        if (voName == null)
        {
            query = sqlHelper.getQueryTakeoverTasksSelect(numberOfTaks);
        }
        else
        {
            query = sqlHelper.getQueryTakeoverTasksSelect(numberOfTaks, voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = null;

        List<RecallTaskTO> taskList = new LinkedList<RecallTaskTO>();
        RecallTaskTO task = null;
        ResultSet res = null;

        try
        {
            dbConnection.setAutoCommit(false);
        }catch(SQLException e)
        {
            log.error("Error setting autocommit to false! " + e.getMessage());
            throw new DataAccessException("Error setting autocommit to false! " + e.getMessage() , e);
        }
        try
        {
            statment = getStatement(dbConnection);
            // start transaction
            log.debug("QUERY: " + query);
            res = statment.executeQuery(query);
            if (res.first() == false)
            {
                log.info("No tape recall rows ready for takeover");
                return taskList;
            }

            List<UUID> taskIdList = new LinkedList<UUID>();

            do
            {
                task = new RecallTaskTO();
                UUID taskId = setTaskInfo(task, res);
                taskList.add(task);
                if (taskIdList.contains(taskId))
                {
                    log.info("Found multiple recall requests on the same file. "
                            + "These will be managed as a unique recall request.");
                }
                else
                {
                    taskIdList.add(taskId);
                }
            } while (res.next());
            if (!taskIdList.isEmpty())
            {
                query = sqlHelper.getQueryTakeoverTasksUpdate(taskIdList);
                statment.executeUpdate(query);
            }
            commit(dbConnection);
        }
        catch (SQLException e)
        {
            rollback(dbConnection);
            throw new DataAccessException("Error executing query: " + query, e);
        }
        finally
        {
            releaseConnection(res, statment, dbConnection);
        }
        return taskList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#updateTask(it.grid.storm. persistence.model.RecallTaskTO)
     */
    @Override
    public void updateTask(RecallTaskTO task) throws DataAccessException {
        // TODO Auto-generated method stub

    }

    private UUID setTaskInfo(RecallTaskTO task, ResultSet res) throws DataAccessException
    {

        if (res == null)
        {
            throw new DataAccessException("Unable to build Task from NULL ResultSet");
        }
       
        UUID taskId = null;
        String taskIdStr = null;
        try
        {
            taskIdStr = res.getString(TapeRecallMySQLHelper.COL_TASK_ID);
            taskId = UUID.fromString(taskIdStr);
            task.setTaskId(taskId);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Unable to retrieve TaskId String from ResultSet. " + e);
        }
        catch (IllegalArgumentException iae)
        {
            throw new DataAccessException("Unable to build UUID from TaskId='" + taskIdStr + "'. " + iae);
        }

        String requestTokenStr = null;
        try
        {
            requestTokenStr = res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN);
            task.setRequestTokenStr(requestTokenStr);
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Unable to retrieve RequestToken String from ResultSet. " + e);
        }
        catch (InvalidTRequestTokenAttributesException e)
        {
            throw new DataAccessException("Unable to build TRequestToken from token='" + requestTokenStr
                    + "'. " + e);
        }

        try
        {
            task.setRequestType(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TYPE));
            task.setFileName(res.getString(TapeRecallMySQLHelper.COL_FILE_NAME));
            task.setPinLifetime(res.getInt(TapeRecallMySQLHelper.COL_PIN_LIFETIME));
            task.setStatusId(res.getInt(TapeRecallMySQLHelper.COL_STATUS));
            task.setVoName(res.getString(TapeRecallMySQLHelper.COL_VO_NAME));
            task.setUserID(res.getString(TapeRecallMySQLHelper.COL_USER_ID));
            task.setRetryAttempt(res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT));
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Unable to getting info from ResultSet. " + e);
        }
        return taskId;
    }
    
    @Override
    protected boolean setTaskStatusDBImpl(UUID taskId, int status) throws DataAccessException
    {

        String query = sqlHelper.getQueryUpdateTaskStatus(taskId, status);

        Connection dbConnection = getConnection();
        Statement statment = null;
        boolean ret = false;

        try
        {
            statment = getStatement(dbConnection);
            if (statment.executeUpdate(query) > 0)
            {
                ret = true;
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Error executing query: " + query, e);
        }
        finally
        {
            releaseConnection(null, statment, dbConnection);
        }
        return ret;
    }
}
