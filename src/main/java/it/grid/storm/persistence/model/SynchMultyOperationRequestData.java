/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TRequestToken;

public interface SynchMultyOperationRequestData extends RequestData {

	public TRequestToken getGeneratedRequestToken();

	public void store();
}
