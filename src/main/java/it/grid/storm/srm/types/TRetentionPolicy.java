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
 * This class represents the TRetentionPolicy SRM type.
 * 
 * @author Alberto Forti
 * @author CNAF - INFN Bologna
 * @date Luglio, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

public class TRetentionPolicy {

	public static String PNAME_retentionPolicy = "retentionPolicy";
	private String retentionPolicy = null;

	public static final TRetentionPolicy REPLICA = new TRetentionPolicy("REPLICA"),
		OUTPUT = new TRetentionPolicy("OUTPUT"), CUSTODIAL = new TRetentionPolicy(
			"CUSTODIAL"), EMPTY = new TRetentionPolicy("EMPTY");

	private TRetentionPolicy(String retPol) {

		this.retentionPolicy = retPol;
	}

	public final static TRetentionPolicy getTRetentionPolicy(int idx) {

		switch (idx) {
		case 0:
			return REPLICA;
		case 1:
			return OUTPUT;
		case 2:
			return CUSTODIAL;
		default:
			return EMPTY;
		}

	}

	/**
	 * decode() method creates a TRetentionPolicy object from the inforation
	 * contained into the structured parameter received from the FE.
	 * 
	 * @param inputParam
	 *          hashtable structure
	 * @param fieldName
	 *          field name
	 * @return
	 */
	public final static TRetentionPolicy decode(Map inputParam, String fieldName) {

		Integer val;

		val = (Integer) inputParam.get(fieldName);
		if (val == null)
			return EMPTY;

		return TRetentionPolicy.getTRetentionPolicy(val.intValue());
	}

	/**
	 * encode() method creates structured parameter representing this ogbject. It
	 * is passed to the FE.
	 * 
	 * @param outputParam
	 *          hashtable structure
	 * @param fieldName
	 *          field name
	 */
	public void encode(Map outputParam, String fieldName) {

		Integer value = null;

		if (this.equals(TRetentionPolicy.REPLICA))
			value = new Integer(0);
		if (this.equals(TRetentionPolicy.OUTPUT))
			value = new Integer(1);
		if (this.equals(TRetentionPolicy.CUSTODIAL))
			value = new Integer(2);

		outputParam.put(fieldName, value);
	}

	public String toString() {

		return retentionPolicy;
	}

	public String getValue() {

		return retentionPolicy;
	}
}
