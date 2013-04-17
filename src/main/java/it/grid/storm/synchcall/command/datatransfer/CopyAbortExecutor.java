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

package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This is the Abort executor for a Copy request.
 * 
 * 
 * Authors:
 * 
 * @author lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 10, 2008
 * 
 */

public class CopyAbortExecutor implements AbortExecutorInterface {

	private static final Logger log = LoggerFactory
		.getLogger(CopyAbortExecutor.class);

	public CopyAbortExecutor() {

	};

	public AbortGeneralOutputData doIt(AbortInputData inputData) {

		AbortGeneralOutputData outputData = new AbortGeneralOutputData();
		boolean requestFailure, requestSuccess;
		TReturnStatus globalStatus = null;

		log.info("SrmAbortRequest: Started AbortCopyExecutor");

		return outputData;
	}
}