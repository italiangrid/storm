/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

/**
 * This class represents the TCheckSumValue of a Permission Area managed by Srm.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

/**
 * Class that represent CheckSum for file.
 */
public class TCheckSumValue {

	private String value = null;

	public static String PNAME_CHECKSUMVALUE = "checkSumValue";

	// TO Complete wut Exception if Strin specified == null
	public TCheckSumValue(String value) {

		this.value = value;
	}

	public String toString() {

		return value;
	}

	public String getValue() {

		return value;
	}

	public void encode(Map param, String name) {

		param.put(name, this.toString());
	}
}
