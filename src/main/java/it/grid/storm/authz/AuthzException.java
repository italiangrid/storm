/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.authz;

public class AuthzException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AuthzException() {

    super();
  }

  public AuthzException(String message) {

    super(message);
  }
}
