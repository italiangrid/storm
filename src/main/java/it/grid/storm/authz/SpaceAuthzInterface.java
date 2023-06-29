/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz;

import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

public interface SpaceAuthzInterface {

	public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);

	public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp);

	void setAuthzDB(AuthzDBInterface authzDB);

	void refresh();

	public String getSpaceAuthzID();

}
