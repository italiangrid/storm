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
 * This class represents the SpaceReservationOutputData associated with the SRM
 * request, that is it contains info about: UserID, spaceType, SizeDesired,
 * SizeGuaranteed,ecc. Number of files progressing, Number of files finished,
 * and whether the request is currently suspended.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.space;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidReserveSpaceOutputDataAttributesException;

public class ReserveSpaceOutputData implements Serializable, OutputData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9112229304313364826L;
	
	private static final Logger log = LoggerFactory.getLogger(ReserveSpaceOutputData.class);
	
	private TSizeInBytes spaceTotal = null;
	private TSizeInBytes spaceGuaranteed = null;
	private TLifeTimeInSeconds spaceLifetime = null;
	private TSpaceToken spaceToken = null;
	private TReturnStatus status = null;
	private TRetentionPolicyInfo retentionPolicyInfo = null;

	public ReserveSpaceOutputData(TReturnStatus status) {

		this.status = status;
	}

	public ReserveSpaceOutputData(TSizeInBytes spaceTotal,
		TSizeInBytes spaceGuaranteed, TLifeTimeInSeconds spaceLifetime,
		TSpaceToken spaceToken, TReturnStatus status)
		throws InvalidReserveSpaceOutputDataAttributesException {

		boolean ok = status != null;

		if (!ok) {
			throw new InvalidReserveSpaceOutputDataAttributesException(spaceTotal,
				spaceToken, status);
		}

		this.spaceTotal = spaceTotal;
		this.spaceGuaranteed = spaceGuaranteed;
		this.spaceLifetime = spaceLifetime;
		this.spaceToken = spaceToken;
		this.status = status;
	}

	/**
	 * Method that returns the number of files in the SRM request that are
	 * currently in progress.
	 */
	public TSpaceToken getSpaceToken() {

		return spaceToken;
	}

	/**
	 * Method that returns the number of files in the SRM request that are
	 * currently finished.
	 */
	public TSizeInBytes getGuaranteedSize() {

		return spaceGuaranteed;
	}

	public TSizeInBytes getTotalSize() {

		return spaceTotal;
	}

	public TLifeTimeInSeconds getLifeTimeInSeconds() {

		return spaceLifetime;
	}

	/**
	 * Method that return TReturnStatus status.
	 */
	public TReturnStatus getStatus() {

		return status;
	}

	/**
	 * Method that returns TRetentionPolicyInfo.
	 */
	public TRetentionPolicyInfo getRetentionPolicyInfo() {

		return retentionPolicyInfo;
	}

	public void setRetentionPolicyInfo(TRetentionPolicyInfo retentionPolicyInfo) {

		this.retentionPolicyInfo = retentionPolicyInfo;
	}

	public void setStatus(TReturnStatus status) {

		this.status = status;
	}

	/**
	 * Print
	 */
	public void print() {

		log.info("****SRM_SR_OutputData******");
		log.info("TSizeInBytesTotal: " + spaceTotal);
		log.info("TSizeInBytesGuar: " + spaceGuaranteed);
		log.info("LifeTimeInSeconds: " + spaceLifetime);
		log.info("TSpaceToken: " + spaceToken);
		log.info("TReturnStatus: " + status);

	}

	// @Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}
}
