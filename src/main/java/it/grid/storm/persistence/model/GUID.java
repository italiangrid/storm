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

package it.grid.storm.persistence.model;

import java.io.Serializable;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//FIXME: Why isn't storm using the standard UUID class?


/**
 * GUID Value Object.
 * <p>
 * Used to retain/generate a GUID/UUID.
 * <p>
 */

public class GUID implements Serializable {

	private static final long serialVersionUID = 7241176020077117264L;

	private static final Logger log = LoggerFactory.getLogger(GUID.class);

	private byte guidValue[] = new byte[16];

	public GUID() {
		buildNewGUID();
	}

	public GUID(String guidString) {

		int pos = 0;
		int count = 0;

		while (pos < guidString.length()) {
			guidValue[count] = getByteValue(guidString.substring(pos, pos + 2));
			pos += 2;
			count++;

			if (pos == guidString.length()) {
				continue;
			}

			if (guidString.charAt(pos) == '-') {
				pos++;
			}
		}
	}

	
	private byte getByteValue(String hex) {

		return (byte) Integer.parseInt(hex, 16);
	}

	private String getHexString(byte val) {

		String hexString;
		if (val < 0) {
			hexString = Integer.toHexString(val + 256);
		} else {
			hexString = Integer.toHexString(val);
		}

		if (hexString.length() < 2) {
			return "0" + hexString.toUpperCase();
		}
		return hexString.toUpperCase();
	}

	private void setByteValues(byte[] lg, int startPos, int count) {

		for (int i = 0; i < count; i++) {
			guidValue[i + startPos] = lg[i];
		}
	}

	private void setByteValues(long lg, int startPos, int count) {

		for (int i = 0; i < count; i++) {
			guidValue[i + startPos] = (byte) (lg & 0xFF);
			lg = lg / 0xFF;
		}
	}

	private void buildNewGUID() {

		try {
			// The time in milli seconds for six bytes
			// gives us until the year 10000ish.
			long lg = System.currentTimeMillis();
			setByteValues(lg, 0, 6);

			// The hash code for this object for two bytes (As a why not option?)
			lg = this.hashCode();
			setByteValues(lg, 6, 2);

			// The ip address for this computer (as we cannot get to the MAC address)
			InetAddress inet = InetAddress.getLocalHost();
			byte[] bytes = inet.getAddress();
			setByteValues(bytes, 8, 4);

			// A random number for two bytes
			lg = (long) ((Math.random() * 0xFFFF));
			setByteValues(lg, 12, 2);

			// Another random number for two bytes
			lg = (long) ((Math.random() * 0xFFFF));
			setByteValues(lg, 14, 2);

		} catch (Exception e) {
			log.error("GUID generation error : {}", e.getMessage(), e);
		}
	}

	public byte[] getBytes() {

		return guidValue;
	}

	/**
	 * Overrides toString(). Returns the array of bytes in the standard form:
	 * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
	 * 
	 * @return the string format
	 */
	@Override
	public String toString() {

		StringBuilder buf = new StringBuilder();

		buf.append(getHexString(guidValue[0]));
		buf.append(getHexString(guidValue[1]));
		buf.append(getHexString(guidValue[2]));
		buf.append(getHexString(guidValue[3]));
		buf.append('-');
		buf.append(getHexString(guidValue[4]));
		buf.append(getHexString(guidValue[5]));
		buf.append('-');
		buf.append(getHexString(guidValue[6]));
		buf.append(getHexString(guidValue[7]));
		buf.append('-');
		buf.append(getHexString(guidValue[8]));
		buf.append(getHexString(guidValue[9]));
		buf.append('-');
		buf.append(getHexString(guidValue[10]));
		buf.append(getHexString(guidValue[11]));
		buf.append(getHexString(guidValue[12]));
		buf.append(getHexString(guidValue[13]));
		buf.append(getHexString(guidValue[14]));
		buf.append(getHexString(guidValue[15]));

		return buf.toString();
	}
}
