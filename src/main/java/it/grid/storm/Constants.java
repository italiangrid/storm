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

package it.grid.storm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	private static final Logger log = LoggerFactory.getLogger(Constants.class);

	public static final Entry BE_VERSION;
	public static final Entry NAMESPACE_VERSION;
	public static final Entry BE_OS_DISTRIBUTION;
	public static final Entry BE_OS_PLATFORM;
	public static final Entry BE_OS_KERNEL_RELEASE;

	private static final String notAvailable = "N/A";

	static {
		BE_VERSION = new Entry("BE-Version", Constants.class.getPackage()
			.getImplementationVersion());
		NAMESPACE_VERSION = new Entry("Namespace-version", "1.5.0");
		BE_OS_DISTRIBUTION = new Entry("BE-OS-Distribution", getDistribution());
		HashMap<String, String> map = getPlatformKernel();
		BE_OS_PLATFORM = new Entry("BE-OS-Platform", map.get("platform"));
		BE_OS_KERNEL_RELEASE = new Entry("BE-OS-Kernel-Release",
			map.get("kernelRelease"));
	}

	/**
     * 
     */
	private static String getDistribution() {

		String distribution = notAvailable;
		String issuePath = File.separatorChar + "etc" + File.separatorChar
			+ "issue";
		File issueFile = new File(issuePath);
		if (!issueFile.exists() || !issueFile.isFile() || !issueFile.canRead()) {
			log.warn("Unable to read {} file!!", issueFile.getAbsolutePath());
		} else {
			try {
				BufferedReader issueReader = new BufferedReader(new FileReader(
					issueFile));
				String output = issueReader.readLine();
				if (output == null) {
					log.warn("The file {} is empty!", issueFile.getAbsolutePath());
				} else {
					distribution = output;
				}
				issueReader.close();
			} catch (FileNotFoundException e) {
				log.error("Unable to read file '{}'. {}", issueFile.getAbsolutePath(), e);
			} catch (IOException e) {
				log.error("Unable to read file '{}'. {}", issueFile.getAbsolutePath(), e);
			}
		}
		return distribution;
	}

	/**
     * 
     */
	private static HashMap<String, String> getPlatformKernel() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("kernelRelease", notAvailable);
		map.put("platform", notAvailable);
		try {
			Process p = Runtime.getRuntime().exec("uname -ri");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));

			String error = stdError.readLine();
			String output = stdInput.readLine();
			if (output == null || stdInput.read() != -1 || error != null) {
				while (error != null) {
					error += stdError.readLine();
				}
				log.error("Unable to invoke \'uname -ri\' . Standard error : {}", error);
			} else {
				String[] fields = output.trim().split(" ");
				map.put("kernelRelease", fields[0]);
				map.put("platform", fields[1]);
			}
		} catch (IOException e) {
			log.error("Unable to invoke \'uname -ri\' . IOException {}", e);
		}
		return map;
	}

	public static class Entry {

		private final String key;
		private final String value;

		private Entry(String key, String value) {

			this.key = key;
			this.value = value;
		}

		public String getKey() {

			return key;
		}

		public String getValue() {

			return value;
		}
	}
}
