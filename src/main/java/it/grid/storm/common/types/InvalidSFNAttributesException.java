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

package it.grid.storm.common.types;

/**
 * This class represents an exception thrown when the SFN constructor is invoked
 * with null Machine, Port or PathName.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 26th, 2005
 * @version 1.0
 */
public class InvalidSFNAttributesException extends Exception {

	private boolean nullMachine; // boolean true if Machine is null
	private boolean nullPort; // boolean true if Port is null
	private boolean nullEndPoint; // boolean true if EndPoint is null
	private boolean nullStFN; // boolean true if PathName is null
	private boolean emptyMachine = false; // boolean indicating if Machine is
																				// empty
	private boolean emptyPort = false; // boolean indicating if Port is empty
	private boolean emptyEndPoint = false; // boolean indicating if EndPoint is
																					// empty
	private boolean emptyStFN = false; // boolean indicating if StFN is empty

	private boolean queryForm = false;

	/**
	 * Constructor that requires the Machine m, the Port p and the PathName pn
	 * that caused the Exception to be thrown.
	 */
	public InvalidSFNAttributesException(Machine m, Port p, StFN s) {

		nullMachine = (m == null);
		if (!nullMachine)
			emptyMachine = m.isEmpty();
		nullPort = (p == null);
		if (!nullPort)
			emptyPort = p.isEmpty();
		nullStFN = (s == null);
		if (!nullStFN)
			emptyStFN = s.isEmpty();
	}

	public InvalidSFNAttributesException(Machine m, Port p, EndPoint e, StFN s) {

		nullMachine = (m == null);
		if (!nullMachine)
			emptyMachine = m.isEmpty();
		nullPort = (p == null);
		if (!nullPort)
			emptyPort = p.isEmpty();
		nullEndPoint = (e == null);
		if (!nullEndPoint)
			emptyEndPoint = e.isEmpty();
		nullStFN = (s == null);
		if (!nullStFN)
			emptyStFN = s.isEmpty();
		queryForm = true;
	}

	/**
	 * Constructor that makes an InvalidSFNAttributesException with Machine, Port
	 * and StFN, as though they had been supplied all null.
	 */
	public InvalidSFNAttributesException() {

		nullMachine = true;
		nullPort = true;
		nullEndPoint = true;
		nullStFN = true;
	}

	public String toString() {

		if (queryForm) {
			return "Invalid SFN Attributes: nullMachine=" + nullMachine
				+ "; nullPort=" + nullPort + "; nullEndPoint=" + nullEndPoint
				+ "; nullStFN=" + nullStFN + "; emptyMachine=" + emptyMachine
				+ "; emptyPort=" + emptyPort + "; emptyEndPoint=" + emptyEndPoint
				+ "; emptyStFN=" + emptyStFN + ".";
		} else {
			return "Invalid SFN Attributes: nullMachine=" + nullMachine
				+ "; nullPort=" + nullPort + "; nullStFN=" + nullStFN
				+ "; emptyMachine=" + emptyMachine + "; emptyPort=" + emptyPort
				+ "; emptyStFN=" + emptyStFN + ".";
		}
	}

}
