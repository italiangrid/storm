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

package it.grid.storm.persistence.util.db;

import java.text.SimpleDateFormat;

public class MySqlFormat implements SQLFormat {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");

	/**
	 * Create a string value of fields insertable into the query
	 * 
	 * @param value
	 *          Object
	 * @return String
	 */
	public String format(Object value) {

		if (value == null) {
			return null;
		}
		Class clazz = value.getClass();
		if (Character.class.equals(clazz) || char.class.equals(clazz)) {
			value = value.toString();
		}
		if (value instanceof String) {
			StringBuffer stringVal = new StringBuffer((String) value);
			for (int i = 0; i < stringVal.length(); i++) {
				if (stringVal.charAt(i) == '\'') {
					stringVal.insert(i, '\'');
					i++;
				}
			}
			return "\'" + stringVal + "\'";
		}
		if (value instanceof java.util.Date) {
			// System.out.println("Date = "+value);
			return "\'" + dateFormat.format(value) + "\'";
		}
		return value.toString();

	}

	// Test main
	public static void main(String[] args) {

		MySqlFormat formatter = new MySqlFormat();
		System.out.println("Integer =" + formatter.format(new Integer(1)));
		System.out.println("String =" + formatter.format("test_string"));
		System.out.println("Long =" + formatter.format(new Long(102043423)));
		System.out.println("Date ="
			+ formatter.format(new java.util.Date(System.currentTimeMillis())));
	}
}
