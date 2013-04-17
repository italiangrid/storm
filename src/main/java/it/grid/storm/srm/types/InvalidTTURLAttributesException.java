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

package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when a TURL constructor is invoked
 * with null TransferProtocol or with null TFN.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 26th, 2005
 * @version 2.0
 */

import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.common.types.TFN;

public class InvalidTTURLAttributesException extends IllegalArgumentException {

	private boolean nullProtocol; // boolean true if TransferProtocol is null
	private boolean nullTFN; // boolean true if TFN is null
	private boolean emptyProtocol = false; // boolean true if the TransferProtocol
																					// specified is EMPTY
	private boolean emptyTFN = false; // boolean true if the suppliedTFN is empty

	public InvalidTTURLAttributesException() {

		super();
	}

	public InvalidTTURLAttributesException(Throwable cause) {

		super(cause);
	}

	/**
	 * Constructor that requires the Protocol and SFN that caused the exception to
	 * be thrown.
	 */
	public InvalidTTURLAttributesException(TransferProtocol prt, TFN tfn) {

		nullProtocol = (prt == null);
		nullTFN = (tfn == null);
		if (!nullProtocol)
			emptyProtocol = (prt == TransferProtocol.EMPTY);
		if (!nullTFN)
			emptyTFN = tfn.isEmpty();
	}

	public String toString() {

		return "nullProtocol=" + nullProtocol + "; nullTFN=" + nullTFN
			+ "; emptyProtocol=" + emptyProtocol + "; emptyTFN=" + emptyTFN + ".";
	}
}
