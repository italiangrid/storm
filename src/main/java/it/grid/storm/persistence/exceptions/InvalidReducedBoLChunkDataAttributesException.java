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

package it.grid.storm.persistence.exceptions;

import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * This class represents an exceptin thrown when the attributes supplied to the constructor of
 * ReducedBoLChunkData are invalid, that is if any is _null_.
 * 
 * @author EGRID - ICTP Trieste
 * @date November, 2006
 * @version 1.0
 */
public class InvalidReducedBoLChunkDataAttributesException extends Exception {

  private static final long serialVersionUID = -8145580437017768234L;

  // booleans that indicate whether the corresponding variable is null
  private boolean nullFromSURL;
  private boolean nullStatus;

  /**
   * Constructor that requires the attributes that caused the exception to be thrown.
   */
  public InvalidReducedBoLChunkDataAttributesException(TSURL fromSURL, TReturnStatus status) {

    nullFromSURL = fromSURL == null;
    nullStatus = status == null;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Invalid BoLChunkData attributes: null-fromSURL=");
    sb.append(nullFromSURL);
    sb.append("; null-status=");
    sb.append(nullStatus);
    sb.append(".");
    return sb.toString();
  }
}
