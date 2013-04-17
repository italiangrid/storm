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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. This class represents the Ping Input
 * Data
 * 
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * 
 * @author lucamag
 * @author Alberto Forti
 * @date May 28, 2008
 * 
 */

public class IdentityPingInputData extends AnonymousPingInputData implements
	IdentityInputData {

	private final GridUserInterface requestor;

	public IdentityPingInputData(GridUserInterface gridUser,
		String authorizationID) throws IllegalArgumentException {

		super(authorizationID);
		if (gridUser == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: gridUser=" + gridUser);
		}
		this.requestor = gridUser;
	}

	/**
	 * Get the Requestor
	 * 
	 * @return GridUserInterface
	 */
	public GridUserInterface getRequestor() {

		return this.requestor;
	}

	@Override
	public String getPrincipal() {

		return this.requestor.getDn();
	}

	@Override
	public GridUserInterface getUser() {

		return requestor;
	}
}
