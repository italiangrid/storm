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
