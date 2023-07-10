/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

public class NamespaceException extends Exception {

  public NamespaceException() {

    super();
  }

  public NamespaceException(String message) {

    super(message);
  }

  public NamespaceException(String message, Throwable cause) {

    super(message, cause);
  }

  public NamespaceException(Throwable cause) {

    super(cause);
  }
}
