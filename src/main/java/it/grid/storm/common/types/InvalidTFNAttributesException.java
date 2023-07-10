/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents an exception thrown when the TFN constructor is invoked with null Machine,
 * Port or PathName, or if any is empty.
 *
 * @author EGRID - ICTP Trieste
 * @date March 26th, 2005
 * @version 2.0
 */
public class InvalidTFNAttributesException extends Exception {

  private boolean nullMachine; // boolean true if Machine is null
  private boolean nullPort; // boolean true if Port is null
  private boolean nullPFN; // boolean true if PathName is null
  private boolean emptyMachine = false; // boolean true if Machine is empty
  private boolean emptyPort = false; // boolean true if Port is empty
  private boolean emptyPFN = false; // boolean true if PFN is empty

  /**
   * Constructor that requires the Machine m, the Port p and the PathName pn that caused the
   * Exception to be thrown.
   */
  public InvalidTFNAttributesException(Machine m, Port p, PFN pfn) {

    nullMachine = (m == null);
    nullPort = (p == null);
    nullPFN = (pfn == null);
    if (!nullMachine) emptyMachine = m.isEmpty();
    if (!nullPort) emptyPort = p.isEmpty();
    if (!nullPFN) emptyPFN = pfn.isEmpty();
  }

  public String toString() {

    return "nullMachine="
        + nullMachine
        + "; emptyMachine="
        + emptyMachine
        + "; nullPort="
        + nullPort
        + "; emptyPort="
        + emptyPort
        + "; nullPFN="
        + nullPFN
        + "; emptyPFN="
        + emptyPFN
        + ".";
  }
}
