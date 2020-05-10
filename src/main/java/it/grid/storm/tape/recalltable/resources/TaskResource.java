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
import static it.grid.storm.tape.recalltable.model.TapeRecallStatus.getRecallTaskStatus;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
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
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Riccardo Zappi
 * @author Enrico Vianello
 */
@Path("/recalltable/task")
public class TaskResource {

  private static final Logger log = LoggerFactory.getLogger(TaskResource.class);

  private static Configuration config = Configuration.getInstance();

  private ResourceService service;
  private TapeRecallCatalog recallCatalog;

  private ObjectMapper mapper = new ObjectMapper();

  public TaskResource() throws NamespaceException {

    NamespaceInterface ns = NamespaceDirector.getNamespace();
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
   */
  @PUT
  @Path("/{groupTaskId}")
  @Consumes("text/plain")
  public Response putNewTaskStatusOrRetryValue(@PathParam("groupTaskId") UUID groupTaskId,
      InputStream input) {

    log.debug("Requested to change recall table value for groupTaskId {}", groupTaskId);

    List<String> lines;
    try {
      lines = getAllLines(input);
    } catch (IOException e) {
      String message = format("Unable to read entity lines: %s", e.getMessage());
      log.error(message, e);
      return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.INTERNAL_SERVER_ERROR).build();
    }

    if (lines.isEmpty() || lines.size() > 1) {

      String message = format("Invalid body '%s'", lines);
      return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.BAD_REQUEST).build();
    }

    Optional<SimpleEntry<String, Integer>> keyValue = getKeyValue(lines.get(0));

    if (!keyValue.isPresent()) {

      String message = format("Invalid body '%s'", lines);
      return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.BAD_REQUEST).build();
    }

    try {

      if (!recallCatalog.existsGroupTask(groupTaskId)) {

        String message = format("No Recall Group Task found with ID = '%s'", groupTaskId);
        log.info(message);
        return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.NOT_FOUND).build();
      }

    } catch (DataAccessException e) {

      String message = format("Unable to retrieve Recall Group Task with ID = '%s' %s", groupTaskId,
          e.getMessage());
      log.error(message, e);
      return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.INTERNAL_SERVER_ERROR).build();
    }

    String keyStatus = config.getStatusKey();
    String key = keyValue.get().getKey();
    Integer value = keyValue.get().getValue();

    if (key.equalsIgnoreCase(keyStatus)) { // **** Set the Status

      log.debug("Changing status of task {} to {}", groupTaskId, value);

      try {

        TapeRecallStatus newStatus = getRecallTaskStatus(value);
        recallCatalog.changeGroupTaskStatus(groupTaskId, newStatus, new Date());

      } catch (DataAccessException e) {

        String message = format(
            "Unable to change the status for group task id %s to status %s DataAccessException : %s",
            groupTaskId, value, e.getMessage());
        log.error(message, e);
        return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.INTERNAL_SERVER_ERROR).build();
      }

    } else { // inputMap.containsKey(keyRetryValue)) **** Set the Retry value

      log.debug("Changing retry attempt of task {} to {}", groupTaskId, value);

      try {

        recallCatalog.changeGroupTaskRetryValue(groupTaskId, value);

      } catch (DataAccessException e) {

        String message = format("Unable to takeover a task: %s", e.getMessage());
        return Response.ok(message, TEXT_PLAIN_TYPE).status(Status.INTERNAL_SERVER_ERROR).build();

      }
    }

    return Response.noContent().build();
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

  private Optional<SimpleEntry<String, Integer>> getKeyValue(String line) {

    Pattern pStatus = Pattern.compile(config.getStatusKey() + "=[0-9]+");
    Pattern pAttempt = Pattern.compile(config.getRetryValueKey() + "=[0-9]+");

    SimpleEntry<String, Integer> entry = null;

    Matcher mStatus = pStatus.matcher(line);
    Matcher mAttempt = pAttempt.matcher(line);

    if (mStatus.matches() || mAttempt.matches()) {
      String key = line.split("=")[0];
      Integer value = Integer.valueOf(line.split("=")[1]);
      entry = new SimpleEntry<String, Integer>(key, value);
    } else {
      log.warn("got invalid key-value data = {}", line);
    }

    return Optional.ofNullable(entry);
  }


  public List<String> getAllLines(InputStream input) throws IOException {

    List<String> lines = Lists.newArrayList();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
      String line = null;
      while ((line = br.readLine()) != null) {
        lines.add(line);
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
    return lines;
  }

}
