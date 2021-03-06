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

/**
 * This class represents an Exception throws if TDirOptionData is not well
 * formed. *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.namespace;

import it.grid.storm.srm.types.*;

public class InvalidSURLException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TSURL surl = null;

	public InvalidSURLException(TSURL surl, String message) {

		super(message);
		this.surl = surl;
	}

	public String toString() {

		return String.format("InvalidSURLException for SURL='%s': %s", this.surl,
			this.getMessage());
	}

}
