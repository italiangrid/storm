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
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;
import it.grid.storm.tape.recalltable.persistence.TapeRecallBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
@Path("/recalltable/tasks")
public class TasksResource {

    private static final Logger log = LoggerFactory.getLogger(TasksResource.class);
    private static Configuration config = Configuration.getInstance();

    @PUT
    @Path("/")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putTakeoverTasks(InputStream input) throws TapeRecallException {

        // Retrieve the Input String
        String inputStr = buildInputString(input);
        log.debug("@PUT (input string) = '" + inputStr + "'");

        // Recall Table Catalog
        TapeRecallCatalog rtCat = null;

        try {
            rtCat = new TapeRecallCatalog();
        } catch (DataAccessException e) {
            log.error("Unable to use RecallTable DB.");
            throw new TapeRecallException("Unable to use RecallTable DB.");
        }

        // Retrieve the number of tasks to takeover (default = 1)
        int numbOfTask = 1;
        // Retrieve value from Body param
        String keyTakeover = config.getTaskoverKey();
        int eqIndex = inputStr.indexOf('=');

        if (eqIndex > 0) {
            String value = inputStr.substring(eqIndex);
            String key = inputStr.substring(0, eqIndex);
            if (key.equals(keyTakeover)) {
                try {
                    // trim out the '\n' end.
                    numbOfTask = Integer.valueOf(value.substring(1, value.length() - 1));
                } catch (NumberFormatException e) {
                    throw new TapeRecallException("Unable to understand the number value = '" + value + "'");
                }
            }
        }

        // Retrieve the Tasks
        ArrayList<TapeRecallTO> tasks = rtCat.takeoverNTasksWithDoubles(numbOfTask);
        HashMap<UUID, ArrayList<TapeRecallTO>> groupTaskMap = buildGroupTaskMap(tasks);
        
        ArrayList<TapeRecallTO> groupTasks = new ArrayList<TapeRecallTO>();
        for (ArrayList<TapeRecallTO> groupTaskList : groupTaskMap.values())
        {
            try
            {
                groupTasks.add(makeOne(groupTaskList));
            }
            catch (IllegalArgumentException e)
            {
                log.error("Unable to makeOne the task list . IllegalArgumentException : " + e.getMessage());
                log.error("Erroneous task list (long output): " + groupTaskList.toString());
                log.error("Skip the erroneous task list and go on...Please contact StoRM support");
            }
        }
        if(tasks.size() > groupTasks.size())
        {
            log.debug("Taking over some multy-group tasks");
        }
        log.debug("Number of tasks recalled : <" + groupTasks.size() + "> over <" + tasks.size() + "> tasks requested");

        // Build the response
        String result = buildTakeoverTasksResponse(groupTasks);
        return result;
    }

    /**
     * Creates a map with the taskIds as keys and the list of tasks related to each taskId (key)
     * as value
     * 
     * @param tasks
     * @return
     */
    private HashMap<UUID, ArrayList<TapeRecallTO>> buildGroupTaskMap(ArrayList<TapeRecallTO> tasks)
    {
        HashMap<UUID, ArrayList<TapeRecallTO>> groupTaskMap = new HashMap<UUID, ArrayList<TapeRecallTO>>();
        for (TapeRecallTO task : tasks)
        {
            ArrayList<TapeRecallTO> taskList = groupTaskMap.get(task.getGroupTaskId());
            if (taskList == null)
            {
                taskList = new ArrayList<TapeRecallTO>();
                groupTaskMap.put(task.getGroupTaskId(), taskList);
            }
            taskList.add(task);
        }
        return groupTaskMap;
    }

    /**
     * Given a list of tasks with the same taskId oproduces a single task 
     * merging the list members
     * 
     * @param recallTasks
     * @return
     */
    private TapeRecallTO makeOne(ArrayList<TapeRecallTO> recallTasks) throws IllegalArgumentException
    {
        TapeRecallTO taskTO = new TapeRecallTO();
        UUID taskId = recallTasks.get(0).getTaskId();
        //verify that all have the same task id
        for(TapeRecallTO recallTask : recallTasks)
        {
            if(!recallTask.getTaskId().equals(taskId))
            {
                log.error("Received a list of not omogeneous tasks, the taskid \'" + taskId + "\' is not matched by : " + recallTask.toString());
                throw new IllegalArgumentException("Received a list of not omogeneous tasks");
            }
        }
        for(TapeRecallTO recallTask : recallTasks)
        {
            //set common fields from any of the tasks
            taskTO.setTaskId(recallTask.getTaskId());
            taskTO.setGroupTaskId(recallTask.getGroupTaskId());
            taskTO.setRequestToken(recallTask.getRequestToken());
            taskTO.setRequestType(recallTask.getRequestType());
            taskTO.setFileName(recallTask.getFileName());
            taskTO.setUserID(recallTask.getUserID());
            taskTO.setVoName(recallTask.getVoName());
            taskTO.setStatus(TapeRecallStatus.QUEUED);
            break;
        }
        
        /* merge task on recall related fields to have a pin that starts as soon as requested and last
         * as long as needed
         */
        int maxRetryAttempt = 0;
        Date minInsertionInstant = null;
        Date minDeferredRecallInstant = null;
        Date maxPinExpirationInstant = null;
        for(TapeRecallTO recallTask : recallTasks)
        {
            if(recallTask.getRetryAttempt() > maxRetryAttempt)
            {
                maxRetryAttempt = recallTask.getRetryAttempt(); 
            }
            if(minInsertionInstant == null || recallTask.getInsertionInstant().before(minInsertionInstant))
            {
                minInsertionInstant = recallTask.getInsertionInstant(); 
            }
            if(minDeferredRecallInstant == null || recallTask.getDeferredRecallInstant().before(minDeferredRecallInstant))
            {
                minDeferredRecallInstant = recallTask.getDeferredRecallInstant(); 
            }
            Date currentPinExpirationInstant = new Date(recallTask.getDeferredRecallInstant().getTime() + (recallTask.getPinLifetime() * 1000));
            if(maxPinExpirationInstant == null || currentPinExpirationInstant.after(maxPinExpirationInstant))
            {
                maxPinExpirationInstant = currentPinExpirationInstant;
            }
        }
        taskTO.setRetryAttempt(maxRetryAttempt);
        taskTO.setInsertionInstant(minInsertionInstant);
        taskTO.setDeferredRecallInstant(minDeferredRecallInstant);
        int pinLifeTime = (int)(maxPinExpirationInstant.getTime() - minDeferredRecallInstant.getTime()) / 1000;
        taskTO.setPinLifetime(pinLifeTime);
        return taskTO;
    }

    /**
     * @param tasks
     * @return
     */
    private String buildTakeoverTasksResponse(ArrayList<TapeRecallTO> tasks) {
        String result = "{";
        for (TapeRecallTO recallTaskTO : tasks) {
            result += recallTaskTO.toGEMSS() + TapeRecallBuilder.elementSep;
        }
        result += "}";
        return result;
    }

    /**
     * UTILITY METHODS
     */

    private String buildInputString(InputStream input) {
        // Build the Input String
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String inputStr = sb.toString();
        return inputStr;
    }

}
