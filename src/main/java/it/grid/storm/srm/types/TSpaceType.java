/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TSpaceType of a Space Area managed by Srm.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0 i
 */
package it.grid.storm.srm.types;

import java.io.Serializable;

public class TSpaceType implements Serializable {

	private String type = null;

	public static final TSpaceType VOLATILE = new TSpaceType("Volatile");
	public static final TSpaceType PERMANENT = new TSpaceType("Permanent");
	public static final TSpaceType DURABLE = new TSpaceType("Durable");
	public static final TSpaceType VOSPACE = new TSpaceType("VOSpace");
	public static final TSpaceType EMPTY = new TSpaceType("Empty");

	private TSpaceType(String type) {

		this.type = type;
	}

	public boolean isEmpty() {

		if (this == this.EMPTY)
			return true;
		else
			return false;
	}

	public String toString() {

		return type;
	}

	public String getValue() {

		return type;
	}

	public static TSpaceType getTSpaceType(String type) {

		if (type == null)
			return EMPTY;

		if (type.toLowerCase().replaceAll(" ", "")
			.equals(VOLATILE.getValue().toLowerCase())) {
			return VOLATILE;
		}
		if (type.toLowerCase().replaceAll(" ", "")
			.equals(PERMANENT.getValue().toLowerCase())) {
			return PERMANENT;
		}
		if (type.toLowerCase().replaceAll(" ", "")
			.equals(DURABLE.getValue().toLowerCase())) {
			return DURABLE;
		}
		if (type.toLowerCase().replaceAll(" ", "")
			.equals(VOSPACE.getValue().toLowerCase())) {
			return VOSPACE;
		} else {
			return EMPTY;
		}
	}
}
