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
