/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class FileSystemCheckerMtabMonolithic implements FileSystemChecker {

	private static final Logger log = LoggerFactory
		.getLogger(FileSystemCheckerMtabMonolithic.class);

	private static final String GPFS_FILESYSTEM_NAME = "gpfs";
	private static final String MTAB_FILE_PATH = "/etc/mtab";

	private static FileSystemCheckerMtabMonolithic instance = new FileSystemCheckerMtabMonolithic();
	private List<String> GPFSMountPoints = null;
	private long initInstant = 0L;

	/**
	 * Singleton private constructor
	 */
	private FileSystemCheckerMtabMonolithic() {

		super();
	}

	/**
	 * Singleton instance getter. initialize the instance if needed
	 * 
	 * @return singleton instance
	 */
	public static FileSystemCheckerMtabMonolithic getInstance()
		throws FileSystemCheckerException {

		synchronized (instance) {
			if (instance.refreshNeeded()) {
				instance.init();
			}
		}
		return instance;
	}

	/**
	 * Initializes the object setting /etc/mtab parsing instant and the list of
	 * GPFS mount points
	 */
	private synchronized void init() throws FileSystemCheckerException {

		this.initInstant = Calendar.getInstance().getTimeInMillis();
		this.GPFSMountPoints = listGPFSMountPoints();
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
		synchronized (instance) {
			if (refreshNeeded()) {
				this.init();
			}
		}
		return this.evaluate(file.getAbsolutePath());
	}

	/**
	 * Checks is /etc/mtab file has been modified since last initialization
	 * 
	 * @return true if a call of init() method is needed
	 */
	private synchronized boolean refreshNeeded() {

		boolean response = false;
		if (initInstant == 0L
			|| initInstant < new File(MTAB_FILE_PATH).lastModified()) {
			response = true;
		}
		return response;
	}

	/**
	 * Checks if file path filePath belongs to one of the stored GPFS mount points
	 * 
	 * @param filePath
	 *          the file path to be checked
	 * 
	 * @return true if file path filePath is on a GPFS mount points
	 */
	private boolean evaluate(String filePath) {

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
	private static List<String> listGPFSMountPoints()
		throws FileSystemCheckerException {

		LinkedList<String> mountPointList = new LinkedList<String>();
		BufferedReader mtab;
		try {
			mtab = new BufferedReader(new FileReader(MTAB_FILE_PATH));
		} catch (FileNotFoundException e) {
		  log.error(e.getMessage(), e);
			throw new FileSystemCheckerException(
				"Error while trying to create a reader for mtab file at "
					+ MTAB_FILE_PATH + ". FileNotFoundException : " + e.getMessage());
		}
		String line;
		try {
			while ((line = mtab.readLine()) != null) {
				if (line.startsWith("#") || !line.startsWith("/dev/")) {
					continue;
				}
				LinkedList<String> elementsList = tokenizeLine(line);
				if (elementsList.get(2).equals(GPFS_FILESYSTEM_NAME)) {
					mountPointList.add(elementsList.get(1));
				}
			}
		} catch (IOException e) {
		  log.error(e.getMessage(), e);
			throw new FileSystemCheckerException(
				"Error while trying to read mtab file at " + MTAB_FILE_PATH
					+ ". IOException : " + e.getMessage());
		}
		return mountPointList;
	}

	/**
	 * Transform the received string in a list of non spaced strings
	 * 
	 * @param line
	 *          a string
	 * @return a list of strings without spaces
	 */
	private static LinkedList<String> tokenizeLine(String line) {

		String[] elementsArray = line.split(" ");
		LinkedList<String> elementsList = new LinkedList<String>(
			Arrays.asList(elementsArray));
		while (elementsList.remove("")) {
		}
		return elementsList;
	}
}
