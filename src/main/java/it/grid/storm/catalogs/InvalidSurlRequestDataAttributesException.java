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

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidSurlRequestDataAttributesException extends Exception {

	private static final long serialVersionUID = -8636768167720753989L;
	protected boolean nullSURL;
	protected boolean nullStatus;

	public InvalidSurlRequestDataAttributesException(TSURL SURL,
		TReturnStatus status) {

		super();
		init(SURL, status);
	}

	public InvalidSurlRequestDataAttributesException(TSURL SURL,
		TReturnStatus status, String message) {

		super(message);
		init(SURL, status);
	}

	public InvalidSurlRequestDataAttributesException(TSURL SURL,
		TReturnStatus status, Throwable cause) {

		super(cause);
		init(SURL, status);
	}

	public InvalidSurlRequestDataAttributesException(TSURL SURL,
		TReturnStatus status, String message, Throwable cause) {

		super(message, cause);
		init(SURL, status);
	}

	private void init(TSURL SURL, TReturnStatus status) {

		nullSURL = SURL == null;
		nullStatus = status == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("InvalidSurlRequestDataAttributesException [nullSURL=");
		builder.append(nullSURL);
		builder.append(", nullStatus=");
		builder.append(nullStatus);
		builder.append("]");
		return builder.toString();
	}

}
