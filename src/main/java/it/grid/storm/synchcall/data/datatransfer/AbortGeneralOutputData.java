/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the AbortFiles Output Data associated with the SRM
 * request AbortFiles
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidAbortFilesOutputDataAttributeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortGeneralOutputData implements OutputData {

	private static final Logger log = LoggerFactory
		.getLogger(AbortGeneralOutputData.class);
	private TReturnStatus returnStatus = null;
	private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

	public AbortGeneralOutputData() {

		this.returnStatus = null;
		this.arrayOfFileStatus = null;
	}

	public AbortGeneralOutputData(TReturnStatus retStatus,
		ArrayOfTSURLReturnStatus arrayOfFileStatus)
		throws InvalidAbortFilesOutputDataAttributeException {

		boolean ok = (arrayOfFileStatus == null);

		if (!ok) {
			throw new InvalidAbortFilesOutputDataAttributeException(arrayOfFileStatus);
		}

		this.returnStatus = retStatus;
		this.arrayOfFileStatus = arrayOfFileStatus;
	}

	/**
	 * Returns the returnStatus field
	 * 
	 * @return TReturnStatus
	 */
	public TReturnStatus getReturnStatus() {

		return returnStatus;
	}

	/**
	 * Set the returnStatus field
	 * 
	 * @param returnStatus
	 */
	public void setReturnStatus(TReturnStatus returnStatus) {

		this.returnStatus = returnStatus;
	}

	/**
	 * Returns the arrayOfFileStatuses field
	 * 
	 * @return TSURLReturnStatus
	 */
	public ArrayOfTSURLReturnStatus getArrayOfFileStatuses() {

		return arrayOfFileStatus;
	}

	/**
	 * Set the arrayOfFileStatuses field
	 * 
	 * @param arrayOfFileStatuses
	 */
	public void setArrayOfFileStatuses(
		ArrayOfTSURLReturnStatus arrayOfFileStatuses) {

		this.arrayOfFileStatus = arrayOfFileStatuses;
	}

	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}

}
