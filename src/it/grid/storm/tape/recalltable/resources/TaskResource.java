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
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.model.PutTaskStatusLogic;
import it.grid.storm.tape.recalltable.model.PutTaskStatusValidator;
import it.grid.storm.tape.recalltable.model.RecallTaskData;
import it.grid.storm.tape.recalltable.model.RecallTaskStatus;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
@Path("/recalltable/task")
public class TaskResource {

    private static final Logger log = LoggerFactory.getLogger(TaskResource.class);
    private static Configuration config = Configuration.getInstance();

    @GET
    @Path("/{taskId}")
    @Produces("text/plain")
    public String doGetWholeTask(@PathParam("taskId") String taskId) {
        return "doGetWholeTask: TASK-ID = " + taskId;
    }

    @GET
    @Path("/{taskId}/retry")
    @Produces("text/plain")
    public String doGetTaskRetry(@PathParam("taskId") String taskId) {
        return "doGetTaskRetry: TASK-ID = " + taskId;
    }

    @GET
    @Path("/{taskId}/status")
    @Produces("text/plain")
    public String doGetTaskStatus(@PathParam("taskId") String taskId) {
        return "doGetTaskStatus: TASK-ID = " + taskId;
    }

    @PUT
    @Path("/")
    @Consumes("text/plain")
    public Response putTaskStatus(InputStream input) throws RecallTableException {
        String inputString = buildInputString(input);
        log.debug("putTaskStatus() - Input:" + inputString);
        
        PutTaskStatusValidator validator = new PutTaskStatusValidator(inputString);
        
        if (!validator.validate()) {
            return validator.getResponse();
        }
        
        /* Business logic */
        Response response = PutTaskStatusLogic.serveRequest(validator.getRequestToken(), validator.getStoRI());

        return response;
    }

    @PUT
    @Path("/{taskId}")
    @Consumes("text/plain")
    public void putNewTaskStatusOrRetryValue(@PathParam("taskId") UUID taskId, InputStream input)
            throws RecallTableException {

        log.debug("Requested to change recall table value for taskId " + taskId);
        // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();

        // Retrieve the Input String
        String inputStr = buildInputString(input);
        TaskResource.log.debug("@PUT (input string) = '" + inputStr + "'");

        // Retrieve Tasks corresponding to taskId 
        //  - the relationship between taskId and entries within the DB is one-to-many
        ArrayList<RecallTaskTO> tasks = new ArrayList<RecallTaskTO>();

        // Recall Table Catalog
        RecallTableCatalog rtCat = null;

        String errorStr = null;

        try {
            rtCat = new RecallTableCatalog(test);
        } catch (DataAccessException e) {
            log.error("Unable to use RecallTable DB.");
            throw new RecallTableException("Unable to use RecallTable DB.");
        }

        try {
            tasks = new ArrayList<RecallTaskTO>(rtCat.getTask(taskId));
        } catch (DataAccessException e) {
            log.error("Unable to retrieve Recall Task with ID = '" + taskId + "' " + e.getMessage());
            throw new RecallTableException("Unable to retrieve Recall Task with ID = '" + taskId + "' " +e.getMessage());
        }

        // Retrieve value from Body param
        String keyRetryValue = config.getRetryValueKey();
        String keyStatus = config.getStatusKey();
        int eqIndex = inputStr.indexOf('=');

        if (eqIndex > 0)
        {
            String value = inputStr.substring(eqIndex);
            String key = inputStr.substring(0, eqIndex);
            if (key.equals(keyRetryValue))
            { // **** Set the Retry value
                try
                {
                    // trim out the '\n' end.
                    int retryValue = Integer.valueOf(value.substring(1, value.length() - 1));
                    rtCat.changeRetryValue(taskId, retryValue);

                }
                catch (NumberFormatException e)
                {
                    errorStr = "Unable to understand the number value = '" + value + "'";
                    throw new RecallTableException(errorStr);
                }
            }
            else
            {
                if (key.equals(keyStatus))
                { // **** Set the Status
                    try
                    {
                        // trim out the '\n' end.
                        int statusValue = Integer.valueOf(value.substring(1, value.length() - 1));
                        log.debug("Changing status of task " + taskId + " to " + statusValue);
                        rtCat.changeStatus(taskId, RecallTaskStatus.getRecallTaskStatus(statusValue));
                    }
                    catch (NumberFormatException e)
                    {
                        errorStr = "Unable to understand the number value = '" + value + "'";
                        throw new RecallTableException(errorStr);
                    }
                }
                else
                {
                    errorStr = "Unable to understand the key = '" + key + "' in @PUT request.";
                    throw new RecallTableException(errorStr);
                }
            }
        }
        else
        {
            errorStr = "Body '" + inputStr + "'is wrong";
            throw new RecallTableException(errorStr);
        }
    }

    @POST
    @Path("/")
    @Consumes("text/plain")
    public Response postNewTask(InputStream input) throws RecallTableException {

        Response result = Response.noContent().build();

        // Retrieve values from Body param
        String errorStr = null;

        // Parse the Input Stream
        String inputStr = buildInputString(input);
        log.debug("@POST (input string) = '" + inputStr + "'");

        // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();

        // Recall Table Catalog
        RecallTableCatalog rtCat = null;
        try {
            rtCat = new RecallTableCatalog(test);
        } catch (DataAccessException e) {
            errorStr = "Unable to use RecallTable DB.";
            log.error(errorStr);
            throw new RecallTableException(errorStr);
        }

        // Parsing of the inputString to extract the fields of RecallTask
        // RecallTaskData rtd = new RecallTaskData(inputStr);
        RecallTaskData rtd = RecallTaskData.buildFromString(inputStr);
        log.debug("RTD=" + rtd.toString());

        // Store the new Recall Task if it is all OK.
        RecallTaskTO task = RecallTaskBuilder.buildFromPOST(rtd);
        if (rtCat != null) {
            rtCat.insertNewTask(task);
            URI newResource = URI.create("/" + task.getTaskId());
            result = Response.created(newResource).build();
            log.debug("New task resource created: " + newResource);
        } else {
            result = Response.serverError().build();
            /**
             * @todo : // Build an error response!
             */
        }
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
