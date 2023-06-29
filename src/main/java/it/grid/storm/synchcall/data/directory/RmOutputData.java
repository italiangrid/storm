/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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

public class RmOutputData implements OutputData {

	private TReturnStatus returnStatus = null;
	private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

	public RmOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus details) {

		if (retStatus == null) {
			throw new IllegalArgumentException("RmOutputData: return status NULL");
		}
		this.returnStatus = retStatus;
		this.arrayOfFileStatus = details;
	}
	
	public RmOutputData(TReturnStatus retStatus) {

		this(retStatus, null);
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

	public boolean isSuccess() {

		return returnStatus.isSRM_SUCCESS();
	}

}
