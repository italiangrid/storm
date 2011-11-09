/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

}
