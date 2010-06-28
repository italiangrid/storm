/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;
import it.grid.storm.tape.recalltable.RecallTableCatalog;
import it.grid.storm.tape.recalltable.RecallTableException;
import it.grid.storm.tape.recalltable.persistence.RecallTaskBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

    private static final Logger log = LoggerFactory.getLogger(TaskResource.class);
    private static Configuration config = Configuration.getInstance();

    @PUT
    @Path("/")
    @Consumes("text/plain")
    @Produces("text/plain")
    public String putTakeoverTasks(InputStream input) throws RecallTableException {

         // Retrieve if running in TEST setup
        boolean test = config.getRecallTableTestingMode();

        // Retrieve the Input String
        String inputStr = buildInputString(input);
        log.debug("@PUT (input string) = '" + inputStr + "'");

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
                    throw new RecallTableException("Unable to understand the number value = '" + value + "'");
                }
            }
        }

        // Retrieve the Task
        ArrayList<RecallTaskTO> tasks = rtCat.takeoverNTasks(numbOfTask);

        // Build the response
        String result = buildTakeoverTasksResponse(tasks);

        return result;
    }

    /**
     * @param tasks
     * @return
     */
    private String buildTakeoverTasksResponse(ArrayList<RecallTaskTO> tasks) {
        String result = "{";
        for (RecallTaskTO recallTaskTO : tasks) {
            result += recallTaskTO + RecallTaskBuilder.elementSep;
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
