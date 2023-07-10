/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check;

/** @author Michele Dibenedetto */
public class GenericCheckException extends Exception {

  private static final long serialVersionUID = -5467729262145881935L;

  public GenericCheckException(String message) {

    super(message);
  }
}
