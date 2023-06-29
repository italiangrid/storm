/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;

/**
 * This class represents an Exception thrown when a RequestSummaryData object is created with any
 * invalid attributes: null TRequestToken, null TRequestType, totalFilesInThisRequest<0,
 * numOfQueuedRequests<0, numOfProgessingRequests<0, numFinished<0.
 * 
 * @author EGRID - ICTP Trieste
 * @date March 18th, 2005
 * @version 2.0
 */
public class InvalidRequestSummaryDataAttributesException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final boolean nullRequestToken;
	private final boolean nullRequestType;
	private final boolean negTotalFilesInThisRequest;
	private final boolean negNumOfQueuedRequests;
	private final boolean negNumOfProgressingRequests;
	private final boolean negNumFinished;

	/**
	 * Constructor that requires the attributes that caused the exception to be thrown.
	 */
	public InvalidRequestSummaryDataAttributesException(TRequestToken requestToken,
			TRequestType requestType, int totalFilesInThisRequest, int numOfQueuedRequests,
			int numOfProgressingRequests, int numFinished) {

		nullRequestToken = (requestToken == null);
		nullRequestType = (requestType == null);
		negTotalFilesInThisRequest = (totalFilesInThisRequest < 0);
		negNumOfQueuedRequests = (numOfQueuedRequests < 0);
		negNumOfProgressingRequests = (numOfProgressingRequests < 0);
		negNumFinished = (numFinished < 0);
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Invalid RequestSummaryData attributes exception: ");
		sb.append("null-requestToken=");
		sb.append(nullRequestToken);
		sb.append("; null-requestType=");
		sb.append(nullRequestType);
		sb.append("; negative-totalFilesInThisRequest=");
		sb.append(negTotalFilesInThisRequest);
		sb.append("; negative-numOfQueuedRequests=");
		sb.append(negNumOfQueuedRequests);
		sb.append("; negative-numOfProgressingRequests=");
		sb.append(negNumOfProgressingRequests);
		sb.append("; negative-numFinished=");
		sb.append(negNumFinished);
		sb.append(".");
		return sb.toString();
	}
}
