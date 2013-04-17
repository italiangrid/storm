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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.asynch.BuilderException;
import it.grid.storm.asynch.PtG;
import it.grid.storm.asynch.PtGBuilder;
import it.grid.storm.scheduler.ChunkTask;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToGetOutputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PrepareToGetRequestCommand implements Command {

	private static final Logger log = LoggerFactory
		.getLogger(PrepareToGetRequestCommand.class);

	@Override
	public OutputData execute(InputData inputData) throws CommandException {

		if (!(inputData instanceof FileTransferInputData)) {
			log.error("Unable to convert from InputData. Wrong InputData type: \'"
				+ inputData.getClass().getName() + "\'");
			throw new IllegalArgumentException(
				"Unable to convert from InputData. Wrong InputData type: \'"
					+ inputData.getClass().getName() + "\'");
		}
		PtG request;
		try {
			request = PtGBuilder.build((FileTransferInputData) inputData);
		} catch (BuilderException e) {
			log
				.error("Unable to build PtG request from the InputData. BuilderException: "
					+ e.getMessage());
			throw new CommandException(
				"Unable to build PtG request from the InputData");
		}
		ChunkTask ptgTask = new ChunkTask(request);
		ptgTask.run();
		try {
			return new PrepareToGetOutputData(request.getRequestData().getSURL(),
				request.getRequestData().getTransferURL(), request.getRequestData()
					.getStatus(), request.getRequestData().getGeneratedRequestToken(),
				request.getRequestData().getFileSize(), request.getRequestData()
					.getPinLifeTime());
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to create PrepareToPutOutputData. IllegalArgumentException: "
					+ e.getMessage());
			throw new CommandException("Unable to create PrepareToPutOutputData");
		}
	}
}
