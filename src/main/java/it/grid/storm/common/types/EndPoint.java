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

import java.util.Iterator;
import java.util.ArrayList;

/**
 * This class represents an EndPoint of a SURL: it must begin with a /.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date August 2006
 */
public class EndPoint {

	private static final String ROOT_ENDPOINT = "/";
	
	private ArrayList<String> name = new ArrayList<String>();
	private boolean empty = true;

	private EndPoint(ArrayList<String> name, boolean empty) {

		this.name.clear();
		this.name.addAll(name);
		this.empty = empty;
	}

	/**
	 * Public static method that returns an empty EndPoint.
	 */
	public static EndPoint makeEmpty() {

		return new EndPoint(new ArrayList<String>(), true);
	}

	/**
	 * Public static method that requires a String representing the EndPoint: it
	 * cannot be null or empty otherwise an InvalidEndPointAttributeException is
	 * thrown. Likewise if it does not begin with a slash (/), or if it contains
	 * two consecutive dots (..).
	 */
	public static EndPoint make(String name)
		throws InvalidEndPointAttributeException {

		if (invalid(name))
			throw new InvalidEndPointAttributeException(name);
		return new EndPoint(normalize(name), false);
	}

	/**
	 * Private method that returns true if the supplied string is null, or is
	 * empty, or does not begin with a slash (/), or contains two consecutive dots
	 * (..).
	 */
	static private boolean invalid(String name) {

		return (name == null) || (name.equals("")) || (name.charAt(0) != '/');
	}

	/**
	 * Private method that accepts a valid String as defined by the private valid
	 * method, and returns an ordered ArrayList of all slash-separated elemets,
	 * trimmed of leading and trailing white spaces. Multiple consecutive slashes
	 * are treated as a single slash.
	 * 
	 * Example1: /a/ b /c/d Result: a b c d
	 * 
	 * Example2: /////a///b//////////// c/d///////// Result: a b c d
	 * 
	 * Example3: / Result: empty ArrayList!
	 * 
	 */
	static private ArrayList<String> normalize(String s) {

		// split around slash!
		String[] pieces = s.split("/");
		// remove all empty Strings which may have been produced because of
		// consecutive slashes!
		ArrayList<String> auxList = new ArrayList<String>();
		int pos = 0;
		String aux = null;
		for (int k = 0; k < pieces.length; k++) {
			aux = pieces[k]; // get the element
			aux = aux.trim(); // remove all leading and trailing white spaces
			if (!aux.equals(""))
				auxList.add(pos++, aux);
		}
		return auxList;
	}

	/**
	 * Method that returns true if this StFN is empty.
	 */
	public boolean isEmpty() {

		return empty;
	}

	public String toString() {

		if (empty)
			return "Empty EndPoint";
		int size = this.name.size();
		if (size == 0)
			return ROOT_ENDPOINT;
		StringBuffer sb = new StringBuffer();
		for (Iterator<String> i = this.name.iterator(); i.hasNext();) {
			sb.append("/");
			sb.append(i.next());
		}
		return sb.toString();
	}

	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof EndPoint))
			return false;
		EndPoint po = (EndPoint) o;
		if (po.empty && empty)
			return true;
		if ((!empty) && (!po.empty) && (name.size() == 0) && (po.name.size() == 0))
			return true;
		return (!empty) && (!po.empty) && name.equals(po.name);
	}

	public int hashCode() {

		if (empty)
			return 0;
		int hash = 17;
		if (name.size() == 0)
			return hash;
		return 37 * hash + name.hashCode();
	}
}
