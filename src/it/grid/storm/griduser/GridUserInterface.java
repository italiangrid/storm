/*
* (c)2004 INFN / ICTP-eGrid
* This file can be distributed and/or modified under the terms of
* the INFN Software License. For a copy of the licence please visit
* http://www.cnaf.infn.it/license.html
*
*/

package it.grid.storm.griduser;

import it.grid.storm.common.types.VO;

/**
 * Common Interface for GridUser.
 *
 * @author Magnoni Luca
 */

public interface GridUserInterface {

	/**
	 * Return the local user on wich the GridUser is mapped.
	 * LocalUser is rapresented into a copmlex type "LocalUser"/ref
	 * that contsin uid and gid[]
	 */	
	public LocalUser getLocalUser() throws CannotMapUserException;
	
	/**
	 * Return the LocalUser Name String on wich the GridUser is mapped.
	 * A string formati is needed by current version of native library to enforce ACL.
	 */

	public String getLocalUserName();

	/**
	 * Return the main Virtual Organization of the User.
	 * In case of VOMS certificate source for GridUser, the main VO is 
	 * the 'default' VO, the first VO specified as voms-proxy-init option
	 */
	public VO getMainVo();

	/**
	 * Get GridUser Domain Name.
	 * Used for metadada pouprose.
	 */
	public String getDn();

	/**
	 * Return String rapresentation.
	 */	
	public String toString();
}
