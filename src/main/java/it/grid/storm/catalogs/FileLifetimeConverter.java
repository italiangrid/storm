/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TLifeTimeInSeconds;

/**
 * Class that handles DB representation of a pinLifetime as expressed by a TLifetimeInSeconds
 * objects; in particular it takes care of protocol specification:
 *
 * <p>0/null/negative are translated as default StoRM configurable values. StoRMs Empty
 * TLifeTimeInSeconds is translated as 0.
 *
 * @author EGRID ICTP
 * @version 1.0
 * @date March 2007
 */
public class FileLifetimeConverter {

  private static FileLifetimeConverter stc = new FileLifetimeConverter(); // only
  // instance

  private FileLifetimeConverter() {}

  /** Method that returns the only instance of SizeInBytesIntConverter */
  public static FileLifetimeConverter getInstance() {

    return stc;
  }

  /**
   * Method that translates the Empty TLifeTimeInSeconds into the empty representation of DB which
   * is 0. Any other value is left as is.
   */
  public int toDB(long l) {

    if (l == TLifeTimeInSeconds.makeEmpty().value()) return 0;
    return new Long(l).intValue();
  }

  /**
   * Method that returns the long corresponding to the int value in the DB, except if it is 0, NULL
   * or negative; a configurable default value is returned instead, corresponding to the
   * getFileLifetimeDefault() Configuration class method.
   */
  public long toStoRM(int s) {

    if (s <= 0) return Configuration.getInstance().getFileLifetimeDefault();
    return new Integer(s).longValue();
  }
}
