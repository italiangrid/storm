package it.grid.storm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SurlValidator {
	
	final static String SRM_URL_REGEXP = "^srm://[A-Za-z0-9\\.\\-]+(:\\d{1,4})?/(srm/managerv2\\?SFN=)?[A-Za-z0-9\\._\\-/]*$";
	static Pattern pattern = Pattern.compile(SRM_URL_REGEXP);
	
	static public boolean valid(String surl){
		
		Matcher matcher = pattern.matcher(surl);

		return(matcher.find());
	}
}
