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

package it.grid.storm.synchcall.data.discovery;

import java.util.Iterator;

import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TExtraInfo;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project. This class represents the Ping
 * Output Data
 * 
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @author Alberto Forti
 * 
 * @date May 28, 2008
 * 
 */

public class PingOutputData implements OutputData {

	private String versionInfo = null;
	private ArrayOfTExtraInfo extraInfoArray = null;

	public PingOutputData() {

	}

	public PingOutputData(String versionInfo, ArrayOfTExtraInfo otherInfo) {

		this.versionInfo = versionInfo;
		this.extraInfoArray = otherInfo;
	}

	/**
	 * Set versionInfo.
	 * 
	 * @param versionInfo
	 *          String
	 */
	public void setVersionInfo(String versionInfo) {

		this.versionInfo = versionInfo;
	}

	/**
	 * Get versionInfo.
	 * 
	 * @return String
	 */
	public String getVersionInfo() {

		return this.versionInfo;
	}

	/**
	 * Set extraInfoArray.
	 * 
	 * @param extraInfoArray
	 *          TExtraInfo
	 */
	public void setExtraInfoArray(ArrayOfTExtraInfo otherInfo) {

		this.extraInfoArray = otherInfo;
	}

	/**
	 * Get extraInfoArray.
	 * 
	 * @return TExtraInfo
	 */
	public ArrayOfTExtraInfo getExtraInfoArray() {

		return this.extraInfoArray;
	}

	// TODO
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}

	public String toString() {

		String result = versionInfo;
		result += this.extraInfoArray.toString();
		return result;

	}
}
