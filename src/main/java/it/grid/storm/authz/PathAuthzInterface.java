/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz;

import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

public interface PathAuthzInterface {

	public AuthzDecision authorize(GridUserInterface guser,
		PathOperation pathOperation, StFN fileStFN);

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest srmPathOp, StoRI stori);

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest srmPathOp, StFN fileStFN);

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest srmPathOp, StoRI storiSource, StoRI storiDest);

	public AuthzDecision authorizeAnonymous(PathOperation pathOperation,
		StFN fileStFN);

	public AuthzDecision authorizeAnonymous(SRMFileRequest srmPathOp,
		StFN fileStFN);

	public AuthzDecision authorizeAnonymous(SRMFileRequest mvSource,
		StoRI fromStori, StoRI toStori);

}
