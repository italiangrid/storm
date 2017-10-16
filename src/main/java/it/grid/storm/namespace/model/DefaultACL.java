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

package it.grid.storm.namespace.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultACL {

	private final Logger log = LoggerFactory.getLogger(DefaultACL.class);
	private List<ACLEntry> acl = new ArrayList<ACLEntry>();

	/**
     *
     */
	public DefaultACL() {

		super();
	}

	/**
	 * 
	 * @param aclEntry
	 *          ACLEntry
	 */
	public void addACLEntry(ACLEntry aclEntry) {

		acl.add(aclEntry);
		log.debug("Added to Default ACL : " + aclEntry);
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isEmpty() {

		return acl.isEmpty();
	}

	/**
	 * 
	 * @return List
	 */
	public List<ACLEntry> getACL() {

		return acl;
	}

	/**
	 * 
	 * @return String
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < acl.size(); i++) {
			sb.append("ACL[" + i + "] = ( ").append(acl.get(i)).append(") \n");
		}
		return sb.toString();
	}

}
