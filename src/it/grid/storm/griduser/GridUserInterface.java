/*
* (c)2004 INFN / ICTP-eGrid
* This file can be distributed and/or modified under the terms of
* the INFN Software License. For a copy of the licence please visit
* http://www.cnaf.infn.it/license.html
*
*/

package it.grid.storm.griduser;


/**
 * Common Interface for GridUser.
 *
 * @author Magnoni Luca
 */

public interface GridUserInterface {

	/**
	 * Return the Local User where the GridUser is mapped.
	 */
	public LocalUser getLocalUser() throws CannotMapUserException;


	/**
	 * Get GridUser Domain Name.
	 * Used for metadada purpose.
	 */
	public String getDn();


        /**
         * Get GridUser Domain Name.
         * Used for metadada pouprose.
	 */
    public DistinguishedName getDistinguishedName();

        /**
	 * Return String rapresentation.
	 */
	public String toString();
}
