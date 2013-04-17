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
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 * 
 */

import it.grid.storm.srm.types.TExtraInfo;

public class InvalidArrayOfTExtraInfoAttributeException extends Exception {

	private boolean nullArray;

	public InvalidArrayOfTExtraInfoAttributeException(Object[] infoArray) {

		nullArray = infoArray == null;
	}

	public String toString() {

		return "Invalid TExtraInfo[]: nullArray = " + nullArray;
	}
}
