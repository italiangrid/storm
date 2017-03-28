/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.ea;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.checksum.ChecksumAlgorithm;

public class StormEA {

	private static final Logger log = LoggerFactory.getLogger(StormEA.class);

	public static final String EA_PINNED = "user.storm.pinned";
	public static final String EA_PREMIGRATE = "user.storm.premigrate";
	public static final String EA_CHECKSUM = "user.storm.checksum.";
	public static final String EA_MIGRATED = "user.storm.migrated";
	public static final String EA_TSMRECD = "user.TSMRecD";
	public static final String EA_TSMRECR = "user.TSMRecR";
	public static final String EA_TSMRECT = "user.TSMRecT";

	private static ExtendedAttributes ea;

	public static void init(ExtendedAttributes extendedAttributes) {
		ea = extendedAttributes;
	}

	static {
		init(ExtendedAttributesFactory.getExtendedAttributes());
	}

	public static Map<String, String> getChecksums(String filename) {

		HashMap<String, String> result = new HashMap<String, String>();

		for (ChecksumAlgorithm checksumAlgorithm : ChecksumAlgorithm.values()) {

			String cksm = null;

			try {

				cksm = getChecksum(filename, checksumAlgorithm.toString());
				if (cksm != null) {
					result.put(checksumAlgorithm.toString(), cksm);
				}

			} catch (ExtendedAttributesException e) {
				log.warn("Cannot retrieve checksum EA for algorithm {}", checksumAlgorithm, e);
			}
		}

		return result;
	}

	public static String getChecksum(String fileName, String algorithm) {

		if (ea.hasXAttr(fileName, EA_CHECKSUM + algorithm.toLowerCase())) {
			return ea.getXAttr(fileName, EA_CHECKSUM + algorithm.toLowerCase());
		}

		return null;

	}

	public static boolean getMigrated(String fileName) {

		return ea.hasXAttr(fileName, EA_MIGRATED);
	}

	public static boolean getPremigrated(String fileName) {

		return ea.hasXAttr(fileName, EA_PREMIGRATE);
	}

	public static long getPinned(String fileName) {

		if (!ea.hasXAttr(fileName, EA_PINNED)) {
			return -1;
		}

		String pinString = ea.getXAttr(fileName, EA_PINNED);
		return Long.decode(pinString);

	}

	public static String getTSMRecT(String fileName) {

		if (!ea.hasXAttr(fileName, EA_TSMRECT)) {
			return null;
		}

		return ea.getXAttr(fileName, EA_TSMRECT);
	}

	public static Integer getTSMRecR(String fileName) {

		if (!ea.hasXAttr(fileName, EA_TSMRECR)) {
			return null;
		}

		String retryStr = ea.getXAttr(fileName, EA_TSMRECR);
		return Integer.valueOf(retryStr);
	}

	public static Long getTSMRecD(String fileName) {

		if (!ea.hasXAttr(fileName, EA_TSMRECD)) {
			return null;
		}

		String dateStr = ea.getXAttr(fileName, EA_TSMRECD);
		return Long.valueOf(dateStr);
	}

	public static void removeChecksum(String fileName) {

		if (!ea.hasXAttr(fileName, EA_CHECKSUM)) {
			log.info("Cannot remove '{}' EA. Attribute not found for file: {}", EA_CHECKSUM, fileName);
			return;
		}

		ea.rmXAttr(fileName, EA_CHECKSUM);
	}

	public static void removePinned(String fileName) {

		ea.rmXAttr(fileName, EA_PINNED);

	}

	public static void setChecksum(String fileName, String checksum, String algorithm) {

		ea.setXAttr(fileName, EA_CHECKSUM + algorithm.toLowerCase(), checksum);
	}

	/**
	 * Set the Extended Attribute "pinned" ({@value StormEA#EA_PINNED}) to the given value.
	 * 
	 * @param fileName
	 * @param expirationDateInSEC expiration time of the pin expressed as "seconds since the epoch".
	 */
	public static void setPinned(String fileName, long expirationDateInSEC) {

		long existingPinValueInSEC = getPinned(fileName);

		Format formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

		if (existingPinValueInSEC >= expirationDateInSEC) {
			log.debug("The file '" + fileName
					+ "' is already Pinned and the pre-existing PinLifeTime is greater than the new one. Nothing is changed in EA. Expiration: "
					+ formatter.format(new Date(existingPinValueInSEC * 1000)));
			return;
		}

		String longString = String.valueOf(expirationDateInSEC);

		try {
			ea.setXAttr(fileName, EA_PINNED, longString);

			if (log.isDebugEnabled()) {
				if (existingPinValueInSEC == -1) {
					log.debug("Added the Pinned EA to '" + fileName + "' with expiration: "
							+ formatter.format(new Date(existingPinValueInSEC * 1000)));
				} else {
					log.debug("Updated the Pinned EA to '" + fileName + "' with expiration: "
							+ formatter.format(new Date(existingPinValueInSEC * 1000)));
				}
			}

		} catch (ExtendedAttributesException e) {
			log.warn("Cannot set pinned EA to file: " + fileName);
		}
	}

	public static void setPremigrate(String fileName) {

		try {
			ea.setXAttr(fileName, EA_PREMIGRATE, null);

		} catch (ExtendedAttributesException e) {
			log.warn("Cannot set pre-migrate EA to file: " + fileName);
		}
	}

	/**
	 * @param absoluteFileName
	 * @return boolean: true if the file is pinned, false else.
	 */
	public static boolean isPinned(String absoluteFileName) {

		return ea.hasXAttr(absoluteFileName, EA_PINNED);
	}
}
