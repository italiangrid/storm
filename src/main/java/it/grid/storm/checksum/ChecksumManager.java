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

package it.grid.storm.checksum;

import it.grid.storm.config.DefaultValue;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.StormEA;

import java.io.FileNotFoundException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumManager {

	private static final Logger log = LoggerFactory
		.getLogger(ChecksumManager.class);

	private static volatile ChecksumManager instance = null;
	private String defaultAlgorithm;

	private ChecksumManager() {

		defaultAlgorithm = DefaultValue.getChecksumAlgorithm().toLowerCase();
	}

	public static synchronized ChecksumManager getInstance() {

		if (instance == null) {
			instance = new ChecksumManager();
		}
		return instance;
	}

	/**
	 * Return the algorithm used to compute checksums as well as retrieve the
	 * value from extended attributes.
	 * 
	 * @return the algorithm used to compute checksums as well as retrieve the
	 *         value from extended attributes.
	 */
	public String getDefaultAlgorithm() {

		return defaultAlgorithm;
	}

	/**
	 * Return the computed checksum for the given file. If the checksum is already
	 * stored in an extended attribute then that value is given back, otherwise: -
	 * check if the computation of checksum is enabled. - if ENABLED then the
	 * checksum is computed by an external service and stored in an extended
	 * attribute. - if NOT ENABLED return with a NULL value. This method is
	 * blocking (i.e. waits for the checksum to be computed, if it is enabled).
	 * 
	 * @param fileName
	 *          file absolute path.
	 * @return the computed checksum for the given file or <code>null</code> if
	 *         some error occurred. The error is logged.
	 * @throws FileNotFoundException
	 */
	public String getDefaultChecksum(String fileName)
		throws FileNotFoundException {

		log.debug("Requesting checksum for file: {}", fileName);

		String checksum = null;
		try {
			checksum = StormEA.getChecksum(fileName, defaultAlgorithm);
		} catch (ExtendedAttributesException e) {
		  log.warn(e.getMessage(),e);
		}

		return checksum;
	}

	/**
	 * Checks whether the given file has a checksum stored in an extended
	 * attribute.
	 * 
	 * @param fileName
	 *          file absolute path.
	 * @return <code>true</code> if an extended attribute storing the checksum was
	 *         found, <code>false</code> otherwise.
	 * @throws ExtendedAttributesException
	 * @throws NotSupportedException
	 * @throws FileNotFoundException
	 */
	public boolean hasChecksum(String fileName) throws FileNotFoundException {

		String value = null;

		try {
			value = StormEA.getChecksum(fileName, defaultAlgorithm);
		} catch (ExtendedAttributesException e) {
			log.warn("Error manipulating EA for default algorithm "
				+ defaultAlgorithm + " on file: " + fileName
				+ " ExtendedAttributesException: " + e.getMessage());
		}

		return (value != null);
	}

	public Map<String, String> getChecksums(String fileName)
		throws FileNotFoundException {

		return StormEA.getChecksums(fileName);
	}

}
