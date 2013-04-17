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
 * This class represents an Exception thrown when the String supplied to the
 * constructor of Machine is null or empty.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 25th, 2005
 * @version 1.0
 */
public class InvalidMachineAttributeException extends Exception {

	private boolean nullName; // boolean representing a null name String
	private boolean emptyName; // boolean true if the supplied String is empty

	/**
	 * Constructor that requires the String that caused the exception to be
	 * thrown.
	 */
	public InvalidMachineAttributeException(String name) {

		nullName = name == null;
		emptyName = (name.equals(""));
	}

	public String toString() {

		return "nullName=" + nullName + "; emptyName=" + emptyName;
	}
}
