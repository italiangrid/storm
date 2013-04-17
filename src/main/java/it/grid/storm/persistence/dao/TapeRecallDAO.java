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

package it.grid.storm.persistence.dao;

import it.grid.storm.persistence.exceptions.DataAccessException;
import it.grid.storm.persistence.model.TapeRecallTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Tape Recall Data Access Object (DAO)
 */

public abstract class TapeRecallDAO extends AbstractDAO {

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getNumberInProgress() throws DataAccessException;

	/**
	 * 
	 * @param voName
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getNumberInProgress(String voName)
		throws DataAccessException;

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getNumberQueued() throws DataAccessException;

	/**
	 * 
	 * @param voName
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getNumberQueued(String voName) throws DataAccessException;

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getReadyForTakeOver() throws DataAccessException;

	/**
	 * 
	 * @param voName
	 * @return
	 * @throws DataAccessException
	 */
	public abstract int getReadyForTakeOver(String voName)
		throws DataAccessException;

	/**
	 * @param taskId
	 * @param requestToken
	 * @return
	 * @throws DataAccessException
	 */
	public abstract TapeRecallTO getTask(UUID taskId, String requestToken)
		throws DataAccessException;

	/**
	 * @param groupTaskId
	 * @return
	 * @throws DataAccessException
	 */
	public abstract List<TapeRecallTO> getGroupTasks(UUID groupTaskId)
		throws DataAccessException;

	/**
	 * Verifies that a recall task with the given taskId and request token exists
	 * on the database
	 * 
	 * @param taskId
	 * @param requestToken
	 * @return true if the recall task exists
	 * @throws DataAccessException
	 */
	public abstract boolean existsTask(UUID taskId, String requestToken)
		throws DataAccessException;

	/**
	 * @param groupTaskId
	 * @return
	 * @throws DataAccessException
	 */
	public abstract boolean existsGroupTask(UUID groupTaskId)
		throws DataAccessException;

	/**
	 * Method called by a garbage collector that removes all tape recalls that are
	 * not in QUEUED (1) or IN_PROGRESS (2) status
	 * 
	 * @throws DataAccessException
	 */
	public abstract void purgeCompletedTasks(int numMaxToPurge)
		throws DataAccessException;

	/**
	 * @param taskId
	 * @param newValue
	 * @throws DataAccessException
	 */
	public abstract void setGroupTaskRetryValue(UUID groupTaskId, int value)
		throws DataAccessException;

	/**
	 * 
	 * @return
	 * @throws DataAccessException
	 */
	public abstract TapeRecallTO takeoverTask() throws DataAccessException;

	/**
	 * 
	 * @param voName
	 * @return
	 * @throws DataAccessException
	 */
	public abstract TapeRecallTO takeoverTask(String voName)
		throws DataAccessException;

	/**
	 * Performs the take-over of max numberOfTaks tasks possibly returning more
	 * than one file recall task for some files
	 * 
	 * @param numberOfTaks
	 * @return
	 * @throws DataAccessException
	 */
	public abstract List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks)
		throws DataAccessException;

	/**
	 * 
	 * @param numberOfTaks
	 * @param voName
	 * @return
	 * @throws DataAccessException
	 */
	public abstract List<TapeRecallTO> takeoverTasksWithDoubles(int numberOfTaks,
		String voName) throws DataAccessException;

	/**
	 * @param task
	 * @param statuses
	 * @param proposedGroupTaskId
	 * @return
	 * @throws DataAccessException
	 */
	public abstract UUID insertCloneTask(TapeRecallTO task, int[] statuses,
		UUID proposedGroupTaskId) throws DataAccessException;

	/**
	 * @param groupTaskId
	 * @param statusId
	 * @return
	 * @throws DataAccessException
	 */
	public abstract boolean setGroupTaskStatus(UUID groupTaskId, int statusId,
		Date timestamp) throws DataAccessException;

}