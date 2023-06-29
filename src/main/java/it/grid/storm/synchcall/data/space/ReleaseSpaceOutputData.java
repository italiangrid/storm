/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import java.io.Serializable;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidReleaseSpaceOutputDataAttributesException;

/**
 * This class represents the SpaceReservationOutputData associated with the SRM
 * request, that is it contains info about: UserID, spaceType, SizeDesired,
 * SizeGuaranteed,ecc. Number of files progressing, Number of files finished,
 * and whether the request is currently suspended.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * 
 */
public class ReleaseSpaceOutputData implements Serializable, OutputData {

	private TReturnStatus status = null;

	public ReleaseSpaceOutputData() {

	}

	public ReleaseSpaceOutputData(TReturnStatus status)
		throws InvalidReleaseSpaceOutputDataAttributesException {

		boolean ok = status != null;
		if (!ok) {
			throw new InvalidReleaseSpaceOutputDataAttributesException(status);
		}
		this.status = status;
	}

	/**
	 * Method that return TReturnStatus status.
	 */
	public TReturnStatus getStatus() {

		return status;
	}

	/**
	 * Method that set TReturnStatus status.
	 */
	public void setStatus(TReturnStatus newstatus) {

		status = newstatus;
	}

	// @Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return true;
	}

}
