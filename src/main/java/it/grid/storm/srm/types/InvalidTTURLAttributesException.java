/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

public class InvalidTTURLAttributesException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public InvalidTTURLAttributesException(String message) {

    super(message);
  }

  public InvalidTTURLAttributesException(Throwable cause) {

    super(cause);
  }
}
