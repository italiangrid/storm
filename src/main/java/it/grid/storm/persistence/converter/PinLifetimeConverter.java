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

package it.grid.storm.persistence.converter;

import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.config.Configuration;

/**
 * Class that handles DB representation of a TLifetimeInSeconds, in particular it takes care of
 * protocol specification:
 * 
 * 0/null/negative are translated as default StoRM configurable values. StoRMs Empty
 * TLifeTimeInSeconds is translated as 0.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date March 2007
 */
public class PinLifetimeConverter {

  private static PinLifetimeConverter stc = new PinLifetimeConverter();

  private PinLifetimeConverter() {

  }

  /**
   * Method that returns the only instance of SizeInBytesIntConverter
   */
  public static PinLifetimeConverter getInstance() {

    return stc;
  }

  /**
   * Method that translates the Empty TLifeTimeInSeconds into the empty representation of DB which
   * is 0. Any other value is left as is.
   */
  public int toDB(long l) {

    if (l == TLifeTimeInSeconds.makeEmpty().value())
      return 0;
    return Long.valueOf(l).intValue();
  }

  /**
   * Method that returns the long corresponding to the int value in the DB, except if it is 0, NULL
   * or negative; a configurable default value is returned instead, corresponding to the
   * getPinLifetimeMinimum() Configuration class method.
   */
  public long toStoRM(int s) {

    if (s == 0) {
      return Configuration.getInstance().getPinLifetimeDefault();
    } else if (s < 0) {
      // The default is used also as a Minimum
      return Configuration.getInstance().getPinLifetimeDefault();
    }
    return Integer.valueOf(s).longValue();
  }

  public long toStoRM(long s) {

    if (s == 0) {
      return Configuration.getInstance().getPinLifetimeDefault();
    } else if (s < 0) {
      // The default is used also as a Minimum
      return Configuration.getInstance().getPinLifetimeDefault();
    }
    return s;
  }
}
