/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;

interface MapperInterface {

	public LocalUser map(final String dn, final String[] fqans)
		throws CannotMapUserException;
}
