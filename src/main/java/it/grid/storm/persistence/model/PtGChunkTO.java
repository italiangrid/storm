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

package it.grid.storm.persistence.model;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TStorageSystemInfo;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a PrepareToGetChunkData, that is part of a multifile
 * PrepareToGet srm request. It contains data about: the requestToken, the
 * fromSURL and the storageSystemInfo for that SURL, the requested lifeTime of
 * pinning, the requested fileStorageType and any available spaceToken, the
 * TDirOption which explains whether the requested SURL is a directory and if it
 * must be recursed at all levels, as well as the desired number of levels to
 * recurse, the desired transferProtocols in order of preference, the fileSize,
 * the estimatedTimeOnQueue, the estimatedProcessingTime, the transferURL for
 * the supplied SURL, and the remainingPinTime.
 * 
 * @author EGRID - ICTP Trieste
 * @date March 21st, 2005
 * @version 2.0
 */
public class PtGChunkTO {

	private static final Logger log = LoggerFactory.getLogger(PtGChunkTO.class); 

	private TRequestToken requestToken; 

	private TSURL fromSURL; 
	private TStorageSystemInfo storageSystemInfo; 

	private TLifeTimeInSeconds lifeTime; // requested lifetime for fromSURL -
																				// BEWARE!!! It is the pin time!!!
	private TFileStorageType fileStorageType; // TFileStorageType requested for
																						// specific fromSURL to get
	private TSpaceToken spaceToken; // SpaceToken to use for fromSURL
	private TDirOption dirOption; // specifies if the request regards a directory
																// and related info

	private TURLPrefix transferProtocols; // list of desired transport protocols
																				// for fromSURL

	private TSizeInBytes fileSize; // size of file
	private TReturnStatus status; // return status for this chunk of request
	private TLifeTimeInSeconds estimatedWaitTimeOnQueue; // estimated time this
																												// chunk will remain in
																												// queue
	private TLifeTimeInSeconds estimatedProcessingTime; // estimated time this
																											// chunk will take to be
																											// processed
	private TTURL transferURL; // TURL for picking up the requested file
	private TLifeTimeInSeconds remainingPinTime; // estimated time remaining for
																								// Pin validity

	public PtGChunkTO(TRequestToken requestToken, TSURL fromSURL,
		TStorageSystemInfo storageSystemInfo, TLifeTimeInSeconds lifeTime,
		TFileStorageType fileStorageType, TSpaceToken spaceToken,
		TDirOption dirOption, TURLPrefix transferProtocols, TSizeInBytes fileSize,
		TReturnStatus status, TLifeTimeInSeconds estimatedWaitTimeOnQueue,
		TLifeTimeInSeconds estimatedProcessingTime, TTURL transferURL,
		TLifeTimeInSeconds remainingPinTime)
		throws InvalidPtGChunkDataAttributesException {

		boolean ok = requestToken != null && fromSURL != null
			&& storageSystemInfo != null && lifeTime != null
			&& fileStorageType != null && spaceToken != null && dirOption != null
			&& transferProtocols != null && fileSize != null && status != null
			&& estimatedWaitTimeOnQueue != null && estimatedProcessingTime != null
			&& transferURL != null && remainingPinTime != null;

		if (!ok) {
			throw new InvalidPtGChunkDataAttributesException(requestToken, fromSURL,
				storageSystemInfo, lifeTime, fileStorageType, spaceToken, dirOption,
				transferProtocols, fileSize, status, estimatedWaitTimeOnQueue,
				estimatedProcessingTime, transferURL, remainingPinTime);
		}
		this.requestToken = requestToken;
		this.fromSURL = fromSURL;
		this.storageSystemInfo = storageSystemInfo;
		this.lifeTime = lifeTime;
		this.fileStorageType = fileStorageType;
		this.spaceToken = spaceToken;
		this.dirOption = dirOption;
		this.transferProtocols = transferProtocols;
		this.fileSize = fileSize;
		this.status = status;
		this.estimatedWaitTimeOnQueue = estimatedWaitTimeOnQueue;
		this.estimatedProcessingTime = estimatedProcessingTime;
		this.transferURL = transferURL;
	}

	/**
	 * Method that returns the requestToken of the srm request to which this chunk
	 * belongs.
	 */
	public TRequestToken requestToken() {

		return requestToken;
	}

	/**
	 * Method that returns the fromSURL of the srm request to which this chunk
	 * belongs.
	 */
	public TSURL fromSURL() {

		return fromSURL;
	}

	/**
	 * Method that returns the storageSystemInfo of the srm request to which this
	 * chunk belongs
	 */
	public TStorageSystemInfo storageSystemInfo() {

		return storageSystemInfo;
	}

	/**
	 * Method that returns the requested pin life time for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds lifeTime() {

		return lifeTime;
	}

	/**
	 * Method that returns the filerequested pin life time for this chunk of the
	 * srm request.
	 */
	public TFileStorageType fileStorageType() {

		return fileStorageType;
	}

	/**
	 * Method that returns the space token supplied for this chunk of the srm
	 * request.
	 */
	public TSpaceToken spaceToken() {

		return spaceToken;
	}

	/**
	 * Method that returns the dirOption specified in the srm request.
	 */
	public TDirOption dirOption() {

		return dirOption;
	}

	/**
	 * Method that returns a TURLPrefix containing the transfer protocols desired
	 * for this chunk of the srm request.
	 */
	public TURLPrefix transferProtocols() {

		return transferProtocols;
	}

	/**
	 * Method that returns the status for this chunk of the srm request.
	 */
	public TReturnStatus status() {

		return status;
	}

	/**
	 * Method that returns the file size for this chunk of the srm request.
	 */
	public TSizeInBytes fileSize() {

		return fileSize;
	}

	/**
	 * Method that returns the estimated time in queue for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds estimatedWaitTimeOnQueue() {

		return estimatedWaitTimeOnQueue;
	}

	/**
	 * Method that returns the estimated processing time for this chunk of the srm
	 * request.
	 */
	public TLifeTimeInSeconds estimatedProcessingTime() {

		return estimatedProcessingTime;
	}

	/**
	 * Method that returns the TURL for this chunk of the srm request.
	 */
	public TTURL transferURL() {

		return transferURL;
	}

	/**
	 * Method that returns the estimated remaining pin time for this chunk of the
	 * srm request.
	 */
	public TLifeTimeInSeconds remainingPinTime() {

		return remainingPinTime;
	}

	/**
	 * Method that sets the status of this request to SRM_REQUEST_QUEUED; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_REQUEST_QUEUED(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_REQUEST_QUEUED, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_DONE; it needs the
	 * explanation String which describes the situation in greater detail; if a
	 * null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_DONE(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_DONE, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_INVALID_REQUEST; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_INVALID_REQUEST(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_AUTHORIZATION_FAILURE;
	 * it needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
			explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_ABORTED; it needs the
	 * explanation String which describes the situation in greater detail; if a
	 * null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_ABORTED(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_ABORTED, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_REQUEST_INPROGRESS; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_REQUEST_INPROGRESS(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_REQUEST_INPROGRESS, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_INTERNAL_ERROR; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_INTERNAL_ERROR(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_FATAL_INTERNAL_ERROR; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_FATAL_INTERNAL_ERROR(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_FATAL_INTERNAL_ERROR,
			explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_INVALID_PATH; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_INVALID_PATH(String explanation) {

		if (explanation == null) {
			explanation = "";
		}
		status = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, explanation);
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("PtGChunkData\n");
		sb.append("RequestToken=");
		sb.append(requestToken);
		sb.append("; ");
		sb.append("fromSURL=");
		sb.append(fromSURL);
		sb.append("; ");
		sb.append("storageSystemInfo=");
		sb.append(storageSystemInfo);
		sb.append("; ");
		sb.append("lifeTime=");
		sb.append(lifeTime);
		sb.append("; ");
		sb.append("fileStorageType=");
		sb.append(fileStorageType);
		sb.append("; ");
		sb.append("spaceToken");
		sb.append(spaceToken);
		sb.append("; ");
		sb.append("dirOption=");
		sb.append(dirOption);
		sb.append("; ");
		sb.append("transferProtocols=");
		sb.append(transferProtocols);
		sb.append("; ");
		sb.append("fileSize=");
		sb.append(fileSize);
		sb.append("; ");
		sb.append("status=");
		sb.append(status);
		sb.append("; ");
		sb.append("estimatedWaitTimeOnQueue=");
		sb.append(estimatedWaitTimeOnQueue);
		sb.append("; ");
		sb.append("estimatedProcessingTime=");
		sb.append(estimatedProcessingTime);
		sb.append("; ");
		sb.append("transferURL=");
		sb.append(transferURL);
		sb.append("; ");
		sb.append("remainingPinTime=");
		sb.append(remainingPinTime);
		sb.append(".");
		return sb.toString();
	}

	@Override
	public int hashCode() {

		int hash = 17;
		hash = 37 * hash + requestToken.hashCode();
		hash = 37 * hash + fromSURL.hashCode();
		hash = 37 * hash + storageSystemInfo.hashCode();
		hash = 37 * hash + lifeTime.hashCode();
		hash = 37 * hash + fileStorageType.hashCode();
		hash = 37 * hash + spaceToken.hashCode();
		hash = 37 * hash + dirOption.hashCode();
		hash = 37 * hash + transferProtocols.hashCode();
		hash = 37 * hash + fileSize.hashCode();
		hash = 37 * hash + status.hashCode();
		hash = 37 * hash + estimatedWaitTimeOnQueue.hashCode();
		hash = 37 * hash + estimatedProcessingTime.hashCode();
		hash = 37 * hash + transferURL.hashCode();
		hash = 37 * hash + remainingPinTime.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (!(o instanceof PtGChunkTO)) {
			return false;
		}
		PtGChunkTO cd = (PtGChunkTO) o;
		return requestToken.equals(cd.requestToken) && fromSURL.equals(cd.fromSURL)
			&& storageSystemInfo.equals(cd.storageSystemInfo)
			&& lifeTime.equals(cd.lifeTime)
			&& fileStorageType.equals(cd.fileStorageType)
			&& spaceToken.equals(cd.spaceToken) && dirOption.equals(cd.dirOption)
			&& transferProtocols.equals(cd.transferProtocols)
			&& fileSize.equals(cd.fileSize) && status.equals(cd.status)
			&& estimatedWaitTimeOnQueue.equals(cd.estimatedWaitTimeOnQueue)
			&& estimatedProcessingTime.equals(cd.estimatedProcessingTime)
			&& transferURL.equals(cd.transferURL)
			&& remainingPinTime.equals(cd.remainingPinTime);
	}

	/**
	 * Method used to set the size of the file corresponding to the requested
	 * SURL. If the supplied TSizeInByte is null, the nothing gets set!
	 */
	public TSizeInBytes setFileSize(final TSizeInBytes size) {

		if (size != null) {
			fileSize = size;
		}
		return null;
	};

	/**
	 * Method used to set the estimated time that the chunk will spend on the
	 * queue. If the supplied TLifeTimeInSeconds is null, then nothing gets set!
	 */
	public void setEstimatedWaitTimeOnQueue(final TLifeTimeInSeconds time) {

		if (time != null) {
			estimatedWaitTimeOnQueue = time;
		}
	};

	/**
	 * Method used to set the estimated time the processing will take. If the
	 * supplied TLifeTimeInSeconds is null, then nothing gets set!
	 */
	public void setEstimatedProcessingTime(final TLifeTimeInSeconds time) {

		if (time != null) {
			estimatedProcessingTime = time;
		}
	};

	/**
	 * Method used to set the transferURL associated to the SURL of this chunk. If
	 * TTURL is null, then nothing gets set!
	 */
	public void setTransferURL(final TTURL turl) {

		if (turl != null) {
			transferURL = turl;
		}
	};

	/**
	 * Method used in the mechanism for suspending and resuming a request. To be
	 * implemented! For now it always returns 0.
	 */
	public int getProgressCounter() {

		return 0;
	};
}
