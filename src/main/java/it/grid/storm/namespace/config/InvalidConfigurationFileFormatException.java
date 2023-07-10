/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.config;

import it.grid.storm.namespace.*;

/**
 * This class represents an Exception throws if TDirOptionData is not well formed. *
 *
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */
public class InvalidConfigurationFileFormatException extends NamespaceException {

  private boolean notSupported = false;

  public InvalidConfigurationFileFormatException(String fileName) {

    notSupported = fileName.endsWith(".cfg") || fileName.endsWith(".xml");
  }

  public String toString() {

    return ("Configuration File Format NOT SUPPORTED = Not .xml or .cfg formati = " + notSupported);
  }
}
