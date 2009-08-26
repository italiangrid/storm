package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.db.SQLHelper;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TapeRecallMySQLHelper extends SQLHelper {

    private final static String TABLE_NAME = "tape_recall";

    public final static String COL_TASK_ID = "taskId";
    public final static String COL_REQUEST_TOKEN = "requestToken";
    public final static String COL_REQUEST_TYPE = "requestType";
    public final static String COL_FILE_NAME = "fileName";
    public final static String COL_PIN_LIFETIME = "pinLifetime";
    public final static String COL_STATUS = "status";
    public final static String COL_USER_ID = "userID";
    public final static String COL_VO_NAME = "voName";
    public final static String COL_DATE = "timeStamp";
    public final static String COL_RETRY_ATTEMPT = "retryAttempt";
    public final static String COL_DEFERRED_STARTTIME = "deferredStartTime";

    public TapeRecallMySQLHelper(String dbmsVendor) {
        super(dbmsVendor);
    }

    public String getQueryGetInprograssTask() {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d";

        return String.format(queryFormat, TABLE_NAME, COL_STATUS, RecallTaskStatus.IN_PROGRESS.getStatusId());
    }

    public String getQueryGetInprograssTask(String voName) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d AND %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId(),
                             COL_VO_NAME,
                             formatString(voName));
    }

    public String getQueryGetRequestToken(String taskId) {

        if (taskId == null) {
            return null;
        }

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_REQUEST_TOKEN, TABLE_NAME, COL_TASK_ID, formatString(taskId));
    }

    public String getQueryGetRetryValue(String taskId) {

        if (taskId == null) {
            return null;
        }

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_RETRY_ATTEMPT, TABLE_NAME, COL_TASK_ID, formatString(taskId));
    }

    public String getQueryGetTask(String taskId) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%s";

        return String.format(queryFormat, TABLE_NAME, COL_TASK_ID, formatString(taskId));
    }

    public PreparedStatement getQueryInsertTask(Connection conn, RecallTaskTO recallTask) {

        if (recallTask == null) {
            return null;
        }

        //String queryFormat = "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (%s, %s, %s, %s, %d, %d, %s, %s, %d, CURRENT_TIMESTAMP)";
        
        String queryFormat = "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        String query = String.format(queryFormat, TABLE_NAME,
                                     COL_TASK_ID,
                                     COL_REQUEST_TOKEN,
                                     COL_REQUEST_TYPE,
                                     COL_FILE_NAME,
                                     COL_PIN_LIFETIME,
                                     COL_STATUS,
                                     COL_VO_NAME,
                                     COL_USER_ID,
                                     COL_RETRY_ATTEMPT,
                                     COL_DEFERRED_STARTTIME,
                                     COL_DATE);

        try {
            PreparedStatement prepStat = conn.prepareStatement(query);
            
            prepStat.setString(1, recallTask.getTaskId());
            prepStat.setString(2, recallTask.getRequestToken());
            prepStat.setString(3, recallTask.getRequestType());
            prepStat.setString(4, recallTask.getFileName());
            prepStat.setInt(5, recallTask.getPinLifetime());
            prepStat.setInt(6, RecallTaskStatus.QUEUED.getStatusId());
            
            prepStat.setString(7, recallTask.getVoName());
            prepStat.setString(8, recallTask.getUserID());
            prepStat.setInt(9, recallTask.getRetryAttempt());
            prepStat.setDate(10, new java.sql.Date(recallTask.getDeferredRecallInstant().getTime()));
            prepStat.setDate(10, new java.sql.Date(recallTask.getInsertionInstant().getTime()));
            
            return prepStat;
            
        } catch (SQLException e) {
            return null;
        }
    }

    public String getQueryNumberInProgress() {

        String queryFormat = "SELECT COUNT(*) FROM %s WHERE %s=%d";

        return String.format(queryFormat, TABLE_NAME, COL_STATUS, RecallTaskStatus.IN_PROGRESS.getStatusId());
    }

    public String getQueryNumberInProgress(String voName) {

        String queryFormat = "SELECT COUNT(*) FROM %s WHERE %s=%d AND %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId(),
                             COL_VO_NAME,
                             formatString(voName));
    }

    public String getQueryNumberQueued() {

        String queryFormat = "SELECT COUNT(*) FROM %s WHERE %s=%d";

        return String.format(queryFormat, TABLE_NAME, COL_STATUS, RecallTaskStatus.QUEUED.getStatusId());
    }

    public String getQueryNumberQueued(String voName) {

        String queryFormat = "SELECT COUNT(*) FROM %s WHERE %s=%d AND %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_VO_NAME,
                             formatString(voName));
    }

    public String getQueryRetrieveTaskStatus(String taskId) {

        if (taskId == null) {
            return null;
        }

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_STATUS, TABLE_NAME, COL_TASK_ID, formatString(taskId));
    }

    public String getQuerySetRetryValue(String taskId, int value) {

        if (taskId == null) {
            return null;
        }

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_RETRY_ATTEMPT,
                             value,
                             COL_TASK_ID,
                             formatString(taskId));
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d ORDER BY %s LIMIT %d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_DATE,
                             numberOfTasks);
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks, String voName) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d AND %s=%s ORDER BY %s LIMIT %d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_VO_NAME,
                             formatString(voName),
                             COL_DATE,
                             numberOfTasks);
    }

    public String getQueryTakeoverTasksUpdate(List<String> taskIdList) {

        if (taskIdList.isEmpty()) {
            return null;
        }

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";
        String whereClauseFormat = " OR " + COL_TASK_ID + "=%s";

        StringBuffer sb = new StringBuffer(String.format(queryFormat,
                                                         TABLE_NAME,
                                                         COL_STATUS,
                                                         RecallTaskStatus.IN_PROGRESS.getStatusId(),
                                                         COL_TASK_ID,
                                                         formatString(taskIdList.get(0))));

        for (int i = 1; i < taskIdList.size(); i++) {
            sb.append(String.format(whereClauseFormat, formatString(taskIdList.get(i))));
        }

        return sb.toString();
    }

    public String getQueryTakeoverTaskUpdate(String taskId) {

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId(),
                             COL_TASK_ID,
                             formatString(taskId));
    }

    public String getQueryUpdateTaskStatus(String taskId, int status) {

        if (taskId == null) {
            return null;
        }

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat, TABLE_NAME, COL_STATUS, status, COL_TASK_ID, formatString(taskId));
    }

    private String formatString(String s) {
        if (s == null) {
            return null;
        }
        return "'" + s + "'";
    }
}
