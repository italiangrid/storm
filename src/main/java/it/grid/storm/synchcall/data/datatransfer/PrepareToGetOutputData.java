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
