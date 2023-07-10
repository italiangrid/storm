/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.asynch.BuilderException;
import it.grid.storm.asynch.PtG;
import it.grid.storm.asynch.PtGBuilder;
import it.grid.storm.scheduler.ChunkTask;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToGetOutputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Michele Dibenedetto */
public class PrepareToGetRequestCommand implements Command {

  private static final Logger log = LoggerFactory.getLogger(PrepareToGetRequestCommand.class);

  @Override
  public OutputData execute(InputData inputData) throws CommandException {

    if (!(inputData instanceof FileTransferInputData)) {
      log.error(
          "Unable to convert from InputData. Wrong InputData type: \'"
              + inputData.getClass().getName()
              + "\'");
      throw new IllegalArgumentException(
          "Unable to convert from InputData. Wrong InputData type: \'"
              + inputData.getClass().getName()
              + "\'");
    }
    PtG request;
    try {
      request = PtGBuilder.build((FileTransferInputData) inputData);
    } catch (BuilderException e) {
      log.error(
          "Unable to build PtG request from the InputData. BuilderException: " + e.getMessage());
      throw new CommandException("Unable to build PtG request from the InputData");
    }
    ChunkTask ptgTask = new ChunkTask(request);
    ptgTask.run();
    try {
      return new PrepareToGetOutputData(
          request.getRequestData().getSURL(),
          request.getRequestData().getTransferURL(),
          request.getRequestData().getStatus(),
          request.getRequestData().getGeneratedRequestToken(),
          request.getRequestData().getFileSize(),
          request.getRequestData().getPinLifeTime());
    } catch (IllegalArgumentException e) {
      log.error(
          "Unable to create PrepareToPutOutputData. IllegalArgumentException: " + e.getMessage());
      throw new CommandException("Unable to create PrepareToPutOutputData");
    }
  }
}
