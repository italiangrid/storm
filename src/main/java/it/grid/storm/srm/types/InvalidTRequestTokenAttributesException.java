/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for TRequestToken is invoked with
 * a null String.
 *
 * @author Magnoni Luca
 * @author CNAF INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
public class InvalidTRequestTokenAttributesException extends Exception {

  private boolean nullString;

  public InvalidTRequestTokenAttributesException(String s) {

    nullString = s == null;
  }

  public String toString() {

    return "Invalid RequestToken Attributes: nullString=" + nullString;
  }
}
