/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package it.grid.storm.tape.recalltable.model;

public enum TapeRecallStatus {

	SUCCESS, QUEUED, IN_PROGRESS, ERROR, ABORTED, UNDEFINED;

	public static TapeRecallStatus getRecallTaskStatus(int statusId) {

		switch (statusId) {
			case 0:
				return SUCCESS;
			case 1:
				return QUEUED;
			case 2:
				return IN_PROGRESS;
			case 3:
				return ERROR;
			case 4:
				return ABORTED;
			default:
				return UNDEFINED;
		}
	}

	public int getStatusId() {

		switch (this) {
			case SUCCESS:
				return 0;
			case QUEUED:
				return 1;
			case IN_PROGRESS:
				return 2;
			case ERROR:
				return 3;
			case ABORTED:
				return 4;
			default:
				return 5;
		}
	}

	/**
	 * @param tapeRecallStatus
	 * @return
	 */
	public boolean isFinalStatus() {

		return SUCCESS.equals(this) || ERROR.equals(this) || ABORTED.equals(this);
	}

	/**
	 * Returns true if the provided status id refers to a status that does not allows state
	 * transitions
	 * 
	 * @param statusId
	 * @return
	 */
	public static boolean isFinalStatus(int statusId) {

		return getRecallTaskStatus(statusId).isFinalStatus();
	}

	/**
	 * @param otherStatusId
	 * @return
	 */
	public boolean precedes(int otherStatusId) {

		return precedes(getRecallTaskStatus(otherStatusId));
	}

	/**
	 * Determines if there is a sequence of transitions that can bring from this status to the given
	 * status parameter
	 * 
	 * NOTE: valid transitions are : queued -> inProgress inProgress -> \<any final status\>
	 * 
	 * @param otherStatus
	 * @return
	 */
	public boolean precedes(TapeRecallStatus otherStatus) {

		if (this == otherStatus || this == UNDEFINED || otherStatus == UNDEFINED) {
			return false;
		}
		if (this == QUEUED) {
			return true;
		}
		return this == IN_PROGRESS && otherStatus.isFinalStatus();
	}
}
