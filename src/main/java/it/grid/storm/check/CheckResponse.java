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

package it.grid.storm.check;

/**
 * @author Michele Dibenedetto
 */
public class CheckResponse {

	/**
	 * The final status of a check
	 */
	private CheckStatus status;

	/**
	 * An error message describing a check failure
	 */
	private final String errorMessage;

	public CheckResponse(CheckStatus status, String message) {

		this.status = status;
		this.errorMessage = message;
	}

	/**
	 * Returns true if the check status is successfull
	 * 
	 * @return the successful
	 */
	public boolean isSuccessfull() {

		return this.status.equals(CheckStatus.SUCCESS);
	}

	public CheckStatus getStatus() {

		return this.status;
	}

	public void setStatus(CheckStatus status) {

		this.status = status;
	}

	/**
	 * @return the error message (eventually blank)
	 */
	public String getMessage() {

		return errorMessage;
	}

	public String toString() {

		if (errorMessage == null || errorMessage.trim().length() == 0) {
			return status.toString();
		} else {
			return "<" + status.toString() + " , " + errorMessage + ">";
		}
	}
}
