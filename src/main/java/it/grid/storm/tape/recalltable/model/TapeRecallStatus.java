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

/**
 * @author zappi
 * 
 */
public enum TapeRecallStatus {

	SUCCESS(0, "success"), QUEUED(1, "queued"), IN_PROGRESS(2, "in-progress"), ERROR(3,
			"error"), ABORTED(4, "aborted"), UNDEFINED(5, "undefined");

	private final int statusId;
	private final String statusName;

	private TapeRecallStatus(int statusId, String statusName) {

		this.statusId = statusId;
		this.statusName = statusName;
	}

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

	public static TapeRecallStatus getRecallTaskStatus(String status) {

		if ("success".equalsIgnoreCase(status)) {
			return SUCCESS;
		}
		if ("queued".equalsIgnoreCase(status)) {
			return QUEUED;
		}
		if ("in-progress".equalsIgnoreCase(status)) {
			return IN_PROGRESS;
		}
		if ("error".equalsIgnoreCase(status)) {
			return ERROR;
		}
		if ("aborted".equalsIgnoreCase(status)) {
			return ABORTED;
		}
		return UNDEFINED;
	}

	/**
	 * @param tapeRecallStatus
	 * @return
	 */
	private boolean isFinalStatus() {

		return isFinalStatus(this.getStatusId());
	}

	/**
	 * Returns true if the provided status id refers to a status that does not allows state
	 * transitions
	 * 
	 * @param statusId
	 * @return
	 */
	public static boolean isFinalStatus(int statusId) {

		switch (statusId) {
			case 0:
				return true; // SUCCESS
			case 1:
				return false; // QUEUED
			case 2:
				return false; // IN_PROGRESS
			case 3:
				return true; // ERROR
			case 4:
				return true; // ABORTED
			default:
				return false; // UNDEFINED
		}
	}

	public int getStatusId() {

		return statusId;
	}

	/**
	 * @param otherStatusId
	 * @return
	 */
	public boolean precedes(int otherStatusId) {

		return precedes(TapeRecallStatus.getRecallTaskStatus(otherStatusId));
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

		if (UNDEFINED.equals(this) || UNDEFINED.equals(otherStatus)) {
			return false;
		}
		if (this.equals(otherStatus)) {
			return false;
		}
		if (QUEUED.equals(this)) {
			return true;
		}
		return IN_PROGRESS.equals(this) && otherStatus.isFinalStatus();
	}

	@Override
	public String toString() {

		return statusName;
	}
}
