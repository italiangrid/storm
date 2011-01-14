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

package it.grid.storm.persistence.util.helper;

import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.persistence.util.db.SQLHelper;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

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

        String queryFormat = "SELECT * FROM "+TABLE_NAME+
                             " WHERE "+COL_STATUS+"="+RecallTaskStatus.IN_PROGRESS.getStatusId()+
                             " AND "+COL_VO_NAME+"="+formatString(voName);

        return queryFormat;
    }

    
    public String getQueryGetRequestToken(UUID taskId) {

        String queryFormat = "SELECT "+COL_REQUEST_TOKEN+" FROM "+ TABLE_NAME+" WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return queryFormat;
    }

    public String getQueryGetRetryValue(UUID taskId) {

        String queryFormat = "SELECT "+COL_RETRY_ATTEMPT+" FROM "+TABLE_NAME+" WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return queryFormat;
    }

    public String getQueryGetTask(UUID taskId) {

        String queryFormat = "SELECT * FROM "+TABLE_NAME+" WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return queryFormat;
    }

    public PreparedStatement getQueryInsertTask(Connection conn, RecallTaskTO recallTask) {

        if (recallTask == null) {
            return null;
        }

        String query = "INSERT INTO "+TABLE_NAME+
                             " ("+COL_TASK_ID+", "+
                             COL_REQUEST_TOKEN+", "+
                             COL_REQUEST_TYPE+", "+
                             COL_FILE_NAME+", "+
                             COL_PIN_LIFETIME+", "+
                             COL_STATUS+", "+
                             COL_VO_NAME+", "+
                             COL_USER_ID+", "+
                             COL_RETRY_ATTEMPT+", "+
                             COL_DEFERRED_STARTTIME+", "+
                             COL_DATE+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
                             
       
        try {
            PreparedStatement prepStat = conn.prepareStatement(query);
            
            int idx = 1;
            prepStat.setString(idx++, recallTask.getTaskId().toString());
            prepStat.setString(idx++, recallTask.getRequestToken().getValue());
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

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.IN_PROGRESS.getStatusId();

        return queryFormat;
    }

    public String getQueryNumberInProgress(String voName) {

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+ COL_STATUS+"="+
                             RecallTaskStatus.IN_PROGRESS.getStatusId()+" AND "+COL_VO_NAME+"="+formatString(voName);

        return queryFormat;
    }

    public String getQueryNumberQueued() {

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId();

        return queryFormat;
    }
    
    public String getQueryNumberQueued(String voName) {

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId()+
                             " AND "+COL_VO_NAME+"="+formatString(voName);

        return queryFormat;
    }

    public String getQueryPurgeCompletedTasks() {

        String queryFormat = "DELETE FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"<>"+RecallTaskStatus.QUEUED.getStatusId()+
                              " AND "+COL_STATUS+"<>"+ RecallTaskStatus.IN_PROGRESS.getStatusId();

        return queryFormat;
    }
    
    public String getQueryPurgeCompletedTasks(int maxNumTasks) {

        String queryFormat = "DELETE FROM "+TABLE_NAME+" WHERE "+COL_STATUS+" != "+RecallTaskStatus.QUEUED.getStatusId()+
                              " AND "+COL_STATUS+" != "+RecallTaskStatus.IN_PROGRESS.getStatusId()+" LIMIT "+maxNumTasks;

        return queryFormat;
    }

    public String getQueryReadyForTakeOver() {

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId()+" AND "+ COL_DEFERRED_STARTTIME+"<=NOW()";

        return queryFormat;
    }

    public String getQueryReadyForTakeOver(String voName) {

        String queryFormat = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId()+ 
                             " AND "+COL_VO_NAME+"="+ formatString(voName)+" AND "+COL_DEFERRED_STARTTIME+"<=NOW()";

        return queryFormat;
    }

    public String getQueryRetrieveTaskId(String requestToken, String pfn) {

        String queryFormat = "SELECT "+COL_TASK_ID+" FROM "+TABLE_NAME+" WHERE "+COL_REQUEST_TOKEN+"="+formatString(requestToken)+" AND "+COL_FILE_NAME+"='"+pfn+"'";

        return queryFormat;
    }
    
    public String getQueryRetrieveTaskStatus(UUID taskId) {

        String queryFormat = "SELECT "+COL_STATUS+" FROM "+TABLE_NAME+" WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return String.format(queryFormat, COL_STATUS, TABLE_NAME, COL_TASK_ID, taskId);
    }

    public String getQuerySetRetryValue(UUID taskId, int value) {

        String queryFormat = "UPDATE "+TABLE_NAME+" SET "+COL_RETRY_ATTEMPT+"="+value+" WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return queryFormat;
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks) {

        String queryFormat = "SELECT * FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId() +
                             " AND "+COL_DEFERRED_STARTTIME+"<=NOW() ORDER BY "+ COL_DEFERRED_STARTTIME +
                             " LIMIT "+numberOfTasks;

        return queryFormat;
    }

    public String getQueryTakeoverTasksSelect(int numberOfTasks, String voName) {

        String queryFormat = "SELECT * FROM "+TABLE_NAME+" WHERE "+COL_STATUS+"="+RecallTaskStatus.QUEUED.getStatusId() + 
                             " AND "+COL_VO_NAME+"="+formatString(voName)+
                             " AND "+COL_DEFERRED_STARTTIME+"<=NOW() ORDER BY "+COL_DEFERRED_STARTTIME+
                             " LIMIT"+numberOfTasks;

        return queryFormat;
    }

    public String getQueryTakeoverTasksUpdate(List<UUID> taskIdArray) {

        if (taskIdArray.size() == 0) {
            return null;
        }

        String queryFormat = "UPDATE "+TABLE_NAME+" SET "+COL_STATUS+"="+RecallTaskStatus.IN_PROGRESS.getStatusId()+
                             " WHERE "+COL_TASK_ID+"="+ formatString(taskIdArray.get(0).toString());     
        
        for (int i = 1; i < taskIdArray.size(); i++) {
            queryFormat += " OR " + COL_TASK_ID + "="+formatString(taskIdArray.get(i).toString());
        }

        return queryFormat;
    }

    public String getQueryTakeoverTaskUpdate(UUID taskId) {

        String queryFormat = "UPDATE "+TABLE_NAME+" SET "+COL_STATUS+"="+ RecallTaskStatus.IN_PROGRESS.getStatusId()+
                             " WHERE "+COL_TASK_ID+"="+formatString(taskId.toString());

        return queryFormat;
    }

    public String getQueryUpdateTaskStatus(UUID taskId, int status) {

        String queryFormat = "UPDATE "+TABLE_NAME+" SET "+COL_STATUS+"="+status+
                             " WHERE "+COL_TASK_ID+"="+formatString(taskId.toString()) + " AND "+COL_STATUS+"!="+status;

        return queryFormat;
    }

    private String formatString(String s) {
        if (s == null) {
            return null;
        }
        return "'" + s + "'";
    }
}
