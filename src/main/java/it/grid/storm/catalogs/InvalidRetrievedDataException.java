/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

/**
 * Class that represents an Exception thrown by the ReservedSpaceCatalog when it
 * is asked to retrieve info from the persistence but the raw data is invalid
 * and does not allow a well-formed domain obejcts to be created.
 * 
 * @author: EGRID ICTP
 * @version: 1.0
 * @date: June 2005
 */
public class InvalidRetrievedDataException extends Exception {

	private static final long serialVersionUID = -3645913441787012438L;

	private String requestToken;
	private String requestType;
	private int totalFilesInThisRequest;
	private int numOfQueuedRequests;
	private int numOfProgressing;
	private int numFinished;
	private boolean isSuspended;

	/**
	 * Constructor that requires the attributes that caused the exception to be
	 * thrown.
	 */
	public InvalidRetrievedDataException(String requestToken, String requestType,
		int totalFilesInThisRequest, int numOfQueuedRequests,
		int numOfProgressingRequests, int numFinished, boolean isSuspended) {

		this.requestToken = requestToken;
		this.requestType = requestType;
		this.totalFilesInThisRequest = totalFilesInThisRequest;
		this.numOfQueuedRequests = numOfQueuedRequests;
		this.numOfProgressing = numOfProgressingRequests;
		this.numFinished = numFinished;
		this.isSuspended = isSuspended;
	}

	public String toString() {

		return "InvalidRetrievedDataException: token=" + requestToken + " type="
			+ requestType + " total-files=" + totalFilesInThisRequest + " queued="
			+ numOfQueuedRequests + " progressing=" + numOfProgressing + " finished="
			+ numFinished + " isSusp=" + isSuspended;
	}

}
