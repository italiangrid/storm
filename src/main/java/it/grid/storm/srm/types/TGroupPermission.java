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
 * This class represents the TGroupPermission in Srm request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TGroupPermission {

	private TGroupID groupID;
	private TPermissionMode permMode;

	public static String PNAME_GROUPPERMISSION = "groupPermission";

	public TGroupPermission(TGroupID groupID, TPermissionMode permMode) {

		this.groupID = groupID;
		this.permMode = permMode;
	}

	public TGroupID getGroupID() {

		return groupID;
	}

	public TPermissionMode getPermissionMode() {

		return permMode;
	}

	public static TGroupPermission makeDirectoryDefault() {

		return new TGroupPermission(new TGroupID("undef"), TPermissionMode.NONE);
	}

	public static TGroupPermission makeFileDefault() {

		return new TGroupPermission(new TGroupID("undef"), TPermissionMode.NONE);
	}

	/**
	 * Encode method use to provide a represnetation of this object into a
	 * structures paramter for communication to FE component.
	 * 
	 * @param param
	 * @param name
	 */
	public void encode(Map param, String name) {

		Map paramStructure = new HashMap();
		if ((groupID != null) && (permMode != null)) {
			groupID.encode(paramStructure, TGroupID.NAME_GROUPID);
			permMode.encode(paramStructure, TPermissionMode.PNAME_MODE);
			param.put(name, paramStructure);
		}
	}

}
