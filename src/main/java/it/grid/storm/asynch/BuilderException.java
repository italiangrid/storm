/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

/**
 * @author Michele Dibenedetto
 * 
 */
public class BuilderException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -7167592101486133296L;

  public BuilderException() {

  }

  public BuilderException(String message) {

    super(message);
  }

  public BuilderException(Throwable cause) {

    super(cause);
  }

  public BuilderException(String message, Throwable cause) {

    super(message, cause);
  }
}
