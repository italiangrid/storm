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
 * This class represents the TSURLReturnStatus data associated with the SRM
 * request, that is it contains info about: TSURL , StorageSystemInfo * @author
 * Magnoni Luca
 * 
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSURLReturnStatus {

	private TSURL surl = null;
	private TReturnStatus returnStatus = null;

	public TSURLReturnStatus() {

	}

	public TSURLReturnStatus(TSURL surl, TReturnStatus status)
		throws InvalidTSURLReturnStatusAttributeException {

		if (surl == null)
			throw new InvalidTSURLReturnStatusAttributeException(surl);
		this.surl = surl;
		this.returnStatus = status;
	}

	/**
	 * Method that return SURL specified in SRM request.
	 */

	public TSURL getSurl() {

		return surl;
	}

	public void setSurl(TSURL surl) {

		this.surl = surl;
	}

	/**
	 * Set Status
	 */
	public void setStatus(TReturnStatus status) {

		this.returnStatus = status;
	}

	/**
	 * Get Status
	 */
	public TReturnStatus getStatus() {

		return this.returnStatus;
	}

	/*
	 * Encode function used to fill output structure for FE communication.
	 */
	public void encode(List outputVector) {

		// Creation of a single TMetaPathDetail struct
		Map surlRetStatusParam = new HashMap();
		// Member name "surl"
		if (this.surl != null)
			this.surl.encode(surlRetStatusParam, TSURL.PNAME_SURL);
		if (this.returnStatus != null)
			this.returnStatus.encode(surlRetStatusParam, TReturnStatus.PNAME_STATUS);

		outputVector.add(surlRetStatusParam);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TSURLReturnStatus [surl=");
		builder.append(surl);
		builder.append(", returnStatus=");
		builder.append(returnStatus);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((returnStatus == null) ? 0 : returnStatus.hashCode());
		result = prime * result + ((surl == null) ? 0 : surl.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TSURLReturnStatus other = (TSURLReturnStatus) obj;
		if (returnStatus == null) {
			if (other.returnStatus != null) {
				return false;
			}
		} else if (!returnStatus.equals(other.returnStatus)) {
			return false;
		}
		if (surl == null) {
			if (other.surl != null) {
				return false;
			}
		} else if (!surl.equals(other.surl)) {
			return false;
		}
		return true;
	}

}
