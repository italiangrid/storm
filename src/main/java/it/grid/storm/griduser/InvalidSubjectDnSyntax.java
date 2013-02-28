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
 * Thrown when a invalid subject DN is detected by the {@link it.grid.storm.griduser.VomsGridUser#VomsGridUser(String,String[])} constructor.
 * Holds and returns the offending subject DN string.
 */
public class InvalidSubjectDnSyntax 
	extends GridUserException {
	
	/** The FQAN string that does not match the FQAN regexp */
	protected final String _offendingSubjectDn;

	/** 
	 * Constructor, with the offending FQAN and a separate exception
	 * message.
	 */
	public InvalidSubjectDnSyntax (String offendingSubjectDn, String message) {
		super(message);
		
		assert (null == offendingSubjectDn) 
			: "Null string passed to InvalidSubjectDnSyntax constructor";

		_offendingSubjectDn = offendingSubjectDn;
	}

	/** 
	 * Constructor, specifying the offending FQAN only.
	 * A standard message is constructed.
	 */
	public InvalidSubjectDnSyntax (String offendingSubjectDn) {
		// damn Java syntax, we cannot check offendingSubjectDn before this...
		super("Invalid FQAN: " + offendingSubjectDn);
		
		_offendingSubjectDn = offendingSubjectDn;
	}

	public String getOffendingSubjectDn() {
		return _offendingSubjectDn;
	}
}
