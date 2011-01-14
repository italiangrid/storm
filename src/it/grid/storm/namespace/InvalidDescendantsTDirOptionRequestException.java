/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
