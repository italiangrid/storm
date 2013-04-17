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
 * This class represents the TGroupID in Srm request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.Hashtable;
import java.util.Map;

public class TGroupID {

	public static String NAME_GROUPID = "groupID";
	private String groupID;

	// To COMPLETE with Exception if string null specified

	public TGroupID(String id) {

		groupID = id;
	}

	public String toString() {

		return groupID;
	}

	public String getValue() {

		return groupID;
	}

	public void encode(Map param, String name) {

		param.put(name, groupID);

	}

};
