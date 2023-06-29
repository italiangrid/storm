/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.discovery;

import it.grid.storm.synchcall.data.InputData;

public interface PingInputData extends InputData {

	/**
	 * Get the authorizatioID.
	 * 
	 * @return String
	 */
	public String getAuthorizationID();

}
