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

package it.grid.storm.griduser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: INFN-CNAF
 * </p>
 * 
 * @author R.Zappi
 * @version 1.0
 */
public class VONameMatchingRule {

	private static final String ADMIT_ALL = ".*";

	private final String voNameString;
	private Pattern voNamePattern = null;

	/**
	 * Default Constructor
	 * 
	 * @param regularExpressionRule
	 *          String
	 */
	public VONameMatchingRule(String regularExpressionRule) {

		if ((regularExpressionRule == null) || (regularExpressionRule.equals("*"))) {
			voNameString = ADMIT_ALL;
		} else {
			voNameString = regularExpressionRule;
		}
		initPattern();
	}

	public static VONameMatchingRule buildMatchAllVONameMatchingRule() {

		return new VONameMatchingRule(ADMIT_ALL);
	}

	/**
	 * init
	 * 
	 * @param regularExpressionRule
	 *          String
	 */
	private void initPattern() {

		// VOName
		if (isMatchAll(voNameString)) {
			voNamePattern = Pattern.compile(ADMIT_ALL);
		} else {
			voNamePattern = Pattern.compile(voNameString);
		}
	}

	private static boolean isMatchAll(String pattern) {

		return pattern == null || pattern.trim().equals("*")
			|| pattern.trim().equals(".*");
	}

	/**
	 * Match voName with this MatchingRule
	 * 
	 * @param voName
	 *          String
	 * @return boolean
	 */
	public boolean match(String voName) {

		boolean result = false;
		CharSequence voNameSequence = voName.subSequence(0, voName.length());
		Matcher voNameMatcher = voNamePattern.matcher(voNameSequence);
		result = voNameMatcher.find();
		return result;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append(" VONAME=" + voNameString);
		return result.toString();
	}

	public boolean isMatchAll() {

		return isMatchAll(voNameString);
	}

}
