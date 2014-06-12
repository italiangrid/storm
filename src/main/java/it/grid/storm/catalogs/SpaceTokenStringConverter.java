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

import it.grid.storm.srm.types.TSpaceToken;

/**
 * Class that handles DPM DB representation of a SpaceToken, in particular it
 * takes care of the NULL/EMPTY logic of DPM. In particular DPM uses the empty
 * string "" as meaning the absence of a value for the field, wheras StoRM
 * accepts it as a valis String with which to create a TSpaceToken; moreover
 * StoRM uses an Empty TSpaceToken type.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date June 2005
 */
class SpaceTokenStringConverter {

	private static SpaceTokenStringConverter stc = new SpaceTokenStringConverter();

	private SpaceTokenStringConverter() {

	}

	/**
	 * Method that returns the only instance od SpaceTokenConverter
	 */
	public static SpaceTokenStringConverter getInstance() {

		return stc;
	}

	/**
	 * Method that translates StoRM Empty TSpaceToken String representation into
	 * DPM empty representation; all other Strings are left as are.
	 */
	public String toDB(String s) {

		if (s.equals(TSpaceToken.makeEmpty().toString()))
			return "";
		return s;
	}

	/**
	 * Method that translates DPM String representing an Empty TSpaceToken into
	 * StoRM representation; any other String is left as is.
	 */
	public String toStoRM(String s) {

		if ((s == null) || (s.equals("")))
			return TSpaceToken.makeEmpty().toString();
		return s;
	}
}
