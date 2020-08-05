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

package it.grid.storm.authz.sa.model;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EGEEFQANPattern extends FQANPattern {

	private static final Logger log = LoggerFactory.getLogger(EGEEFQANPattern.class);

	// To verify the Regular Expression visit the site
	// "http://www.fileformat.info/tool/regex.htm"
	static private Pattern fqanWildcardPattern = Pattern
		.compile("/[\\w-\\.]+(((/[\\w-\\.]+)*)(/\\u002A)?)(/Role=(([\\w-\\.]+)|(\\u002A)))?");

	private String fqanRE = null;
	private String voName = null;
	private final boolean checkValidity;

	/**
	 * CONSTRUCTOR
	 */

	public EGEEFQANPattern(String fqanRE) throws AuthzDBReaderException {

		this(fqanRE, true);
	}

	public EGEEFQANPattern(String fqanRE, boolean checkValidity)
		throws AuthzDBReaderException {

		this.checkValidity = checkValidity;
		this.fqanRE = fqanRE;
		if (isValidPattern()) {
			generatePattern();
		}
	}

	/**
	 * PRIVATE SETTER /VO[/group[/subgroup(s)]][/Role=role]
	 * 
	 * - voName = VO - groupPattern = '/group[/subgroup(s)]' - rolePattern =
	 * 'role'
	 * 
	 **/

	private void generatePattern() throws AuthzDBReaderException {

		/**
		 * --------- CAPABILITY is not more permitted --------- //Remove capability
		 * if present int capIndex = fqanRE.indexOf("/Capability="); if (capIndex>0)
		 * { fqanRE = fqanRE.substring(0, capIndex); }
		 **/

		// Retrieve Role String if present
		String role = null;
		int roleIndex = fqanRE.indexOf("Role");
		if (roleIndex > 0) {
			role = fqanRE.substring(roleIndex);
			rolePatternString = role.substring(5);
		}

		// Retrieve VOName and Subgroups
		String vogroup = null;
		if (roleIndex > 0) {
			vogroup = fqanRE.substring(0, roleIndex);
		} else { // Only VO and groups defined
			vogroup = fqanRE;
		}

		String[] groups = vogroup.split("/");
		if ((groups == null) || (groups.length == 1)) {
			throw new AuthzDBReaderException("FQAN Pattern '" + fqanRE
				+ "' does not contain VO Name and groups.");
		} else {
			voName = groups[1];
			if (groups.length > 2) {
				groupPatternString = "";
				for (int i = 2; i < groups.length; i++) {
					groupPatternString = groupPatternString + "/" + groups[i];
				}
			}
			log.debug("voName = {}", voName);
			log.debug("groupPattern = {}", groupPatternString);
		}
		log.debug("RolePattern = {}", rolePatternString);
	}

	/**
	 * validateMR
	 */
	@Override
	public boolean isValidPattern() throws AuthzDBReaderException {

		// Matches to the specification.
		Matcher m = EGEEFQANPattern.fqanWildcardPattern.matcher(fqanRE);
		if (!m.matches()) {
			if (checkValidity) {
				throw new IllegalArgumentException("FQAN '" + fqanRE
					+ "' is malformed (syntax: /VO[(/subgroup(s)|*)]][/Role=(role|*)])");
			}
			return false;
		}
		return true;
	}

}
