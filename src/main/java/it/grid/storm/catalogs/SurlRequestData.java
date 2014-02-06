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

import java.util.Map;

import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public abstract class SurlRequestData implements RequestData {

	private static final Logger log = LoggerFactory
		.getLogger(SurlRequestData.class);

	protected TSURL SURL;
	protected TReturnStatus status;

	public SurlRequestData(TSURL toSURL, TReturnStatus status)
		throws InvalidSurlRequestDataAttributesException {

		if (toSURL == null || status == null || status.getStatusCode() == null) {
			throw new InvalidSurlRequestDataAttributesException(toSURL, status);
		}
		this.SURL = toSURL;
		this.status = status;
	}

	/**
	 * Method that returns the TURL for this chunk of the srm request.
	 */
	@Override
	public final TSURL getSURL() {

		return SURL;
	}

	/**
	 * Method that returns the status for this chunk of the srm request.
	 */
	@Override
	public final TReturnStatus getStatus() {

		return status;
	}

	/**
	 * Method used to set the Status associated to this chunk. If status is null,
	 * then nothing gets set!
	 */
	public void setStatus(TReturnStatus newstat) {

		if (newstat != null) {
			status = newstat;
		}
	}

	protected void setStatus(TStatusCode statusCode, String explanation) {

		try {
			if (explanation == null) {
				status = new TReturnStatus(statusCode);
			} else {
				status = new TReturnStatus(statusCode, explanation);
			}
		} catch (InvalidTReturnStatusAttributeException e) {
			log.error("Unable to set SRM request status to [{},{}]. Error: {}", 
				statusCode.getValue(), explanation, e.getMessage(), e);
		}
	}

	/**
	 * Method that sets the status of this request to SRM_REQUEST_QUEUED; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_REQUEST_QUEUED(String explanation) {

		setStatus(TStatusCode.SRM_REQUEST_QUEUED, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_REQUEST_INPROGRESS; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_REQUEST_INPROGRESS(String explanation) {

		setStatus(TStatusCode.SRM_REQUEST_INPROGRESS, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_SUCCESS; it needs the
	 * explanation String which describes the situation in greater detail; if a
	 * null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_SUCCESS(String explanation) {

		setStatus(TStatusCode.SRM_SUCCESS, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_INTERNAL_ERROR; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_INTERNAL_ERROR(String explanation) {

		setStatus(TStatusCode.SRM_INTERNAL_ERROR, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_INVALID_REQUEST; it
	 * needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_INVALID_REQUEST(String explanation) {

		setStatus(TStatusCode.SRM_INVALID_REQUEST, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_AUTHORIZATION_FAILURE;
	 * it needs the explanation String which describes the situation in greater
	 * detail; if a null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_AUTHORIZATION_FAILURE(String explanation) {

		setStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, explanation);
	}

	/**
	 * Method that sets the status of this request to SRM_ABORTED; it needs the
	 * explanation String which describes the situation in greater detail; if a
	 * null is passed, then an empty String is used as explanation.
	 */
	@Override
	public final void changeStatusSRM_ABORTED(String explanation) {

		setStatus(TStatusCode.SRM_ABORTED, explanation);
	}

	@Override
	public final void changeStatusSRM_FILE_BUSY(String explanation) {

		setStatus(TStatusCode.SRM_FILE_BUSY, explanation);
	}

	@Override
	public final void changeStatusSRM_INVALID_PATH(String explanation) {

		setStatus(TStatusCode.SRM_INVALID_PATH, explanation);
	}

	@Override
	public final void changeStatusSRM_NOT_SUPPORTED(String explanation) {

		setStatus(TStatusCode.SRM_NOT_SUPPORTED, explanation);
	}

	@Override
	public final void changeStatusSRM_FAILURE(String explanation) {

		setStatus(TStatusCode.SRM_FAILURE, explanation);
	}

	@Override
	public final void changeStatusSRM_SPACE_LIFETIME_EXPIRED(String explanation) {

		setStatus(TStatusCode.SRM_SPACE_LIFETIME_EXPIRED, explanation);
	}

	@Override
	public String display(Map<?, ?> map) {

		// nonsense method
		return "";
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
		result = prime * result + ((SURL == null) ? 0 : SURL.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		SurlRequestData other = (SurlRequestData) obj;
		if (SURL == null) {
			if (other.SURL != null) {
				return false;
			}
		} else if (!SURL.equals(other.SURL)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SurlRequestData [SURL=");
		builder.append(SURL);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
}
