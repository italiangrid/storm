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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSpaceToken;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidSpaceDataAttributesException extends Exception {

  private static final long serialVersionUID = -5317879266114702669L;

  private boolean nullAuth = true;
  private boolean nullToken = true;

  public InvalidSpaceDataAttributesException(GridUserInterface guser) {

    nullAuth = (guser == null);
  }

  public InvalidSpaceDataAttributesException(TSpaceToken token) {

    nullToken = (token == null);
  }

  public String toString() {

    return "null-Auth=" + nullAuth + "nullToken=" + nullToken;
  }

}
