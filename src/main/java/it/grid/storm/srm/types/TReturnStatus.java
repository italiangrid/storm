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
 * This class represents the TReturnStatus value in SRM request. It is composed
 * by a TStatusCode and an explanetion String
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TReturnStatus implements Serializable {

	private static final long serialVersionUID = -4550845540710062810L;

	private static final Logger log = LoggerFactory
		.getLogger(TReturnStatus.class);
	
	protected TStatusCode statusCode = null;
	protected String explanation = null;
	private Long lastUpdateTIme = null;

	private static final String EMPTY_EXPLANATION = "";
	private static final int MAX_EXPLANATION_LENGTH = 255;

	public static final String PNAME_RETURNSTATUS = "returnStatus";
	public static final String PNAME_STATUS = "status";

	public TReturnStatus(TStatusCode statusCode)
		throws IllegalArgumentException {

		this(statusCode, EMPTY_EXPLANATION);
	}

	/**
	 * Can be Explanation String a null value?
	 */
	public TReturnStatus(TStatusCode statusCode, String explanation) {

	  setStatusCode(statusCode);
	  setExplanation(explanation);
	}

	public TReturnStatus clone() {

		return new TReturnStatus(getStatusCode(), getExplanation());
	}

	/**
	 * Returns the status code
	 * 
	 * @return TStatusCode
	 */
	public TStatusCode getStatusCode() {

		return statusCode;
	}

	/**
	 * @param statusCode
	 *          the statusCode to set
	 */
	protected void setStatusCode(TStatusCode statusCode) {

		if (statusCode == null) {
			throw new IllegalArgumentException(
				"Cannot set the status code, received null argument: statusCode="
					+ statusCode);
		}
		this.statusCode = statusCode;
		updated();
	}

	/**
	 * Set explanation string
	 * 
	 * @param expl
	 *          String
	 */
	protected void setExplanation(String explanationString) {

		if (explanationString == null) {
			explanation = EMPTY_EXPLANATION;
		} else if (explanationString.length() <= MAX_EXPLANATION_LENGTH) {
			explanation = explanationString;
		} else {
			explanation = explanationString.substring(0, MAX_EXPLANATION_LENGTH);
			log.warn("TReturnStatus: Explanation string truncated at {} characters: "
			  + "'{}'", MAX_EXPLANATION_LENGTH, explanation);
		}
		updated();
	}

	/**
	 * Returns the explanation string
	 * 
	 * @return String
	 */
	public String getExplanation() {

		return explanation;
	}

	/**
	 * @return the lastUpdateTIme
	 */
	public Long getLastUpdateTIme() {

		return lastUpdateTIme;
	}

	private void updated() {

		this.lastUpdateTIme = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * This method encode a TReturnStatus Object into an Hashtable used for xmlrpc
	 * communication.
	 */
	public void encode(Map outputParam, String name) {

		// Return STATUS OF REQUEST
		HashMap<String, String> globalStatus = new HashMap<String, String>();
		globalStatus.put("statusCode", getStatusCode().getValue());
		globalStatus.put("explanation", getExplanation());

		// Insert TReturnStatus struct into global Output structure
		outputParam.put(name, globalStatus);

	}

	public String toString() {

		return statusCode + ": " + explanation;
	}

	public boolean isSRM_SUCCESS() {

		return statusCode.equals(TStatusCode.SRM_SUCCESS);
	}

	public void extendExplaination(String string) {

		setExplanation(getExplanation() + " [ " + string + " ]");
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
			+ ((explanation == null) ? 0 : explanation.hashCode());
		result = prime * result
			+ ((lastUpdateTIme == null) ? 0 : lastUpdateTIme.hashCode());
		result = prime * result
			+ ((statusCode == null) ? 0 : statusCode.hashCode());
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
		TReturnStatus other = (TReturnStatus) obj;
		if (explanation == null) {
			if (other.explanation != null) {
				return false;
			}
		} else if (!explanation.equals(other.explanation)) {
			return false;
		}
		if (lastUpdateTIme == null) {
			if (other.lastUpdateTIme != null) {
				return false;
			}
		} else if (!lastUpdateTIme.equals(other.lastUpdateTIme)) {
			return false;
		}
		if (statusCode != other.statusCode) {
			return false;
		}
		return true;
	}

}
