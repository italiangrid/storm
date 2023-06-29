/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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
