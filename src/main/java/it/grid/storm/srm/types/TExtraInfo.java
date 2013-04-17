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
 * This class represents the TExtraInfo additional data associated with the SRM
 * request.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Map;

public class TExtraInfo {

	public static String PNAME_EXTRAINFO = "extraInfo";

	private static String PNAME_KEY = "key";
	private static String PNAME_VALUE = "value";

	private String key = null;
	private String value = null;

	public TExtraInfo() {

		this.key = "unknown";
		this.value = "N/A";
	}

	public TExtraInfo(String key, String value)
		throws InvalidTExtraInfoAttributeException {

		if (key == null) {
			throw new InvalidTExtraInfoAttributeException(key);
		}
		this.key = key;
		this.value = value;
	}

	/**
	 * @param inputParam
	 * @param name
	 * @return
	 * @throws InvalidTExtraInfoAttributeException
	 */
	public static TExtraInfo decode(Map inputParam, String name)
		throws InvalidTExtraInfoAttributeException {

		String k, val;
		Map param = (Map) inputParam.get(name);
		k = (String) param.get(TExtraInfo.PNAME_KEY);
		val = (String) param.get(TExtraInfo.PNAME_VALUE);
		return new TExtraInfo(k, val);
	}

	/**
	 * @param param
	 * @return
	 * @throws InvalidTExtraInfoAttributeException
	 */
	public static TExtraInfo decode(Map param)
		throws InvalidTExtraInfoAttributeException {

		String k, val;
		k = (String) param.get(TExtraInfo.PNAME_KEY);
		val = (String) param.get(TExtraInfo.PNAME_VALUE);
		return new TExtraInfo(k, val);
	}

	/**
	 * @param outputParam
	 * @param fieldName
	 */
	public void encode(Map outputParam, String fieldName) {

		HashMap<String, String> param = new HashMap<String, String>();
		this.encode(param);
		outputParam.put(fieldName, param);
	}

	/**
	 * @param outputParam
	 */
	public void encode(Map outputParam) {

		outputParam.put(TExtraInfo.PNAME_KEY, (String) key);
		outputParam.put(TExtraInfo.PNAME_VALUE, (String) value);
	}

	public String toString() {

		return "<'" + this.key + "','" + this.value + "'>";
	}
}
