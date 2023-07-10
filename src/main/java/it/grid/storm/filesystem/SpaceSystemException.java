/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

/**
 * Class that represents an Exception thrown whenever a SpaceSystem cannot be instantiated.
 *
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date June 2006
 */
public class SpaceSystemException extends Exception {

  private String explanation = "";

  /**
   * Constructor that requires a non-null String describing the problem encountered. If a null is
   * supplied, then an empty String is used instead.
   */
  public SpaceSystemException(String explanation) {

    if (explanation != null) this.explanation = explanation;
  }

  public String toString() {

    return explanation;
  }
}
