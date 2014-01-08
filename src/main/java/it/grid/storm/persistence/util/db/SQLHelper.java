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

public abstract class SQLHelper {

	public String dbmsVendor;
	private SQLFormat formatter;

	protected SQLHelper(String dbmsVendor) {

		this.dbmsVendor = dbmsVendor;
		this.formatter = DataBaseStrategy.getInstance(dbmsVendor).getFormatter();
	}

	public String format(Object value) {

		return formatter.format(value);
	}

	/**
	 * 
	 * @param value
	 *          boolean
	 * @return String
	 */
	public String format(boolean value) {

		String result = null;
		Boolean boolValue = new Boolean(value);
		result = formatter.format(boolValue);
		return result;
	}

	/**
	 * 
	 * @param value
	 *          int
	 * @return String
	 */
	public String format(int value) {

		String result = null;
		Integer intValue = null;
		try {
			intValue = new Integer(value);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		result = formatter.format(intValue);
		return result;
	}

	/**
	 * 
	 * @param value
	 *          long
	 * @return String
	 */
	public String format(long value) {

		String result = null;
		Long longValue = null;
		try {
			longValue = new Long(value);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		result = formatter.format(longValue);
		return result;
	}

	/**
	 * 
	 * @param date
	 *          Date
	 * @return String
	 */
	public String format(java.util.Date date) {

		return formatter.format(date);
	}

}
