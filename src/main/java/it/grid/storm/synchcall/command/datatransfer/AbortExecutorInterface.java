/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the Abort Executor Interface for the SRM request Abort*
 *
 * @author Magnoni Luca
 * @author CNAF -INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;

public interface AbortExecutorInterface {

  public AbortGeneralOutputData doIt(AbortInputData inputData);
}
