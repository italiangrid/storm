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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

/**
 * @author Michele Dibenedetto
 * 
 */
abstract class FileSystemCheckerFromFile implements FileSystemChecker {

	private final Logger log;
	private List<String> GPFSMountPoints = null;
	private long initInstant = 0L;
	private static final String GPFS_FILESYSTEM_NAME = "gpfs";

	protected FileSystemCheckerFromFile(Logger log) {
		this.log = log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.filesystem.FileSystemChecker#isGPFS(java.io.File)
	 */
	public boolean isGPFS(File file) throws IllegalArgumentException,
		FileSystemCheckerException {

		if (file == null) {
			log.error("IsGPFS method invoked with null File parameter!");
			throw new IllegalArgumentException("Provided null File argument");
		}
		tryInit();
		return this.evaluate(file.getAbsolutePath());
	}

	protected synchronized void tryInit() throws FileSystemCheckerException {

		if (this.refreshNeeded()) {
			this.init();
		}
	}

	/**
	 * Checks is /etc/mtab file has been modified since last initialization
	 * 
	 * @return true if a call of init() method is needed
	 */
	private boolean refreshNeeded() {

		boolean response = false;
		if (initInstant == 0L
			|| initInstant < new File(getFilePath()).lastModified()) {
			response = true;
		}
		return response;
	}

	/**
	 * Initializes the object setting /etc/mtab parsing instant and the list of
	 * GPFS mount points
	 */
	private void init() throws FileSystemCheckerException {

		this.initInstant = Calendar.getInstance().getTimeInMillis();
		this.GPFSMountPoints = listGPFSMountPoints();
	}

	/**
	 * Checks if file path filePath belongs to one of the stored GPFS mount points
	 * 
	 * @param filePath
	 *          the file path to be checked
	 * 
	 * @return true if file path filePath is on a GPFS mount points
	 */
	private synchronized boolean evaluate(String filePath) {

		boolean response = false;
		for (String GPFSMountPoint : this.GPFSMountPoints) {
			if (filePath.startsWith(GPFSMountPoint)) {
				response = true;
				break;
			}
		}
		return response;
	}

	/**
	 * Parse /etc/mtab file and retrieves all GPFS mount points
	 * 
	 * @return a list of GPFS mount points
	 */
	private List<String> listGPFSMountPoints() throws FileSystemCheckerException {

		LinkedList<String> mountPointList = new LinkedList<String>();
		BufferedReader mtab;
		try {
			mtab = new BufferedReader(new FileReader(getFilePath()));
		} catch (FileNotFoundException e) {
		  log.error(e.getMessage(),e);
		  
			throw new FileSystemCheckerException(
				"Error while trying to create a reader for mtab file at "
					+ getFilePath() + ". FileNotFoundException : " + e.getMessage());
		}
		String line;
		try {
			while ((line = mtab.readLine()) != null) {
				if (this.skipLine(line)) {
					continue;
				}
				LinkedList<String> elementsList = tokenizeLine(line);
				if (elementsList.get(getFsNameIndex()).equals(GPFS_FILESYSTEM_NAME)) {
					mountPointList.add(elementsList.get(getMountPointIndex()));
				}
			}
		} catch (IOException e) {
		  log.error(e.getMessage(), e);
			throw new FileSystemCheckerException(
				"Error while trying to read mtab file at " + getFilePath()
					+ ". IOException : " + e.getMessage());
		}
		return mountPointList;
	}

	/**
	 * Provides the path of file containing GPFS mount points
	 * 
	 * @return the path of a file containing GPFS mount points
	 */
	protected abstract String getFilePath();

	/**
	 * Provides the index of file system name in the list provided by method
	 * tokenizeLine
	 * 
	 * @return the index of file system name in a tokenized list
	 */
	protected abstract int getFsNameIndex();

	/**
	 * Provides the index of file mount point in the list provided by method
	 * tokenizeLine
	 * 
	 * @return the index of file mount point in a tokenized list
	 */
	protected abstract int getMountPointIndex();

	/**
	 * Tokenizes a line putting in a list all the strings from the line related to
	 * mounted partitions
	 * 
	 * @param line
	 *          a line from mounted partitions file containing informations about
	 *          mounted partition
	 * @return a list of strings containing space-free informations about mounted
	 *         partitions
	 */
	protected abstract LinkedList<String> tokenizeLine(String line);

	/**
	 * Checks if the provided line has to be skipped because contains information
	 * not concerning to mounted partition
	 * 
	 * @param a
	 *          string line from mounted partitions file
	 * @return true if the line has to be skipped, true otherwise
	 */
	protected abstract boolean skipLine(String line);
}