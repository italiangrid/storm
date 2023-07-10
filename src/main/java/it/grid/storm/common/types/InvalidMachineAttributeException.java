/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents an Exception thrown when the String supplied to the constructor of Machine
 * is null or empty.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 25th, 2005
 * @version 1.0
 */
public class InvalidMachineAttributeException extends Exception {

  private boolean nullName; // boolean representing a null name String
  private boolean emptyName; // boolean true if the supplied String is empty

  /** Constructor that requires the String that caused the exception to be thrown. */
  public InvalidMachineAttributeException(String name) {

    nullName = name == null;
    emptyName = (name.equals(""));
  }

  public String toString() {

    return "nullName=" + nullName + "; emptyName=" + emptyName;
  }
}
