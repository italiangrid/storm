/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if TSURLINFO is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.srm.types;

public class InvalidTSURLInfoAttributeException extends Exception {

  private boolean nullSurl = true;

  public InvalidTSURLInfoAttributeException(TSURL surl) {

    nullSurl = (surl == null);
  }

  public String toString() {

    return "nullSurl = " + nullSurl;
  }
}
