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

package it.grid.storm.asynch;

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents the reply returned from an invocation of
 * SRMStatusOfPutRequest. It supplies methods for quering the toTURL assigned,
 * and the returnStatus of the request.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September 2005
 */
public class SRMStatusOfPutRequestReply {

	private TTURL toTURL = null; // TTURL as supplied by the invoked server in the
																// SRMStatusOfPutRequest
	private TReturnStatus returnStatus = null; // returnStatus as supplied by the
																							// invoked server in the
																							// SRMStatusOfPutRequest

	public SRMStatusOfPutRequestReply(TTURL toTURL, TReturnStatus returnStatus)
		throws InvalidPutStatusAttributesException {

		if ((toTURL == null) || (returnStatus == null))
			throw new InvalidPutStatusAttributesException(toTURL, returnStatus);
		this.toTURL = toTURL;
		this.returnStatus = returnStatus;
	}

	/**
	 * Method that returns the toTURL that the invoked server assigned to the put
	 * request.
	 */
	public TTURL toTURL() {

		return toTURL;
	}

	/**
	 * Method that returns the TReturnStatus that the invoked server assigned to
	 * the put request.
	 */
	public TReturnStatus returnStatus() {

		return returnStatus;
	}

	public String toString() {

		return "toTURL= " + toTURL + "; returnStatus=" + returnStatus;
	}
}
