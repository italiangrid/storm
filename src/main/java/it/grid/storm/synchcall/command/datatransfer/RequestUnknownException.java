/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

public class RequestUnknownException extends Exception {

  /** */
  private static final long serialVersionUID = 2766075955119694140L;

  public RequestUnknownException() {}

  public RequestUnknownException(String message) {

    super(message);
  }

  public RequestUnknownException(Throwable cause) {

    super(cause);
  }

  public RequestUnknownException(String message, Throwable cause) {

    super(message, cause);
  }
}
