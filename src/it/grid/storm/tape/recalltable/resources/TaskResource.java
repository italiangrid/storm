/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zappi
 * 
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
    @Path("/{taskId}")
    @Consumes("text/plain")
    public void putNewTaskStatusOrRetryValue(@PathParam("taskId") String taskId, InputStream input)
            throws RecallTableException {

        // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();
        // @todo : REMOVE THIS
        test = true;

        // Retrieve the Input String
        String inputStr = buildInputString(input);
        TaskResource.log.debug("@PUT (input string) = '" + inputStr + "'");

        // Retrieve Task corresponding to taskId
        RecallTaskTO task = null;
        RecallTableCatalog rtCat = new RecallTableCatalog(test);
        try {
            task = rtCat.getTask(taskId);
        } catch (DataAccessException e1) {
            log.error("Unable to retrieve Recall Task with ID = '" + taskId + "'");
            throw new RecallTableException("Unable to retrieve Recall Task with ID = '" + taskId + "'");
        }

        // Retrieve value from Body param
        String keyRetryValue = config.getRetryValueKey();
        String keyStatus = config.getStatusKey();
        int eqIndex = inputStr.indexOf('=');
        String errorStr = null;
        if (eqIndex > 0) {
            String value = inputStr.substring(eqIndex);
            String key = inputStr.substring(0, eqIndex - 1);
            if (key.equals(keyRetryValue)) {
                try {
                    int retryValue = Integer.valueOf(value);
                    task.setRetryAttempt(retryValue);
                } catch (NumberFormatException e) {
                    errorStr = "Unable to understand the number value = '" + value + "'";
                }
            } else {
                if (key.equals(keyStatus)) {
                    try {
                        int statusValue = Integer.valueOf(value);
                        task.setStatusId(statusValue);
                    } catch (NumberFormatException e) {
                        errorStr = "Unable to understand the number value = '" + value + "'";
                    }
                } else {
                    errorStr = "Unable to understand the key = '" + key + "' in @PUT request.";
                }
            }
        } else {
            errorStr = "Body '" + inputStr + "'is wrong";
        }
        if (errorStr != null) {
            throw new RecallTableException(errorStr);
        } else {
            // Store the Task
            rtCat.updateTask(task);
        }
    }


    @POST
    @Path("/")
    @Consumes("text/plain")
    public void postNewTask(InputStream input) throws RecallTableException {

        // Parse the Input Stream
        String inputStr = buildInputString(input);
        TaskResource.log.debug("@POST (input string) = '" + inputStr + "'");

        // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();
        // @todo : REMOVE THIS
        test = true;

        // Recall Table Catalog
        RecallTableCatalog rtCat = new RecallTableCatalog(test);

        // Retrieve value from Body param
        String errorStr = null;

        // Create a new RecallTaskTO
        RecallTaskTO task = new RecallTaskTO();
        String taskId = task.getTaskId();

        // Parsing of the inputString to extract the fields of RecallTask
        /**
         * @todo
         */

        // Store the new Recall Task if it is all OK.
        if (errorStr != null) {
            throw new RecallTableException(errorStr);
        } else {
            rtCat.insertNewTask(task);
        }
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
