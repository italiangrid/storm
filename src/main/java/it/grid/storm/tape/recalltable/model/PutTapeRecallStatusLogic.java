/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.tape.recalltable.model;

import java.util.Date;
import java.util.UUID;
import it.grid.storm.filesystem.FSException;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;
import it.grid.storm.tape.recalltable.TapeRecallCatalog;
import it.grid.storm.tape.recalltable.TapeRecallException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PutTapeRecallStatusLogic {

	private static final Logger log = LoggerFactory
		.getLogger(PutTapeRecallStatusLogic.class);

	/**
	 * @param requestToken
	 * @param stori
	 * @return
	 * @throws TapeRecallException
	 */
	public static Response serveRequest(String requestToken, StoRI stori)
		throws TapeRecallException {

		LocalFile localFile = stori.getLocalFile();
		String outputMessage;
		boolean fileOnDisk;
		try {
			fileOnDisk = localFile.isOnDisk();
		} catch (FSException e) {
			log.error("Unable to test file " + localFile.getAbsolutePath()
				+ " presence on disk. FSException " + e.getMessage());
			throw new TapeRecallException("Error checking file existence");
		}
		if (fileOnDisk) {
			if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {
				String pfn = localFile.getAbsolutePath();

				UUID taskId = TapeRecallTO.buildTaskIdFromFileName(pfn);
				TapeRecallCatalog rtCat = new TapeRecallCatalog();
				boolean exists = false;
				try {
					exists = rtCat.existsTask(taskId, requestToken);
				} catch (DataAccessException e) {
					log.error("Error checking existence of a recall task for taskId="
						+ taskId + " requestToken=" + requestToken
						+ ". DataAccessException: " + e);
					throw new TapeRecallException("Error reading from tape recall table");
				}
				if (exists) {
					TapeRecallTO task;
					try {
						task = rtCat.getTask(taskId, requestToken);
					} catch (DataAccessException e) {
						log
							.error("Unable to update task recall status because unable to retrieve groupTaskId for token "
								+ requestToken + " DataAccessException: " + e.getMessage());
						throw new TapeRecallException(
							"Error reading from tape recall table");
					}
					UUID groupTaskId = task.getGroupTaskId();
					if (!TapeRecallStatus.getRecallTaskStatus(task.getStatusId()).equals(
						TapeRecallStatus.SUCCESS)) {
						boolean statusUpdated;
						try {
							statusUpdated = rtCat.changeGroupTaskStatus(groupTaskId,
								TapeRecallStatus.SUCCESS, new Date());
						} catch (DataAccessException e) {
							log.error("Unable to update task recall status for token "
								+ requestToken + " with groupTaskId=" + groupTaskId
								+ ". DataAccessException : " + e.getMessage());
							throw new TapeRecallException("Error updating tape recall table");
						}
						if (statusUpdated) {
							log.info("Task status set to SUCCESS. groupTaskId=" + groupTaskId
								+ " requestToken=" + requestToken + " pfn=" + pfn);
						}
						outputMessage = "true";
					} else {
						// status already updated, nothing to do
						outputMessage = "true";
					}
				} else {
					// no recall tasks for this file, nothing to do
					outputMessage = "true";
				}
			} else {
				// tape not enable for StoRI filesystem, nothing to do
				outputMessage = "true";
			}
		} else {
			outputMessage = "false";
		}

		return Response.ok(outputMessage, MediaType.TEXT_PLAIN_TYPE).status(200)
			.build();
	}
}
