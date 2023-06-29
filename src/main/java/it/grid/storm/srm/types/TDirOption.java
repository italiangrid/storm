/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an TDirOption Object. TDirOption contains information
 * about directory visit.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril 20
 * @version 1.0
 */
public class TDirOption {

	private boolean isASourceDirectory;
	private boolean allLevelRecursive;
	private int numOfLevels = 0;

	/**
	 * Constructor that requires boolean isDirectory indicating whether the SURL
	 * refers to a Directory or not, and a boolean allLevel to indicate if
	 * recursion on all sub-directories is wanted. If allLevel is false, an
	 * InvalidTDirOptionAttributesException is thrown.
	 */
	public TDirOption(boolean isDirectory, boolean allLevel)
		throws InvalidTDirOptionAttributesException {

		this.allLevelRecursive = allLevel;
		this.isASourceDirectory = isDirectory;
		if (allLevelRecursive == false)
			throw new InvalidTDirOptionAttributesException(allLevel, -1);
	}

	private TDirOption(boolean isDirectory) {

		this.isASourceDirectory = isDirectory;
		this.allLevelRecursive = false;
	}

	public static TDirOption makeNotDirectory() {

		return new TDirOption(false);
	}

	public static TDirOption makeFirstLevel() {

		return new TDirOption(true, false, 1);
	}
	/**
	 * Constructor that requires boolean isDirectory, boolean allLevel, int
	 * numLevel. An exception is thrown if allLevel is true, and numLevel>0.
	 */
	public TDirOption(boolean isDirectory, boolean allLevel, int numLevel)
		throws InvalidTDirOptionAttributesException {

		if ((allLevel == true) && (numLevel > 0))
			throw new InvalidTDirOptionAttributesException(allLevel, numLevel);
		allLevelRecursive = allLevel;
		numOfLevels = numLevel;
		isASourceDirectory = isDirectory;
	}

	/**
	 * Return True if SURL associated with TDirOption is a valid directory for
	 * visit.
	 */
	public boolean isDirectory() {

		return isASourceDirectory;
	}

	/**
	 * Return true if allLevelRecursive is true
	 */
	public boolean isAllLevelRecursive() {

		return allLevelRecursive;
	}

	/**
	 * Return num of recursive level to visit. If isAllLevelRecursive then 0 is
	 * returned.
	 */
	public int getNumLevel() {

		if (!allLevelRecursive)
			return numOfLevels;
		else
			return 0;
	}

	public String toString() {

		return "isASourceDirectory=" + isASourceDirectory + " allLevelRecursive="
			+ allLevelRecursive + " numOfLevels=" + numOfLevels;
	}
}
