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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortFilesOutputData extends AbortGeneralOutputData {

	private static final Logger log = LoggerFactory
		.getLogger(AbortFilesOutputData.class);
	private TReturnStatus returnStatus = null;
	private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

	public AbortFilesOutputData() {

		this.returnStatus = null;
		this.arrayOfFileStatus = null;
	}

	public static AbortFilesOutputData make(AbortGeneralOutputData filesOutData) {

		// Create an output data from an AbortFiles output data.
		// this.returnStatus = filesOutData.getReturnStatus();
		// this.arrayOfFileStatus = filesOutData.getArrayOfFileStatuses();
		return new AbortFilesOutputData(filesOutData.getReturnStatus(),
			filesOutData.getArrayOfFileStatuses());
	}

	public AbortFilesOutputData(TReturnStatus retStatus,
		ArrayOfTSURLReturnStatus arrayOfFileStatus)
	// throws InvalidAbortFilesOutputDataAttributeException
	{

		boolean ok = (arrayOfFileStatus == null);

		if (!ok) {
			;// throw new
				// InvalidAbortFilesOutputDataAttributeException(arrayOfFileStatus);
		}

		this.returnStatus = retStatus;
		this.arrayOfFileStatus = arrayOfFileStatus;
	}

	/**
	 * Returns the returnStatus field
	 * 
	 * @return TReturnStatus
	 */
	@Override
	public TReturnStatus getReturnStatus() {

		return returnStatus;
	}

	/**
	 * Set the returnStatus field
	 * 
	 * @param returnStatus
	 */
	@Override
	public void setReturnStatus(TReturnStatus returnStatus) {

		this.returnStatus = returnStatus;
	}

	/**
	 * Returns the arrayOfFileStatuses field
	 * 
	 * @return TSURLReturnStatus
	 */
	@Override
	public ArrayOfTSURLReturnStatus getArrayOfFileStatuses() {

		return arrayOfFileStatus;
	}

	/**
	 * Set the arrayOfFileStatuses field
	 * 
	 * @param arrayOfFileStatuses
	 */
	@Override
	public void setArrayOfFileStatuses(
		ArrayOfTSURLReturnStatus arrayOfFileStatuses) {

		this.arrayOfFileStatus = arrayOfFileStatuses;
	}

	@Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return false;
	}

}
