/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import it.grid.storm.tape.recalltable.model.PutTapeRecallStatusLogic;
import it.grid.storm.tape.recalltable.model.PutTapeRecallStatusValidator;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
@Path("/recalltable/task")
public class TaskResource {

	private static final Logger log = LoggerFactory.getLogger(TaskResource.class);

	private static Configuration config = Configuration.getInstance();

	/**
	 * Get recall tasks that are currently in progress.
	 * 
	 * @param maxResults
	 *          the maximum number of result to be returned
	 * @return a Response with a 200 code containing a list of the tasks currently
	 *         in progress or with a 500 if something went wrong
	 */
	@GET
	public Response getTasks(@QueryParam("maxResults") Integer maxResults) {

		List<TapeRecallTO> tasks = 
			new TapeRecallCatalog().getAllInProgressTasks(maxResults);

		GenericEntity<List<TapeRecallTO>> entity = 
			new GenericEntity<List<TapeRecallTO>>(tasks) {};

		return Response.ok(entity).build();
	}

	/**
	 * This method takes a request token and a SURL encoded as a string as follows
	 * <pre> requestToken=<TOKEN> surl=<SURL> </pre>
	 * 
	 * This method checks that the requested SURL has been recalled and if so
	 * updates the request status to the proper final status.
	 * 
	 * This method returns a 200 response status and a string containing either
	 * true or false. It returns true if the file is present on the filesystem,
	 * false otherwise (this may happen when querying the status of a surl for
	 * which the recall operation is still in progress on a tape enabled storage
	 * area).
	 * 
	 * This method returns a 500 response in case of errors
	 * 
	 * The StoRM Frontend calls this method whenever a ptg or bol status request
	 * is submitted and the related ptg or bol status is marked as in progress in
	 * StoRM database. (for both tape enabled and disk only SA).
	 */
	@PUT
	@Path("/")
	@Consumes("text/plain")
	public Response putTaskStatus(InputStream input) {

		String inputString = buildInputString(input);
		
		log.debug("putTaskStatus() - Input:" + inputString);

		PutTapeRecallStatusValidator validator = 
			new PutTapeRecallStatusValidator(inputString);

		if (!validator.validate()) {
			
			return validator.getResponse();
		}

		/* Business logic */
		Response response;
		
		try {
		
			response = PutTapeRecallStatusLogic.serveRequest(
				validator.getRequestToken(), validator.getStoRI());
		
		} catch (TapeRecallException e) {
			
			log.error("Error serving request. TapeRecallException: " + e.getMessage());
			
			response = 
				Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}

	/**
	 * Method called by GEMSS
	 */
	@PUT
	@Path("/{groupTaskId}")
	@Consumes("text/plain")
	public void putNewTaskStatusOrRetryValue(@PathParam("groupTaskId") UUID groupTaskId, InputStream input)
		throws TapeRecallException {

		log.debug("Requested to change recall table value for taskId " + groupTaskId);

		String inputStr = buildInputString(input);
		
		log.debug("@PUT (input string) = '" + inputStr + "'");

		// Retrieve Tasks corresponding to taskId
		// - the relationship between groupTaskId and entries within the DB is
		// one-to-many

		String errorStr = null;

		TapeRecallCatalog rtCat = new TapeRecallCatalog();

		try {
			
			if (!rtCat.existsGroupTask(groupTaskId)) {
				
				log.info("Received a tape recall status update but no Recall Group Task found with ID = '" + groupTaskId + "'");
				
				throw new TapeRecallException("No Recall Group Task found with ID = '" + groupTaskId + "'");
			}
		
		} catch (DataAccessException e) {
			
			log.error("Unable to retrieve Recall Group Task with ID = '" + groupTaskId + "' " + e.getMessage());
			
			throw new TapeRecallException("Unable to retrieve recall group task " +
					"with ID = '" + groupTaskId + "' " + e.getMessage());
		}

		String keyRetryValue = config.getRetryValueKey();
		String keyStatus = config.getStatusKey();
		
		int eqIndex = inputStr.indexOf('=');

		String value, key = null;

		if (eqIndex > 0) {
		
			value = inputStr.substring(eqIndex);
			key = inputStr.substring(0, eqIndex);
		
		} else {
			
			errorStr = "Body '" + inputStr + "'is wrong";
			throw new TapeRecallException(errorStr);
		}
		
		int intValue;
		
		try {
		
			// trim out the '\n' end.
			intValue = Integer.valueOf(value.substring(1, value.length() - 1));

		} catch (NumberFormatException e) {
			
			errorStr = "Unable to understand the number value = '" + value + "'";
			throw new TapeRecallException(errorStr);
		}
		
		if (key.equals(keyRetryValue)) { // **** Set the Retry value
		
			log.debug("Changing retry attempt of task " + groupTaskId + " to " + intValue);
		
			rtCat.changeGroupTaskRetryValue(groupTaskId, intValue);
		
		} else {
			
			if (key.equals(keyStatus)) { // **** Set the Status
				log.debug("Changing status of task " + groupTaskId + " to " + intValue);
				
				try {
			
					rtCat.changeGroupTaskStatus(groupTaskId,
						TapeRecallStatus.getRecallTaskStatus(intValue), new Date());
				
				} catch (DataAccessException e) {
				
					log.error("Unable to change the status for group task id "
						+ groupTaskId + " to status " + intValue
						+ " . DataAccessException : " + e.getMessage());
					
					throw new TapeRecallException(
						"Unable to change the status for group task id " + groupTaskId
							+ " to status " + intValue + " . DataAccessException : "
							+ e.getMessage());
				}
			
			} else {
			
				errorStr = "Unable to understand the key = '" + key
					+ "' in @PUT request.";
				
				throw new TapeRecallException(errorStr);
			}
		}
	}

	/**
	 * Creates a new recall task.
	 * 
	 */
	@POST
	@Path("/")
	@Consumes("text/plain")
	public Response postNewTask(TapeRecallTO task) {

		Response result = Response.noContent().build();
		
		try {
		
			new TapeRecallCatalog().insertNewTask(task);
		
		} catch (DataAccessException e) {
			
			log.error("Unable to insert the new task in tape recall DB.");
			
			return Response.serverError().entity("Unable to insert the new task in tape recall DB.").build();
		}
		
		URI newResource = URI.create("/" + task.getTaskId());
		
		result = Response.created(newResource).build();
		
		log.debug("New task resource created: " + newResource);
		
		return result;
	}

	/**
	 * Utility method.
	 * 
	 */
	private String buildInputString(InputStream input) {

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
