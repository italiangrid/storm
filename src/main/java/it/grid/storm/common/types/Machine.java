/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

import com.google.common.base.Preconditions;

/**
 * This class represents the name of a machine in a SFN.
 * 
 * @author EGRID - ICTP Trieste; CNAF - Bologna
 * @date March 25th, 2005
 * @version 2.0
 */
public class Machine {

  private String name = ""; // name of the machine in the SFN
  private boolean empty = true; // boolean true if this object is the empty
                                // object

  private Machine(String name, boolean empty) {

    this.name = name.replaceAll(" ", "");
    this.empty = empty;
  }

  /**
   * Static method that returns an empty Machine.
   */
  public static Machine makeEmpty() {

    return new Machine("", true);
  }

  public static Machine make(String s) {

    Preconditions.checkNotNull(s, "Invalid machine: null");
    Preconditions.checkArgument(!s.trim().isEmpty(), "Invalid machine: empty");
    return new Machine(s.trim(), false);
  }

  /**
   * Return true if Empty instance of machine object
   */
  public boolean isEmpty() {

    return empty;
  }

  public String getValue() {

    return name;
  }

  public String toString() {

    if (empty)
      return "Empty Machine";
    return name;
  }

  public boolean equals(Object o) {

    if (o == this)
      return true;
    if (!(o instanceof Machine))
      return false;
    Machine mo = (Machine) o;
    if (mo.empty && empty)
      return true;
    return (!mo.empty && !empty && mo.getValue().equals(name));
  }

  public int hashCode() {

    if (empty)
      return 0;
    int hash = 17;
    return 37 * hash + name.hashCode();
  }
}
