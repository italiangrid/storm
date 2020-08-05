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
 * 
 */
package it.grid.storm.authz.path;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.PathAuthzInterface;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.StFN;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.util.userinfo.LocalGroups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
public class PathAuthz implements PathAuthzInterface {

	private static final Logger log = LoggerFactory.getLogger(PathAuthz.class);
	private final PathAuthzDB pathAuthzDB;

	public PathAuthz(PathAuthzDB pathAuthzDB) {

		this.pathAuthzDB = pathAuthzDB;
	}

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest pathOperation, StoRI stori) {

		return authorize(guser, pathOperation, stori.getStFN());
	}

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest pathOperation, StFN fileStFN) {

		String groupName = null;
		try {
			groupName = LocalGroups.getInstance().getGroupName(
				guser.getLocalUser().getPrimaryGid());
		} catch (CannotMapUserException e) {
			log.error("Unable to retrieve the local group for '{}'", guser, e);
			return AuthzDecision.INDETERMINATE;
		}
		log.debug("<PathAuthz> Compute authorization for groupName:'{}', "
			+ "filename:'{}', pathOperation:'{}'", groupName, fileStFN, pathOperation);
		return pathAuthzDB.evaluate(groupName, fileStFN, pathOperation);
	}

	public AuthzDecision authorize(GridUserInterface guser,
		PathOperation pathOperation, StFN fileStFN) {

		String groupName = null;
		try {
			groupName = LocalGroups.getInstance().getGroupName(
				guser.getLocalUser().getPrimaryGid());
		} catch (CannotMapUserException e) {
			log.error("Unable to retrieve the local group for '{}'", guser, e);
			return AuthzDecision.INDETERMINATE;
		}
		log.debug("<PathAuthz> Compute authorization for groupName:'{}', "
			+ "filename:'{}', pathOperation:'{}'", groupName, fileStFN, pathOperation);
		return pathAuthzDB.evaluate(groupName, fileStFN, pathOperation);
	}

	@Override
	public AuthzDecision authorizeAnonymous(PathOperation pathOperation,
		StFN fileStFN) {

		log.debug("<PathAuthz> Compute authorization for anonymous user on "
			+ "filename:'{}', pathOperation:'{}'", fileStFN, pathOperation);
		return pathAuthzDB.evaluateAnonymous(fileStFN, pathOperation);
	}

	@Override
	public AuthzDecision authorizeAnonymous(SRMFileRequest srmPathOp,
		StFN fileStFN) {

		log.debug("<PathAuthz> Compute authorization for anonymous user on "
			+ "filename:'{}', SRMFileRequest:'{}'", fileStFN, srmPathOp);
		return pathAuthzDB.evaluateAnonymous(fileStFN, srmPathOp);
	}

	@Override
	public AuthzDecision authorizeAnonymous(SRMFileRequest pathOperation,
		StoRI storiSource, StoRI storiDest) {

		AuthzDecision result = AuthzDecision.INDETERMINATE;

		// Retrieve the StFN source from StoRI
		StFN sourceFileName = storiSource.getStFN();
		// Retrieve the StFN destination from StoRI
		StFN destFileName = storiDest.getStFN();

		switch (pathOperation) {
		case CPfrom:
			AuthzDecision fromDec = pathAuthzDB.evaluateAnonymous(sourceFileName,
				SRMFileRequest.CPfrom);
			AuthzDecision toDec = pathAuthzDB.evaluateAnonymous(destFileName,
				SRMFileRequest.CPto);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case CPto:
			fromDec = pathAuthzDB.evaluateAnonymous(sourceFileName,
				SRMFileRequest.CPfrom);
			toDec = pathAuthzDB.evaluateAnonymous(destFileName, SRMFileRequest.CPto);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case MV_dest:
			fromDec = pathAuthzDB.evaluateAnonymous(sourceFileName,
				SRMFileRequest.MV_source);
			toDec = pathAuthzDB.evaluateAnonymous(destFileName,
				SRMFileRequest.MV_dest);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case MV_source:
			fromDec = pathAuthzDB.evaluateAnonymous(sourceFileName,
				SRMFileRequest.MV_source);
			toDec = pathAuthzDB.evaluateAnonymous(destFileName,
				SRMFileRequest.MV_dest);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		default:
			break;
		}
		return result;
	}

	public AuthzDecision authorize(GridUserInterface guser,
		SRMFileRequest pathOperation, StoRI storiSource, StoRI storiDest) {

		AuthzDecision result = AuthzDecision.INDETERMINATE;

		// Retrieve the local group of Requestor
		String groupName = null;
		try {
			int localGroup = guser.getLocalUser().getPrimaryGid();
			groupName = LocalGroups.getInstance().getGroupName(localGroup);
		} catch (CannotMapUserException e) {
			log.error("Unable to retrieve the local group for '{}'", guser, e);
			groupName = "unknown";
		}

		// Retrieve the StFN source from StoRI
		StFN sourceFileName = storiSource.getStFN();
		// Retrieve the StFN destination from StoRI
		StFN destFileName = storiDest.getStFN();

		switch (pathOperation) {
		case CPfrom:
			AuthzDecision fromDec = pathAuthzDB.evaluate(groupName, sourceFileName,
				SRMFileRequest.CPfrom);
			AuthzDecision toDec = pathAuthzDB.evaluate(groupName, destFileName,
				SRMFileRequest.CPto);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case CPto:
			fromDec = pathAuthzDB.evaluate(groupName, sourceFileName,
				SRMFileRequest.CPfrom);
			toDec = pathAuthzDB
				.evaluate(groupName, destFileName, SRMFileRequest.CPto);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case MV_dest:
			fromDec = pathAuthzDB.evaluate(groupName, sourceFileName,
				SRMFileRequest.MV_source);
			toDec = pathAuthzDB.evaluate(groupName, destFileName,
				SRMFileRequest.MV_dest);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		case MV_source:
			fromDec = pathAuthzDB.evaluate(groupName, sourceFileName,
				SRMFileRequest.MV_source);
			toDec = pathAuthzDB.evaluate(groupName, destFileName,
				SRMFileRequest.MV_dest);
			if (fromDec.equals(AuthzDecision.PERMIT)
				&& (toDec.equals(AuthzDecision.PERMIT))) {
				result = AuthzDecision.PERMIT;
			} else if (fromDec.equals(AuthzDecision.DENY)
				|| (toDec.equals(AuthzDecision.DENY))) {
				result = AuthzDecision.DENY;
			} else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE)
				|| (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
				result = AuthzDecision.NOT_APPLICABLE;
			}
			break;
		default:
			break;
		}
		return result;
	}

}
