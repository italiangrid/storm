/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.checksum;

public enum ChecksumAlgorithm {
  CRC32("CRC32"),
  ADLER32("ADLER32"),
  MD2("MD2"),
  MD5("MD5"),
  SHA_1("SHA-1"),
  SHA_256("SHA-256"),
  SHA_384("SHA-384"),
  SHA_512("SHA-512");

  public static ChecksumAlgorithm getChecksumAlgorithm(String algorithm) {

    algorithm = algorithm.toUpperCase();

    for (ChecksumAlgorithm checksumAlgorithm : ChecksumAlgorithm.values()) {
      if (checksumAlgorithm.toString().toUpperCase().equals(algorithm)) {
        return checksumAlgorithm;
      }
    }
    return null;
  }

  private final String value;

  ChecksumAlgorithm(String value) {

    this.value = value;
  }

  public String getValue() {

    return value;
  }

  public String toString() {

    return value;
  }
}
