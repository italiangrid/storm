/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc;

/** @author Michele Dibenedetto */
public class StoRMXmlRpcException extends Exception {

  /** */
  private static final long serialVersionUID = 1L;

  public StoRMXmlRpcException() {}

  public StoRMXmlRpcException(String message) {

    super(message);
  }

  public StoRMXmlRpcException(Throwable cause) {

    super(cause);
  }

  public StoRMXmlRpcException(String message, Throwable cause) {

    super(message, cause);
  }
}
