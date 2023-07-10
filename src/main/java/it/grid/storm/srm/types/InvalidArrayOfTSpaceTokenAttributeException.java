/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken is invoked with a
 * null String.
 */
public class InvalidArrayOfTSpaceTokenAttributeException extends Exception {

  private boolean nullArray;

  public InvalidArrayOfTSpaceTokenAttributeException(TSpaceToken[] tokenArray) {

    nullArray = tokenArray == null;
  }

  public String toString() {

    return "Invalid SpaceToken[]: nullArray = " + nullArray;
  }
}
