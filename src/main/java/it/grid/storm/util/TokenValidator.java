package it.grid.storm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TokenValidator {
	
	final static String UUID_REGEXP = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
	static Pattern pattern = Pattern.compile(UUID_REGEXP);
	
	static public boolean valid(String token){
		
		Matcher matcher = pattern.matcher(token);

		return(matcher.find());
	}
}
