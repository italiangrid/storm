/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import it.grid.storm.persistence.PersistenceDirector;
import it.grid.storm.persistence.dao.TapeRecallDAO;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.RecallTaskTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
    public void putNewTaskStatus(@PathParam("taskId") String taskId, InputStream input) {
       // Retrieve Task corresponding to taskId
        RecallTaskTO task;
        try {
            TapeRecallDAO tapeDAO = PersistenceDirector.getDAOFactory().getTapeRecallDAO();
            task = tapeDAO.getTask(taskId);

        } catch (DataAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        // Parse the Input Stream to retrieve the new status

        // Set the new Status
        log.debug("RecallTask Status NEW = " + input);

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
        log.debug("New Status INPUT : " + inputStr);
    }    
}
