/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/**
 * Interface that the DN matching algorithms must implement.
 */
public interface DnMatch {

	/**
	 * Compare two DNs and return true if they match, according to the implemented
	 * criterion.
	 * 
	 * @return <code>true</code> if the DNs do match.
	 */
	public boolean match(final String proxyDn, final String fixedDn);
}
