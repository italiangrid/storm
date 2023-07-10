/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.checksum;

import it.grid.storm.config.DefaultValue;
import it.grid.storm.ea.ExtendedAttributesException;
import it.grid.storm.ea.StormEA;
import java.io.FileNotFoundException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumManager {

  private static final Logger log = LoggerFactory.getLogger(ChecksumManager.class);

  private static volatile ChecksumManager instance = null;
  private ChecksumAlgorithm defaultAlgorithm;

  private ChecksumManager() {

    defaultAlgorithm = DefaultValue.getChecksumAlgorithm();
  }

  public static synchronized ChecksumManager getInstance() {

    if (instance == null) {
      instance = new ChecksumManager();
    }
    return instance;
  }

  /**
   * Return the algorithm used to compute checksums as well as retrieve the value from extended
   * attributes.
   *
   * @return the algorithm used to compute checksums as well as retrieve the value from extended
   *     attributes.
   */
  public ChecksumAlgorithm getDefaultAlgorithm() {

    return defaultAlgorithm;
  }

  /**
   * Return the computed checksum for the given file. If the checksum is already stored in an
   * extended attribute then that value is given back, otherwise: - check if the computation of
   * checksum is enabled. - if ENABLED then the checksum is computed by an external service and
   * stored in an extended attribute. - if NOT ENABLED return with a NULL value. This method is
   * blocking (i.e. waits for the checksum to be computed, if it is enabled).
   *
   * @param fileName file absolute path.
   * @return the computed checksum for the given file or <code>null</code> if some error occurred.
   *     The error is logged.
   * @throws FileNotFoundException
   */
  public String getDefaultChecksum(String fileName) throws FileNotFoundException {

    log.debug("Requesting checksum for file: {}", fileName);

    String checksum = null;
    try {
      checksum = StormEA.getChecksum(fileName, defaultAlgorithm);
    } catch (ExtendedAttributesException e) {
      log.warn(e.getMessage(), e);
    }

    return checksum;
  }

  /**
   * Checks whether the given file has a checksum stored in an extended attribute.
   *
   * @param fileName file absolute path.
   * @return <code>true</code> if an extended attribute storing the checksum was found, <code>false
   *     </code> otherwise.
   * @throws ExtendedAttributesException
   * @throws NotSupportedException
   * @throws FileNotFoundException
   */
  public boolean hasDefaultChecksum(String fileName) throws FileNotFoundException {

    String value = null;

    try {
      value = StormEA.getChecksum(fileName, defaultAlgorithm);
    } catch (ExtendedAttributesException e) {
      log.warn(
          "Error manipulating EA for default algorithm {} on file: {} ExtendedAttributesException: {}",
          defaultAlgorithm,
          fileName,
          e.getMessage());
    }

    return (value != null);
  }

  public Map<ChecksumAlgorithm, String> getChecksums(String fileName) throws FileNotFoundException {

    return StormEA.getChecksums(fileName);
  }
}
