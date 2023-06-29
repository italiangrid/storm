/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Michele Dibenedetto
 */
public class PrepareToGetOutputData extends FileTransferOutputData {

	private final TSizeInBytes fileSize;
	private final TLifeTimeInSeconds remainingPinTime;

	public PrepareToGetOutputData(TSURL surl, TTURL turl, TReturnStatus status,
		TRequestToken requestToken, TSizeInBytes fileSize,
		TLifeTimeInSeconds remainingPinTime) throws IllegalArgumentException {

		super(surl, turl, status, requestToken);
		if (fileSize == null || remainingPinTime == null) {
			throw new IllegalArgumentException(
				"Unable to create FileTransferOutputData. Received null arguments: "
					+ "fileSize = " + fileSize + " , remainingPinTime = "
					+ remainingPinTime);
		}
		this.fileSize = fileSize;
		this.remainingPinTime = remainingPinTime;
	}

	/**
	 * @return the fileSize
	 */
	public TSizeInBytes getFileSize() {

		return fileSize;
	}

	/**
	 * @return the remainingPinTime
	 */
	public TLifeTimeInSeconds getRemainingPinTime() {

		return remainingPinTime;
	}
	
	@Override
	public boolean isSuccess() {

		return this.getStatus().getStatusCode().equals(TStatusCode.SRM_FILE_PINNED);
	}
}
