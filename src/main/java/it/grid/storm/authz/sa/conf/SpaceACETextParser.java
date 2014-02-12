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

package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.sa.model.AceType;
import it.grid.storm.authz.sa.model.SpaceACE;
import it.grid.storm.authz.sa.model.SpaceAccessMask;
import it.grid.storm.authz.sa.model.SpaceOperation;
import it.grid.storm.authz.sa.model.SubjectPattern;
import it.grid.storm.authz.sa.model.SubjectType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceACETextParser {

	private static final Logger log = LoggerFactory.getLogger(SpaceACETextParser.class);

	private SpaceACETextParser() {

	}

	public static SpaceACE parse(String aceString) throws AuthzDBReaderException {

		return parseACE(aceString);
	}

	private static SpaceACE parseACE(String aceString)
		throws AuthzDBReaderException {

		int aceNumber = parseAceNumber(aceString);

		// Remove ace number from ACE String
		aceString = aceString.substring(aceString.indexOf('='));

		String patternStr = ":";
		String[] fields = aceString.split(patternStr);
		if (fields.length != 4) {
			throw new AuthzDBReaderException("ACEString :" + aceString
				+ " is not well formed");
		}
		for (int i = 0; i < fields.length; i++) {
			log.debug("Field[{}]='{}'", i, fields[i]);
		}
		// FIELD 0 = SubjectType + SubjectPattern
		SubjectType subjectType = parseSubjectType(fields[0].substring(1));
		SubjectPattern subjectPattern = parseSubjectPattern(fields[0]);

		// FIELD 1 = Space Access Mask
		SpaceAccessMask spaceAccessMask = parseSpaceAccessMask(fields[1]);

		// FIELD 2 = ACE Type
		AceType aceType = parseACEType(fields[2]);

		// Build the result
		SpaceACE spaceACE = new SpaceACE();
		spaceACE.setAceNumber(aceNumber);
		spaceACE.setSpaceAccessMask(spaceAccessMask);
		spaceACE.setSubjectType(subjectType);
		spaceACE.setSubjectPattern(subjectPattern);

		spaceACE.setAceType(aceType);
		return spaceACE;
	}

	/**
	 * Parsing the number of ACE string (ace.<b>NR</b>).
	 * 
	 * @param aceNumber
	 * @return
	 */
	private static int parseAceNumber(String aceString)
		throws AuthzDBReaderException {

		int aceNumber = -1;
		int index = aceString.indexOf('=');
		String prefix = aceString.substring(0, 4);
		if (!(prefix.equals(SpaceACE.ACE_PREFIX))) {
			throw new AuthzDBReaderException("Prefix of ACE '" + aceString
				+ "' is not compliant with 'ace.'");
		}
		String numb = aceString.substring(4, index);
		try {
			aceNumber = Integer.parseInt(numb);
		} catch (Exception e) {
			throw new AuthzDBReaderException("Number Format error in '" + aceString
				+ "'");
		}
		log.debug("number = {}", aceNumber);
		return aceNumber;
	}

	/**
	 * @param string
	 * @return
	 */
	private static SubjectType parseSubjectType(String subjectTp)
		throws AuthzDBReaderException {

		SubjectType st = SubjectType.getSubjectType(subjectTp);
		if (st.equals(SubjectType.UNKNOWN)) {
			throw new AuthzDBReaderException("Unknown Subject Type in '" + subjectTp
				+ "'");
		}
		return st;
	}

	/**
	 * @param string
	 * @return
	 */
	private static SubjectPattern parseSubjectPattern(String subjectPattern)
		throws AuthzDBReaderException {

		// SubjectPattern sp = new SubjectPattern();
		return null;
	}

	/**
	 * @param string
	 * @return
	 */
	private static SpaceAccessMask parseSpaceAccessMask(String accessMaskStr)
		throws AuthzDBReaderException {

		SpaceAccessMask spAM = new SpaceAccessMask();
		if (accessMaskStr != null) {
			char[] spAMarray = accessMaskStr.toCharArray();
			for (char element : spAMarray) {
				spAM.addSpaceOperation(SpaceOperation.getSpaceOperation(element));
			}
		}
		return spAM;
	}

	/**
	 * @param string
	 * @return
	 */
	private static AceType parseACEType(String aceTypeStr)
		throws AuthzDBReaderException {

		return AceType.getAceType(aceTypeStr);
	}

}
