/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

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
  private boolean empty = true;

  private TFN(Machine m, Port p, PFN pfn, boolean empty) {

    this.m = m;
    this.p = p;
    this.pfn = pfn;
    this.empty = empty;
  }

  /**
   * Static method that returns an empty TFN.
   */
  public static TFN makeEmpty() {

    return new TFN(Machine.makeEmpty(), Port.makeEmpty(), PFN.makeEmpty(), true);
  }

  public static TFN make(Machine m) {

    Preconditions.checkNotNull(m, "Invalid Machine: null");
    Preconditions.checkArgument(!m.isEmpty(), "Invalid Machine: empty");
    return new TFN(m, Port.makeEmpty(), PFN.makeEmpty(), false);
  }
  
  public static TFN make(Machine m, PFN pfn) {

    Preconditions.checkNotNull(m, "Invalid Machine: null");
    Preconditions.checkNotNull(pfn, "Invalid PFN: null");
    Preconditions.checkArgument(!m.isEmpty(), "Invalid Machine: empty");
    Preconditions.checkArgument(!pfn.isEmpty(), "Invalid PFN: empty");
    return new TFN(m, Port.makeEmpty(), pfn, false);
  }

  public static TFN make(Machine m, Port p, PFN pfn) {

    Preconditions.checkNotNull(m, "Invalid Machine: null");
    Preconditions.checkNotNull(p, "Invalid Port: null");
    Preconditions.checkNotNull(pfn, "Invalid PFN: null");
    Preconditions.checkArgument(!m.isEmpty(), "Invalid Machine: empty");
    Preconditions.checkArgument(!pfn.isEmpty(), "Invalid PFN: empty");
    return new TFN(m, p, pfn, false);
  }

  /**
   * Static method that returns a TFN with the specified PFN, and EmptyMachine as well as EmptyPort.
   * The intended use of this method is to create TFN to be used in TURLs with FILE protocol, where
   * no machine and port are needed. An InvalidTFNAttributesException is thrown if pfn is null or
   * empty: notice that the exception will also show Machine and Port as null.
   */
  public static TFN make(PFN pfn) {

    Preconditions.checkNotNull(pfn, "Invalid PFN: null");
    Preconditions.checkArgument(!pfn.isEmpty(), "Invalid PFN: empty");
    return new TFN(Machine.makeEmpty(), Port.makeEmpty(), pfn, false);
  }

  /**
   * Static method that returns a TFN from a String representation. If the supplied String is null
   * or malformed, an InvalidTFNAttributesException is thrown.
   */
  public static TFN makeFromString(String s) throws InvalidTFNAttributesException {

    if (s == null)
      throw new InvalidTFNAttributesException(null, null, null);
    int colon = s.indexOf(":"); // first occurence of :
    int slash = s.indexOf("/"); // first occurence of /

    if (colon == -1) {
      // missing port specification
      if ((slash == -1) || (slash == 0))
        throw new InvalidTFNAttributesException(null, null, null);
      // machine
      String mString = s.substring(0, slash);
      Machine m = Machine.make(mString);

      // Port is empty because it is optional specification
      Port p = Port.makeEmpty();
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = PFN.make(pfnString);
      return TFN.make(m, p, pfn);
    } else if ((slash != -1) && (colon > slash)) {
      // colon follows existing slash: the colon does NOT stand as port number
      // delimiter
      // treat it as missing port specification
      // machine
      if (slash == 0)
        throw new InvalidTFNAttributesException(null, null, null);
      String mString = s.substring(0, slash);
      Machine m = Machine.make(mString);
      // Port is empty because it is optional specification
      Port p = Port.makeEmpty();
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = PFN.make(pfnString);
      return TFN.make(m, p, pfn);
    } else if ((slash != -1) && (colon < slash)) {
      // both machine and port are present
      // machine
      if (colon == 0)
        throw new InvalidTFNAttributesException(null, null, null);
      String mString = s.substring(0, colon);
      Machine m = Machine.make(mString);
      // port
      if ((colon + 1) == slash)
        throw new InvalidTFNAttributesException(m, null, null);
      String pString = s.substring(colon + 1, slash);
      Port p = null;
      try {
        p = Port.make(Integer.parseInt(pString));
      } catch (NumberFormatException e) {
        log.warn("TFN: Unable to build -port- attribute from {}.{}", pString, e.getMessage());
      }
      // PFN checks only for a starting / while the rest can be empty! So it is
      // sufficient to choose whatever String starts at the /... even just the
      // slash itself if that is what is left!!! Should the StFN definition be
      // changed???
      String pfnString = s.substring(slash, s.length());
      PFN pfn = PFN.make(pfnString);
      return TFN.make(m, p, pfn);
    } else {
      // slash missing! Only colon is present: the TFN does not make sense!
      throw new InvalidTFNAttributesException(null, null, null);
    }
  }

  /**
   * Method that returns true if this Object is the empty TFN
   */
  public boolean isEmpty() {

    return empty;
  }

  /**
   * Method that returns the Machine specified in this TFN. If this is an empty TFN, then an empty
   * Machine is returned.
   */
  public Machine machine() {

    if (empty)
      return Machine.makeEmpty();
    return m;
  }

  /**
   * Method that returns the Port specified in this TFN. If this is an empty TFN,then an empty Port
   * is returned.
   */
  public Port port() {

    if (empty)
      return Port.makeEmpty();
    return p;
  }

  /**
   * Method that returns the PhysicalFileName specified in this TFN. If this is an empty TFN, then
   * an empty PFN is returned.
   */
  public PFN pfn() {

    if (empty)
      return PFN.makeEmpty();
    return pfn;
  }

  public String toString() {

    if (empty)
      return "Empty TFN";
    if (m.isEmpty() && p.isEmpty())
      return pfn.toString();
    if ((!m.isEmpty()) && p.isEmpty())
      return m.toString() + pfn.toString();
    return m + ":" + p + pfn;
  }

  public boolean equals(Object o) {

    if (o == this)
      return true;
    if (!(o instanceof TFN))
      return false;
    TFN tfno = (TFN) o;
    if (empty && tfno.empty)
      return true;
    return (!empty) && (!tfno.empty) && m.equals(tfno.m) && p.equals(tfno.p)
        && pfn.equals(tfno.pfn);
  }

  public int hashCode() {

    if (empty)
      return 0;
    int hash = 17;
    hash = 37 * hash + m.hashCode();
    hash = 37 * hash + p.hashCode();
    hash = 37 * hash + pfn.hashCode();
    return hash;
  }

}
