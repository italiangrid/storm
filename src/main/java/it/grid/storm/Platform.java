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
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Platform {

	private static final Logger log = LoggerFactory.getLogger(Platform.class);
	
	private static String OSDistribution;
	private static String platformName;
	
	private static final String RELEASE_FILE_PATH = File.separatorChar + "etc" + File.separatorChar + "redhat-release";
	private static final String DEFAULT_OS_DISTRIBUTION = "N/A";
	
	static {
		
		platformName = System.getProperty("os.name")
			+ "-" + System.getProperty("os.arch") + "-"
			+ System.getProperty("sun.arch.data.model");
		log.info("Platform name: " + platformName);
		
		try {
			OSDistribution = loadOSDistribution(new File(RELEASE_FILE_PATH));
		} catch (IOException e) {
			log.error(e.getMessage());
			OSDistribution = DEFAULT_OS_DISTRIBUTION;
		}
		log.info("Platform name: " + platformName);
	
	}
	
	/**
	 * Get the complete platform as per the java-vm.
	 * 
	 * @return returns the complete platform as per the java-vm.
	 */
	public static String getPlatformName() {

		return platformName;
	}

	public static String getOSDitribution() {
		
		return OSDistribution;
	}
	
	private static String loadOSDistribution(File releaseFile) throws IOException {
		if (releaseFile == null) {
			return DEFAULT_OS_DISTRIBUTION;
		}
		String os_dist = DEFAULT_OS_DISTRIBUTION;
		if (releaseFile.exists() && releaseFile.isFile() && releaseFile.canRead()) {
			BufferedReader releaseReader = new BufferedReader(new FileReader(releaseFile));
			String output = releaseReader.readLine();
			if ((output != null) && (output.length() > 0)) {
				os_dist = output;
			}
			releaseReader.close();
			// String os_dist="Scientific Linux SL release 4.8 (Beryllium)";
			// String os_dist="Scientific Linux SL release 5.3 (Boron)";
			if (os_dist != null) {
				int pos = os_dist.indexOf("release");
				if ((pos > 0) && (os_dist.length() > pos + 9)) {
					String rel = os_dist.substring(pos + 7, pos + 9).trim();
					os_dist = "sl" + rel;
				}
			}
		}
		return os_dist;
	}
	
}
