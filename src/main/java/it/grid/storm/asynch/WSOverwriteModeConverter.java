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

import it.grid.storm.srm.types.*;

import srmClientStubs.TOverwriteMode;

/**
 * Class used to convert between TOverwriteMode from the WebService and StoRM
 * object model counterpart.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date October 2005
 */
public class WSOverwriteModeConverter {

	/**
	 * Method that accepts an srmClientStubs.TOverwriteMode and returns an
	 * it.grid.storm.srm.types.TTURL; if the conversion fails, a
	 * WSConversionException is thrown with a String reporting the problem
	 * encountered.
	 * 
	 * If the supplied srmClientStubs.TOverwriteMode is null, a
	 * WSConversionException is thrown.
	 * 
	 * Beware that it was noticed that sometimes a null stub represents an empty
	 * field for the remote WS. Instead of getting an empty String, null may be
	 * used instead! This is why null stubs are interpreted as a field that the
	 * remote WS decided not to fill in.
	 * 
	 * In particular, since the TOverwriteMode cannot be optional, a missing field
	 * is just not accepted by StoRM: it is treated as a misbehaving WS! For null,
	 * then, an Exception is thrown!
	 */
	public it.grid.storm.srm.types.TOverwriteMode fromWS(
		srmClientStubs.TOverwriteMode stub) throws WSConversionException {

		if (stub == null)
			throw new WSConversionException(
				"Supplied srmClientStubs.TOverwriteMode was null!");
		if (stub == srmClientStubs.TOverwriteMode.ALWAYS)
			return it.grid.storm.srm.types.TOverwriteMode.ALWAYS;
		else if (stub == srmClientStubs.TOverwriteMode.NEVER)
			return it.grid.storm.srm.types.TOverwriteMode.NEVER;
		else if (stub == srmClientStubs.TOverwriteMode.WHEN_FILES_ARE_DIFFERENT)
			return it.grid.storm.srm.types.TOverwriteMode.WHENFILESAREDIFFERENT;
		else
			throw new WSConversionException(
				"Unable to convert srmClientStubs.TOverwriteMode " + stub.toString());
	}

	/**
	 * Method that accepts an it.grid.storm.srm.types.TOverwriteMode and returns
	 * an srmClientStubs.TOverwriteMode; if the conversion fails, a
	 * WSConversionException is thrown with a String reporting the problem
	 * encountered.
	 * 
	 * If the supplied it.grid.storm.srm.types.TOverwriteMode is null, a
	 * WSConversionException is thrown: StoRM makes no use of null in its object
	 * model, so it must be interpreted as a programming bug!
	 */
	public srmClientStubs.TOverwriteMode fromStoRM(
		it.grid.storm.srm.types.TOverwriteMode storm) throws WSConversionException {

		if (storm == null)
			throw new WSConversionException(
				"Supplied it.grid.storm.srm.types.TOverwriteMode was null!");
		if (storm == it.grid.storm.srm.types.TOverwriteMode.ALWAYS)
			return srmClientStubs.TOverwriteMode.ALWAYS;
		else if (storm == it.grid.storm.srm.types.TOverwriteMode.NEVER)
			return srmClientStubs.TOverwriteMode.NEVER;
		else if (storm == it.grid.storm.srm.types.TOverwriteMode.WHENFILESAREDIFFERENT)
			return srmClientStubs.TOverwriteMode.WHEN_FILES_ARE_DIFFERENT;
		else
			throw new WSConversionException(
				"Unable to convert it.grid.storm.srm.types.TOverwriteMode "
					+ storm.toString());
	}
}
