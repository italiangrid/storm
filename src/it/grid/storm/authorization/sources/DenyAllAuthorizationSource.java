/*
 * DenyAllAuthorizationSource
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>
 * 
 * You may copy, distribute and modify this file under the terms of
 * the LICENSE.txt file at the root of the StoRM backend source tree.
 *
 * $Id: DenyAllAuthorizationSource.java,v 1.3 2006/03/21 16:56:35 rmurri Exp $
 *
 */
package it.grid.storm.authorization.sources;


import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authorization.sources.ConstantAuthorizationSource;
import it.grid.storm.namespace.StoRI;


/**
 * Returns the <code>AuthorizationDecision.Deny</code> instance as
 * result to all authorization queries.
 *
 * @see     it.grid.storm.authorization.AuthorizationDecision
 * @see     it.grid.storm.authorization.AuthorizationQueryInterface
 * @see     it.grid.storm.authorization.sources.ConstantAuthorizationSource
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 */

public class DenyAllAuthorizationSource 
	extends ConstantAuthorizationSource  {

	public DenyAllAuthorizationSource () {
		super(AuthorizationDecision.Deny);
	}
}
