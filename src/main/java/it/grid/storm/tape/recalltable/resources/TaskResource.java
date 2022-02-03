/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package it.grid.storm.tape.recalltable.resources;

import static it.grid.storm.persistence.model.TapeRecallTO.RecallTaskType.RCLL;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.rest.metadata.service.ResourceNotFoundException;
import it.grid.storm.rest.metadata.service.ResourceService;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.PutTapeRecallStatusLogic;
import it.grid.storm.tape.recalltable.model.PutTapeRecallStatusValidator;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;
import it.grid.storm.tape.recalltable.model.TaskInsertRequestValidator;

@Path("/recalltable/task")
public class TaskResource {

  private static final Logger log = LoggerFactory.getLogger(TaskResource.class);

  private static Configuration config = Configuration.getInstance();

  private ResourceService service;
  private TapeRecallCatalog recallCatalog;

  private ObjectMapper mapper = new ObjectMapper();

  public TaskResource() throws NamespaceException {

    Namespace ns = Namespace.getInstance();
    recallCatalog = new TapeRecallCatalog();
    service = new ResourceService(ns.getAllDefinedVFS(), ns.getAllDefinedMappingRules());
  }

  public TaskResource(ResourceService service, TapeRecallCatalog recallCatalog) {

    this.service = service;
    this.recallCatalog = recallCatalog;
  }

  /**
   * Get recall tasks that are currently in progress.
   * 
   * @param maxResults the maximum number of result to be returned
   * @return a Response with a 200 code containing a list of the tasks currently in progress or with
   *         a 500 if something went wrong
   */
  @GET
  public Response getTasks(@QueryParam("maxResults") Integer maxResults) {

    List<TapeRecallTO> tasks = recallCatalog.getAllInProgressTasks(maxResults);

    GenericEntity<List<TapeRecallTO>> entity = new GenericEntity<List<TapeRecallTO>>(tasks) {};

    return Response.ok(entity).build();
  }

  /**
   * This method takes a request token and a SURL encoded as a string as follows
   * 
   * <pre>
   *  requestToken=<TOKEN> surl=<SURL>
   * </pre>
   * 
   * This method checks that the requested SURL has been recalled and if so updates the request
   * status to the proper final status.
   * 
   * This method returns a 200 response status and a string containing either true or false. It
   * returns true if the file is present on the filesystem, false otherwise (this may happen when
   * querying the status of a surl for which the recall operation is still in progress on a tape
   * enabled storage area).
   * 
   * This method returns a 500 response in case of errors
   * 
   * The StoRM Frontend calls this method whenever a ptg or bol status request is submitted and the
   * related ptg or bol status is marked as in progress in StoRM database. (for both tape enabled
   * and disk only SA).
   */
  @PUT
  @Consumes("text/plain")
  public Response putTaskStatus(InputStream input) {

    String inputString = buildInputString(input);

    log.debug("putTaskStatus() - Input: {}", inputString);

    PutTapeRecallStatusValidator validator = new PutTapeRecallStatusValidator(inputString);

    if (!validator.validate()) {

      return validator.getResponse();
    }

    /* Business logic */
    Response response;

    try {

      response =
          PutTapeRecallStatusLogic.serveRequest(validator.getRequestToken(), validator.getStoRI());

    } catch (TapeRecallException e) {

      log.error("Error serving request. TapeRecallException: {}", e.getMessage(), e);

      response = Response.serverError().build();
    }

    return response;
  }

  /**
   * Updates the status or retry value of a recall task. Called by GEMSS after a recall tasks is
   * finished.
   * 
   */
  @PUT
  @Path("/{groupTaskId}")
  @Consumes("text/plain")
  public void putNewTaskStatusOrRetryValue(@PathParam("groupTaskId") UUID groupTaskId,
      InputStream input) throws TapeRecallException {

    log.debug("Requested to change recall table value for taskId {}", groupTaskId);

    String inputStr = buildInputString(input);

    log.debug("@PUT (input string) = '{}'", inputStr);

    // Retrieve Tasks corresponding to taskId
    // - the relationship between groupTaskId and entries within the DB is
    // one-to-many

    String errorStr = null;

    try {

      if (!recallCatalog.existsGroupTask(groupTaskId)) {

        log.info(
            "Received a tape recall status update but no Recall Group Task found with ID = '{}'",
            groupTaskId);

        throw new TapeRecallException("No Recall Group Task found with ID = '" + groupTaskId + "'");
      }

    } catch (DataAccessException e) {

      log.error("Unable to retrieve Recall Group Task with ID = '{}' DataAccessException: {}",
          groupTaskId, e.getMessage(), e);

      throw new TapeRecallException("Unable to retrieve recall group task " + "with ID = '"
          + groupTaskId + "' " + e.getMessage());
    }

    String keyRetryValue = config.getRetryValueKey();
    String keyStatus = config.getStatusKey();

    int eqIndex = inputStr.indexOf('=');

    String value = null;
    String key = null;

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

      log.debug("Changing retry attempt of task {} to {}", groupTaskId, intValue);

      recallCatalog.changeGroupTaskRetryValue(groupTaskId, intValue);

    } else {

      if (key.equals(keyStatus)) { // **** Set the Status
        log.debug("Changing status of task {} to {}", groupTaskId, intValue);

        try {

          TapeRecallStatus updatedStatus = TapeRecallStatus.getRecallTaskStatus(intValue);
          recallCatalog.changeGroupTaskStatus(groupTaskId, updatedStatus, new Date());
          // Update all PtG or BoL related
          if (updatedStatus.isFinalStatus()) {
            
          }
          

        } catch (DataAccessException e) {

          log.error(
              "Unable to change the status for group task id {} to status {} DataAccessException : {}",
              groupTaskId, intValue, e.getMessage(), e);

          throw new TapeRecallException(
              "Unable to change the status for group task id " + groupTaskId + " to status "
                  + intValue + " . DataAccessException : " + e.getMessage());
        }

      } else {

        errorStr = "Unable to understand the key = '" + key + "' in @PUT request.";

        throw new TapeRecallException(errorStr);
      }
    }
  }

  /**
   * Creates a new recall task.
   * 
   * @author Enrico Vianello
   */
  @POST
  @Consumes(APPLICATION_JSON)
  public Response postNewTask(TaskInsertRequest request) {

    log.info("POST /recalltable/task {}", request);

    TaskInsertRequestValidator validator = new TaskInsertRequestValidator(request);
    if (!validator.validate()) {
      log.info("BAD REQUEST: {}", validator.getErrorMessage());
      Response r = Response.status(BAD_REQUEST).entity(validator.getErrorMessage()).build();
      throw new WebApplicationException(validator.getErrorMessage(), r);
    }

    StoRI resource = null;

    try {

      resource = service.getResource(request.getStfn());

    } catch (ResourceNotFoundException e) {

      log.info(e.getMessage(), e);
      throw new WebApplicationException(e.getMessage(), e, NOT_FOUND);

    } catch (NamespaceException e) {

      log.error(e.getMessage(), e);
      throw new WebApplicationException(e.getMessage(), e, INTERNAL_SERVER_ERROR);
    }

    String voName;

    try {

      voName = resource.getVirtualFileSystem()
        .getApproachableRules()
        .get(0)
        .getSubjectRules()
        .getVONameMatchingRule()
        .getVOName();

    } catch (NamespaceException e) {

      log.error(e.getMessage(), e);
      throw new WebApplicationException(e.getMessage(), INTERNAL_SERVER_ERROR);
    }

    if (request.getVoName() != null && !request.getVoName().equals(voName)) {
      String message = String.format(
          "The voName included in the request does not match the voName resolved for this request: %s != %s",
          request.getVoName(), voName);
      log.error(message);
      throw new WebApplicationException(message, BAD_REQUEST);
    }

    Date currentDate = new Date();
    TapeRecallTO task = new TapeRecallTO();
    task.setFileName(resource.getAbsolutePath());
    task.setFakeRequestToken();
    task.setRequestType(RCLL);
    task.setRetryAttempt(request.getRetryAttempts());
    task.setUserID(request.getUserId());
    task.setVoName(voName);
    task.setInsertionInstant(currentDate);
    task.setDeferredRecallInstant(currentDate);
    task.setPinLifetime(request.getPinLifetime() != null ? request.getPinLifetime() : -1);

    log.info("Builded TapeRecallTO: {}", task);

    UUID groupTaskId = null;

    try {

      groupTaskId = recallCatalog.insertNewTask(task);

    } catch (DataAccessException e) {

      log.error(e.getMessage(), e);
      throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
    }

    String location = String.format("/recalltable/task/%s?requestToken=%s", groupTaskId,
        task.getRequestToken().getValue());
    log.debug("Location: {}", location);

    return Response.created(URI.create(location)).build();
  }

  @GET
  @Path("/{groupTaskId}")
  @Produces(APPLICATION_JSON)
  public Response getGroupTaskInfo(@PathParam("groupTaskId") String groupTaskId,
      @QueryParam("requestToken") String requestToken) {

    log.info("GET info for groupTaskId={} and requestToken={})", groupTaskId, requestToken);

    List<TapeRecallTO> tasks = null;
    try {
      tasks = recallCatalog.getGroupTasks(UUID.fromString(groupTaskId));
    } catch (DataAccessException e) {
      log.error(e.getMessage());
      throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
    }
    if (tasks.isEmpty()) {
      throw new WebApplicationException("No tasks found for " + groupTaskId, NOT_FOUND);
    }

    log.debug("Found {} task(s): {}", tasks.size(), tasks);
    TapeRecallTO task = null;
    for (TapeRecallTO current : tasks) {
      if (current.getRequestToken().getValue().equals(requestToken)) {
        task = current;
        break;
      }
    }
    if (task == null) {
      throw new WebApplicationException("No task found for requestToken " + requestToken,
          NOT_FOUND);
    }

    String jsonString = null;
    try {
      jsonString = mapper.writeValueAsString(task);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
    }

    return Response.ok(jsonString).build();
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

      log.error(e.getMessage(), e);

    } finally {

      try {

        input.close();

      } catch (IOException e) {

        log.error(e.getMessage(), e);
      }
    }

    return sb.toString();
  }

}
