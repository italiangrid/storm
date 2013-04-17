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

package it.grid.storm.info.model;

import it.grid.storm.srm.types.TSizeInBytes;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF
 * </p>
 * 
 * @author R.Zappi
 * @version 1.0
 */
public class VOInfoStatusData {

	private String voInfoLocalIdentifier = null;
	private TSizeInBytes usedSpaceNearLine = TSizeInBytes.makeEmpty();
	private TSizeInBytes availableSpaceNearLine = TSizeInBytes.makeEmpty();
	private TSizeInBytes ReservedSpaceNearLine = TSizeInBytes.makeEmpty();
	private TSizeInBytes usedSpaceOnLine = TSizeInBytes.makeEmpty();
	private TSizeInBytes availableSpaceOnLine = TSizeInBytes.makeEmpty();
	private TSizeInBytes ReservedSpaceOnLine = TSizeInBytes.makeEmpty();

	public VOInfoStatusData() {

	}

	public void setVOInfoLocalID(String voInfoLocalID) {

		this.voInfoLocalIdentifier = voInfoLocalID;
	}

	public String getVOInfoLocalID() {

		return this.voInfoLocalIdentifier;
	}

}
