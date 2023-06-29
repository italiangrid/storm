/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TUserID managed by Srm.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TUserID implements Serializable {

	private static final long serialVersionUID = -7547071983406828938L;
	private static final Logger log = LoggerFactory.getLogger(TUserID.class);
	public static String PNAME_USERID = "userID";
	public static String PNAME_OWNER = "owner";

	private String userID = new String();

	// TO Complete with Exception if null string speified
	public TUserID(String id) throws InvalidTUserIDAttributeException {

		if ((id == null) || (id.length() == 0))
			throw new InvalidTUserIDAttributeException(id);
		userID = id;
	}

	public static TUserID makeEmpty() {

		try {

			return new TUserID("Unknown.");

		} catch (InvalidTUserIDAttributeException e) {

			log.error("Invalid TUserID: {}", e.getMessage(), e);
		} 

		return null;
	}

	public String toString() {

		return userID;
	}

	public String getValue() {

		return userID;
	}

	public void encode(Map param, String name) {

		param.put(name, userID);
	}

}
