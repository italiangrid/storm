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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.OutputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class FileTransferOutputData implements OutputData {

	private final TSURL surl;
	private final TTURL turl;
	private final TReturnStatus status;
	private final TRequestToken requestToken;

	public FileTransferOutputData(TSURL surl, TTURL turl, TReturnStatus status,
		TRequestToken requestToken) throws IllegalArgumentException {

		if (surl == null || turl == null || status == null || requestToken == null) {
			throw new IllegalArgumentException(
				"Unable to create FileTransferOutputData. Received null arguments: "
					+ "surl = " + surl + " , turl = " + turl + " , status = " + status);
		}
		this.surl = surl;
		this.turl = turl;
		this.status = status;
		this.requestToken = requestToken;
	}

	@Override
	public boolean isSuccess() {

		return status.isSRM_SUCCESS();
	}

	public TSURL getSurl() {

		return this.surl;

	}

	public TTURL getTurl() {

		return this.turl;

	}

	public TReturnStatus getStatus() {

		return this.status;

	}

	public TRequestToken getRequestToken() {

		return this.requestToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("FileTransferOutputData [surl=");
		builder.append(surl);
		builder.append(", turl=");
		builder.append(turl);
		builder.append(", status=");
		builder.append(status);
		builder.append(", requestToken=");
		builder.append(requestToken);
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
			+ ((requestToken == null) ? 0 : requestToken.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((surl == null) ? 0 : surl.hashCode());
		result = prime * result + ((turl == null) ? 0 : turl.hashCode());
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
		FileTransferOutputData other = (FileTransferOutputData) obj;
		if (requestToken == null) {
			if (other.requestToken != null) {
				return false;
			}
		} else if (!requestToken.equals(other.requestToken)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		if (surl == null) {
			if (other.surl != null) {
				return false;
			}
		} else if (!surl.equals(other.surl)) {
			return false;
		}
		if (turl == null) {
			if (other.turl != null) {
				return false;
			}
		} else if (!turl.equals(other.turl)) {
			return false;
		}
		return true;
	}

}
