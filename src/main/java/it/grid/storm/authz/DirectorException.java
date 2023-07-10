/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz;

public class DirectorException extends Exception {

  /** */
  private static final long serialVersionUID = 8391356294029256927L;

  public DirectorException() {}

  public DirectorException(String message) {

    super(message);
  }

  public DirectorException(Throwable cause) {

    super(cause);
  }

  public DirectorException(String message, Throwable cause) {

    super(message, cause);
  }
}
