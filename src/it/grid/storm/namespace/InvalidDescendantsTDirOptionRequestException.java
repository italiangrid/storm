package it.grid.storm.namespace;

import it.grid.storm.srm.types.TDirOption;

import java.io.*;
import java.util.*;

/**
 * This class represents an Exception throws if TDirOptionData  is not valid to explore directory content. *
 * @author  Michele Dibenedetto
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

@SuppressWarnings("serial")
public class InvalidDescendantsTDirOptionRequestException extends Exception
{

	private String filePath = "";
	private final boolean allRecursive;
	private final int levels;

	public InvalidDescendantsTDirOptionRequestException(File fh, TDirOption dirOption) {

		filePath = fh.getAbsolutePath();
		allRecursive = dirOption.isAllLevelRecursive();
		levels = dirOption.getNumLevel();
	}

	public String toString() {

		return ("Unable to explore folder " + filePath + " allRecursive = " + allRecursive
			+ " allowed recursion levels = " + levels);
	}
}
