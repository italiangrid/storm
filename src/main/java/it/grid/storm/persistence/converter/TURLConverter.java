/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.converter;

import it.grid.storm.srm.types.TTURL;

/**
 * Class that handles DPM DB representation of a TTURL, in particular it takes care of the
 * NULL/EMPTY logic of DPM. Indeed DPM uses 0/null to mean an empty field, whereas StoRM uses the
 * type TTURL.makeEmpty(); in particular StoRM converts an empty String or a null to an Empty TTURL!
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date March 2006
 */
public class TURLConverter {

  private static TURLConverter stc = new TURLConverter(); // only instance

  private TURLConverter() {

  }

  /**
   * Method that returns the only instance of SizeInBytesIntConverter
   */
  public static TURLConverter getInstance() {

    return stc;
  }

  /**
   * Method that transaltes the Empty TTURL into the empty representation of DPM which is a null!
   * Any other String is left as is.
   */
  public String toDB(String s) {

    if (s.equals(TTURL.makeEmpty().toString()))
      return null;
    return s;
  }

  /**
   * Method that translates DPMs "" or null String as the Empty TTURL String representation. Any
   * other String is left as is.
   */
  public String toStoRM(String s) {

    if ((s == null) || (s.equals("")))
      return TTURL.makeEmpty().toString();
    return s;
  }
}
