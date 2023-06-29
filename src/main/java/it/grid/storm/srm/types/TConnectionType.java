/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TTransferParameters SRM type.
 * 
 * @author Alberto Forti
 * @author Cnaf -INFN Bologna
 * @date July, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

public class TConnectionType {

	public static String PNAME_connectionType = "connectionType";
	private String connectionType = null;

	public static final TConnectionType WAN = new TConnectionType("WAN"),
		LAN = new TConnectionType("LAN"), EMPTY = new TConnectionType("EMPTY");

	private TConnectionType(String connectionType) {

		this.connectionType = connectionType;
	}

	public final static TConnectionType getTConnectionType(int idx) {

		switch (idx) {
		case 0:
			return WAN;
		case 1:
			return LAN;
		default:
			return EMPTY;
		}
	}

	public final static TConnectionType decode(Map inputParam, String fieldName) {

		Integer val;

		val = (Integer) inputParam.get(fieldName);
		if (val == null)
			return EMPTY;

		return TConnectionType.getTConnectionType(val.intValue());
	}

	public int toInt(TConnectionType conType) {

		if (conType.equals(WAN))
			return 0;
		if (conType.equals(LAN))
			return 1;
		return 2;
	}

	public String toString() {

		return connectionType;
	}

	public String getValue() {

		return connectionType;
	}
}
