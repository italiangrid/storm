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

import it.grid.storm.srm.types.InvalidTTURLAttributesException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to convert between TTURL from the WebService and StoRM object
 * model counterpart.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date October 2005
 */
public class WSTurlConverter {

	private static Logger log = LoggerFactory.getLogger(WSTurlConverter.class);

	/**
	 * Method that accepts an srmClientStubs.TTURL and returns an
	 * it.grid.storm.srm.types.TTURL; if the conversion fails, a
	 * WSConversionException is thrown with a String reporting the problem
	 * encountered.
	 * 
	 * Beware that the representation of an empty srmClientStubs.TTURL is the
	 * _null_ handle; so null causes an EmptyTTURL to be returned.
	 * 
	 * However, even if srmClientStubs.TTURL is not null, but its URI then again
	 * an EmptyTTURL is returned.
	 * 
	 * Finally, if the URI s String is null or "", then egain an EmptyTTURL is
	 * returned.
	 * 
	 * In case for any reason th econversion cannot proceed, a
	 * WSConverterException is thrown.
	 */
	public it.grid.storm.srm.types.TTURL fromWS(org.apache.axis.types.URI auxURI)
		throws WSConversionException {

		try {
			if (auxURI == null) {
				return it.grid.storm.srm.types.TTURL.makeEmpty();
			}
			String auxString = auxURI.toString();
			if ((auxString == null) || (auxString.equals(""))) {
				return it.grid.storm.srm.types.TTURL.makeEmpty();
			}
			return it.grid.storm.srm.types.TTURL.makeFromString(auxString);
		} catch (InvalidTTURLAttributesException e) {
			log.debug("Unexpected error in WSTurlConverter! This is a programming "
				+ "bug! Could not translate TTURL from WS! {}", e);
			throw new WSConversionException(e.toString());
		}
	}
}
