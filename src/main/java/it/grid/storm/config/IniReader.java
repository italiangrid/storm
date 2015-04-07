/*
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

package it.grid.storm.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class IniReader {

	private static Logger log = LoggerFactory.getLogger(IniReader.class);
	private final String configurationPath;

	public IniReader() {

		this(Configuration.getInstance().configurationDir());
	}

	public IniReader(String configurationPath) {

		checkConfigurationPath(configurationPath);
		this.configurationPath = configurationPath;
	}

	private void checkConfigurationPath(String configurationPath) {
		
		Preconditions.checkNotNull(configurationPath,
			"Null configurationPath argument provided");
		
		if (!(new File(configurationPath)).isDirectory()) {

			log.error("The provided configurationPath {} is not a valid directory", 
			  configurationPath);
			throw new IllegalArgumentException("The provided configurationPath "
				+ configurationPath + " is not a valid directory");
		}
	}
	
	public final String getConfigurationPath() {

		return configurationPath;
	}

	private List<File> getIniFiles() {
		
		List<File> iniFiles = new ArrayList<File>();
		File confDir = new File(this.configurationPath);
		for (File file : confDir.listFiles()) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".ini")) {
				iniFiles.add(file);
			}
		}
		return iniFiles;
	}
	
	public Ini getIniFile(String iniFileName) throws InvalidFileFormatException,
		IOException {

		Preconditions.checkNotNull(iniFileName,
			"Null iniFileName argument provided");
		
		List<File> iniFiles = getIniFiles();
		if (iniFiles.size() == 0) {
			log.error("The provided configurationPath {} does not contains ini files", 
			  configurationPath);
		} else {
			log.debug("{} ini files found in {}", iniFiles.size(), configurationPath);
			String name = iniFileName.trim().toLowerCase();
			for (File iniFile : iniFiles) {
				if (name.equals(iniFile.getName().toLowerCase())) {
					log.debug("{} found: {}", name, iniFile);
					return new Ini(new FileReader(iniFile));
				}
			}
		}
		throw new FileNotFoundException(String.format(
			"Ini file %s not found in %s", iniFileName, configurationPath));
	}
}
