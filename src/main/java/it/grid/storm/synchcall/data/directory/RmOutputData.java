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
 * This class represents the Rm Output Data associated with the SRM request,
 * that is it contains info about: ...,ecc. * @author Magnoni Luca
 * 
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidRmOutputAttributeException;

public class RmOutputData implements OutputData {

	private TReturnStatus returnStatus = null;
	private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

	public RmOutputData() {

	}

	public RmOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus details)
		throws InvalidRmOutputAttributeException {

		boolean ok = (details == null);
		if (!ok) {
			throw new InvalidRmOutputAttributeException(details);
		}
		this.returnStatus = retStatus;
		this.arrayOfFileStatus = details;
	}

	/**
	 * Method that return Status.
	 */

	public TReturnStatus getStatus() {

		return returnStatus;
	}

	/**
	 * Set ReturnStatus
	 * 
	 */
	public void setStatus(TReturnStatus retStat) {

		this.returnStatus = retStat;
	}

	/**
	 * Method that return TSURLReturnStatus[].
	 */

	public ArrayOfTSURLReturnStatus getSurlStatus() {

		return arrayOfFileStatus;
	}

	/**
	 * Set TSURLReturnStatus
	 * 
	 */
	public void setSurlStatus(ArrayOfTSURLReturnStatus details) {

		this.arrayOfFileStatus = details;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.OutputData#isSuccess()
	 */
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Get Log message
	 */
	// public String getLogMessage() {
	// StringBuffer buf = new StringBuffer("srmRm: ");
	// if((returnStatus!=null)&&
	// (returnStatus.getStatusCode()!=null)&&
	// (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))) {
	// buf.append("<"++">");
	//
	//
	// } else {
	//
	// }
	//
	// return buf.toString();
	// }
}
