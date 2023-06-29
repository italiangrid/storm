/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

import java.io.File;

/**
 * @author Michele Dibenedetto
 * 
 */
public interface FileSystemChecker {

	/**
	 * @param file
	 * @return
	 * @throws IllegalArgumentException
	 *           if File is null
	 */
	public boolean isGPFS(File file) throws IllegalArgumentException,
		FileSystemCheckerException;
}
