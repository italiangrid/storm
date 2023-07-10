/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.filesystem;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a SpaceSystem that always acknowledges a successful space reservation; it
 * is meant to be used with filesystems that do not support natively space reservation operations.
 * It acts as a mock object!
 *
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date June 2006
 */
public class MockSpaceSystem implements SpaceSystem {

  private String mountpoint = ""; // String representing the local mount point,
  // that is the root from which This
  // SpaceSystem operates!
  private static Logger log = LoggerFactory.getLogger(MockSpaceSystem.class);

  public MockSpaceSystem(String mountpoint) throws SpaceSystemException {

    if (mountpoint == null) {
      throw new SpaceSystemException("Supplied mountpoint is null!");
    }
    this.mountpoint = mountpoint;
  }

  /**
   * Method that follows the contract specified in SpaceSystem Interface: please refer there for
   * further info.
   */
  public long reserveSpace(String pathToFile, long size) throws ReservationException {

    java.io.File localFile = new java.io.File(pathToFile);
    try {
      localFile.createNewFile();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new ReservationException(
          "IO exception while creating local File named : " + pathToFile, e);
    } catch (SecurityException e) {
      log.error(e.getMessage(), e);
      throw new ReservationException(
          "Security exception while creating local File named : " + pathToFile, e);
    }
    return size;
  }

  /**
   * Method that follows the contract specified in SpaceSystem Interface: please refer there for
   * further info.
   */
  public long compactSpace(String pathToFile) throws ReservationException {

    String explanation = "Compact Space operation currently not supported!";
    log.error(explanation);
    throw new ReservationException(explanation);
  }

  /**
   * Method that follows the contract specified in SpaceSystem Interface: please refer there for
   * further info.
   */
  public void removeSpace(String pathToFile) throws ReservationException {

    String explanation = "Remove Space operation currently not supported!";
    log.error(explanation);
    throw new ReservationException(explanation);
  }

  /**
   * Method that follows the contract specified in SpaceSystem Interface: please refer there for
   * further info.
   */
  public long changeSize(String pathToFile, long newSize) throws ReservationException {

    String explanation = "Change Size operation currently not supported!";
    log.error(explanation);
    throw new ReservationException(explanation);
  }

  @Override
  public String toString() {

    return "MockSpaceSystem";
  }
}
