/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa;

public class AuthzDBReaderException extends Exception {

  /**
  * 
  */
  private static final long serialVersionUID = 1L;

  public AuthzDBReaderException() {

    super();
  }

  public AuthzDBReaderException(String message) {

    super(message);
  }

  public AuthzDBReaderException(String message, Throwable cause) {

    super(message, cause);
  }

  public AuthzDBReaderException(Throwable cause) {

    super(cause);
  }
}
