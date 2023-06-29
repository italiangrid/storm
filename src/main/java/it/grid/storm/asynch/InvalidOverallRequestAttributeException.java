/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

/**
 * Class that represents an Exception thrown when OverallRequest was attempted
 * to be created with a null TRequestToken.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2006
 */
public class InvalidOverallRequestAttributeException extends Exception {

	/**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public String toString() {

		return "Null TRequestToken supplied!";
	}
}
