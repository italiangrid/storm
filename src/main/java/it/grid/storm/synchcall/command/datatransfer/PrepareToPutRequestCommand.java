/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.asynch.BuilderException;
import it.grid.storm.asynch.PtP;
import it.grid.storm.asynch.PtPBuilder;
import it.grid.storm.scheduler.ChunkTask;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutOutputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
public class PrepareToPutRequestCommand implements Command {

  private static final Logger log = LoggerFactory.getLogger(PrepareToPutRequestCommand.class);

  @Override
  public OutputData execute(InputData inputData) throws IllegalArgumentException, CommandException {

    if (!(inputData instanceof PrepareToPutInputData)) {
      log.error(
          "Unable to convert from InputData. Wrong InputData type: \'"
              + inputData.getClass().getName()
              + "\'");
      throw new IllegalArgumentException(
          "Unable to convert from InputData. Wrong InputData type: \'"
              + inputData.getClass().getName()
              + "\'");
    }
    PtP request;
    try {
      request = PtPBuilder.build((PrepareToPutInputData) inputData);
    } catch (BuilderException e) {
      log.error(
          "Unable to build PtP request from the InputData. BuilderException: " + e.getMessage());
      throw new CommandException("Unable to build PtP request from the InputData");
    }
    ChunkTask ptpTask = new ChunkTask(request);
    ptpTask.run();
    try {
      return new PrepareToPutOutputData(
          request.getRequestData().getSURL(),
          request.getRequestData().getTransferURL(),
          request.getRequestData().getStatus(),
          request.getRequestData().getGeneratedRequestToken());
    } catch (IllegalArgumentException e) {
      log.error(
          "Unable to create PrepareToPutOutputData. IllegalArgumentException: " + e.getMessage());
      throw new CommandException("Unable to create PrepareToPutOutputData");
    }
  }
}
