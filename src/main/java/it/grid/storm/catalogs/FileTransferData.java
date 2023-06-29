/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TTURL;

public interface FileTransferData extends SynchMultyOperationRequestData {

	/**
	 * Method that returns a TURLPrefix containing the transfer protocols desired
	 * for this chunk of the srm request.
	 */
	public TURLPrefix getTransferProtocols();

	/**
	 * Method that returns the TURL for this chunk of the srm request.
	 */
	public TTURL getTransferURL();

	/**
	 * Method used to set the transferURL associated to the SURL of this chunk. If
	 * TTURL is null, then nothing gets set!
	 */
	public void setTransferURL(final TTURL turl);

}
