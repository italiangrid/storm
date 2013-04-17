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

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents an Exception thrown by the ReservedSpaceCatalog when it
 * finds no data for the specified request.
 * 
 * @author: EGRID ICTP
 * @version: 1.0
 * @date: June 2005
 */
public class NoDataFoundException extends Exception {

	private TRequestToken requestToken;

	/**
	 * Constructor tha trequires the attributes that caused the exception to be
	 * thrown.
	 */
	public NoDataFoundException(TRequestToken requestToken) {

		this.requestToken = requestToken;
	}

	public String toString() {

		return "NoDataFoundException: requestToken=" + requestToken;
	}

}
