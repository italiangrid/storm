/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if TExtraInfo is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.srm.types;

public class InvalidArrayOfTSURLReturnStatusAttributeException extends Exception {

  private boolean nullArray = true;

  public InvalidArrayOfTSURLReturnStatusAttributeException(Object[] array) {

    nullArray = (array == null);
  }

  public String toString() {

    return "nullArray = " + nullArray;
  }
}
