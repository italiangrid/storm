/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when FileSize receives null as constructor attributes;
 * or a negative size.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 23rd, 2005
 * @version 1.0
 */
import it.grid.storm.common.types.SizeUnit;

public class InvalidTSizeAttributesException extends RuntimeException {

  /** */
  private static final long serialVersionUID = 1L;

  private boolean negativeSize;
  private boolean nullUnit;

  /** Constructor that requires the long and the SizeUnit that caused the exception to be thrown. */
  public InvalidTSizeAttributesException(long size, SizeUnit unit) {

    nullUnit = unit == null;
    negativeSize = size < 0;
  }

  public String toString() {

    return "Ivalid TFileSize Attributes: nullSizeUnit="
        + nullUnit
        + "; negativeSize="
        + negativeSize;
  }
}
