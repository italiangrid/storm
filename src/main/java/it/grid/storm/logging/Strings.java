/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.logging;

/** Helper class for working with Strings. */
public final class Strings {

  /** Constructor. */
  private Strings() {}

  /**
   * A "safe" null/empty check for strings.
   *
   * @param s The string to check
   * @return true if the string is null or the trimmed string is length zero
   */
  public static boolean isEmpty(String s) {

    if (s != null) {
      String sTrimmed = s.trim();
      if (sTrimmed.length() > 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * Compares two strings for equality, allowing for nulls.
   *
   * @param <T> type of object to compare
   * @param s1 The first operand
   * @param s2 The second operand
   * @return true if both are null or both are non-null and the same string value
   */
  public static <T> boolean safeEquals(T s1, T s2) {

    if (s1 == null || s2 == null) {
      return s1 == s2;
    }

    return s1.equals(s2);
  }

  /**
   * A safe string trim that handles nulls.
   *
   * @param s the string to trim
   * @return the trimmed string or null if the given string was null
   */
  public static String safeTrim(String s) {

    if (s != null) {
      return s.trim();
    }

    return null;
  }

  /**
   * Removes preceding or proceeding whitespace from a string or return null if the string is null
   * or of zero length after trimming (i.e. if the string only contained whitespace).
   *
   * @param s the string to trim
   * @return the trimmed string or null
   */
  public static String safeTrimOrNullString(String s) {

    if (s != null) {
      String sTrimmed = s.trim();
      if (sTrimmed.length() > 0) {
        return sTrimmed;
      }
    }

    return null;
  }
}
