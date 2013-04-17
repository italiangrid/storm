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

package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * GetSpaceTokens output data.
 * 
 * 
 * @author lucamag
 * @author Alberto Forti
 * 
 * @date May 29, 2008
 * 
 */
public class GetSpaceTokensOutputData implements OutputData {

	private TReturnStatus status = null;
	private ArrayOfTSpaceToken arrayOfSpaceTokens = null;

	public GetSpaceTokensOutputData() {

	}

	public GetSpaceTokensOutputData(TReturnStatus status,
		ArrayOfTSpaceToken arrayOfSpaceTokens) {

		this.status = status;
		this.arrayOfSpaceTokens = arrayOfSpaceTokens;
	}

	/**
	 * Returns the status.
	 */
	public TReturnStatus getStatus() {

		return status;
	}

	/**
	 * Sets the status.
	 */
	public void setStatus(TReturnStatus status) {

		this.status = status;
	}

	/**
	 * Returns arrayOfSpaceTokens.
	 */
	public ArrayOfTSpaceToken getArrayOfSpaceTokens() {

		return this.arrayOfSpaceTokens;
	}

	/**
	 * Sets arrayOfSpaceTokens.
	 */
	public void setArrayOfSpaceTokens(ArrayOfTSpaceToken arrayOfSpaceTokens) {

		this.arrayOfSpaceTokens = arrayOfSpaceTokens;
	}

	// @Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}
}
