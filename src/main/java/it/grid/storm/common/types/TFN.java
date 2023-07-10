/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a TFN Transfer File Name.
 *
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 25th, 2005
 * @version 2.0
 */
public class TFN {

  private static Logger log = LoggerFactory.getLogger(TFN.class);

  private Machine m = null;
  private Port p = null;
  private PFN pfn = null;
  private boolean empty = true; // boolean indicating whether this is an empty
  // TFN

  private TFN(Machine m, Port p, PFN pfn, boolean empty) {

    this.m = m;
    this.p = p;
    this.pfn = pfn;
    this.empty = empty;
  }

  /** Static method that returns an empty TFN. */
  public static TFN makeEmpty() {

    return new TFN(Machine.makeEmpty(), Port.makeEmpty(), PFN.makeEmpty(), true);
  }

  /**
   * Static method that returns a TFN, and requires a Machine m, the Port p on that Machine, and the
   * PhysicalFileName pfn. An InvalidTFNAttributesException is thrown if any is null or empty.
   */
  public static TFN make(Machine m, Port p, PFN pfn) throws InvalidTFNAttributesException {

    if ((m == null) || (p == null) || (pfn == null) || m.isEmpty() || pfn.isEmpty())
      throw new InvalidTFNAttributesException(m, p, pfn);
    return new TFN(m, p, pfn, false);
  }

  /**
   * Static method that returns a TFN with the specified PFN, and EmptyMachine as well as EmptyPort.
   * The intended use of this method is to create TFN to be used in TURLs with FILE protocol, where
   * no machine and port are needed. An InvalidTFNAttributesException is thrown if pfn is null or
   * empty: notice that the exception will also show Machine and Port as null.
   */
  public static TFN makeByPFN(PFN pfn) throws InvalidTFNAttributesException {

    if ((pfn == null) || (pfn.isEmpty())) throw new InvalidTFNAttributesException(null, null, pfn);
    return new TFN(Machine.makeEmpty(), Port.makeEmpty(), pfn, false);
  }

  /**
   * Static method that returns a TFN from a String representation. If the supplied String is null
   * or malformed, an InvalidTFNAttributesException is thrown.
   */
  public static TFN makeFromString(String s) throws InvalidTFNAttributesException {

    if (s == null) throw new InvalidTFNAttributesException(null, null, null);
    int colon = s.indexOf(":"); // first occurence of :
    int slash = s.indexOf("/"); // first occurence of /

    if (colon == -1) {
      // missing port specification
      if ((slash == -1) || (slash == 0))
        throw new InvalidTFNAttributesException(null, null, null); // no colon
      // and no
      // slash or
      // slash
      // right at
      // the
      // beginning!
      // machine
      String mString = s.substring(0, slash);
      Machine m = null;
      try {
        m = Machine.make(mString);
      } catch (InvalidMachineAttributeException e) {

        log.warn("TFN: Unable to build -machine- attribute from {}. {}", mString, e.getMessage());
      }

      // Port is empty because it is optional specification
      Port p = Port.makeEmpty();
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = null;
      try {
        pfn = PFN.make(pfnString);
      } catch (InvalidPFNAttributeException e) {
        log.warn("TFN: Unable to build -pfn- attribute from {}. {}", pfnString, e.getMessage());
      }
      return TFN.make(m, p, pfn);
    } else if ((slash != -1) && (colon > slash)) {
      // colon follows existing slash: the colon does NOT stand as port number
      // delimiter
      // treat it as missing port specification
      // machine
      if (slash == 0) throw new InvalidTFNAttributesException(null, null, null); // slash
      // right at
      // the
      // beginning!
      String mString = s.substring(0, slash);
      Machine m = null;
      try {
        m = Machine.make(mString);
      } catch (InvalidMachineAttributeException e) {

        log.warn("TFN: Unable to build -machine- attribute from {}. {}", mString, e.getMessage());
      }
      // Port is empty because it is optional specification
      Port p = Port.makeEmpty();
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = null;
      try {
        pfn = PFN.make(pfnString);
      } catch (InvalidPFNAttributeException e) {
        log.warn("TFN: Unable to build -pfn- attribute from {}. {}", pfnString, e.getMessage());
      }
      return TFN.make(m, p, pfn);
    } else if ((slash != -1) && (colon < slash)) {
      // both machine and port are present
      // machine
      if (colon == 0) throw new InvalidTFNAttributesException(null, null, null); // colon
      // right at
      // the
      // beginning!
      String mString = s.substring(0, colon);
      Machine m = null;
      try {
        m = Machine.make(mString);
      } catch (InvalidMachineAttributeException e) {

        log.warn("TFN: Unable to build -machine- attribute from {}. {}", mString, e.getMessage());
      }
      // port
      if ((colon + 1) == slash)
        throw new InvalidTFNAttributesException(m, null, null); // slash found
      // right after
      // colon! There
      // is no port!
      String pString = s.substring(colon + 1, slash);
      Port p = null;
      try {
        p = Port.make(Integer.parseInt(pString));
      } catch (InvalidPortAttributeException e) {
        log.warn("TFN: Unable to build -port- attribute from {}.{}", pString, e.getMessage());
      } catch (NumberFormatException e) {
        log.warn("TFN: Unable to build -port- attribute from {}.{}", pString, e.getMessage());
      }
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = null;
      try {
        pfn = PFN.make(pfnString);
      } catch (InvalidPFNAttributeException e) {

        log.warn("TFN: Unable to build -pfn- attribute from {}. {}", pfnString, e.getMessage());
      }
      return TFN.make(m, p, pfn);
    } else {
      // slash missing! Only colon is present: the TFN does not make sense!
      throw new InvalidTFNAttributesException(null, null, null);
    }
  }

  /** Method that returns true if this Object is the empty TFN */
  public boolean isEmpty() {

    return empty;
  }

  /**
   * Method that returns the Machine specified in this TFN. If this is an empty TFN, then an empty
   * Machine is returned.
   */
  public Machine machine() {

    if (empty) return Machine.makeEmpty();
    return m;
  }

  /**
   * Method that returns the Port specified in this TFN. If this is an empty TFN,then an empty Port
   * is returned.
   */
  public Port port() {

    if (empty) return Port.makeEmpty();
    return p;
  }

  /**
   * Method that returns the PhysicalFileName specified in this TFN. If this is an empty TFN, then
   * an empty PFN is returned.
   */
  public PFN pfn() {

    if (empty) return PFN.makeEmpty();
    return pfn;
  }

  public String toString() {

    if (empty) return "Empty TFN";
    if (m.isEmpty() && p.isEmpty()) return pfn.toString();
    if ((!m.isEmpty()) && p.isEmpty()) return m.toString() + pfn.toString();
    return m + ":" + p + pfn;
  }

  public boolean equals(Object o) {

    if (o == this) return true;
    if (!(o instanceof TFN)) return false;
    TFN tfno = (TFN) o;
    if (empty && tfno.empty) return true;
    return (!empty)
        && (!tfno.empty)
        && m.equals(tfno.m)
        && p.equals(tfno.p)
        && pfn.equals(tfno.pfn);
  }

  public int hashCode() {

    if (empty) return 0;
    int hash = 17;
    hash = 37 * hash + m.hashCode();
    hash = 37 * hash + p.hashCode();
    hash = 37 * hash + pfn.hashCode();
    return hash;
  }
}
