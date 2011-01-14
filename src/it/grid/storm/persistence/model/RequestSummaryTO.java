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

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;

/**
 * This class represents the SummaryData associated with the SRM request, that is
 * it contains info about: TRequestToken, TRequsetType, total files in this request,
 * number of files in queue, number of files progressing, number of files finished,
 * and whether the request is currently suspended.
 *
 * @author  EGRID - ICTP Trieste
 * @date    March 18th, 2005
 * @version 3.0
 */
public class RequestSummaryTO  {

    private TRequestToken requestToken = null;  //TRequestToken of SRM request
    private TRequestType requestType = null;    //request type of SRM request
    private int totalFilesInThisRequest = 0;    //total number of files in SRM request
    private int numOfQueuedRequests = 0;        //number of files in SRM request that are in queue
    private int numOfProgressingRequests = 0;     //number of files in SRM request that are still in progress
    private int numFinished = 0;                //number of files in SRM request whose processing has completed
    private boolean isSuspended = false;        //flag that indicates whether the SRM request is suspended

    public RequestSummaryTO(TRequestToken requestToken, TRequestType requestType, int totalFilesInThisRequest, int numOfQueuedRequests, int numOfProgressingRequests, int numFinished, boolean isSuspended) throws InvalidRequestSummaryDataAttributesException {
        boolean ok = requestToken!=null &&
            requestType!=null &&
            totalFilesInThisRequest>=0 &&
            numOfQueuedRequests>=0 &&
            numOfProgressingRequests>=0 &&
            numFinished>=0;
        if (!ok) throw new InvalidRequestSummaryDataAttributesException(requestToken,requestType,totalFilesInThisRequest,numOfQueuedRequests,numOfProgressingRequests,numFinished);
        this.requestToken = requestToken;
        this.requestType = requestType;
        this.totalFilesInThisRequest = totalFilesInThisRequest;
        this.numOfQueuedRequests = numOfQueuedRequests;
        this.numOfProgressingRequests = numOfProgressingRequests;
        this.numFinished = numFinished;
        this.isSuspended = isSuspended;
    }

    /**
     * Method that returns the SRM request TRequestToken
     */
    public TRequestToken requestToken() {
        return requestToken;
    }

    /**
     * Method that returns the type of SRM request
     */
    public TRequestType requestType() {
        return requestType;
    }

    /**
     * Method that returns the total number of files in the SRM request
     */
    public int totalFilesInThisRequest() {
        return totalFilesInThisRequest;
    }

    /**
     * Method that returns the number of files in the SRM request that are currently
     * in queue.
     */
    public int numOfQueuedRequests() {
        return numOfQueuedRequests;
    }

    /**
     * Method that returns the number of files in the SRM request that are currently
     * in progress.
     */
    public int numOfProgressingRequests() {
        return numOfProgressingRequests;
    }

    /**
     * Method that returns the number of files in the SRM request that are currently finished.
     */
    public int numFinished() {
        return numFinished;
    }

    /**
     * Method that tells whether the SRM requst is suspended.
     */
    public boolean isSuspended() {
        return isSuspended;
    }

    /**
     * Method that increments the counter for the number of files in queue.
     */
    public void incNumOfQueuedRequests() {
        numOfQueuedRequests++;
    }

    /**
     * Methos used to decrement the counter fo the number of files in queue.
     */
    public void decNumOfQueuedRequests() {
        numOfQueuedRequests--;
    }

    /**
     * Method used to increment the counter for the number of progressing requests.
     */
    public void incNumOfProgressingRequests() {
        numOfProgressingRequests++;
    }

    /**
     * Method used to decrement the counter for the number of progressing requests.
     */
    public void decNumOfProgressingRequests() {
        numOfProgressingRequests--;
    }

    /**
     * Method used to increment the counter for the number of total files in the request.
     */
    public void incTotalFilesInThisRequest() {
        totalFilesInThisRequest++;
    }

    /**
     * Method used to decrement the counter fot the number of total files in this request.
     */
    public void decTotalFilesInThisRequest() {
        totalFilesInThisRequest--;
    }

    /**
     * Method used to increment the counter for the processing of files that are
     * currently finished.
     */
    public void incNumFinished() {
        numFinished++;
    }

    /**
     * Method used to decrement the counter that keeps track of the number of
     * files that are currently finished.
     */
    public void decNumFinished() {
        numFinished--;
    }

    /**
     * Method used to set the SRM flag that signals the processing of the
     * request this RequestSummaryData applies to, is suspended.
     */
    public void srmSuspend() {
        isSuspended = true;
    }

    /**
     * Method used to set the SRM flag that signals the procesing of the request
     * this RequestSummaryData applies to, is _not_ suspended
     */
    public void srmUnSuspend() {
        isSuspended = false;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SummaryRequestData");
        sb.append("; requestToken="); sb.append(requestToken);
        sb.append("; requestType="); sb.append(requestType);
        sb.append("; totalFilesInThisRequest="); sb.append(totalFilesInThisRequest);
        sb.append("; numOfQueuedRequests="); sb.append(numOfQueuedRequests);
        sb.append("; numOfProgressingRequests="); sb.append(numOfProgressingRequests);
        sb.append("; numFinished="); sb.append(numFinished);
        sb.append("; isSuspended="); sb.append(isSuspended);
        sb.append(".");
        return sb.toString();
    }

    public int hashCode() {
        int hash = 17;
        hash = 37*hash + requestToken.hashCode();
        hash = 37*hash + requestType.hashCode();
        hash = 37*hash + totalFilesInThisRequest;
        hash = 37*hash + numOfQueuedRequests;
        hash = 37*hash + numOfProgressingRequests;
        hash = 37*hash + numFinished;
        hash = (isSuspended)? (37*hash+1) : (37*hash+0);
        return hash;
    }

    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof RequestSummaryTO)) return false;
        RequestSummaryTO rsd = (RequestSummaryTO) o;
        return requestToken.equals(rsd.requestToken) &&
            requestType.equals(rsd.requestType) &&
            (totalFilesInThisRequest==rsd.totalFilesInThisRequest) &&
            (numOfQueuedRequests==rsd.numOfQueuedRequests) &&
            (numOfProgressingRequests==rsd.numOfProgressingRequests) &&
            (numFinished==rsd.numFinished) &&
            (isSuspended==rsd.isSuspended);
    }
}
