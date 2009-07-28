package it.grid.storm.persistence.impl.mysql;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallDAOMySql extends TapeRecallDAO {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TapeRecallDAOMySql.class);

    private TapeRecallMySQLHelper sqlHelper;

    public TapeRecallDAOMySql() {
        sqlHelper = new TapeRecallMySQLHelper(PersistenceDirector.getDataBase().getDbmsVendor());
    }

    public static void main(String[] args) throws DataAccessException {

        TapeRecallDAO trDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();

        RecallTaskTO rtTO = new RecallTaskTO();

        rtTO.setFileName("pippo_00");
        rtTO.setRequestToken("toke_00");
        rtTO.setStatus(TapeRecallDAO.IN_PROGRESS);
        rtTO.setVoName("infngrid");

        String taskId = trDAO.insertTask(rtTO);

        int status = trDAO.getTaskStatus(taskId);

        System.out.println("GOT STATUS " + status + " for taskId=" + taskId);
    }

    public List<RecallTaskTO> getInProgressTask() throws DataAccessException {
        return getInProgressTask(null);
    }

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

    public int getNumberInProgress() throws DataAccessException {

        return getNumberInProgress(null);

    }

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

    public int getNumberQueued() throws DataAccessException {
        return getNumberQueued(null);
    }

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

    public String getRequestToken(String taskId) throws DataAccessException {

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

    public int getRetryValue(String taskId) throws DataAccessException {

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

    public RecallTaskTO getTask(String taskId) throws DataAccessException {

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

    public int getTaskStatus(String taskId) throws DataAccessException {

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

    public String insertTask(RecallTaskTO task) throws DataAccessException {

        String taskId = UUID.randomUUID().toString();
        task.setTaskId(taskId);

        String query = sqlHelper.getQueryInsertTask(task);

        Connection dbConnection = getConnection();
        Statement statment = getStatement(dbConnection);

        try {

            statment.executeUpdate(query);

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        }

        releaseConnection(null, statment, dbConnection);
        return taskId;
    }

    public void setRetryValue(String taskId, int value) throws DataAccessException {

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

    public RecallTaskTO takeoverTask() throws DataAccessException {
        return takeoverTask(null);
    }

    public RecallTaskTO takeoverTask(String voName) throws DataAccessException {

        List<RecallTaskTO> taskList = takeoverTasks(1, voName);

        if (taskList.isEmpty()) {
            return null;
        }

        return taskList.get(0);
    }

    public List<RecallTaskTO> takeoverTasks(int numberOfTaks) throws DataAccessException {
        return takeoverTasks(numberOfTaks, null);
    }

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

            statment.executeUpdate("START TRANSACTION");

            res = statment.executeQuery(query);

            if (res == null) {
                return taskList;
            }

            List<String> taskIdList = new LinkedList<String>();

            while (res.next()) {
                task = new RecallTaskTO();

                String taskId = setTaskInfo(task, res);

                taskList.add(task);
                taskIdList.add(taskId);
            }

            if (taskIdList.isEmpty()) {

                statment.executeUpdate("COMMIT");

            } else {

                query = sqlHelper.getQueryTakeoverTasksUpdate(taskIdList);
                statment.executeUpdate(query);

                statment.executeUpdate("COMMIT");
            }

        } catch (SQLException e) {

            throw new DataAccessException("Error executing query: " + query, e);

        } finally {

            releaseConnection(res, statment, dbConnection);
        }

        return taskList;
    }

    protected boolean setTaskStatusDBImpl(String taskId, int status) throws DataAccessException {

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

    private String setTaskInfo(RecallTaskTO task, ResultSet res) throws SQLException {

        String taskId = res.getString(TapeRecallMySQLHelper.COL_TASK_ID);

        task.setTaskId(taskId);
        task.setRequestToken(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TOKEN));
        task.setRequestType(res.getString(TapeRecallMySQLHelper.COL_REQUEST_TYPE));
        task.setFileName(res.getString(TapeRecallMySQLHelper.COL_FILE_NAME));
        task.setPinLifetime(res.getInt(TapeRecallMySQLHelper.COL_PIN_LIFETIME));
        task.setStatus(res.getInt(TapeRecallMySQLHelper.COL_STATUS));
        task.setVoName(res.getString(TapeRecallMySQLHelper.COL_VO_NAME));
        task.setUserID(res.getString(TapeRecallMySQLHelper.COL_USER_ID));
        task.setRetryAttempt(res.getInt(TapeRecallMySQLHelper.COL_RETRY_ATTEMPT));

        return taskId;
    }
}
