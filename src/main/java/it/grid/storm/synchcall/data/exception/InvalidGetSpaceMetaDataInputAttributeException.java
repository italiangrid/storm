/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.exception;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;

public class InvalidGetSpaceMetaDataInputAttributeException extends Exception {

  private boolean nullUser = true;
  private boolean nullToken = true;

  public InvalidGetSpaceMetaDataInputAttributeException(
      GridUserInterface user, ArrayOfTSpaceToken tokenArray) {

    nullToken = (tokenArray == null);
    nullUser = (user == null);
  }

  public String toString() {

    return "nullTokenArray = " + nullToken + "- nullUser = " + nullUser;
  }
}
