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

package it.grid.storm.filesystem;

/**
 * Super class that represents a generic reservation exception.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date May 2006
 */
public class ReservationException extends Exception {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private String error = "";

  /**
   * Public constructor requiring a String explaining the nature of the error. If the String is
   * null, then an empty one is used instead.
   */
  public ReservationException(String error) {

    if (error != null)
      this.error = error;
  }

  public ReservationException(String error, Throwable cause) {
    super(error, cause);
  }



  public String toString() {

    return error;
  }
}
