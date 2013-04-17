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

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

public class MkdirOutputData implements OutputData {

	private TReturnStatus returnStatus = null;

	public MkdirOutputData() {

	}

	public MkdirOutputData(TReturnStatus retStatus) {

		this.returnStatus = retStatus;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.OutputData#isSuccess()
	 */
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}

}
