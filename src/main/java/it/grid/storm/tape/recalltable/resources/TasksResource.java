/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/** */
package it.grid.storm.tape.recalltable.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author zappi */
@Path("/recalltable/tasks")
public class TasksResource {

  private static final Logger log = LoggerFactory.getLogger(TasksResource.class);

  private static Configuration config = Configuration.getInstance();

  /**
   * Return recall tasks for being taken over. The status of the tasks that are returned is set to
   * in progress.
   *
   * @param input a key value pair in which the value is the number of results to be returned in the
   * @return the tasks ready to takeover
   * @throws TapeRecallException
   */
  @PUT
  @Consumes("text/plain")
  @Produces("text/plain")
  public Response putTakeoverTasks(InputStream input) throws TapeRecallException {

    // retrieve the Input String
    String inputStr = buildInputString(input);

    log.debug("@PUT (input string) = '{}'", inputStr);

    // retrieve the number of tasks to takeover (default = 1)
    int numbOfTask = 1;

    // retrieve value from Body param
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

          throw new TapeRecallException(
              "Unable to understand " + "the number value = '" + value + "'");
        }
      }
    }

    // retrieve the tasks
    List<TapeRecallTO> tasks = new TapeRecallCatalog().takeoverNTasksWithDoubles(numbOfTask);

    HashMap<UUID, List<TapeRecallTO>> groupTaskMap = buildGroupTaskMap(tasks);

    List<TapeRecallTO> groupTasks = Lists.newArrayList();

    for (List<TapeRecallTO> groupTaskList : groupTaskMap.values()) {

      try {

        groupTasks.add(makeOne(groupTaskList));

      } catch (IllegalArgumentException e) {

        log.error(
            "Unable to makeOne the task list . IllegalArgumentException : {}", e.getMessage(), e);
        log.error("Erroneous task list (long output): {}", groupTaskList.toString());
        log.error("Skip the erroneous task list and go on...Please contact StoRM support");
      }
    }

    if (tasks.size() > groupTasks.size()) {

      log.debug("Taking over some multy-group tasks");
    }

    log.debug(
        "Number of tasks recalled : <{}> over <{}> tasks requested",
        groupTasks.size(),
        tasks.size());

    // need a generic entity
    GenericEntity<List<TapeRecallTO>> entity = new GenericEntity<List<TapeRecallTO>>(tasks) {};

    return Response.ok(entity).build();
  }

  /**
   * Creates a map with the taskIds as keys and the list of tasks related to each taskId (key) as
   * value
   *
   * @param tasks
   * @return
   */
  private HashMap<UUID, List<TapeRecallTO>> buildGroupTaskMap(List<TapeRecallTO> tasks) {

    HashMap<UUID, List<TapeRecallTO>> groupTaskMap = Maps.newHashMap();

    for (TapeRecallTO task : tasks) {

      List<TapeRecallTO> taskList = groupTaskMap.get(task.getGroupTaskId());

      if (taskList == null) {

        taskList = Lists.newArrayList();
        groupTaskMap.put(task.getGroupTaskId(), taskList);
      }

      taskList.add(task);
    }

    return groupTaskMap;
  }

  /**
   * Given a list of tasks with the same taskId produces a single task merging the list members
   *
   * @param recallTasks
   * @return
   */
  private TapeRecallTO makeOne(List<TapeRecallTO> recallTasks) {

    TapeRecallTO taskTO = new TapeRecallTO();

    UUID taskId = recallTasks.get(0).getTaskId();

    // verify that all have the same task id
    for (TapeRecallTO recallTask : recallTasks) {

      if (!recallTask.getTaskId().equals(taskId)) {

        log.error(
            "Received a list of not omogeneous tasks, the taskid '{}' is not matched by : {}",
            taskId,
            recallTask);

        throw new IllegalArgumentException("Received a list of not omogeneous tasks");
      }
    }

    for (TapeRecallTO recallTask : recallTasks) {

      // set common fields from any of the tasks
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

    /*
     * merge task on recall related fields to have a pin that starts as soon as
     * requested and last as long as needed
     */

    int maxRetryAttempt = 0;

    Date minInsertionInstant = null;
    Date minDeferredRecallInstant = null;
    Date maxPinExpirationInstant = null;

    for (TapeRecallTO recallTask : recallTasks) {

      if (recallTask.getRetryAttempt() > maxRetryAttempt) {
        maxRetryAttempt = recallTask.getRetryAttempt();
      }

      if (minInsertionInstant == null
          || recallTask.getInsertionInstant().before(minInsertionInstant)) {

        minInsertionInstant = recallTask.getInsertionInstant();
      }

      if (minDeferredRecallInstant == null
          || recallTask.getDeferredRecallInstant().before(minDeferredRecallInstant)) {

        minDeferredRecallInstant = recallTask.getDeferredRecallInstant();
      }

      Date currentPinExpirationInstant =
          new Date(
              recallTask.getDeferredRecallInstant().getTime()
                  + (recallTask.getPinLifetime() * 1000));

      if (maxPinExpirationInstant == null
          || currentPinExpirationInstant.after(maxPinExpirationInstant)) {

        maxPinExpirationInstant = currentPinExpirationInstant;
      }
    }

    taskTO.setRetryAttempt(maxRetryAttempt);
    taskTO.setInsertionInstant(minInsertionInstant);
    taskTO.setDeferredRecallInstant(minDeferredRecallInstant);

    int pinLifeTime =
        (int) (maxPinExpirationInstant.getTime() - minDeferredRecallInstant.getTime()) / 1000;

    taskTO.setPinLifetime(pinLifeTime);

    return taskTO;
  }

  /** Utility method. */
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
