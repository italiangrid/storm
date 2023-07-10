/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the file size as a long and contains a unit of measure
 *
 * @author Ezio Corso - Magnoni Luca
 * @author EGRID ICTP Trieste / CNAF INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.common.types.SizeUnit;
import java.io.Serializable;
import java.util.Map;

public class TSizeInBytes implements Serializable {

  public static String PNAME_SIZE = "size";
  public static String PNAME_DESIREDSIZEOFTOTALSPACE = "desiredSizeOfTotalSpace";
  public static String PNAME_DESIREDSIZEOFGUARANTEEDSPACE = "desiredSizeOfGuaranteedSpace";
  public static String PNAME_SIZEOFTOTALRESERVEDSPACE = "sizeOfTotalReservedSpace";
  public static String PNAME_SIZEOFGUARANTEEDRESERVEDSPACE = "sizeOfGuaranteedReservedSpace";
  public static String PNAME_TOTALSIZE = "totalSize";
  public static String PNAME_GUARANTEEDSIZE = "guaranteedSize";
  public static String PNAME_UNUSEDSIZE = "unusedSize";

  private long size = -1;
  private SizeUnit unit = SizeUnit.EMPTY;
  private boolean empty = true;
  private static TSizeInBytes emptySize = null; // only instance of empty
  // TSizeInBytes!

  /** Constructor requiring the size as a long, and the unit of measure SizeUnit. */
  private TSizeInBytes(long size, SizeUnit unit, boolean empty) {

    this.size = size;
    this.unit = unit;
    this.empty = empty;
  }

  /**
   * Factory method that returns a TSizeInBytes object; an InvalidTSizeAttributesException is thrown
   * if a null SizeUnit is passed, or if a negative long is passed as size.
   */
  public static TSizeInBytes make(long size, SizeUnit unit) throws InvalidTSizeAttributesException {

    if ((unit == null) || (size < 0)) throw new InvalidTSizeAttributesException(size, unit);
    return new TSizeInBytes(size, unit, false);
  }

  /** Method that returns an empty TSizeInBytes object. */
  public static TSizeInBytes makeEmpty() {

    if (emptySize != null) return emptySize;
    emptySize = new TSizeInBytes(-1, SizeUnit.EMPTY, true);
    return emptySize;
  }

  /**
   * Method that returns a TSizeInBytes object retrieving its value by the Hashtable used for
   * comunicating with the FE
   */
  public static TSizeInBytes decode(Map inputParam, String fieldName) {

    String size = (String) inputParam.get(fieldName);

    if (size == null) return TSizeInBytes.makeEmpty();
    long sizeLong = Long.parseLong(size);

    TSizeInBytes decodedSize = null;
    try {
      decodedSize = TSizeInBytes.make(sizeLong, SizeUnit.BYTES);
    } catch (InvalidTSizeAttributesException e) {
      return TSizeInBytes.makeEmpty();
    }

    return decodedSize;
  }

  /**
   * Method that converts this FileSize to the specified SizeUnit; beware that converting back will
   * _not_ satisfy equality because of rounding in calculation. In case the wanted unit is null, or
   * this TSizeInBytes is empty, -1 is returned.
   */
  public double getSizeIn(SizeUnit unit) {

    if ((unit != null) && (!empty)) {
      Long l_size = Long.valueOf(size);
      double result =
          l_size.doubleValue() * (this.unit.conversionFactor() / unit.conversionFactor());
      return result;
    } else return -1;
  }

  /**
   * Method that returns a long that represents the value with which this TSizeInBytes was created.
   * In case this is empty, -1 is returned.
   */
  public long value() {

    if (empty) return -1;
    return size;
  }

  /**
   * Method that returns the SizeUnit with which this TSizeInBytes was created. In case this is
   * empty, SizeUnit.EMPTY is returned.
   */
  public SizeUnit unit() {

    if (empty) return SizeUnit.EMPTY;
    return unit;
  }

  /** Method that returns whether this is an empty TSizeInBytes. */
  public boolean isEmpty() {

    return empty;
  }

  /** Method uses to encode value for FE communication. */
  public void encode(Map param, String fieldName) {

    if (empty) return;

    long size_out;
    Long sizeInBytes = Long.valueOf(this.value());
    if (sizeInBytes != null) size_out = sizeInBytes.longValue();
    else size_out = -1;

    param.put(fieldName, String.valueOf(size_out));
  }

  public String toString() {

    if (empty) return "Empty";
    return size + " " + unit;
  }

  /**
   * Beware that this equality will _not_ return true for the same quantity expressed in different
   * units of measure!
   */
  public boolean equals(Object o) {

    if (o == this) return true;
    if (!(o instanceof TSizeInBytes)) return false;
    TSizeInBytes fs = (TSizeInBytes) o;
    if ((empty) && (fs.empty)) return true;
    return ((!empty) && (!fs.empty) && (this.size == fs.size) && (this.unit == fs.unit));
  }

  public int hashCode() {

    if (empty) return 0;
    int hash = 17;
    hash = 37 * hash + (Long.valueOf(size)).hashCode();
    hash = 37 * hash + unit.hashCode();
    return hash;
  }
}
