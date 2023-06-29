/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface ManageFileTransferRequestInputData extends InputData {

	/**
	 * @return the requestToken
	 */
	public TRequestToken getRequestToken();
}
