/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.griduser;

/**
 * Thrown when a invalid FQAN is detected by the {@link it.grid.storm.griduser.VomsGridUser#VomsGridUser(String,String[])} constructor.
 * Holds and returns the offending FQAN string.
 */
public class InvalidFqanSyntax 
	extends GridUserException {
	
	/** The FQAN string that does not match the FQAN regexp */
	protected final String _offendingFqan;

	/** 
	 * Constructor, with the offending FQAN and a separate exception
	 * message.
	 */
	public InvalidFqanSyntax (String offendingFqan, String message) {
		super(message);
		
		assert (null == offendingFqan) 
			: "Null string passed to InvalidFqanSyntax constructor";

		_offendingFqan = offendingFqan;
	}

	/** 
	 * Constructor, specifying the offending FQAN only.
	 * A standard message is constructed.
	 */
	public InvalidFqanSyntax (String offendingFqan) {
		// damn Java syntax, we cannot check offendingFqan before this...
		super("Invalid FQAN: " + offendingFqan);
		
		_offendingFqan = offendingFqan;
	}

	public String getOffendingFqan() {
		return _offendingFqan;
	}
}
