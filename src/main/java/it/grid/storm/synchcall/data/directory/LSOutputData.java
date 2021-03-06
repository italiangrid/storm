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

package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidLSOutputAttributeException;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the LS Output Data associated with the SRM request,
 * that is it contains info about: ...,ecc.
 * 
 * @author lucamag
 * @date May 28, 2008
 * 
 */

public class LSOutputData implements OutputData {

	private TReturnStatus returnStatus = null;
	private TRequestToken requestToken = null;
	private ArrayOfTMetaDataPathDetail details = null;

	public LSOutputData() {

	}

	public LSOutputData(TReturnStatus retStatus, TRequestToken token,
		ArrayOfTMetaDataPathDetail details)
		throws InvalidLSOutputAttributeException {

		boolean ok = (details == null);

		if (!ok)
			throw new InvalidLSOutputAttributeException(details);

		this.returnStatus = retStatus;
		this.requestToken = token;
		this.details = details;

	}

	/**
	 * Method that get return Status.
	 */
	public TReturnStatus getStatus() {

		return returnStatus;
	}

	/**
	 * Set ReturnStatus
	 */
	public void setStatus(TReturnStatus retStat) {

		this.returnStatus = retStat;
	}

	/**
	 * Method that get return Status.
	 */
	public TRequestToken getRequestToken() {

		return this.requestToken;
	}

	/**
	 * Set TRequestToken
	 */
	public void setRequestToken(TRequestToken token) {

		this.requestToken = token;
	}

	/**
	 * Method that return ArrayOfTMetaDataPath.
	 */
	public ArrayOfTMetaDataPathDetail getDetails() {

		return details;
	}

	/**
	 * Set ArrayOfTMetaDataPath
	 */
	public void setDetails(ArrayOfTMetaDataPathDetail details) {

		this.details = details;
	}

	// @Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}
}
