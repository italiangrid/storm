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

import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;

public class InvalidLSOutputAttributeException extends Exception {

  private boolean nullArray = true;

  public InvalidLSOutputAttributeException(ArrayOfTMetaDataPathDetail array) {

    nullArray = (array == null);
  }

  public String toString() {

    return "nullArray = " + nullArray;
  }
}
