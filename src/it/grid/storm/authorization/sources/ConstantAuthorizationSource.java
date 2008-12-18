/*
 * ConstantAuthorizationSource
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: ConstantAuthorizationSource.java,v 1.5 2006/03/21 16:56:35 rmurri Exp $
 *
 */
package it.grid.storm.authorization.sources;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.sources.AbstractAuthorizationSource;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;


/**
 * Returns to all queries the <code>AuthorizationDecision</code>
 * instance it was initialized with.
 *
 * @see     it.grid.storm.authorization.AuthorizationDecision
 * @see     it.grid.storm.authorization.AuthorizationQueryInterface
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */

public class ConstantAuthorizationSource 
	extends AbstractAuthorizationSource  {

	// --- private --- //

	/** The <code>AuthorizationDecision</code> instance to return as a
	 * result for all queries. */
	private final AuthorizationDecision __response;


	// --- public --- //

	/** Constructor, taking the <code>AuthorizationDecision</code>
	 * instance to return as a result for all queries.
	 *
	 * @param response the <code>AuthorizationDecision</code> instance 
	 *                 to return as a result for all queries
	 */
	public ConstantAuthorizationSource (AuthorizationDecision response) {
		__response = response;
	}

	public AuthorizationDecision canUseStormAtAll(final GridUserInterface gridUser) {
		return __response;
	}

	public AuthorizationDecision 
		canReadFile(final GridUserInterface gridUser, final StoRI file) {
		return __response;
	}
	public AuthorizationDecision 
		canWriteFile(final GridUserInterface gridUser, final StoRI existingFile) {
		return __response;
	}
	public AuthorizationDecision 
		canCreateNewFile(final GridUserInterface gridUser, final StoRI targetFile) {
		return __response;
	}

	public AuthorizationDecision 
		canChangeAcl(final GridUserInterface gridUser, 
					 final StoRI fileOrDirectory) {
		return __response;
	}
	public AuthorizationDecision 
		canGiveaway(final GridUserInterface gridUser, final StoRI fileOrDirectory) {
		return __response;
	}

	public AuthorizationDecision 
		canListDirectory(final GridUserInterface gridUser, final StoRI directory) {
		return __response;
	}
	public AuthorizationDecision 
		canTraverseDirectory(final GridUserInterface gridUser, 
							 final StoRI directory) {
		return __response;
	}

	public AuthorizationDecision 
		canRename(final GridUserInterface gridUser, final StoRI file) {
		return __response;
	}
	public AuthorizationDecision 
		canDelete(final GridUserInterface gridUser, final StoRI file) {
		return __response;
	}
	public AuthorizationDecision 
		canMakeDirectory(final GridUserInterface gridUser, 
						 final StoRI targetDirectory) {
		return __response;
	}
}
