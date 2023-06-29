/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

import it.grid.storm.filesystem.FilesystemPermission;

/**
 * This class represents the TPermissionMode of a File or Space Area managed by
 * Srm.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

public class TPermissionMode {

	public static String PNAME_OTHERPERMISSION = "otherPermission";
	public static String PNAME_MODE = "mode";

	private String mode = null;

	public static final TPermissionMode NONE = new TPermissionMode("None");
	public static final TPermissionMode X = new TPermissionMode("X");
	public static final TPermissionMode W = new TPermissionMode("W");
	public static final TPermissionMode WX = new TPermissionMode("WX");
	public static final TPermissionMode R = new TPermissionMode("R");
	public static final TPermissionMode RX = new TPermissionMode("RX");
	public static final TPermissionMode RW = new TPermissionMode("RW");
	public static final TPermissionMode RWX = new TPermissionMode("RWX");

	private TPermissionMode(String mode) {

		this.mode = mode;
	}

	public String toString() {

		return mode;
	}

	public String getValue() {

		return mode;
	}

	public static TPermissionMode getTPermissionMode(String type) {

		if (type.equals(NONE.getValue()))
			return NONE;
		if (type.equals(X.getValue()))
			return X;
		if (type.equals(W.getValue()))
			return W;
		if (type.equals(WX.getValue()))
			return WX;
		if (type.equals(R.getValue()))
			return R;
		if (type.equals(RX.getValue()))
			return RX;
		if (type.equals(RW.getValue()))
			return RW;
		if (type.equals(RWX.getValue()))
			return RWX;
		return null;
	}

	public static TPermissionMode getTPermissionMode(int type) {

		switch (type) {
		case 0:
			return NONE;
		case 1:
			return X;
		case 2:
			return W;
		case 3:
			return WX;
		case 4:
			return R;
		case 5:
			return RX;
		case 6:
			return RW;
		case 7:
			return RWX;
		default:
			return NONE;
		}
	}

	public static TPermissionMode getTPermissionMode(FilesystemPermission type) {

		String perm = "";

		if (type.canReadFile() || type.canListDirectory())
			perm += "R";
		if (type.canWriteFile())
			perm += "W";
		if (type.canTraverseDirectory())
			perm += "X";
		if (perm.length() == 0)
			perm = "None";
		return getTPermissionMode(perm);
	}

	/**
	 * This method is used to encode Permission mode from BE to FE commonucation.
	 * 
	 * @param param
	 *          Hashtable that will contains output xmlrpc structure.
	 * @param name
	 *          The name of the field to be added.
	 */
	public void encode(Map param, String name) {

		Integer permissionInt = null;
		if (this.equals(NONE))
			permissionInt = Integer.valueOf(0);
		if (this.equals(X))
			permissionInt = Integer.valueOf(1);
		if (this.equals(W))
			permissionInt = Integer.valueOf(2);
		if (this.equals(WX))
			permissionInt = Integer.valueOf(3);
		if (this.equals(R))
			permissionInt = Integer.valueOf(4);
		if (this.equals(RX))
			permissionInt = Integer.valueOf(5);
		if (this.equals(RW))
			permissionInt = Integer.valueOf(6);
		if (this.equals(RWX))
			permissionInt = Integer.valueOf(7);

		param.put(name, permissionInt);
	}
}
