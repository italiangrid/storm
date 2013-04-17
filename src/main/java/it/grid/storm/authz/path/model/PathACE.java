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
 * 
 */
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.util.userinfo.LocalGroups;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathACE {

	private static final Logger log = AuthzDirector.getLogger();

	public static final String ALL_GROUPS_PATTERN = "@ALL@?|\\*";
	public static final String ALL_GROUPS = "@ALL@";
	private static final Pattern allGroupsPattern = Pattern
		.compile(ALL_GROUPS_PATTERN);
	public static final String FIELD_SEP = "\\s"; // * White space character **/
	private static final boolean PERMIT_ACE = true;
	public static final String ALGORITHM = "algorithm"; // property key used to
																											// define the algorithm

	public static final PathACE PERMIT_ALL = buildPermitAllPathACE();

	public static final String COMMENT = "#";

	private final String localGroupName;
	private StFN storageFileName;
	private PathAccessMask pathAccessMask;
	private boolean isPermitACE;

	// =========== CONSTRUCTORs ============

	/**
	 * Quite similar to clone
	 * 
	 * @throws AuthzException
	 */
	public static PathACE build(PathACE other) throws AuthzException {

		PathACE result = new PathACE(other.localGroupName,
			other.getStorageFileName(), other.getPathAccessMask(),
			other.isPermitAce());
		return result;
	}

	private static PathACE buildPermitAllPathACE() throws IllegalStateException {

		try {
			return new PathACE(PathACE.ALL_GROUPS, StFN.makeEmpty(),
				PathAccessMask.DEFAULT, PathACE.PERMIT_ACE);
		} catch (AuthzException e) {
			// never thrown
			throw new IllegalStateException("Unexpected AuthzException: " + e);
		}
	}

	public PathACE(String localGroup, StFN stfn, PathAccessMask accessMask,
		boolean permitACE) throws AuthzException {

		storageFileName = stfn;
		pathAccessMask = accessMask;
		isPermitACE = permitACE;
		if (allGroupsPattern.matcher(localGroup).matches()) {
			localGroupName = PathACE.ALL_GROUPS;
		} else {
			if (LocalGroups.getInstance().isGroupDefined(localGroup)) {
				localGroupName = localGroup;
			} else {
				throw new AuthzException("The local group :'" + localGroup
					+ "' is not defined");
			}
		}
	}

	public boolean isAllGroupsACE() {

		return localGroupName.equals(PathACE.ALL_GROUPS);
	}

	/**
	 * @param pathACEString
	 * @return
	 * @throws AuthzException
	 */
	public static PathACE buildFromString(String pathACEString)
		throws AuthzException {

		String localGroupName;
		StFN stfn;
		PathAccessMask pAccessMask = new PathAccessMask();
		boolean permit;
		String[] fields = pathACEString.split(PathACE.FIELD_SEP, -1);
		LinkedList<String> notemptyFields = new LinkedList<String>();
		for (String field : fields) {
			if (!field.trim().isEmpty()) {
				notemptyFields.add(field);
			}
		}
		if (notemptyFields.size() < 4) {
			throw new AuthzException("Error while parsing the Path ACE '"
				+ pathACEString + "'");
		} else {
			// Setting the Local Group Name
			localGroupName = notemptyFields.get(0);
			try {
				/* Checks if the path string represents a valid URI */
				URI.create(notemptyFields.get(1));
			} catch (IllegalArgumentException uriEx) {
				throw new AuthzException(
					"Error (IllegalArgumentException )while parsing the StFN '"
						+ notemptyFields.get(1) + "' in Path ACE. Is not a valid URI");
			} catch (NullPointerException npe) {
				throw new AuthzException(
					"Error (NullPointerException )while parsing the StFN '"
						+ notemptyFields.get(1) + "' in Path ACE.");
			}
			// Setting the StFN
			try {
				stfn = StFN.make(notemptyFields.get(1));
			} catch (InvalidStFNAttributeException e) {
				throw new AuthzException("Error while parsing the StFN '"
					+ notemptyFields.get(1) + "' in Path ACE ");
			}

			// Setting the Permission Mask
			for (int i = 0; i < notemptyFields.get(2).length(); i++) {
				PathOperation pathOper = PathOperation.getSpaceOperation(notemptyFields
					.get(2).charAt(i));
				pAccessMask.addPathOperation(pathOper);
			}

			// Check if the ACE is DENY or PERMIT
			// ** IMP ** : permit is the default
			if (notemptyFields.get(3).toLowerCase().equals("deny")) {
				permit = false;
			} else {
				permit = true;
			}
		}
		return new PathACE(localGroupName, stfn, pAccessMask, permit);
	}

	public void setStorageFileName(StFN stfn) {

		storageFileName = stfn;
	}

	public void setPathAccessMask(PathAccessMask accessMask) {

		pathAccessMask = accessMask;
	}

	public void setIsPermitType(boolean value) {

		isPermitACE = value;
	}

	public String getLocalGroupName() {

		return localGroupName;
	}

	public StFN getStorageFileName() {

		return storageFileName;
	}

	public PathAccessMask getPathAccessMask() {

		return pathAccessMask;
	}

	public boolean isPermitAce() {

		return isPermitACE;
	}

	/**
	 * ## BUSINESS Methods
	 */

	public boolean subjectMatch(String subjectGroup) {

		Matcher allGroupsMatcher = allGroupsPattern.matcher(localGroupName);
		if (allGroupsMatcher.matches() || localGroupName.equals(subjectGroup)) {
			log.debug("ACE (" + toString() + ") matches with the requestor '"
				+ subjectGroup + "'");
			return true;
		}
		return false;
	}

	/**
     * 
     */
	@Override
	public boolean equals(Object other) {

		boolean result = false;
		if (other instanceof PathACE) {
			PathACE pathOther = (PathACE) other;
			if (pathOther.getLocalGroupName() == localGroupName) {
				if (pathOther.getStorageFileName().equals(storageFileName)) {
					if (pathOther.getPathAccessMask().equals(pathAccessMask)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {

		int result = 17;
		result += 31 * result
			+ (localGroupName == null ? 0 : localGroupName.hashCode());
		result += 31 * result
			+ (storageFileName == null ? 0 : storageFileName.hashCode());
		result += 31 * result
			+ (pathAccessMask == null ? 0 : pathAccessMask.hashCode());
		result += 31 * result + (isPermitACE ? 0 : 1);
		return result;
	}

	@Override
	public String toString() {

		String result = "";
		if (localGroupName == null) {
			result += "NULL";
		} else {
			result += localGroupName;
		}
		result += " ";
		result += storageFileName;
		result += " ";
		result += pathAccessMask;
		result += " ";
		result += isPermitACE ? "PERMIT" : "DENY";
		return result;
	}

}
