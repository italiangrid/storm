/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Michele Dibenedetto
 */
public class InvalidFileTransferDataAttributesException extends
	InvalidSurlRequestDataAttributesException {

	private static final long serialVersionUID = 4416318501544415810L;
	protected boolean nullTransferProtocols;
	protected boolean nullTransferURL;

	public InvalidFileTransferDataAttributesException(TSURL SURL,
		TURLPrefix transferProtocols, TReturnStatus status, TTURL transferURL) {

		super(SURL, status);
		init(transferProtocols, transferURL);
	}

	public InvalidFileTransferDataAttributesException(TSURL SURL,
		TURLPrefix transferProtocols, TReturnStatus status, TTURL transferURL,
		String message) {

		super(SURL, status, message);
		init(transferProtocols, transferURL);
	}

	public InvalidFileTransferDataAttributesException(TSURL SURL,
		TURLPrefix transferProtocols, TReturnStatus status, TTURL transferURL,
		Throwable cause) {

		super(SURL, status, cause);
		init(transferProtocols, transferURL);
	}

	public InvalidFileTransferDataAttributesException(TSURL SURL,
		TURLPrefix transferProtocols, TReturnStatus status, TTURL transferURL,
		String message, Throwable cause) {

		super(SURL, status, message, cause);
		init(transferProtocols, transferURL);
	}

	private void init(TURLPrefix transferProtocols, TTURL transferURL) {

		nullTransferProtocols = transferProtocols == null;
		nullTransferURL = transferURL == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder
			.append("InvalidFileTransferDataAttributesException [nullTransferProtocols=");
		builder.append(nullTransferProtocols);
		builder.append(", nullTransferURL=");
		builder.append(nullTransferURL);
		builder.append(", nullSURL=");
		builder.append(nullSURL);
		builder.append(", nullStatus=");
		builder.append(nullStatus);
		builder.append("]");
		return builder.toString();
	}
}
