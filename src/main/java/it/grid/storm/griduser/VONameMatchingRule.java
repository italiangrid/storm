/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class VONameMatchingRule {

	private static final String ADMIT_ALL = ".*";

	private final String voNameString;
	private Pattern voNamePattern = null;

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

	public boolean match(String voName) {

		boolean result = false;
		CharSequence voNameSequence = voName.subSequence(0, voName.length());
		Matcher voNameMatcher = voNamePattern.matcher(voNameSequence);
		result = voNameMatcher.find();
		return result;
	}

	public String toString() {

		StringBuilder result = new StringBuilder();
		result.append(" VONAME=" + voNameString);
		return result.toString();
	}

	public boolean isMatchAll() {
		return isMatchAll(voNameString);
	}
	
	public String getVOName() {
		return voNameString;
	}
}