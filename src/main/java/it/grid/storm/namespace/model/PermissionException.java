/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class PermissionException extends RuntimeException {

  public PermissionException() {

    super();
  }

  public PermissionException(String message) {

    super(message);
  }

  public PermissionException(String message, Throwable cause) {

    super(message, cause);
  }

  public PermissionException(Throwable cause) {

    super(cause);
  }
}
