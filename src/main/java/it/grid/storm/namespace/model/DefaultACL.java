/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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
