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

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents an exception thrown when an SRMStatusOfPutRequestReply cannot be created
 * because the supplied toTURL or returnStatus are null.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date October, 2005
 */
public class InvalidPutStatusAttributesException extends Exception {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  // boolean indicating whether the supplied TURL is null or not
  private final boolean nullToTURL;

  // boolean indicating whether the supplied TReturnStatus is null or not
  private final boolean nullReturnStatus;

  /**
   * Constructor that requires the attributes that caused the exception to be thrown.
   */
  public InvalidPutStatusAttributesException(TTURL toTURL, TReturnStatus returnStatus) {

    nullToTURL = (toTURL == null);
    nullReturnStatus = (returnStatus == null);
  }

  @Override
  public String toString() {

    return String.format("nullToTURL=%b; nullReturnStatus=%b", nullToTURL, nullReturnStatus);
  }
}
