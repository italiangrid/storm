/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.util.userinfo;

public class UserInfoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UserInfoException() {

    super();
  }

  public UserInfoException(String message) {

    super(message);
  }

  public UserInfoException(String message, Throwable cause) {

    super(message, cause);
  }

  public UserInfoException(Throwable cause) {

    super(cause);
  }
}
