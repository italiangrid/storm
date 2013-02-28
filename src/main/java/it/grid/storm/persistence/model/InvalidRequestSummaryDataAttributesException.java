/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.*;

/**
 * This class represents an Exception thrown when a RequestSummaryData object is created
 * with any invalid attributes: null TRequestToken, null TRequestType, totalFilesInThisRequest<0,
 * numOfQueuedRequests<0, numOfProgessingRequests<0, numFinished<0.
 *
 * @author  EGRID - ICTP Trieste
 * @date    March 18th, 2005
 * @version 2.0
 */
public class InvalidRequestSummaryDataAttributesException extends Exception {

    //booleans true if the corresponding variablesare null or negative
    private boolean nullRequestToken = true;
    private boolean nullRequestType = true;
    private boolean negTotalFilesInThisRequest = true;
    private boolean negNumOfQueuedRequests = true;
    private boolean negNumOfProgressingRequests = true;
    private boolean negNumFinished = true;

    /**
     * Constructor that requires the attributes that caused the
     * exception to be thrown.
     */
    public InvalidRequestSummaryDataAttributesException(TRequestToken requestToken, TRequestType requestType, int totalFilesInThisRequest, int numOfQueuedRequests, int numOfProgressingRequests, int numFinished) {
        nullRequestToken = (requestToken==null);
        nullRequestType = (requestType==null);
        negTotalFilesInThisRequest = (totalFilesInThisRequest<0);
        negNumOfQueuedRequests = (numOfQueuedRequests<0);
        negNumOfProgressingRequests = (numOfProgressingRequests<0);
        negNumFinished = (numFinished<0);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid RequestSummaryData attributes exception: ");
        sb.append("null-requestToken="); sb.append(nullRequestToken);
        sb.append("; null-requestType="); sb.append(nullRequestType);
        sb.append("; negative-totalFilesInThisRequest="); sb.append(negTotalFilesInThisRequest);
        sb.append("; negative-numOfQueuedRequests="); sb.append(negNumOfQueuedRequests);
        sb.append("; negative-numOfProgressingRequests="); sb.append(negNumOfProgressingRequests);
        sb.append("; negative-numFinished="); sb.append(negNumFinished);
        sb.append(".");
        return sb.toString();
    }
}
