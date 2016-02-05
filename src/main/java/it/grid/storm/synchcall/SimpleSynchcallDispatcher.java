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

package it.grid.storm.synchcall;

import com.codahale.metrics.Timer;

import it.grid.storm.common.OperationType;
import it.grid.storm.metrics.StormMetricRegistry;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandFactory;
import it.grid.storm.synchcall.command.datatransfer.CommandException;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project.
 * 
 * This class contains the logic to process the different synchcall request.
 * This dispatcher simply execute a new request when it's just arrived. A more
 * complex version can have thread pools and more complicated pattern.
 * 
 * @author lucamag @date May 27, 2008
 * 
 */

public class SimpleSynchcallDispatcher implements SynchcallDispatcher {

  public static final String SYNCH_CALL_TIMER_NAME = "synch";

  public OutputData processRequest(OperationType type, InputData inputData)
    throws IllegalArgumentException, CommandException {

    final Timer timer = StormMetricRegistry.INSTANCE.getRegistry()
      .timer(SYNCH_CALL_TIMER_NAME);

    // This provides metrics for all synch calls
    final Timer.Context synchContext = timer.time();

    Command cmd = CommandFactory.getCommand(type);
    
    // This provides metrics for the specific synch call type 
    final Timer.Context context = type.getTimer().time();
    
    try {
      return cmd.execute(inputData);
    } finally {
      context.stop();
      synchContext.stop();
    }
  }

}
