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

import it.grid.storm.srm.types.TSizeInBytes;

/**
 * Class that handles DB representation of a TSizeInBytes, in particular it takes care of the NULL
 * logic of the DB: 0/null are used to mean an empty field, whereas StoRM Object model uses the type
 * TSizeInBytes.makeEmpty(); moreover StoRM does accept 0 as a valid TSizeInBytes, so it _is_
 * important to use this converter!
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date July 2005
 */
public class SizeInBytesIntConverter {

  private static SizeInBytesIntConverter stc = new SizeInBytesIntConverter();

  private SizeInBytesIntConverter() {

  }

  /**
   * Method that returns the only instance of SizeInBytesIntConverter
   */
  public static SizeInBytesIntConverter getInstance() {

    return stc;
  }

  /**
   * Method that transaltes the Empty TSizeInBytes into the empty representation of DB which is 0.
   * Any other int is left as is.
   */
  public long toDB(long s) {

    if (s == TSizeInBytes.makeEmpty().value())
      return 0;
    return s;
  }

  /**
   * Method that returns the int as is, except if it is 0 which DB interprests as empty field: in
   * that case it then returns the Empty TSizeInBytes int representation.
   */
  public long toStoRM(long s) {

    if (s == 0)
      return TSizeInBytes.makeEmpty().value();
    return s;
  }
}
