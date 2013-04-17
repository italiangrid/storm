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

/**
 * This class represents the TStatusCode of TReturnStatus
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.Collection;
import java.util.LinkedList;

public enum TStatusCode {

	EMPTY, SRM_SUCCESS, SRM_FAILURE, SRM_AUTHENTICATION_FAILURE, SRM_AUTHORIZATION_FAILURE, SRM_INVALID_REQUEST, SRM_INVALID_PATH, SRM_FILE_LIFETIME_EXPIRED, SRM_SPACE_LIFETIME_EXPIRED, SRM_EXCEED_ALLOCATION, SRM_NO_USER_SPACE, SRM_NO_FREE_SPACE, SRM_DUPLICATION_ERROR, SRM_NON_EMPTY_DIRECTORY, SRM_TOO_MANY_RESULTS, SRM_INTERNAL_ERROR, SRM_FATAL_INTERNAL_ERROR, SRM_NOT_SUPPORTED, SRM_REQUEST_QUEUED(
		false), SRM_REQUEST_INPROGRESS(false), SRM_REQUEST_SUSPENDED(false), SRM_ABORTED, SRM_RELEASED, SRM_FILE_PINNED(
		false), SRM_FILE_IN_CACHE(false), SRM_FILE_BUSY, SRM_SPACE_AVAILABLE(false), SRM_LOWER_SPACE_GRANTED, SRM_DONE, SRM_PARTIAL_SUCCESS, SRM_REQUEST_TIMED_OUT, SRM_LAST_COPY, SRM_FILE_LOST, SRM_FILE_UNAVAILABLE, SRM_CUSTOM_STATUS(
		false);

	static {
		SRM_FILE_PINNED.addIncompatibleStatus(SRM_REQUEST_SUSPENDED);
		SRM_FILE_PINNED.addIncompatibleStatus(SRM_SPACE_AVAILABLE);
		SRM_FILE_PINNED.addIncompatibleStatus(SRM_FILE_BUSY);
		SRM_SPACE_AVAILABLE.addIncompatibleStatus(SRM_REQUEST_SUSPENDED);
		SRM_SPACE_AVAILABLE.addIncompatibleStatus(SRM_FILE_PINNED);
		SRM_SPACE_AVAILABLE.addIncompatibleStatus(SRM_FILE_BUSY);
	}
	private final boolean finalStatus;

	private final LinkedList<TStatusCode> incompatibleStatuses = new LinkedList<TStatusCode>();

	private TStatusCode(boolean isFinal) {

		this.finalStatus = isFinal;
	}

	private void addIncompatibleStatus(TStatusCode incompatibleStatus) {

		incompatibleStatuses.add(incompatibleStatus);
	}

	private TStatusCode() {

		this(true);
	}

	public String getValue() {

		return this.name();
	}

	public boolean isFinalStatus() throws IllegalArgumentException {

		return finalStatus;
	}

	/*
	 * For a given surl checks the compatibility of all associated statuses with
	 * the final status of the current operation (e.g.: SRM_FILE_PINNED for
	 * PtG/BoL or SRM_SPACE_AVAILABLE for PtP)
	 */
	public boolean isCompatibleWith(Collection<TReturnStatus> statuses) {

		for (TReturnStatus status : statuses) {
			if (!this.isCompatibleWith(status.getStatusCode())) {
				return false;
			}
		}
		return true;
	}

	/*
	 * To be compatible with the final status for the invoked operation
	 * (PtP/PtG/BoL) the passed-in status must be different and it must belong to
	 * the list of compatible statuses or just be a final status itself. For the
	 * PtG operation the compatibility must be true even if the provided
	 * statuscode is equal to the final status of the operation (SRM_FILE_PINNED)
	 */
	public boolean isCompatibleWith(TStatusCode statusCode) {

		if (statusCode.finalStatus) {
			return !finalStatus;
		}

		if (this.incompatibleStatuses.contains(statusCode))
			return false;
		else {
			if (this.equals(statusCode)) {
				if (statusCode.equals(SRM_FILE_PINNED))
					return true;
				else
					return false;
			} else
				return true;
		}
	}

}
