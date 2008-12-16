/*
 * LocalUser
 *
 * Copyright (c) 2005,2006 Riccardo Murri <riccardo.murri@ictp.it>
 *
 * You may copy, distribute and modify this file under the terms
 * listed in the fikle LICENSE.txt
 *
 * $Id: MapperInterface.java,v 1.2 2006/03/15 19:23:08 rmurri Exp $
 *
 */
package it.grid.storm.griduser;


import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;


/** Defines a template method for mapping Grid credentials to local
 * account credentials.
 */
interface MapperInterface {
	/** Template factory method for Mapper objects.  Takes Grid
	 * credentials (user certificate subject DN and an array of VOMS
	 * FQANs) and returns a new LocalUser object holding the local
	 * credentials (UID, GIDs) of the POSIX account the given Grid
	 * user is mapped to.
	 *
	 * @param dn Grid user certificate subject DN
	 * @param fqans array of VOMS FQANs
	 *
	 * @return a new LocalUser object holding the local credentials
	 * (UID, GIDs) of the POSIX account the given Grid user is mapped
	 * to.
	 */
	public LocalUser map(final String dn, final String[] fqans) throws CannotMapUserException;
}
