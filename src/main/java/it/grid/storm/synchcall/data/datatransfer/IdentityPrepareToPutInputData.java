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
 * This class represents the PutDone Input Data associated with the SRM request
 * PutDone
 * 
 * @author Alberto Forti
 * @author CNAF -INFN Bologna
 * @date Aug 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityPrepareToPutInputData extends
	AnonymousPrepareToPutInputData implements IdentityInputData {

	private final GridUserInterface auth;

	public IdentityPrepareToPutInputData(GridUserInterface auth, TSURL surl,
		TURLPrefix transferProtocols) throws IllegalArgumentException {

		super(surl, transferProtocols);
		if (auth == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: auth=" + auth);
		}
		this.auth = auth;
	}

	public GridUserInterface getUser() {

		return this.auth;
	}

	@Override
	public String getPrincipal() {

		return this.auth.getDn();
	}
}
