/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidBoLDataAttributesException extends
	InvalidFileTransferDataAttributesException {

	private static final long serialVersionUID = 8113403994527678088L;
	// booleans that indicate whether the corresponding variable is null
	protected boolean nullLifeTime;
	protected boolean nullDirOption;
	protected boolean nullFileSize;

	public InvalidBoLDataAttributesException(TSURL fromSURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix transferProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL) {

		super(fromSURL, transferProtocols, status, transferURL);
		init(lifeTime, dirOption, fileSize);
	}

	public InvalidBoLDataAttributesException(TSURL fromSURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix transferProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL, String message) {

		super(fromSURL, transferProtocols, status, transferURL, message);
		init(lifeTime, dirOption, fileSize);
	}

	public InvalidBoLDataAttributesException(TSURL fromSURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix transferProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL, Throwable cause) {

		super(fromSURL, transferProtocols, status, transferURL, cause);
		init(lifeTime, dirOption, fileSize);
	}

	public InvalidBoLDataAttributesException(TSURL fromSURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix transferProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL, String message, Throwable cause) {

		super(fromSURL, transferProtocols, status, transferURL, message, cause);
		init(lifeTime, dirOption, fileSize);
	}

	private void init(TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TSizeInBytes fileSize) {

		nullLifeTime = lifeTime == null;
		nullDirOption = dirOption == null;
		nullFileSize = fileSize == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("InvalidBoLDataAttributesException [nullLifeTime=");
		builder.append(nullLifeTime);
		builder.append(", nullDirOption=");
		builder.append(nullDirOption);
		builder.append(", nullFileSize=");
		builder.append(nullFileSize);
		builder.append(", nullSURL=");
		builder.append(nullSURL);
		builder.append(", nullTransferProtocols=");
		builder.append(nullTransferProtocols);
		builder.append(", nullStatus=");
		builder.append(nullStatus);
		builder.append(", nullTransferURL=");
		builder.append(nullTransferURL);
		builder.append("]");
		return builder.toString();
	}
}
