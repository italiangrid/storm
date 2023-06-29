/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the SpaceReservationData associated with the SRM
 * request, that is it contains info about: UserID, spaceType, SizeDesired,
 * SizeGuaranteed,ecc. Number of files progressing, Number of files finished,
 * and whether the request is currently suspended.
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.space;

import java.io.Serializable;

import it.grid.storm.srm.types.ArrayOfTMetaDataSpace;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidGetSpaceMetaDataOutputAttributeException;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the SpaceReservationData associated with the SRM
 * request, that is it contains info about: UserID, spaceType, SizeDesired,
 * SizeGuaranteed,ecc. Number of files progressing, Number of files finished,
 * and whether the request is currently suspended.
 * 
 * @author lucamag
 * @date May 29, 2008
 * 
 */

public class GetSpaceMetaDataOutputData implements OutputData, Serializable {

	private TReturnStatus status = null;
	private ArrayOfTMetaDataSpace metaDataArray = null;

	public GetSpaceMetaDataOutputData() {

	}

	public GetSpaceMetaDataOutputData(TReturnStatus status,
		ArrayOfTMetaDataSpace metaDataArray)
		throws InvalidGetSpaceMetaDataOutputAttributeException {

		boolean ok = status != null && metaDataArray != null;

		if (!ok) {
			throw new InvalidGetSpaceMetaDataOutputAttributeException(status,
				metaDataArray);
		}

		this.status = status;
		this.metaDataArray = metaDataArray;

	}

	/**
	 * Method that returns GridUser specify in SRM request.
	 */

	public TReturnStatus getStatus() {

		return status;
	}

	/**
     *
     *
     */
	public void setStatus(TReturnStatus status) {

		this.status = status;

	}

	/**
	 * Method return metaData. i n queue.
	 */

	public ArrayOfTMetaDataSpace getMetaDataSpaceArray() {

		return metaDataArray;
	}

	public void setMetaDataSpaceArray(ArrayOfTMetaDataSpace metaDataArray) {

		this.metaDataArray = metaDataArray;
	}

	// @Override
	public boolean isSuccess() {

		// TODO Auto-generated method stub
		return false;
	}

}
