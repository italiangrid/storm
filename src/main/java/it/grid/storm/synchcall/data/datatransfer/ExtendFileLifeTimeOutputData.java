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
 * This class represents the ExtendFileLifeTime Output Data.
 * 
 * @author Alberto Forti
 * @author CNAF-INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

public class ExtendFileLifeTimeOutputData implements OutputData {

	private TReturnStatus returnStatus = null;
	private ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses = null;

	public ExtendFileLifeTimeOutputData() {

		this.returnStatus = null;
		this.arrayOfFileStatuses = null;
	}

	public ExtendFileLifeTimeOutputData(TReturnStatus retStatus,
		ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses) {

		this.returnStatus = retStatus;
		this.arrayOfFileStatuses = arrayOfFileStatuses;
	}

	/**
	 * Returns the returnStatus field.
	 * 
	 * @return TReturnStatus
	 */
	public TReturnStatus getReturnStatus() {

		return returnStatus;
	}

	/**
	 * Set the returnStatus field.
	 * 
	 * @param returnStatus
	 *          TReturnStatus
	 */
	public void setReturnStatus(TReturnStatus returnStatus) {

		this.returnStatus = returnStatus;
	}

	/**
	 * Returns the arrayOfFileStatuses field.
	 * 
	 * @return ArrayOfTSURLLifetimeReturnStatus
	 */
	public ArrayOfTSURLLifetimeReturnStatus getArrayOfFileStatuses() {

		return arrayOfFileStatuses;
	}

	/**
	 * Set the arrayOfFileStatuses field.
	 * 
	 * @param arrayOfFileStatuses
	 */
	public void setArrayOfFileStatuses(
		ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses) {

		this.arrayOfFileStatuses = arrayOfFileStatuses;
	}

	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}
}
