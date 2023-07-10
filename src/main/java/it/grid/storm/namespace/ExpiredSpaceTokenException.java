/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

public class ExpiredSpaceTokenException extends Exception {

  public ExpiredSpaceTokenException() {

    super();
  }

  public ExpiredSpaceTokenException(String message) {

    super(message);
  }

  public ExpiredSpaceTokenException(String message, Throwable cause) {

    super(message, cause);
  }

  public ExpiredSpaceTokenException(Throwable cause) {

    super(cause);
  }
}
