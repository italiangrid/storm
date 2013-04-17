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

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents a reply to an issued SRMPrepareToPut command. It
 * provides a method to recover the assigned request token.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2005
 */
public class SRMPrepareToPutReply {

	private TRequestToken requestToken = null; // TRequestToken assigned during
																							// the srm prepare to put
																							// operation

	/**
	 * Constructor that requires the assigned TRequestToken; if it is null, an
	 * InvalidPutReplyAttributeException is thrown.
	 */
	public SRMPrepareToPutReply(TRequestToken requestToken)
		throws InvalidPutReplyAttributeException {

		if (requestToken == null)
			throw new InvalidPutReplyAttributeException();
		this.requestToken = requestToken;
	}

	/**
	 * Method that returns the assigned request token.
	 */
	public TRequestToken requestToken() {

		return requestToken;
	}

	public String toString() {

		return "requestToken=" + requestToken;
	}
}
