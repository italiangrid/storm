/*
 * AbstractAuthorizationSource
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: AbstractAuthorizationSource.java,v 1.5 2006/03/21 16:56:35 rmurri Exp $
 *
 */
package it.grid.storm.authorization.sources;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.AuthorizationQueryInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.griduser.GridUserInterface;


/**
 * Root class for all <code>AuthorizationSource</code> classes.
 * Responds <code>AuthorizationDecision.NotApplicable</code> to all
 * queries; real sources should override the methods for which they
 * can give a meaningful response.
 *
 * @see     it.grid.storm.authorization.AuthorizationQueryInterface
 * @author  Riccardo Murri <riccardo.murri@ictp.it><riccardo.murri@ictp.it>
 */

public class AbstractAuthorizationSource
	implements AuthorizationQueryInterface {

	public AuthorizationDecision canUseStormAtAll(final GridUserInterface gridUser) {
		return AuthorizationDecision.NotApplicable;
	}

	public AuthorizationDecision
		canReadFile(final GridUserInterface gridUser, final StoRI file) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canWriteFile(final GridUserInterface gridUser, final StoRI existingFile) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canCreateNewFile(final GridUserInterface gridUser, final StoRI targetFile) {
		return AuthorizationDecision.NotApplicable;
	}

	public AuthorizationDecision
		canChangeAcl(final GridUserInterface gridUser,
					 final StoRI fileOrDirectory) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canGiveaway(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
		return AuthorizationDecision.NotApplicable;
	}

	public AuthorizationDecision
		canListDirectory(final GridUserInterface gridUser, final StoRI directory) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canTraverseDirectory(final GridUserInterface gridUser,
							 final StoRI directory) {
		return AuthorizationDecision.NotApplicable;
	}

	public AuthorizationDecision
		canRename(final GridUserInterface gridUser, final StoRI file) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canDelete(final GridUserInterface gridUser, final StoRI file) {
		return AuthorizationDecision.NotApplicable;
	}
	public AuthorizationDecision
		canMakeDirectory(final GridUserInterface gridUser,
						 final StoRI targetDirectory) {
		return AuthorizationDecision.NotApplicable;
	}
}
