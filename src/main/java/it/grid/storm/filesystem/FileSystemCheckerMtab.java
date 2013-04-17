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

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class FileSystemCheckerMtab extends FileSystemCheckerFromFile {

	private static final Logger log = LoggerFactory
		.getLogger(FileSystemCheckerMtab.class);

	private static FileSystemCheckerMtab instance = new FileSystemCheckerMtab();

	/**
	 * Singleton private constructor
	 */
	private FileSystemCheckerMtab() {

		super(log);
	}

	/**
	 * Singleton instance getter. initialize the instance if needed
	 * 
	 * @return singleton instance
	 */
	public static FileSystemCheckerMtab getInstance()
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

		return MtabUtil.getFilePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.filesystem.FileSystemCheckerFromFile#getFsNameIndex()
	 */
	@Override
	protected int getFsNameIndex() {

		return MtabUtil.getFsNameIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.filesystem.FileSystemCheckerFromFile#getMountPointIndex()
	 */
	@Override
	protected int getMountPointIndex() {

		return MtabUtil.getMountPointIndex();
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

		return MtabUtil.skipLineForMountPoints(line);
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

		return MtabUtil.tokenizeLine(line);
	}
}
