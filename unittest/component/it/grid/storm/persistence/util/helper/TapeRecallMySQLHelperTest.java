package component.it.grid.storm.persistence.util.helper;


import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.persistence.util.helper.TapeRecallMySQLHelper;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class TapeRecallMySQLHelperTest {


    TapeRecallMySQLHelper helper = new TapeRecallMySQLHelper("mysql");
    private final int NUM_TASKS = 2;

    static List<TapeRecallTO> taskList = new LinkedList<TapeRecallTO>();
    static List<TapeRecallTO> takeoverTaskList = new LinkedList<TapeRecallTO>();
    static UUID taskId = null;
    static String requestToken;
    static UUID groupTaskId;
    int[] statuses = new int[] { 1, 2 };
    String voName = "dteam";
    int successStatus = 0;
    int inProgressStatus = 2;
    int value = 3;
    int maxNumTasks = 5;

    //queued = 4
    //success = 5
    //in_progress = 4
    
    @Test
    public final void testTESTgetQueryInsertTasks() {
        for (int i = 0; i < NUM_TASKS; i++) {
            // NUM_TASKS random tasks
            testTESTgetQueryInsertTask();
        }
        for (int i = 0; i < 2; i++) {
            // other 2 task queued equals to the first random
            testTESTgetQueryInsertTask(taskList.get(0));
        }
        for(TapeRecallTO queuedTask : taskList)
        {
            takeoverTaskList.add(queuedTask);
        }
        for (int i = 0; i < 1; i++) {
            // other 1 task in success state equals to the first random
            testTESTgetQueryInsertTaskSuccess(taskList.get(0));
        }
        // an in progress task
        TapeRecallTO inProgressTask = testTESTgetQueryInsertTaskInProgress();
        for (int i = 0; i < 2; i++) {
            // other 2 task in progress equals to the one created
            testTESTgetQueryInsertTaskInProgress(inProgressTask);
        }
        UUID successTaksGroupId = UUID.randomUUID();
        for (int i = 0; i < 2; i++) {
            // other 2 task in success state equals to the one in progress
            // on the same groupTask
            
            testTESTgetQueryInsertTaskSuccess(inProgressTask, successTaksGroupId);
        }
        // another in progress task
        testTESTgetQueryInsertTaskInProgress();
        TapeRecallTO  successTask = testTESTgetQueryInsertTaskSuccess();
        for (int i = 0; i < 1; i++) {
            // other 1 task in success state equals to the one success
            testTESTgetQueryInsertTaskSuccess(successTask);
        }
//        for (int i = 0; i < 1; i++) {
//            testTESTgetQueryInsertTask(taskList.get(1));
//        }
//        for (int i = 0; i < 4; i++) {
//            testTESTgetQueryInsertTask(taskList.get(2));
//        }
    }


    public final TapeRecallTO testTESTgetQueryInsertTask() {
        TapeRecallTO task = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        System.out.println(TESTgetQueryInsertTask(task) + ";");
        System.out.println();
        taskList.add(task);
        if (taskId == null) {
            taskId = task.getTaskId();
            requestToken = task.getRequestTokenStr();
            groupTaskId = task.getGroupTaskId();
        }
        return task;
    }


    private TapeRecallTO testTESTgetQueryInsertTaskInProgress() {
        TapeRecallTO task = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        task.setStatus(TapeRecallStatus.IN_PROGRESS);
        System.out.println(TESTgetQueryInsertTask(task) + ";");
        System.out.println();
        taskList.add(task);
        if (taskId == null) {
            taskId = task.getTaskId();
            requestToken = task.getRequestTokenStr();
            groupTaskId = task.getGroupTaskId();
        }
        return task;
    }


    public final void testTESTgetQueryInsertTask(TapeRecallTO task) {
        TapeRecallTO newTask = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        newTask.setFileName(task.getFileName());
        newTask.setGroupTaskId(task.getGroupTaskId());
        System.out.println(TESTgetQueryInsertTask(newTask) + ";");
        System.out.println();
        taskList.add(newTask);
    }


    public final TapeRecallTO testTESTgetQueryInsertTaskSuccess() {
        TapeRecallTO newTask = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        newTask.setStatus(TapeRecallStatus.SUCCESS);
        System.out.println(TESTgetQueryInsertTask(newTask) + ";");
        System.out.println();
        taskList.add(newTask);
        return newTask;
    }
    
    public final void testTESTgetQueryInsertTaskSuccess(TapeRecallTO task) {
        TapeRecallTO newTask = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        newTask.setFileName(task.getFileName());
        newTask.setStatus(TapeRecallStatus.SUCCESS);
        System.out.println(TESTgetQueryInsertTask(newTask) + ";");
        System.out.println();
        taskList.add(newTask);
    }

    private void testTESTgetQueryInsertTaskSuccess(TapeRecallTO task, UUID groupTaskId) {
        TapeRecallTO newTask = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        newTask.setFileName(task.getFileName());
        newTask.setGroupTaskId(groupTaskId);
        newTask.setStatus(TapeRecallStatus.SUCCESS);
        System.out.println(TESTgetQueryInsertTask(newTask) + ";");
        System.out.println();
        taskList.add(newTask);
    }


    public final void testTESTgetQueryInsertTaskInProgress(TapeRecallTO task) {
        TapeRecallTO newTask = TapeRecallTO.createRandom(Calendar.getInstance().getTime(), voName);
        newTask.setFileName(task.getFileName());
        newTask.setStatus(TapeRecallStatus.IN_PROGRESS);
        System.out.println(TESTgetQueryInsertTask(newTask) + ";");
        System.out.println();
        taskList.add(newTask);
    }
    String TABLE_NAME = "tape_recall";

    public String TESTgetQueryInsertTask(TapeRecallTO recallTask) {

        if (recallTask == null) {
            return null;
        }

        String query = "INSERT INTO "+TABLE_NAME+
                             " ("+TapeRecallMySQLHelper.COL_TASK_ID+", "+
                             TapeRecallMySQLHelper.COL_REQUEST_TOKEN+", "+
                             TapeRecallMySQLHelper.COL_REQUEST_TYPE+", "+
                             TapeRecallMySQLHelper.COL_FILE_NAME+", "+
                             TapeRecallMySQLHelper.COL_PIN_LIFETIME+", "+
                             TapeRecallMySQLHelper.COL_STATUS+", "+
                             TapeRecallMySQLHelper.COL_VO_NAME+", "+
                             TapeRecallMySQLHelper.COL_USER_ID+", "+
                             TapeRecallMySQLHelper.COL_RETRY_ATTEMPT+", "+
                             TapeRecallMySQLHelper.COL_DEFERRED_STARTTIME+", "+
                             TapeRecallMySQLHelper.COL_DATE+", "+
                             TapeRecallMySQLHelper.COL_GROUP_TASK_ID+") VALUES ("+
                             formatString(recallTask.getTaskId().toString())+"," +
                                    " "+ formatString(recallTask.getRequestToken().getValue())+"," +
                                    " "+formatString(recallTask.getRequestType())+"," +
                                    " "+formatString(recallTask.getFileName())+"," +
                                    " "+recallTask.getPinLifetime()+"," +
                                    " "+recallTask.getStatusId()+"," +
                                    " "+formatString(recallTask.getVoName())+"," +
                                    " "+formatString(recallTask.getUserID())+"," +
                                    " "+recallTask.getRetryAttempt()+"," +
                                    " "+helper.format(new java.sql.Timestamp(recallTask.getDeferredRecallInstant().getTime()))+"," +
                                    " "+helper.format(new java.sql.Timestamp(recallTask.getInsertionInstant().getTime()))+"," +
                                    " "+formatString(recallTask.getGroupTaskId().toString())+
                                    ")" ;
        return query;
    }
    

    private String formatString(String s) {
        if (s == null) {
            return null;
        }
        return "'" + s + "'";
    }
    
    @Test
    public final void testGetQueryGetTask() {
        System.out.println("testGetQueryGetTask " + helper.getQueryGetTask(taskId, requestToken) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryGetGroupTasks() {
        System.out.println("testGetQueryGetGroupTasks " + helper.getQueryGetGroupTasks(groupTaskId) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryGetGroupTaskIdsUUID() {
        System.out.println("testGetQueryGetGroupTaskIdsUUID " + helper.getQueryGetGroupTaskIds(taskId) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryGetGroupTaskIdsUUIDIntArray() {
        System.out.println("testGetQueryGetGroupTaskIdsUUIDIntArray " + helper.getQueryGetGroupTaskIds(taskId, statuses) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryNumberQueued() {
        System.out.println("testGetQueryNumberQueued " + helper.getQueryNumberQueued() + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryNumberQueuedString() {
        System.out.println("testGetQueryNumberQueuedString " + helper.getQueryNumberQueued(voName) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryReadyForTakeOver() {
        System.out.println("testGetQueryReadyForTakeOver " + helper.getQueryReadyForTakeOver() + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryReadyForTakeOverString() {
        System.out.println("testGetQueryReadyForTakeOverString " + helper.getQueryReadyForTakeOver(voName) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryNumberInProgress() {
        System.out.println("testGetQueryNumberInProgress " + helper.getQueryNumberInProgress() + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryNumberInProgressString() {
        System.out.println("testGetQueryNumberInProgressString " + helper.getQueryNumberInProgress(voName) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryGetTakeoverTasksWithDoublesInt() {
        System.out.println("testGetQueryGetTakeoverTasksInt " + helper.getQueryGetTakeoverTasksWithDoubles(takeoverTaskList.size()) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryGetTakeoverTasksWithDoublesIntString() {
        System.out.println("testGetQueryGetTakeoverTasksIntString " + helper.getQueryGetTakeoverTasksWithDoubles(takeoverTaskList.size(), voName) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQuerySetTakeoverTasks() {
        System.out.println("testGetQuerySetTakeoverTasks "
                + helper.getQueryUpdateTasksStatus(takeoverTaskList,
                                                   inProgressStatus,
                                                   TapeRecallMySQLHelper.COL_IN_PROGRESS_DATE,
                                                   new Date()) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQuerySetGroupTaskStatus() {
        System.out.println("testGetQuerySetGroupTaskStatus " + helper.getQuerySetGroupTaskStatus(takeoverTaskList.get(0).getGroupTaskId(), successStatus) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQuerySetGroupTaskRetryValue() {
        System.out.println("testGetQuerySetGroupTaskRetryValue " + helper.getQuerySetGroupTaskRetryValue(takeoverTaskList.get(0).getGroupTaskId(), value) + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryDeleteCompletedTasks() {
        System.out.println("testGetQueryDeleteCompletedTasks " + helper.getQueryDeleteCompletedTasks() + ";");
        System.out.println();
    }


    @Test
    public final void testGetQueryDeleteCompletedTasksInt() {
        System.out.println("testGetQueryDeleteCompletedTasksInt " + helper.getQueryDeleteCompletedTasks(maxNumTasks) + ";");
    }
}
