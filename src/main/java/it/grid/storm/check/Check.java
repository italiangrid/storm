/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check;

/**
 * @author Michele Dibenedetto
 */
public interface Check {

	/**
	 * Provides the name of the check
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Provides the description of the check
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Return true if the check is critical, if a critical check fails storm
	 * backend must not start
	 * 
	 * @return
	 */
	public boolean isCritical();

	/**
	 * Executes the check
	 * 
	 * @return a successful CheckResponse if the check succeeds, a non successful
	 *         with an error message otherwise
	 * @throws GenericCheckException
	 *           if an error occurs during check execution that prevents check
	 *           execution
	 */
	public CheckResponse execute() throws GenericCheckException;
}
