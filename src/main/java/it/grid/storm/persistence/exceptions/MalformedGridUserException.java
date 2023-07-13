/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.exceptions;

/**
 * This class represents an Exception thrown when the RequestSummaryCatalog cannot create a
 * VomsGridUser with the available data.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 1.0
 */
public class MalformedGridUserException extends Exception {

  private static final long serialVersionUID = 5607710323595609428L;

  public MalformedGridUserException(String message) {
    super(message);
  }
}
