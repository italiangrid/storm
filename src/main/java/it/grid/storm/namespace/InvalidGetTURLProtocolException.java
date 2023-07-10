/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

/**
 * This class represents an Exception throws if TDirOptionData is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
public class InvalidGetTURLProtocolException extends Exception {

  /** */
  private static final long serialVersionUID = 1L;

  public InvalidGetTURLProtocolException(String message) {

    super(message);
  }
}
