/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TokenValidator {
	
	static final String UUID_REGEXP = 
	  "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
	
	static final Pattern pattern = Pattern.compile(UUID_REGEXP);
	
	static public boolean valid(String token){
		Matcher matcher = pattern.matcher(token);
		return(matcher.matches());
	}
}
