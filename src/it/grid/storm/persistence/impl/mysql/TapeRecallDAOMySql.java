package it.grid.storm.persistence.impl.mysql;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallDAOMySql extends TapeRecallDAO {

    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAOMySql.class);

    private final TapeRecallMySQLHelper sqlHelper;

    public TapeRecallDAOMySql() {
        sqlHelper = new TapeRecallMySQLHelper(PersistenceDirector.getDataBase().getDbmsVendor());
    }

    public static void main(String[] args) throws DataAccessException {

        TapeRecallDAO trDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();

        RecallTaskTO rtTO = new RecallTaskTO();

        rtTO.setFileName("pippo_00");
        rtTO.setRequestToken("toke_00");
        rtTO.setStatus(RecallTaskStatus.IN_PROGRESS);
        rtTO.setVoName("infngrid");

        int taskId = trDAO.insertTask(rtTO);

        int status = trDAO.getTaskStatus(taskId);

        System.out.println("GOT STATUS " + status + " for taskId=" + taskId);
    }

    @Override
    public List<RecallTaskTO> getInProgressTask() throws DataAccessException {
        return getInProgressTask(null);
    }

    @Override
    public List<RecallTaskTO> getInProgressTask(String voName) throws DataAccessException {

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        String query;

        if (voName == null) {
            query = sqlHelper.getQueryGetInprograssTask();
        } else {
            query = sqlHelper.getQueryGetInprograssTask(voName);
        }

        List<RecallTaskTO> taskList = new LinkedList<RecallTaskTO>();
        RecallTaskTO task = null;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                return taskList;
            }

            if (res.first() == false) {
                return taskList;
            }

            while (res.next()) {
                task = new RecallTaskTO();

                setTaskInfo(task, res);

                taskList.add(task);
            }

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

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
        Statement statment = getStatement(dbConnection);

        int status;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                return 0;
            }

            if (res.first() == false) {
                return -1;
            }

            status = res.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

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

        if (voName == null) {
            query = sqlHelper.getQueryNumberQueued();
        } else {
            query = sqlHelper.getQueryNumberQueued(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        int status;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                return 0;
            }

            if (res.first() == false) {
                return -1;
            }

            status = res.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return status;
    }

    @Override
    public int getReadyForTakeOver() throws DataAccessException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getReadyForTakeOver(String voName) throws DataAccessException {

        String query;

        if (voName == null) {
            query = sqlHelper.getQueryReadyForTakeOver();
        } else {
            query = sqlHelper.getQueryReadyForTakeOver(voName);
        }

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        int status;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                return 0;
            }

            if (res.first() == false) {
                return -1;
            }

            status = res.getInt(1);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return status;
    }

    @Override
    public String getRequestToken(int taskId) throws DataAccessException {

        String query = sqlHelper.getQueryGetRequestToken(taskId);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        String requestToken = null;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                throw new DataAccessException("No task found for taskId=" + taskId);
            }

            if (res.first() == false) {
                return null;
            }

            requestToken = res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return requestToken;
    }

    @Override
    public int getRetryValue(int taskId) throws DataAccessException {

        String query = sqlHelper.getQueryGetRetryValue(taskId);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        int retryValue;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                throw new DataAccessException("No task found for taskId=" + taskId);
            }

            if (res.first() == false) {
                return -1;
            }

            retryValue = res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return retryValue;
    }

    @Override
    public RecallTaskTO getTask(int taskId) throws DataAccessException {

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        String query = sqlHelper.getQueryGetTask(taskId);

        RecallTaskTO task = null;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                return null;
            }

            if (res.first() == false) {
                return null;
            }

            task = new RecallTaskTO();

            setTaskInfo(task, res);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return task;
    }

    @Override
    public int getTaskStatus(int taskId) throws DataAccessException {

        String query = sqlHelper.getQueryRetrieveTaskStatus(taskId);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        int status;
        ResultSet res = null;

        try {

            res = statment.executeQuery(query);

            if (res == null) {
                throw new DataAccessException("No task found for taskId=" + taskId);
            }

            if (res.first() == false) {
                return -1;
            }

            status = res.getInt(TapeRecallMySQLHelper.COL_STATUS);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return status;
    }

    @Override
    public int insertTask(RecallTaskTO task) throws DataAccessException {

        Connection dbConnection = getConnection();
        PreparedStatement prepStat = sqlHelper.getQueryInsertTask(dbConnection, task);
        log.debug("Query(insert-task)=" + prepStat.toString());

        int taskId = -1;
        try {

            prepStat.executeUpdate();

            ResultSet rs = prepStat.getGeneratedKeys();

            if (rs.next()) {
                taskId = rs.getInt(1);
            } else {
                throw new DataAccessException("Cannot retrieve the last inserted index. Query: "
                        + prepStat.toString());
            }

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + prepStat.toString(), e);

        }

        releaseConnection(null, prepStat, dbConnection);
        return taskId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#purgeCompletedTasks(int)
     */
    @Override
    public void purgeCompletedTasks(int numMaxToPurge) throws DataAccessException {

        String query = null;

        if (numMaxToPurge == -1) {
            query = sqlHelper.getQueryPurgeCompletedTasks();
        } else {
            query = sqlHelper.getQueryPurgeCompletedTasks(numMaxToPurge);
        }

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        try {

            int count = statment.executeUpdate(query);

            if (count == 0) {
                log.info("No entries have been purged from tape_recall table");
            } else {
                log.info(count + " entries have been purged from tape_recall table");
            }

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(null, statment, dbConnection);
        }
    }

    @Override
    public void setRetryValue(int taskId, int value) throws DataAccessException {

        String query = sqlHelper.getQuerySetRetryValue(taskId, value);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        try {

            statment.executeUpdate(query);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

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
    public List<RecallTaskTO> takeoverTasks(int numberOfTaks, String voName) throws DataAccessException {

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        String query;
        if (voName == null) {
            query = sqlHelper.getQueryTakeoverTasksSelect(numberOfTaks);
        } else {
            query = sqlHelper.getQueryTakeoverTasksSelect(numberOfTaks, voName);
        }

        List<RecallTaskTO> taskList = new LinkedList<RecallTaskTO>();
        RecallTaskTO task = null;
        ResultSet res = null;

        try {

            // start transaction
            dbConnection.setAutoCommit(false);

            res = statment.executeQuery(query);

            if (res == null) {
                return taskList;
            }

            List<Integer> taskIdList = new LinkedList<Integer>();

            while (res.next()) {
                task = new RecallTaskTO();

                int taskId = setTaskInfo(task, res);

                taskList.add(task);
                taskIdList.add(taskId);
            }

            if (!taskIdList.isEmpty()) {

                query = sqlHelper.getQueryTakeoverTasksUpdate(taskIdList);
                statment.executeUpdate(query);

            }

            commit(dbConnection);

        } catch (SQLException e) {

            rollback(dbConnection);
            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return taskList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.persistence.dao.TapeRecallDAO#updateTask(it.grid.storm.
     * persistence.model.RecallTaskTO)
     */
    @Override
    public void updateTask(RecallTaskTO task) throws DataAccessException {
    // TODO Auto-generated method stub

    }

    @Override
    protected boolean setTaskStatusDBImpl(int taskId, int status) throws DataAccessException {

        String query = sqlHelper.getQueryUpdateTaskStatus(taskId, status);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        boolean ret = false;

        try {

            if (statment.executeUpdate(query) > 0) {
                ret = true;
            }

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(null, statment, dbConnection);
        }

        return ret;
    }

    private int setTaskInfo(RecallTaskTO task, ResultSet res) throws SQLException {

        int taskId = res.getInt(TapeRecallMySQLHelper.COL_TASK_ID);

        task.setTaskId(taskId);
        task.setRequestToken(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN));
        task.setRequestType(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TYPE));
        task.setFileName(res.getString(TapeRecallMySQLHelper.COL_FILE_NAME));
        task.setPinLifetime(res.getInt(TapeRecallMySQLHelper.COL_PIN_LIFETIME));
        task.setStatusId(res.getInt(TapeRecallMySQLHelper.COL_STATUS));
        task.setVoName(res.getString(TapeRecallMySQLHelper.COL_VO_NAME));
        task.setUserID(res.getString(TapeRecallMySQLHelper.COL_USER_ID));
        task.setRetryAttempt(res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT));

        return taskId;
    }
}
