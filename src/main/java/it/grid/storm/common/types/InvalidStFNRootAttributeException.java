/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.common.types;

/**
 * This class represents an Exception throw nwhen attempting to create a
 * StFNRoot with a null or empty String, or with a String that does not begin a
 * /.
 * 
 */
public class InvalidStFNRootAttributeException extends Exception {

	private boolean nullName; // boolean true if the supplied String is null
	private boolean emptyName; // boolean true if the supplied String is empty
	private boolean wrong = false; // boolean true if the supplied String does not
																	// begin with a /

	/**
	 * Constructor requiring the String that caused the exception to be thrown.
	 */
	public InvalidStFNRootAttributeException(String name) {

		this.nullName = (name == null);
		this.emptyName = (name.equals(""));
		if (!nullName)
			wrong = (name.charAt(0) == '/');
	}

	public String toString() {

		return "nullName=" + nullName + "; emptyName=" + emptyName
			+ "; not-beginning-with-/=" + wrong;
	}

}
