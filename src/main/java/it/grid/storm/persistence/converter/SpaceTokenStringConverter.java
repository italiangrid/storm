/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.converter;

import it.grid.storm.srm.types.TSpaceToken;

/**
 * Class that handles DPM DB representation of a SpaceToken, in particular it takes care of the
 * NULL/EMPTY logic of DPM. In particular DPM uses the empty string "" as meaning the absence of a
 * value for the field, whereas StoRM accepts it as a valid String with which to create a
 * TSpaceToken; moreover StoRM uses an Empty TSpaceToken type.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date June 2005
 */
public class SpaceTokenStringConverter {

  /**
   * Method that translates StoRM Empty TSpaceToken String representation into DPM empty
   * representation; all other Strings are left as are.
   */
  public static String toDB(String s) {

    if (s.equals(TSpaceToken.makeEmpty().toString()))
      return "";
    return s;
  }

  /**
   * Method that translates DPM String representing an Empty TSpaceToken into StoRM representation;
   * any other String is left as is.
   */
  public static String toStoRM(String s) {

    if ((s == null) || (s.equals("")))
      return TSpaceToken.makeEmpty().toString();
    return s;
  }
}
