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

package it.grid.storm.srm.types;

/**
 * This class represents an Exception throws if TDirOptionData is not well
 * formed. *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidTDirOptionAttributesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private boolean allLevel = true;
	private int numLevel = -1;

	public InvalidTDirOptionAttributesException(boolean allLevel, int numLevel) {

		this.allLevel = allLevel;
		this.numLevel = numLevel;
	}

	public String toString() {

		return "Invalid TDirOption: recursion as specified by allLevel is "
			+ allLevel + ", but numLevel is set to " + numLevel;
	}
}
