package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.db.SQLHelper;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TapeRecallMySQLHelper extends SQLHelper {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TapeRecallMySQLHelper.class);

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

    public String getQueryGetRequestToken(int taskId) {

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_REQUEST_TOKEN, TABLE_NAME, COL_TASK_ID, taskId);
    }

    public String getQueryGetRetryValue(int taskId) {

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_RETRY_ATTEMPT, TABLE_NAME, COL_TASK_ID, taskId);
    }

    public String getQueryGetTask(int taskId) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%s";

        return String.format(queryFormat, TABLE_NAME, COL_TASK_ID, taskId);
    }

    public PreparedStatement getQueryInsertTask(Connection conn, RecallTaskTO recallTask) {

        if (recallTask == null) {
            return null;
        }

        String queryFormat = "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String query = String.format(queryFormat,
                                     TABLE_NAME,
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
            
            int idx = 1;
            
            prepStat.setString(idx++, recallTask.getRequestToken());
            prepStat.setString(idx++, recallTask.getRequestType());
            prepStat.setString(idx++, recallTask.getFileName());
            prepStat.setInt(idx++, recallTask.getPinLifetime());
            prepStat.setInt(idx++, RecallTaskStatus.QUEUED.getStatusId());

            prepStat.setString(idx++, recallTask.getVoName());
            prepStat.setString(idx++, recallTask.getUserID());
            prepStat.setInt(idx++, recallTask.getRetryAttempt());
            prepStat.setTimestamp(idx++, new java.sql.Timestamp(recallTask.getDeferredRecallInstant().getTime()));
            prepStat.setTimestamp(idx++, new java.sql.Timestamp(recallTask.getInsertionInstant().getTime()));

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

    public String getQueryPurgeCompletedTasks() {

        String queryFormat = "DELETE FROM %s WHERE %s<>%d AND %s<>%d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId());
    }

    public String getQueryPurgeCompletedTasks(int maxNumTasks) {

        String queryFormat = "DELETE FROM %s WHERE %s != %d AND %s != %d LIMIT %d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId(),
                             maxNumTasks);
    }

    public String getQueryRetrieveTaskStatus(int taskId) {

        String queryFormat = "SELECT %s FROM %s WHERE %s=%s";

        return String.format(queryFormat, COL_STATUS, TABLE_NAME, COL_TASK_ID, taskId);
    }

    public String getQuerySetRetryValue(int taskId, int value) {

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_RETRY_ATTEMPT,
                             value,
                             COL_TASK_ID,
                             taskId);
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d AND %s<=NOW() ORDER BY %s LIMIT %d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_DEFERRED_STARTTIME,
                             COL_DEFERRED_STARTTIME,
                             numberOfTasks);
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks, String voName) {

        String queryFormat = "SELECT * FROM %s WHERE %s=%d AND %s=%s AND %s<=NOW() ORDER BY %s LIMIT %d";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.QUEUED.getStatusId(),
                             COL_VO_NAME,
                             formatString(voName),
                             COL_DEFERRED_STARTTIME,
                             COL_DEFERRED_STARTTIME,
                             numberOfTasks);
    }

    public String getQueryTakeoverTasksUpdate(List<Integer> taskIdArray) {

        if (taskIdArray.size() == 0) {
            return null;
        }

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";
        String whereClauseFormat = " OR " + COL_TASK_ID + "=%s";

        StringBuffer sb = new StringBuffer(String.format(queryFormat,
                                                         TABLE_NAME,
                                                         COL_STATUS,
                                                         RecallTaskStatus.IN_PROGRESS.getStatusId(),
                                                         COL_TASK_ID,
                                                         taskIdArray.get(0)));

        for (int i = 1; i < taskIdArray.size(); i++) {
            sb.append(String.format(whereClauseFormat, taskIdArray.get(i)));
        }

        return sb.toString();
    }

    public String getQueryTakeoverTaskUpdate(int taskId) {

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat,
                             TABLE_NAME,
                             COL_STATUS,
                             RecallTaskStatus.IN_PROGRESS.getStatusId(),
                             COL_TASK_ID,
                             taskId);
    }

    public String getQueryUpdateTaskStatus(int taskId, int status) {

        String queryFormat = "UPDATE %s SET %s=%d WHERE %s=%s";

        return String.format(queryFormat, TABLE_NAME, COL_STATUS, status, COL_TASK_ID, taskId);
    }

    private String formatString(String s) {
        if (s == null) {
            return null;
        }
        return "'" + s + "'";
    }
}
