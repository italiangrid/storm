/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
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
