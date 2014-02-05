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
 * This class represents the TSURLLifetimeReturnStatus data associated with the
 * SRM request.
 * 
 * @author Alberto Forti
 * @author CNAF-INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSURLLifetimeReturnStatus {

	private TSURL surl = null;
	private TReturnStatus returnStatus = null;
	private TLifeTimeInSeconds fileLifetime = null;
	private TLifeTimeInSeconds pinLifetime = null;

	public TSURLLifetimeReturnStatus() {

	}

	public TSURLLifetimeReturnStatus(TSURL surl, TReturnStatus status,
		TLifeTimeInSeconds fileLifetime, TLifeTimeInSeconds pinLifetime)
		throws InvalidTSURLLifetimeReturnStatusAttributeException {

		boolean ok = (surl != null);
		if (!ok)
			throw new InvalidTSURLLifetimeReturnStatusAttributeException(surl);
		this.surl = surl;
		this.returnStatus = status;
		this.fileLifetime = fileLifetime;
		this.pinLifetime = pinLifetime;
	}

	/**
	 * Returns the SURL.
	 * 
	 * @return TSURL
	 */
	public TSURL getSurl() {

		return surl;
	}

	/**
	 * Sets the SURL
	 * 
	 * @param surl
	 *          TSURL
	 */
	public void setSurl(TSURL surl) {

		this.surl = surl;
	}

	/**
	 * Set the status.
	 * 
	 * @param status
	 *          TReturnStatus
	 */
	public void setStatus(TReturnStatus status) {

		this.returnStatus = status;
	}

	/**
	 * Get the status.
	 * 
	 * @return TReturnStatus
	 */
	public TReturnStatus getStatus() {

		return this.returnStatus;
	}

	/**
	 * Get fileLifetime.
	 * 
	 * @return TLifeTimeInSeconds
	 */
	public TLifeTimeInSeconds getFileLifetime() {

		return this.fileLifetime;
	}

	/**
	 * Set fileLifetime.
	 * 
	 * @param fileLifetime
	 */
	public void setFileLifetime(TLifeTimeInSeconds fileLifetime) {

		this.fileLifetime = fileLifetime;
	}

	/**
	 * Get pinLifetime.
	 * 
	 * @return TLifeTimeInSeconds
	 */
	public TLifeTimeInSeconds getpinLifetime() {

		return this.pinLifetime;
	}

	/**
	 * Set pinLifetime.
	 * 
	 * @param fileLifetime
	 */
	public void setpinLifetime(TLifeTimeInSeconds pinLifetime) {

		this.pinLifetime = pinLifetime;
	}

	/**
	 * Add an element to 'outputVector'. The element is a Hashtable structure of
	 * this instance of TSURLLifetimeReturnStatus (used to comunicate with the
	 * FE).
	 * 
	 * @param outputVector
	 *          Vector
	 */
	public void encode(List outputVector) {

		Map surlRetStatusParam = new HashMap();
		if (this.surl != null)
			this.surl.encode(surlRetStatusParam, TSURL.PNAME_SURL);
		if (this.returnStatus != null)
			this.returnStatus.encode(surlRetStatusParam, TReturnStatus.PNAME_STATUS);
		if (this.fileLifetime != null)
			this.fileLifetime.encode(surlRetStatusParam,
				TLifeTimeInSeconds.PNAME_FILELIFETIME);
		if (this.pinLifetime != null)
			this.pinLifetime.encode(surlRetStatusParam,
				TLifeTimeInSeconds.PNAME_PINLIFETIME);

		outputVector.add(surlRetStatusParam);
	}
}
