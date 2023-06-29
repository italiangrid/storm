/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check;

/**
 * @author Michele Dibenedetto
 */
public enum CheckStatus {
	SUCCESS, FAILURE, CRITICAL_FAILURE, NOT_APPLICABLE, INDETERMINATE;

	/**
	 * Performs the logic "and" between status and bol
	 * 
	 * @param status
	 * @param bol
	 * @return a CheckStatus that is successful only if bol is true and status is
	 *         successful
	 */
	public static CheckStatus and(CheckStatus status, boolean bol) {

		CheckStatus otherStatus;
		if (bol) {
			otherStatus = SUCCESS;
		} else {
			otherStatus = FAILURE;
		}
		return and(status, otherStatus);
	}

	/**
	 * Performs the logic "and" between status and bol
	 * 
	 * @param status
	 * @param otherStatus
	 * @return a successful CheckStatus if the provided status and otherStatus are
	 *         successful, a failed CheckStatus if at least one of status and
	 *         otherStatus is failed, a notApplicable status if status and
	 *         otherStatus are notApplicable, an indeterminate status otherwise
	 */
	public static CheckStatus and(CheckStatus status, CheckStatus otherStatus) {

		if (SUCCESS.equals(status) && SUCCESS.equals(otherStatus)) {
			return SUCCESS;
		}
		if ((FAILURE.equals(status) && FAILURE.equals(otherStatus))
			|| ((FAILURE.equals(status) || FAILURE.equals(otherStatus)) && (SUCCESS
				.equals(status) || SUCCESS.equals(otherStatus)))) {
			return FAILURE;
		}
		if (CRITICAL_FAILURE.equals(status) || CRITICAL_FAILURE.equals(otherStatus)) {
			return CRITICAL_FAILURE;
		}
		if (NOT_APPLICABLE.equals(status) && NOT_APPLICABLE.equals(otherStatus)) {
			return NOT_APPLICABLE;
		}
		return INDETERMINATE;
	}
}
