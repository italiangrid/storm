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
 * This class represents the name of a machine in a SFN.
 * 
 * @author EGRID - ICTP Trieste; CNAF - Bologna
 * @date March 25th, 2005
 * @version 2.0
 */
public class Machine {

	private String name = ""; // name of the machine in the SFN
	private boolean empty = true; // boolean true if this object is the empty
																// object

	private Machine(String name, boolean empty) {

		this.name = name.replaceAll(" ", "");
		this.empty = empty;
	}

	/**
	 * Static method that returns an empty Machine.
	 */
	public static Machine makeEmpty() {

		return new Machine("", true);
	}

	/**
	 * Static method requiring the name of the machine: it cannot be null or the
	 * empty String, otherwise an InvalidMachineAttributeException is thrown.
	 * Beware that any empty space is removed.
	 */
	public static Machine make(String s) throws InvalidMachineAttributeException {

		if ((s == null) || (s.equals("")))
			throw new InvalidMachineAttributeException(s);
		return new Machine(s, false);
	}

	/**
	 * Return true if Empty instance of machine object
	 */
	public boolean isEmpty() {

		return empty;
	}

	public String getValue() {

		return name;
	}

	public String toString() {

		if (empty)
			return "Empty Machine";
		return name;
	}

	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof Machine))
			return false;
		Machine mo = (Machine) o;
		if (mo.empty && empty)
			return true;
		return (!mo.empty && !empty && mo.getValue().equals(name));
	}

	public int hashCode() {

		if (empty)
			return 0;
		int hash = 17;
		return 37 * hash + name.hashCode();
	}
}
