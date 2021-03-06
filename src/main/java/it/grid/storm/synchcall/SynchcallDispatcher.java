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

import it.grid.storm.common.OperationType;
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
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 28, 2008
 * 
 */
public interface SynchcallDispatcher {

	/**
	 * @param type
	 *          Type of the SRM request to execute.
	 * @param inputData
	 *          InputDaata contining input information.
	 * @return outputData OutputData containing outputData.
	 * @throws IllegalArgumentException
	 */
	public abstract OutputData processRequest(OperationType type,
		InputData inputData) throws IllegalArgumentException, CommandException;

}