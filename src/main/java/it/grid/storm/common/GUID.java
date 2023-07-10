/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common;

import java.io.Serializable;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GUID implements Serializable {

  private static final long serialVersionUID = -1750955753676929827L;

  private static final Logger log = LoggerFactory.getLogger(GUID.class);

  private byte guidValue[] = new byte[16];

  public GUID() {
    buildNewGUID();
  }

  public GUID(String guidString) {

    int pos = 0;
    int count = 0;

    while (pos < guidString.length()) {
      guidValue[count] = getByteValue(guidString.substring(pos, pos + 2));
      pos += 2;
      count++;

      if (pos == guidString.length()) {
        continue;
      }

      if (guidString.charAt(pos) == '-') {
        pos++;
      }
    }
  }

  /**
   * Calculates the byte from a hex string.
   *
   * @param hex A string hex value.
   * @return a byte value.
   */
  private byte getByteValue(String hex) {

    return (byte) Integer.parseInt(hex, 16);
  }

  /**
   * Calculates the hex string from a byte.
   *
   * @param val A byte value.
   * @return a string hex value.
   */
  private String getHexString(byte val) {

    String hexString;
    if (val < 0) {
      hexString = Integer.toHexString(val + 256);
    } else {
      hexString = Integer.toHexString(val);
    }

    if (hexString.length() < 2) {
      return "0" + hexString.toUpperCase();
    }
    return hexString.toUpperCase();
  }

  /**
   * Set the bytes in the array from another array.
   *
   * @param lg The other array of bytes.
   * @param count How many there are.
   * @param startPos The point in the main byte array these should go.
   */
  private void setByteValues(byte[] lg, int startPos, int count) {

    for (int i = 0; i < count; i++) {
      guidValue[i + startPos] = lg[i];
    }
  }

  /**
   * Sets the byte in the array to the bytes in a long value. In reverse order(IE Least significant
   * byte goes first)
   *
   * @param lg The long value.
   * @param count How many there are.
   * @param startPos The point in the main byte array these should go.
   */
  private void setByteValues(long lg, int startPos, int count) {

    for (int i = 0; i < count; i++) {
      guidValue[i + startPos] = (byte) (lg & 0xFF);
      lg = lg / 0xFF;
    }
  }

  /**
   * Creates a new GUID from nowhere. Actually it uses the time, IPAddress and a random number.
   * Stores it all in the main byte array.
   */
  private void buildNewGUID() {

    try {
      // The time in milli seconds for six bytes
      // gives us until the year 10000ish.
      long lg = System.currentTimeMillis();
      setByteValues(lg, 0, 6);

      // The hash code for this object for two bytes (As a why not option?)
      lg = this.hashCode();
      setByteValues(lg, 6, 2);

      // The ip address for this computer (as we cannot get to the MAC address)
      InetAddress inet = InetAddress.getLocalHost();
      byte[] bytes = inet.getAddress();
      setByteValues(bytes, 8, 4);

      // A random number for two bytes
      lg = (long) ((Math.random() * 0xFFFF));
      setByteValues(lg, 12, 2);

      // Another random number for two bytes
      lg = (long) ((Math.random() * 0xFFFF));
      setByteValues(lg, 14, 2);

    } catch (Exception e) {
      log.error("Error while generating a GUID", e);
    }
  }

  /**
   * Stores the 16 bytes seperately, this returns that array Not sure why though.
   *
   * @return array of bytes.
   */
  public byte[] getBytes() {

    return guidValue;
  }

  /**
   * Overrides toString(). Returns the array of bytes in the standard form:
   * xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
   *
   * @return the string format
   */
  @Override
  public String toString() {

    StringBuilder buf = new StringBuilder();

    buf.append(getHexString(guidValue[0]));
    buf.append(getHexString(guidValue[1]));
    buf.append(getHexString(guidValue[2]));
    buf.append(getHexString(guidValue[3]));
    buf.append('-');
    buf.append(getHexString(guidValue[4]));
    buf.append(getHexString(guidValue[5]));
    buf.append('-');
    buf.append(getHexString(guidValue[6]));
    buf.append(getHexString(guidValue[7]));
    buf.append('-');
    buf.append(getHexString(guidValue[8]));
    buf.append(getHexString(guidValue[9]));
    buf.append('-');
    buf.append(getHexString(guidValue[10]));
    buf.append(getHexString(guidValue[11]));
    buf.append(getHexString(guidValue[12]));
    buf.append(getHexString(guidValue[13]));
    buf.append(getHexString(guidValue[14]));
    buf.append(getHexString(guidValue[15]));

    return buf.toString();
  }
}
