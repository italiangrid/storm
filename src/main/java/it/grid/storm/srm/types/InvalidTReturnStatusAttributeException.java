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
 * This class represents an Exception thrown when the constructor for
 * TReturnStatus is invoked with a null TStatusCode.
 * 
 * @author Magnoni Luca
 * @author CNAF INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */

import it.grid.storm.srm.types.TStatusCode;

public class InvalidTReturnStatusAttributeException extends Exception {

	private static final long serialVersionUID = 7879809688892494891L;
	private boolean nullTStatusCode;

	public InvalidTReturnStatusAttributeException(TStatusCode s) {

		nullTStatusCode = s == null;
	}

	public String toString() {

		return "Invalid TReturnStatus Attributes: nullTStatusCode="
			+ nullTStatusCode;
	}
}
