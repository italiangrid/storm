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
package it.grid.storm.filesystem;

import java.util.Arrays;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class FileSystemCheckerMounts extends FileSystemCheckerFromFile {

	private static final Logger log = LoggerFactory
		.getLogger(FileSystemCheckerMounts.class);
	private static final String MOUNTS_FILE_PATH = "/proc/mounts";

	private static final FileSystemCheckerMounts instance = new FileSystemCheckerMounts();

	/**
	 * Singleton private constructor
	 */
	private FileSystemCheckerMounts() {

		super(log);
	}

	/**
	 * Singleton instance getter. initialize the instance if needed
	 * 
	 * @return singleton instance
	 */
	public static FileSystemCheckerMounts getInstance()
		throws FileSystemCheckerException {

		instance.tryInit();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.filesystem.FileSystemCheckerFromFile#getFilePath()
	 */
	@Override
	protected String getFilePath() {

		return MOUNTS_FILE_PATH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.filesystem.FileSystemCheckerFromFile#getFsNameIndex()
	 */
	@Override
	protected int getFsNameIndex() {

		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.filesystem.FileSystemCheckerFromFile#getMountPointIndex()
	 */
	@Override
	protected int getMountPointIndex() {

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.filesystem.FileSystemCheckerFromFile#skipLine(java.lang.String
	 * )
	 */
	@Override
	protected boolean skipLine(String line) {

		if (line.startsWith("#") || !line.startsWith("/dev/")) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.filesystem.FileSystemCheckerFromFile#tokenizeLine(java.lang
	 * .String)
	 */
	@Override
	protected LinkedList<String> tokenizeLine(String line) {

		String[] elementsArray = line.split(" ");
		LinkedList<String> elementsList = new LinkedList<String>(
			Arrays.asList(elementsArray));
		while (elementsList.remove("")) {
		}
		return elementsList;
	}
}
