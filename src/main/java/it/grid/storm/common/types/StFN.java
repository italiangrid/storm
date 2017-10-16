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

import it.grid.storm.namespace.naming.NamingConst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents the Storage File Name of a SURL.
 * 
 * @author CNAF Bologna - EGRID ICTP
 * @version 2.0
 * @date March 2005
 */
public class StFN {

	private ArrayList<String> name = new ArrayList<String>();
	private boolean directory = false;

	private boolean empty = true;
	public static final String PNAME_PATH = "path";
	
	private static final String ROOT_STFN = "/";

	private StFN(ArrayList<String> name, boolean empty, boolean dir) {

		this.name.clear();
		this.name.addAll(name);
		this.empty = empty;
		this.directory = dir;
	}

	/**
	 * Public static method that returns an empty StFN.
	 */
	public static StFN makeEmpty() {

		return new StFN(new ArrayList<String>(), true, false);
	}

	/**
	 * Public static method that requires a String representing the pathname of
	 * the SFN: it cannot be null or empty otherwise an
	 * InvalidStFNAttributeException is thrown. Likewise if it contains two
	 * consecutive dots (..). or does not begin with a slash (/).
	 */
	public static StFN make(String name) throws InvalidStFNAttributeException {

		if (invalid(name)) {
			throw new InvalidStFNAttributeException(name);
		}
		return new StFN(normalize(name), false, checkDirectory(name));
	}

	/**
	 * Public static method that returns true if the supplied String ends with the
	 * Separator, thereby indicating a directory.
	 */
	private static boolean checkDirectory(String path) {

		if (path != null) {
			return path.endsWith(NamingConst.SEPARATOR);
		} else {
			return false;
		}
	}

	/**
	 * Private method that returns true if the supplied string is null, or is
	 * empty, or contains two consecutive dots (..), or does not begin with a
	 * slash (/).
	 */
	static private boolean invalid(String name) {

		boolean wrong = (name == null) || (name.equals(""))
			|| (name.charAt(0) != '/');
		return wrong;
	}

	/**
	 * Private method that accepts a valid String as defined by the private valid
	 * method, and returns an ordered ArrayList of all slash-separated elemets,
	 * trimmed of leading and trailing white spaces. Multiple consecutive slashes
	 * are treated as a single slash. Example1: /a/ b /c/d Result: a b c d
	 * Example2: /////a///b//////////// c/d///////// Result: a b c d Example3: /
	 * Result: empty ArrayList!
	 */
	static private ArrayList<String> normalize(String s) {

		// split around slash!
		String[] pieces = s.split("/");
		// remove all empty Strings which may have been produced because of
		// consecutive slashes!
		ArrayList<String> auxList = new ArrayList<String>();
		int pos = 0;
		String aux = null;
		for (String piece : pieces) {
			aux = piece; // get the element
			aux = aux.trim(); // remove all leading and trailing white spaces
			if (!aux.equals("")) {
				auxList.add(pos++, aux);
			}
		}
		return auxList;
	}

	/**
	 * Method that returns a Collection of all parent StFNs, stopping at root
	 * parent. The following example clarifies what is meant by parent StFNs, and
	 * by stopping at root parent. Original StFN:
	 * /EGRID/original/data/nyse/file.txt Parent StFNs: /EGRID/original/data/nyse
	 * /EGRID/original/data /EGRID/original /EGRID Second example: /file.txt
	 * Parent StFNs: Empty collection! Third example: /EGRID/ Parent StFNs: Empty
	 * collection! An empty collection is returned if any error occurs during
	 * creation of parent StFNs. Likewise if This is an EmptyStFN.
	 */
	public Collection<StFN> getParents() {

		Collection<StFN> aux = new ArrayList<StFN>();
		if (empty) {
			// empty StFN!
			return aux;
		}
		// number of elements in this StFN
		int size = name.size();
		if ((size == 0) || (size == 1)) {
			// StFN directly on root, or with only _one_ element!
			return aux; 
		}
		for (int i = 1; i < size; i++) {
		  // recall sublist goes from 0 inclusive, to i _EXCLUDED_!!!
			aux.add(new StFN(new ArrayList<String>(name.subList(0, i)), false, true)); 
		}
		return aux;
	}

	/**
	 * Method that returns the parent StFN. The following example clarifies what
	 * is meant by parent StFN. Beware of the root of the StFN: the parent is
	 * calculated from the root! Original StFN: /EGRID/original/data/nyse/file.txt
	 * Parent StFN: /EGRID/original/data/nyse Second example, Original StFN:
	 * /file.txt Parent StFN: Empty StFN! Third example: /EGRID/ Parent StFN:
	 * Empty StFN! An empty StFN is returned if any error occurs during creation
	 * of parent. Likewise if This is an EmptyStFN.
	 */
	public StFN getParent() {

		if (empty) {
			return makeEmpty(); // empty StFN!
		}
		int size = name.size(); // number of elements in this StFN
		if ((size == 0) || (size == 1)) {
			return makeEmpty(); // either directly on root, or only one element!
		}
		return new StFN(new ArrayList<String>(name.subList(0, size - 1)), false, true);
	}

	/**
	 * Method that returns true if this StFN is empty.
	 */
	public boolean isEmpty() {

		return empty;
	}

	public String getValue() {

		return toString();
	}

	@Override
	public String toString() {

		if (empty) {
			return "Empty StFN";
		}
		int size = name.size();
		if (size == 0) {
			return ROOT_STFN;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("/");
		for (Iterator<String> i = name.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append("/");
			}
		}
		if (directory) {
			sb.append(NamingConst.SEPARATOR);
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;
		}
		if (!(o instanceof StFN)) {
			return false;
		}
		StFN po = (StFN) o;
		if (po.empty && empty) {
			return true;
		}
		if ((!empty) && (!po.empty) && (name.size() == 0) && (po.name.size() == 0)) {
			return true;
		}
		return (!empty) && (!po.empty) && (directory == po.directory) && name.equals(po.name);
	}

	@Override
	public int hashCode() {

		if (empty) {
			return 0;
		}
		int hash = 17;
		if (name.size() != 0) {
			hash = 31 * hash + name.hashCode();
		}
		hash = 31 * hash + (directory ? 1 : 0);
		return hash;
	}

	/**
	 * Encode StFN for FE communication.
	 */
	public void encode(Map<String, String> param, String name) {

		param.put(name, toString());
	}

}
