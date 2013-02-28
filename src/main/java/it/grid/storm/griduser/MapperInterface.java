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
