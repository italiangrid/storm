/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command;

import it.grid.storm.synchcall.command.datatransfer.CommandException;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

public interface Command {

  /**
   * @param inputData Contains information about input data for the request.
   * @return OutputData Contains output data
   */
  public OutputData execute(InputData inputData) throws IllegalArgumentException, CommandException;
}
