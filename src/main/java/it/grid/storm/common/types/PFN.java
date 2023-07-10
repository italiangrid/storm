/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents the PFN of a TTURL: it must begin with a /, and it cannot be an empty or
 * null string. Any white spaces are automatically removed. For Empty PFN there is the appropriate
 * method to be used.
 *
 * @author CNAF - Bologna
 * @version 1.0
 * @date April 2005
 */
public class PFN {

  private String name; // String containing the PFN
  private boolean empty = true; // boolean indicating whether this is an Empty
  // PFN

  /**
   * Private constructor that requires a String representing the pathname of the TTURL. Empty spaces
   * are automatically removed.
   */
  private PFN(String name, boolean empty) {

    this.name = name.replaceAll(" ", "");
    this.empty = empty;
  }

  /** Method that returns an Empty PFN. */
  public static PFN makeEmpty() {

    return new PFN("", true);
  }

  /**
   * Method that returns a PFN corresponding to the supplied String. The String cannot be null or
   * empty otherwise an InvalidPFNAttributeException is thrown. Likewise if it does not begin with a
   * /.
   */
  public static PFN make(String name) throws InvalidPFNAttributeException {

    if ((name == null) || (name.equals(""))) throw new InvalidPFNAttributeException(name);
    return new PFN(name, false);
  }

  public String getValue() {

    return name;
  }

  /** Method that returns true if this PFN is an Empty object. */
  public boolean isEmpty() {

    return empty;
  }

  public String toString() {

    if (empty) return "Empty PFN";
    return name;
  }

  public boolean equals(Object o) {

    if (o == this) return true;
    if (!(o instanceof PFN)) return false;
    PFN po = (PFN) o;
    if (po.empty && empty) return true;
    return (!po.empty) && (!empty) && (name.equals(po.name));
  }

  public int hashCode() {

    if (empty) return 0;
    int hash = 17;
    return hash + 37 * name.hashCode();
  }
}
