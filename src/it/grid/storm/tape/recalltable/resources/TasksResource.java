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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 *
 */
@Path("/recalltable/tasks")
public class TasksResource {

    private static final Logger log = LoggerFactory.getLogger(TaskResource.class);
    private static Configuration config = Configuration.getInstance();

    @PUT
    @Path("/")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putTakeoverTasks(InputStream input) throws RecallTableException {

        Response response = Response.noContent().build();
        
        // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();
        // @todo : REMOVE THIS
        // test = true;

        // Retrieve the Input String
        String inputStr = buildInputString(input);
        log.debug("@PUT (input string) = '" + inputStr + "'");

        // Retrieve Task corresponding to taskId
        RecallTaskTO task = null;

        // Recall Table Catalog
        RecallTableCatalog rtCat = null;

        try {
            rtCat = new RecallTableCatalog(test);
        } catch (DataAccessException e) {
            log.error("Unable to use RecallTable DB.");
            throw new RecallTableException("Unable to use RecallTable DB.");
        }
        
        // Retrieve the number of tasks to takeover (default = 1)
        int numbOfTask = 1;
        /**
         * currently we ignore the parameter (first = 1) .
         * 
         * @todo
         */

        // Retrieve the Task
        task = rtCat.taskOverTask();

        // Build the body of response
        ResponseBuilder responseBuilder = Response.ok();

        return task.getFileName();
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
