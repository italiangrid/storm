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

/**
 * This class represents an ArrayOfTSURLLifetimeReturnStatus.
 * 
 * @author Alberto Forti
 * @author CNAF Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class ArrayOfTSURLLifetimeReturnStatus {

	public static String PNAME_ARRAYOFFILESTATUSES = "arrayOfFileStatuses";

	ArrayList array;

	/**
	 * Constructs an ArrayOfTSURLLifetimeReturnStatus of 'numItems' empty
	 * elements.
	 * 
	 * @param numItems
	 */
	public ArrayOfTSURLLifetimeReturnStatus(int numItems) {

		array = new ArrayList(numItems);
	}

	// /**
	// * Constructor that requires a String. If it is null, then an
	// * InvalidArrayOfTExtraInfoAttributeException is thrown.
	// */
	// public ArrayOfTSURLLifetimeReturnStatus(TSURLReturnStatus[] surlArray)
	// throws InvalidArrayOfTSURLReturnStatusAttributeException {
	//
	// if (surlArray == null) throw new
	// InvalidArrayOfTSURLReturnStatusAttributeException(surlArray);
	// //FIXME this.tokenArray = tokenArray;
	// }

	/**
	 * Constructs an empty ArrayOfTSURLLifetimeReturnStatus.
	 */
	public ArrayOfTSURLLifetimeReturnStatus() {

		array = new ArrayList();
	}

	/**
	 * Get the array list.
	 * 
	 * @return ArrayList
	 */
	public ArrayList getArray() {

		return array;
	}

	/**
	 * Get the i-th element of the array.
	 * 
	 * @param i
	 *          int
	 * @return TSURLLifetimeReturnStatus
	 */
	public TSURLLifetimeReturnStatus getTSURLLifetimeReturnStatus(int i) {

		return (TSURLLifetimeReturnStatus) array.get(i);
	}

	/**
	 * Set the i-th element of the array.
	 * 
	 * @param index
	 *          int
	 * @param item
	 *          TSURLLifetimeReturnStatus
	 */
	public void setTSURLReturnStatus(int index, TSURLLifetimeReturnStatus item) {

		array.set(index, item);
	}

	/**
	 * Add an element to the array.
	 * 
	 * @param item
	 *          TSURLLifetimeReturnStatus
	 */
	public void addTSurlReturnStatus(TSURLLifetimeReturnStatus item) {

		array.add(item);
	}

	/**
	 * Returns the size of the array.
	 * 
	 * @return int
	 */
	public int size() {

		return array.size();
	}

	/**
	 * Encodes the array to a Hashtable structure.
	 * 
	 * @param outputParam
	 *          Hashtable
	 * @param name
	 *          String
	 */
	public void encode(Map outputParam, String name) {

		ArrayList list = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			((TSURLLifetimeReturnStatus) array.get(i)).encode(list);
		}
		outputParam.put(name, list);
	}
}
